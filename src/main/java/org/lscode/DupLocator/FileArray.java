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
//import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
//import java.util.Set;
//import java.util.HashSet;

public class FileArray implements Iterable<FileData>{
    private List<FileData> fDataArray = new ArrayList<>();

    public FileArray(){}

    public FileArray(List<FileData> fDataList){
        fDataArray.addAll(fDataList);
    }

    public void add(FileData newFile){
        fDataArray.add(newFile);
    }

    public void addAll(FileArray anotherFileArray){
        fDataArray.addAll(anotherFileArray.innerList());
    }

//    public void addAll(List<FileData> fDataList){         // probably not needed
//        fDataArray.addAll(fDataList);
//    }

//    public boolean isEmpty(){          // probably not needed
//        return fileArray.isEmpty();
//    }

    protected List<FileData> innerList(){
        return fDataArray;
    }

    public Iterator<FileData> iterator(){
        return fDataArray.iterator();
    }

    public int size(){
        return fDataArray.size();
    }

    public FileData get(int index){
        return fDataArray.get(index);
    }

    public List<String> getDirs() {
//        Set<String> uniqDirs = new HashSet<>();
//        for (FileData fData : fileArray){
//            uniqDirs.add(fData.getPath());
//        }
//        return new ArrayList<>(uniqDirs);

        return fDataArray.stream().map(FileData::getPath).distinct().collect(Collectors.toList());
    }

    public Stream<FileData> stream(){
        return fDataArray.stream();
    }

    public FileStorage<Long> groupBySize(){
        FileStorage<Long> groupped = new FileStorage<>();
        for (FileData fData : fDataArray){
            groupped.put(fData.getSize(), fData);
        }
        return groupped;
    }

    public FileStorage<String> groupByName(){
        FileStorage<String> groupped = new FileStorage<>();
        for (FileData fData : fDataArray){
            groupped.put(fData.getName(), fData);
        }
        return groupped;
    }

    public FileArray errorless(){
        List<FileData> problemless;
        problemless = fDataArray.stream()
//                .filter(((Predicate<FileData>)FileData::hasProblems).negate())
                .filter((FileData::noProblems))
                .collect(Collectors.toList());
        return new FileArray(problemless);
    }

    public void fillInDigests(MultiDigest digestGenerator){
        for (FileData fData: fDataArray){
            fData.fillInDigests(digestGenerator);
        }
    }
}
