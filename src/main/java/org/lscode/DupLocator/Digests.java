/**
 *  file:    Digests.java
 *  desc:    class for storing digests (hashes) of a file
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.util.*;

public class Digests {
    private List<String> algos = new ArrayList<>();
    private Map<String, String> digests = new HashMap<>();

    Digests() {
    }

    Digests(String[] algorithms, String initialValue) {
        for (String algo : algorithms) {
            this.put(algo, initialValue);
        }
    }

    public void put(String algo, String digestVal) {
        algos.add(algo);
        digests.put(algo, digestVal);
    }

    public String combined() {
        Boolean first = true;
        StringBuilder allDigests = new StringBuilder();

        if (algos.size() == 0) return "";
        if (algos.size() == 1) return digests.get(algos.get(0));

        for (String algo : algos){
            if (!first) {
                allDigests.append("&");
            }
            else{
                first = false;
            }
            allDigests.append(digests.get(algo));
        }
        return allDigests.toString();
    }
}
