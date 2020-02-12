/*
 *  file:    FileFinder.java
 *  desc:    searches for files in a filesystem
 *  author:  ls-code
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class FileFinder extends Observable {
    private FileArray files = new FileArray();
    private File startDir;
    private List<String> failedDirs = new ArrayList<>();
    private int filesProcessed = 0;
    private Boolean processing = false;
    private final int step = 50;

    public FileFinder(String startPath) {
        this.startDir = new File(startPath);
    }

    public FileFinder(File startDir) {
        this.startDir = startDir;
    }

    public FileArray getFiles() {
        return files;
    }

    public void find(){
        processing = true;
        find(startDir);
        processing = false;
        setChanged();
        notifyObservers();
    }

    private void find(File nextDir){
        String [] subNodes = nextDir.list();
        if (subNodes == null){
            failedDirs.add(nextDir.getAbsolutePath());
        }
        else {
            for (String aPath : subNodes) {
                java.io.File aNode = new File(nextDir, aPath);
                if (aNode.isDirectory()) {
                    find(aNode);
                }
                if (aNode.isFile()) {
                    files.add(new FileData(aNode));
                    filesProcessed += 1;
                    if (filesProcessed % step == 0){
                        setChanged();
                        notifyObservers();
                    }
                }
            }
        }
    }

    public List<String> getFailedDirs(){
        return failedDirs;
    }

    public int filesProcessed(){
        return this.filesProcessed;
    }

    public Boolean processing(){
        return this.processing;
    }

}
