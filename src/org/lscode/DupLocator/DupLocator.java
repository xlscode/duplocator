/**
 *  file:    DupLocator.java
 *  desc:    the main class of the DupLocator package
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.util.*;

public class DupLocator {

    private MultiDigest digestGenerator;
    private String startPath;
    private FileArray allFiles;
    private AbstractFileStorage<String> dups;
    private FileStorageMap<String, String> namesakes;
    private List<String> dirs;
    private List<String> failedDirs;

    public DupLocator(MultiDigest digestGenerator, String startPath){
        this.startPath = startPath;
        this.digestGenerator = digestGenerator;
    }

    public AbstractFileStorage<String> getDups(){
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

    protected void findFiles(){
        FileFinder finder = new FileFinder(startPath);
        allFiles = finder.find();
        failedDirs = finder.getFailedDirs();
    }

    protected void findDups() {
        AbstractFileStorage<Long> fSameSize = new FileStorageForRepeated<>();
        dups = new FileStorageForRepeated<>();

        for (FileData aFile : allFiles){
            fSameSize.put(aFile.getSize(), aFile);
        }

        for (FileArray group : fSameSize) {
            for (FileData fData : group){
                fData.fillInDigests(digestGenerator);
                if (!fData.hasProblems()) {
                    String fdCombined = fData.getDigests().combined();
                    dups.put(fdCombined, fData);
                }
            }
        }
    }

    protected void findNamesakes(){
        namesakes = new FileStorageMap<>();
        AbstractFileStorage<String> fSameName = new FileStorageForRepeated<>();

        for (FileData fData : allFiles){
            fSameName.put(fData.getName(), fData);
        }

        for (FileArray nameGroup : fSameName) {
            AbstractFileStorage<String> fSameWithSameName = new FileStorage<>();
            int assortedCnt = 1;
            String fName = "";

            for (FileData fData : nameGroup) {
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

    }

    protected void findDirs(){
        dirs = dups.getDirs();
    }

}
