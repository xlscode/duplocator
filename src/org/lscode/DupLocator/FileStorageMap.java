/**
 *  file:    FileStorageMap.java
 *  desc:    map class for storing FileStorage objects
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileStorageMap<T, U> implements Iterable<AbstractFileStorage<U>>  {
    private Map<T, AbstractFileStorage<U>> storageMap = new HashMap<>();

    @Override
    public Iterator<AbstractFileStorage<U>> iterator() {
        return storageMap.values().iterator();
    }

    public void add(T key, AbstractFileStorage<U> fileStorage){
        storageMap.put(key, fileStorage);
    }

    public AbstractFileStorage<U> get(T key){
        return storageMap.get(key);
    }

    public int size(){
        return storageMap.size();
    }
}
