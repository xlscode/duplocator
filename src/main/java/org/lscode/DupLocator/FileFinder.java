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

        // TODO: consider Files::newDirectoryStream - watch out for IOException

        String [] subNodes = nextDir.list();
        if (subNodes != null){
            for (String path : subNodes) {
                java.io.File newNode = new File(nextDir, path);
                if (newNode.isDirectory()) {
                    find(newNode);
                }
                if (newNode.isFile()) {
                    files.add(new FileData(newNode));
                    if (files.size() % step == 0){
                        setChanged();
                        notifyObservers();
                    }
                }
            }
        }
        else{
            failedDirs.add(nextDir.getAbsolutePath());
        }
    }

    public List<String> getFailedDirs(){
        return failedDirs;
    }

    public int filesProcessed(){
        return this.files.size();
    }

    public Boolean processing(){
        return this.processing;
    }

}
