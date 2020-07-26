/*
 *  file:    DupLocator.java
 *  desc:    the main class of the DupLocator package
 *  author:  ls-code
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

import org.lscode.DepExec.DependencyExecutor;

public class DupLocator extends Observable implements Observer {

    private MultiDigest digestGenerator;
    private String[] paths;
    private FileArray allFiles = new FileArray();
    private FileStorage<String> dups;
    private FileStorageMap<String, String> namesakes;
    private List<String> dirs;
    private List<String> failedDirs = new ArrayList<>();
    private long filesProcessedTotal = 0;
    private long filesProcessedByCurrentFinder = 0;
    private Stage stage = Stage.NONE;
    private Phase phase = Phase.NONE;
//    private Boolean processing = false;  // probably not necessary
//    private int dirsTotal = 0; // probably not necessary
//    private int dirNo = 0; // probably not necessary
    private DependencyExecutor<Processes> executor = new DependencyExecutor<>();


    public enum Stage {
        NONE, FILES, DUPLICATES, NAMESAKES
    }

    public enum Phase {
        NONE, START, INPROGRESS, RESULT, END
    }

    public enum Processes {
        FIND_DUPS, FIND_NAMESAKES, FIND_DIRS, FIND_ALLFILES
    }

    public DupLocator(MultiDigest digestGenerator, String[] paths){
        this.paths = paths;
        this.digestGenerator = digestGenerator;
//        dirsTotal = Array.getLength(paths);  // probably not necessary
        setupDependencies();
    }

    public FileStorage<String> getDups(){
        executor.runProcessChain(Processes.FIND_DUPS);
        return dups;
    }

    public FileStorageMap<String, String> getNamesakes(){
        executor.runProcessChain(Processes.FIND_NAMESAKES);
        return namesakes;
    }

    public List<String> getDirectories(){
        executor.runProcessChain(Processes.FIND_DIRS);
        return dirs;
    }

    public List<String> getDirectoriesSorted(){
        List<String> dirs = getDirectories();
        Collections.sort(dirs);
        return dirs;
    }

    public List<String> getFailedFiles(){
//        List<String> failedFiles = new ArrayList<>();
//        for (FileData fData : allFiles){
//            if (fData.hasProblems()) {failedFiles.add(fData.getFullPath());}
//        }
//        return failedFiles;

        return allFiles.stream()
                .filter(FileData::hasProblems)
                .map(FileData::getFullPath)
                .collect(Collectors.toList());
    }

    public List<String> getFailedDirs(){
        return failedDirs;
    }

    public long filesProcessed(){
        return filesProcessedTotal + filesProcessedByCurrentFinder;
    }

//    public Boolean processing(){
//        return processing;
//    }

    public Stage stage(){
        return stage;
    }

    public Phase phase() {
        return phase;
    }

    protected void findFilesInAllDirs(){
        broadcastChange(Stage.FILES, Phase.START);
        stateChange(Stage.FILES, Phase.INPROGRESS);

        for (String aPath : paths){
//            dirNo++;  // probably not necessary
            findFilesInOneDir(aPath);
        }
        broadcastChange(Stage.FILES, Phase.RESULT);
        resetChange();
    }

    protected void findFilesInOneDir(String startPath){
        FileFinder finder = new FileFinder(startPath);
        finder.addObserver(this);
        finder.find();
        allFiles.addAll(finder.getFiles());
        failedDirs.addAll(finder.getFailedDirs());
    }

    protected void findDups() {
        broadcastChange(Stage.DUPLICATES, Phase.START);
        int step = 50;
        FileStorage<String> digested = new FileStorage<>();
        FileStorage<Long> fAllSizes = allFiles.groupBySize();
        FileStorage<Long> fSameSize = fAllSizes.getRepeated();
        stateChange(Stage.DUPLICATES, Phase.INPROGRESS);
        for (FileArray group : fSameSize) {
            group.fillInDigests(digestGenerator);
            FileArray newGroup = group.errorless();
            for (FileData fData : newGroup){
                String fdCombined = fData.getDigests().combined();
                digested.put(fdCombined, fData);
                broadcastChangeIfStep(step);
            }
        }
        this.dups = digested.getRepeated();
        broadcastChange(Stage.DUPLICATES, Phase.RESULT);

        filesProcessedTotal = dups.filesTotal();
        broadcastChange(Stage.DUPLICATES, Phase.END);
        resetChange();
    }

    protected void findNamesakes(){
        this.namesakes = new FileStorageMap<>();
        broadcastChange(Stage.NAMESAKES, Phase.START);
        int step = 20;
        FileStorage<String>  fAllNames = allFiles.groupByName();
        FileStorage<String> fSameNames = fAllNames.getRepeated();
        broadcastChange(Stage.NAMESAKES, Phase.INPROGRESS);
        for (FileArray nameGroup : fSameNames) {
            FileStorage<String> fSameWithSameName = new FileStorage<>();
            int assortedCnt = 1;
            for (FileData fData : nameGroup) {
                broadcastChangeIfStep(step);
                if (fData.hasDigests()) {
                    fSameWithSameName.put(fData.getDigests().combined(), fData);
                } else {
                    String assortedKey = String.format("assorted_%05d", assortedCnt);
                    assortedCnt += 1;
                    fSameWithSameName.put(assortedKey, fData);
                }
            }
            String fName = nameGroup.get(0).getName();
            namesakes.add(fName, fSameWithSameName);
        }
        broadcastChange(Stage.NAMESAKES, Phase.RESULT);
        resetChange();
    }

    protected void findDirs(){
        dirs = dups.getDirs();
    }

    @Override
    public void update(Observable o, Object arg) {
        FileFinder fileFinder = (FileFinder)o;
        filesProcessedByCurrentFinder = fileFinder.filesProcessed();
        if (!fileFinder.processing()){
            filesProcessedTotal = filesProcessedTotal + filesProcessedByCurrentFinder;
            filesProcessedByCurrentFinder = 0;
        }
        broadcastChange();
    }

    private void broadcastChange(Stage stage, Phase phase){
        this.stage = stage;
        this.phase = phase;
        setChanged();
        notifyObservers();
    }

    private void broadcastChange(){
        setChanged();
        notifyObservers();
    }

    private void broadcastChangeIfStep(int step){
        filesProcessedTotal += 1;
        if (filesProcessedTotal % step == 0){
            broadcastChange();
        }
        broadcastChange();
    }

    private void stateChange(Stage stage, Phase phase){
        this.stage = stage;
        this.phase = phase;
    }

    private void resetChange() {
        stateChange(Stage.NONE, Phase.NONE);
        filesProcessedTotal = 0;
    }

    private void setupDependencies(){
        executor.addProcess(Processes.FIND_DUPS, this::findDups, Processes.FIND_ALLFILES);
        executor.addProcess(Processes.FIND_NAMESAKES, this::findNamesakes, Processes.FIND_DUPS);
        executor.addProcess(Processes.FIND_DIRS, this::findDirs, Processes.FIND_DUPS);
        executor.addLastProcess(Processes.FIND_ALLFILES, this::findFilesInAllDirs);
    }
}
