/**
 *  file:    FileArray.java
 *  desc:    list of the FileData objects
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.io.File;
import java.util.*;

public class FileArray implements Iterable<FileData>{
    private List<FileData> fileArray = new ArrayList<>();

    public void add(FileData newFile){
        fileArray.add(newFile);
    }

    public void addAll(FileArray anotherFileArray){
        for (FileData fileData : anotherFileArray){
            fileArray.add(fileData);
        }
    }

    public boolean isEmpty(){
        return fileArray.isEmpty();
    }

    public Iterator<FileData> iterator(){
        return fileArray.iterator();
    }

    public int size(){
        return fileArray.size();
    }

    public FileData get(int index){
        return fileArray.get(index);
    }

    public List<String> getDirs() {
        Set<String> uniqDirs = new HashSet<>();
        for (FileData fData : fileArray){
            uniqDirs.add(fData.getPath());
        }
        return new ArrayList<>(uniqDirs);
    }

}
