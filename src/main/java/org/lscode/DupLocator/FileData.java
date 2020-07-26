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
    private String name;
    private String path;
    private long size;
    private long mtime;
    private Digests digests;
    private Boolean error = false;

    FileData(final File file) {
        this.name   = file.getName();
        this.path   = file.getParent();
        this.size   = file.length();
        this.mtime  = file.lastModified();
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
        return (digests != null);
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
        return builder.append(name)
                .append("\n")
                .append(path)
                .append("\n")
                .append(size)
                .append("\n")
                .append(mtime)
                .append("\n")
                .toString();
    }
}
