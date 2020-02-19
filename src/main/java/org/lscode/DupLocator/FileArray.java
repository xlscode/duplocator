/*
 *  file:    FileArray.java
 *  desc:    list of the FileData objects
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//import java.util.Set;
//import java.util.HashSet;

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

//    public boolean isEmpty(){          // probably not needed
//        return fileArray.isEmpty();
//    }

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
//        Set<String> uniqDirs = new HashSet<>();
//        for (FileData fData : fileArray){
//            uniqDirs.add(fData.getPath());
//        }
//        return new ArrayList<>(uniqDirs);

        return fileArray.stream().map(FileData::getPath).distinct().collect(Collectors.toList());
    }

    public Stream<FileData> stream(){
        return fileArray.stream();
    }

}
