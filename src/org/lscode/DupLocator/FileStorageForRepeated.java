/**
 *  file:    FileStorageForRepeated.java
 *  desc:    concrete class for storing FileArray objects segregated according to a chosen criterion
 *           the iterator returns only FileArray objects with more than one element
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FileStorageForRepeated<T> extends AbstractFileStorage<T> {

    private class FStorageIterator implements Iterator<FileArray> {
        private Iterator<FileArray> fileArrayIterator;
        private FileArray next;
        private Boolean   haveNext = false;
        private Boolean   nextSet = false;
        private Boolean   endOfCollection = false;


        public FStorageIterator(){
            fileArrayIterator = fileMap.values().iterator();
        }

        @Override
        public boolean hasNext() {
            if (!nextSet) {prepareNext();}
            if (endOfCollection) return false;
            return haveNext;
        }

        @Override
        public FileArray next() {
            if (!nextSet) prepareNext();
            if (endOfCollection) throw new NoSuchElementException();
            nextSet = false;
            return next;
        }

        private void prepareNext(){
            FileArray nextFA;
            Boolean   nextFound = false;

            while (!nextFound && fileArrayIterator.hasNext()){
                nextFA = fileArrayIterator.next();

                if (nextFA.size() > 1){
                    next = nextFA;
                    haveNext = true;
                    nextSet = true;
                    nextFound = true;
                    break;
                }
            }
            if (!nextFound){
                next = null;
                haveNext = false;
                nextSet = true;
                endOfCollection = true;
            }
        }
    }

    @Override
    public Iterator<FileArray> iterator() {
        return new FStorageIterator();
    }

}
