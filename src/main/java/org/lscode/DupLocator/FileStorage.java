/*
 *  file:    FileStorage.java
 *  desc:    concrete class for storing FileArray objects segregated according to a chosen criterion
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileStorage<T> implements Iterable<FileArray>{

    protected Map<T, FileArray> fileMap = new HashMap<>();

//    public FileStorage(){}

//    public FileStorage(FileStorage<T> source){
//    }

    public Iterator<FileArray> iterator() {
        return fileMap.values().iterator();
    }

    public void put(T criterion, FileData fileData){
        FileArray fileList;
        if (fileMap.containsKey(criterion)){
            fileList = fileMap.get(criterion);
        }
        else{
            fileList = new FileArray();
            fileMap.put(criterion, fileList);
        }
        fileList.add(fileData);
    }

    public void putArray(T criterion, FileArray fileArray){
        if (fileMap.containsKey(criterion)){
            fileMap.get(criterion).addAll(fileArray);
        }
        else{
            fileMap.put(criterion, fileArray);
        }
    }

    public int size(){
        return fileMap.size();
    }

    public FileArray get(T criterion){
        return fileMap.get(criterion);
    }

    public boolean containsKey(T criterion){
        return fileMap.containsKey(criterion);
    }

    public Collection<FileArray> values(){
        return fileMap.values();
    }

    public List<String> getDirs() {
        Set<String> uniqDirs = new HashSet<>();
        for (FileArray fileArray: this){
            uniqDirs.addAll(fileArray.getDirs());
        }
//        Set<String> uniqDirs = fileMap.values().stream().flatMap(fa->getDirs().stream()).collect(Collectors.toSet());
        return new ArrayList<>(uniqDirs);
    }

    public FileStorage<T> getRepeated(){
        FileStorage<T> result = new FileStorage<>();
        FileArray arr;

        //TODO: change to stream
        for (T aKey : fileMap.keySet()){
            arr = fileMap.get(aKey);
            if (arr.size() > 1){
                result.putArray(aKey, arr);
            }
        }
        return result;
    }

    public long filesTotal(){
        return fileMap.values().stream().mapToInt(fa->size()).sum();
    }
}
