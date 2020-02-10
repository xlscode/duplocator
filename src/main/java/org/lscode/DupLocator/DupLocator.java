/**
 *  file:    DupLocator.java
 *  desc:    the main class of the DupLocator package
 *  author:  ls-code
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

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
    private Phase phase;
    private Boolean processing = false;
    private int dirsTotal = 0;
    private int dirNo = 0;


    public enum Stage {
        NONE, FILES, DUPLICATES, NAMESAKES
    }

    public enum Phase {
        START, INPROGRESS, RESULT, END
    }

    public DupLocator(MultiDigest digestGenerator, String[] paths){
        this.paths = paths;
        this.digestGenerator = digestGenerator;
        dirsTotal = Array.getLength(paths);
    }

    public FileStorage<String> getDups(){
        if (dups == null){
            if (allFiles.isEmpty() ){
                findFilesInAllDirs();
            }
            findDups();
        }
        return dups;
    }

    public FileStorageMap<String, String> getNamesakes(){
        if (namesakes == null){
            if (allFiles == null){
                findFilesInAllDirs();
            }
            findNamesakes();
        }
        return namesakes;
    }

    public List<String> getDirectories(){
        if (dirs == null) {
            if (dups == null){
                if (allFiles == null){
                    findFilesInAllDirs();
                }
                findDups();
            }
            findDirs();
        }
        return dirs;
    }

    public List<String> getDirectoriesSorted(){
        List<String> dirs = getDirectories();
        Collections.sort(dirs);
        return dirs;
    }

    public List<String> getFailedFiles(){
        List<String> failedFiles = new ArrayList<>();
        for (FileData fData : allFiles){
            if (fData.hasProblems()) {failedFiles.add(fData.getFullPath());}
        }
        return failedFiles;
    }

    public List<String> getFailedDirs(){
        return failedDirs;
    }

    public long filesProcessed(){
        return filesProcessedTotal + filesProcessedByCurrentFinder;
    }

    public Boolean processing(){
        return processing;
    }

    public Stage stage(){
        return stage;
    }

    public Phase phase() {
        return phase;
    }

    protected void findFilesInAllDirs(){
        stage = Stage.FILES;
        phase = Phase.START;
        setChanged();
        notifyObservers();
        phase = Phase.INPROGRESS;

        for (String aPath : paths){
            dirNo++;
            findFilesInOneDir(aPath);
        }
        phase = Phase.RESULT;
        setChanged();
        notifyObservers();
        stage = Stage.NONE;
    }

    protected void findFilesInOneDir(String startPath){
        FileFinder finder = new FileFinder(startPath);
        finder.addObserver(this);
        finder.find();
        allFiles.addAll(finder.getFiles());
        failedDirs.addAll(finder.getFailedDirs());
    }

    protected void findDups() {
        FileStorage<Long> fAllSizes = new FileStorage<>();
        FileStorage<Long> fSameSize;
        FileStorage<String> digested = new FileStorage<>();
        int step = 50;

        stage = Stage.DUPLICATES;
        filesProcessedTotal = 0;
        phase = Phase.START;
        setChanged();
        notifyObservers();

        for (FileData aFile : allFiles){
            fAllSizes.put(aFile.getSize(), aFile);
        }

        fSameSize = fAllSizes.getRepeated();
        phase = Phase.INPROGRESS;
        for (FileArray group : fSameSize) {
            for (FileData fData : group){
                fData.fillInDigests(digestGenerator);
                if (!fData.hasProblems()) {
                    String fdCombined = fData.getDigests().combined();
                    digested.put(fdCombined, fData);
                    filesProcessedTotal +=1;
                    if (filesProcessedTotal % step == 0){
                        setChanged();
                        notifyObservers();
                    }

                }
            }
        }
        dups = digested.getRepeated();
        phase = Phase.RESULT;
        setChanged();
        notifyObservers();

        filesProcessedTotal = dups.filesTotal();
        phase = Phase.END;
        setChanged();
        notifyObservers();

        stage = Stage.NONE;
    }

    protected void findNamesakes(){
        namesakes = new FileStorageMap<>();
        FileStorage<String> fAllNames = new FileStorage<>();
        FileStorage<String> fSameNames;
        int step = 20;

        stage = Stage.NAMESAKES;
        filesProcessedTotal = 0;
        phase = Phase.START;
        setChanged();
        notifyObservers();
        for (FileData fData : allFiles){
            fAllNames.put(fData.getName(), fData);
        }
        fSameNames = fAllNames.getRepeated();

        phase = Phase.INPROGRESS;
        for (FileArray nameGroup : fSameNames) {
            FileStorage<String> fSameWithSameName = new FileStorage<>();
            int assortedCnt = 1;
            String fName = "";

            for (FileData fData : nameGroup) {
                filesProcessedTotal +=1;
                if (filesProcessedTotal % step == 0){
                    setChanged();
                    notifyObservers();
                }
                fName = fData.getName();
                if (fData.hasDigests()) {
                    fSameWithSameName.put(fData.getDigests().combined(), fData);
                } else {
                    String assortedKey = String.format("assorted_%05d", assortedCnt);
                    assortedCnt += 1;
                    fSameWithSameName.put(assortedKey, fData);
                }
            }
            namesakes.add(fName, fSameWithSameName);
        }
        phase = Phase.RESULT;
        setChanged();
        notifyObservers();
        stage = Stage.NONE;
    }

    protected void findDirs(){
        dirs = dups.getDirs();
    }

    @Override
    public void update(Observable o, Object arg) {
        this.filesProcessedByCurrentFinder = ((FileFinder)o).filesProcessed();
        if (!((FileFinder)o).processing()){
            filesProcessedTotal = filesProcessedTotal + filesProcessedByCurrentFinder;
            filesProcessedByCurrentFinder = 0;
        }
        setChanged();
        notifyObservers();
    }
}
