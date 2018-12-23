/**
 *  file:    AbstractFileStorage.java
 *  desc:    abstract parent for classes storing FileArray objects segregated according to a chosen criterion
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.util.*;

public abstract class AbstractFileStorage<T> implements Iterable<FileArray>{

    protected Map<T, FileArray> fileMap = new HashMap<>();

    @Override
    abstract public Iterator<FileArray> iterator();

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

}
