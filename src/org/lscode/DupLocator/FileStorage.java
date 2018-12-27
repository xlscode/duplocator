/**
 *  file:    FileStorage.java
 *  desc:    concrete class for storing FileArray objects segregated according to a chosen criterion
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.util.*;

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

    public void putArray(T criterion, FileArray fArray){
        if (fileMap.containsKey(criterion)){
            fileMap.get(criterion).addAll(fArray);
        }
        else{
            fileMap.put(criterion, fArray);
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

        for (FileArray fArray: this){
            uniqDirs.addAll(fArray.getDirs());
        }
        return new ArrayList<>(uniqDirs);
    }

    public FileStorage<T> getRepeated(){
        FileStorage<T> result = new FileStorage<>();
        FileArray arr;

        for (T aKey : fileMap.keySet()){
            arr = fileMap.get(aKey);
            if (arr.size() > 1){
                result.putArray(aKey, arr);
            }
        }

        return result;
    }

    public long filesTotal(){
        long result = 0;

        for (FileArray fArray: this){
            result += fArray.size();
        }
        return result;
    }
}
