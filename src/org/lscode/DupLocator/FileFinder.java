/**
 *  file:    FileFinder.java
 *  desc:    searches for files in a filesystem
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileFinder {
    private File startDir;
    private List<String> failedDirs = new ArrayList<>();

    public FileFinder(String startPath) {
        this.startDir = new File(startPath);
    }

    public FileFinder(File startDir) {
        this.startDir = startDir;
    }

    public FileArray find(){
        return find(startDir);
    }

    private FileArray find(File nextDir){
        FileArray files = new FileArray();
        String [] subNodes;
        subNodes = nextDir.list();
        if (subNodes == null){
            failedDirs.add(nextDir.getAbsolutePath());
        }
        else {
            for (String aPath : subNodes) {
                java.io.File aNode = new File(nextDir, aPath);
                if (aNode.isDirectory()) {
                    files.addAll(find(aNode));
                }
                if (aNode.isFile()) {
                    files.add(new FileData(aNode));
                }
            }
        }

        return files;
    }

    public List<String> getFailedDirs(){
        return failedDirs;
    }

}

