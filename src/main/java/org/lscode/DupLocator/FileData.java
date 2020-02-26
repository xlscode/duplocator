/*
 *  file:    FileData.java
 *  desc:    class representing a file
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.io.File;
import java.io.IOException;

public class FileData {

    private String  name;
    private String  path;
    private long    size;
    private long    mtime;
    private Digests digests;
    private Boolean error = false;

    FileData(File aFile) {
        this.name   = aFile.getName();
        this.path   = aFile.getParent();
        this.size   = aFile.length();
        this.mtime  = aFile.lastModified();
    }


    FileData(String name, String path, long size, long mtime) {
        this.name   = name;
        this.path   = path;
        this.size   = size;
        this.mtime  = mtime;
    }

    FileData(String name, String path, long size, long mtime, Digests digests) {
        this.name    = name;
        this.path    = path;
        this.size    = size;
        this.mtime   = mtime;
        this.digests = digests;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public long getMtime() {
        return mtime;
    }

    public String getFullPath(){
        return path + File.separator + name;
    }

    public Digests getDigests() {
        return digests;
    }

    public void fillInDigests(MultiDigest digestCalc){
        try{
            this.digests = digestCalc.calculate(this);
        }
        catch(IOException e){
            error = true;
        }
    }

//    public void setDigests(Digests digests) {  // probably not needed
//        this.digests = digests;
//    }

    public Boolean hasDigests(){
        return !(digests == null);
    }

    public Boolean hasProblems(){
        return error;
    }

    public Boolean noProblems(){
        return !error;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(name);
        builder.append("\n");
        builder.append(path);
        builder.append("\n");
        builder.append(size);
        builder.append("\n");
        builder.append(mtime);
        builder.append("\n");

        return builder.toString();
    }
}
