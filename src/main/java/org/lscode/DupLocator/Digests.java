/**
 *  file:    Digests.java
 *  desc:    class for storing digests (hashes) of a file
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Digests {
    private Map<String, String> digests = new LinkedHashMap<>();

    Digests() {
    }

    Digests(final String[] algorithms, final String initialValue) {
        for (String algo : algorithms) {
            this.put(algo, initialValue);
        }
    }

    public void put(String algo, String digestVal) {
        digests.put(algo, digestVal);
    }

    public String combined() {
        return digests.values().stream().collect(Collectors.joining());
    }
}
