/**
 *  file:    FileStorage.java
 *  desc:    concrete class for storing FileArray objects segregated according to a chosen criterion
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.util.*;

public class FileStorage<T> extends AbstractFileStorage<T> {

    @Override
    public Iterator<FileArray> iterator() {
        return fileMap.values().iterator();
    }

}
