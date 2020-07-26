/*
 *  file:    LocDupsXX.java
 *  desc:    the "app" file - calls classes from the DupLocator package
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.LocDups;

import org.lscode.DupLocator.DupLocator;
import org.lscode.DupLocator.MultiDigest;

public class LocDups {

    private final static String[] HASH_LIST = {"MD5", "SHA-256"};
    private final static int BUFFER_SIZE = 1024 * 32;

    public static void main(String[] args){
        if (args.length == 0){
            System.err.println("I need a parameter (start directory), please.");
            System.exit(1);
        }

        MultiDigest digestGenerator = null;
        try {
            digestGenerator = new MultiDigest(HASH_LIST, BUFFER_SIZE);
        }
        catch (Exception e){
            System.err.println("Error occured while initializing the app.");
            System.err.println("Actually, this should never happen.");
            System.exit(1);
        }
        final DupLocator dupLoc = new DupLocator(digestGenerator, args);
        new LocDupsApp(dupLoc).run();
    }
}
