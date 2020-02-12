/*
 *  file:    FileStorageMap.java
 *  desc:    map class for storing FileStorage objects
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileStorageMap<T, U> implements Iterable<FileStorage<U>>  {
    private Map<T, FileStorage<U>> storageMap = new HashMap<>();

    @Override
    public Iterator<FileStorage<U>> iterator() {
        return storageMap.values().iterator();
    }

    public void add(T key, FileStorage<U> fileStorage){
        storageMap.put(key, fileStorage);
    }

    public FileStorage<U> get(T key){
        return storageMap.get(key);
    }

    public int size(){
        return storageMap.size();
    }
}
