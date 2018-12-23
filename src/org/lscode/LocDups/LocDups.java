/**
 *  file:    LocDups.java
 *  desc:    the "app" file - calls classes from the DupLocator package
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.LocDups;

import org.lscode.DupLocator.*;

import java.lang.reflect.Array;
import java.util.List;

public class LocDups {

    private final static String[] HASH_LIST = {"MD5", "SHA-256"};
    private final static int BUFFER_SIZE = 1024 * 32;

    private DupLocator dupLocator;

    public static void main(String[] args){
        DupLocator dupLoc;
        MultiDigest digestGenerator = null;

        if (Array.getLength(args) == 0){
            System.err.println("I need a parameter (start directory), please.");
            System.exit(1);
        }

        try {
            digestGenerator = new MultiDigest(HASH_LIST, BUFFER_SIZE);
        }
        catch (Exception e){
            System.err.println("Error occured while initializing the app.");
            System.err.println("This should never happen.");
            System.exit(1);
        }
        dupLoc = new DupLocator(digestGenerator, args[0]);
        LocDups app = new LocDups(dupLoc);
        app.run();
    }

    public LocDups(DupLocator dupLocator){

        this.dupLocator = dupLocator;
    }

    public void run(){
        AbstractFileStorage<String> dups = dupLocator.getDups();
        FileStorageMap<String, String> namesakes = dupLocator.getNamesakes();
        List<String> dupDirs;
        List<String> failedDirs;
        List<String> failedFiles;

        if (dups.size() > 0){
            System.out.println("\n\nDuplicates:\n");
            printDups(dups);

            System.out.println("\n\nDirs:\n");

            dupDirs = dupLocator.getDirectoriesSorted();
            printDirs(dupDirs);
        }

        if (namesakes.size() > 0){
            System.out.println("\n\nNamesakes:\n");
            printNamesakes(namesakes);
        }

        failedDirs = dupLocator.getFailedDirs();
        if (failedDirs.size() > 0){
            System.out.println("\n\nProblems occured reading the following directories:\n");
            printStringList(failedDirs);
        }

        failedFiles = dupLocator.getFailedFiles();
        if (failedFiles.size() > 0){
            System.out.println("\n\nProblems occured reading the following files:\n");
            printStringList(failedFiles);
        }

    }

    private void printDups(AbstractFileStorage<String> dubs){
        for (FileArray fileArray: dubs){
            for (FileData fd : fileArray){
                System.out.println(fd.getFullPath());
            }
            System.out.println("---");
        }
    }

    private void printDirs(List<String> dirs){
        for (String dir : dirs){
            System.out.println(dir);
        }
    }

    private void printNamesakes(FileStorageMap<String, String> namesakes){
        for (AbstractFileStorage<String> fs : namesakes){
            Boolean namePrinted = false;
            FileArray assorted = new FileArray();
            for (FileArray fa : fs){
                if (!namePrinted){
                    System.out.println(fa.get(0).getName());
                    namePrinted = true;
                }
                if (fa.size() > 1){
                    for (FileData fd : fa){
                        System.out.println("  "  +fd.getFullPath());
                    }
                    System.out.println("  --");
                }
                else {
                    assorted.add(fa.get(0));
                }
            }
            for (FileData fd : assorted){
                System.out.println("  "  +fd.getFullPath());
                System.out.println("  --");
            }
        }
    }

    private void printStringList(List<String> stringList) {
        for (String aStr : stringList){
            System.out.println(aStr);
        }
    }

}
