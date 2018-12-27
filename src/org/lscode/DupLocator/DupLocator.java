/**
 *  file:    DupLocator.java
 *  desc:    the main class of the DupLocator package
 *  author:  ls-code
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.util.*;

public class DupLocator extends Observable implements Observer{

    private MultiDigest digestGenerator;
    private String startPath;
    private FileArray allFiles;
    private FileStorage<String> dups;
    private FileStorageMap<String, String> namesakes;
    private List<String> dirs;
    private List<String> failedDirs;
    private long filesProcessed = 0;
    private Stage stage = Stage.NONE;
    private Phase phase;
    private Boolean processing = false;

    public enum Stage {
        NONE, FILES, DUPLICATES, NAMESAKES
    }

    public enum Phase {
        START, INPROGRESS, RESULT, END
    }

    public DupLocator(MultiDigest digestGenerator, String startPath){
        this.startPath = startPath;
        this.digestGenerator = digestGenerator;
    }

    public FileStorage<String> getDups(){
        if (dups == null){
            if (allFiles == null){
                findFiles();
            }
            findDups();
        }
        return dups;
    }

    public FileStorageMap<String, String> getNamesakes(){
        if (namesakes == null){
            if (dups == null){
                if (allFiles == null){
                    findFiles();
                }
                findDups();
            }
            findNamesakes();
        }
        return namesakes;
    }

    public List<String> getDirectories(){
        if (dirs == null) {
            if (dups == null){
                if (allFiles == null){
                    findFiles();
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
        return filesProcessed;
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

    protected void findFiles(){
        FileFinder finder = new FileFinder(startPath);
        finder.addObserver(this);
        stage = Stage.FILES;
        phase = Phase.START;
        setChanged();
        notifyObservers();
        phase = Phase.INPROGRESS;
        finder.find();
        allFiles = finder.getFiles();
        failedDirs = finder.getFailedDirs();
        stage = Stage.NONE;
    }

    protected void findDups() {
        FileStorage<Long> fAllSizes = new FileStorage<>();
        FileStorage<Long> fSameSize;
        FileStorage<String> digested = new FileStorage<>();
        int step = 50;

        stage = Stage.DUPLICATES;
        filesProcessed = 0;
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
                    filesProcessed +=1;
                    if (filesProcessed % step == 0){
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

        filesProcessed = dups.filesTotal();
        phase = Phase.END;
        setChanged();
        notifyObservers();


        stage = Stage.NONE;
    }

    protected void findNamesakes(){
        namesakes = new FileStorageMap<>();
        FileStorage<String> fAllNames = new FileStorage<>();
        FileStorage<String> fSameNames = new FileStorage<>();
        int step = 20;

        stage = Stage.NAMESAKES;
        filesProcessed = 0;
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
                filesProcessed +=1;
                if (filesProcessed % step == 0){
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
        this.filesProcessed = ((FileFinder)o).filesProcessed();
        if (!((FileFinder)o).processing()){
            phase = Phase.RESULT;
        }
        setChanged();
        notifyObservers();
    }
}
