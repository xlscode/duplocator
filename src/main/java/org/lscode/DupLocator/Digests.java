/*
 *  file:    Digests.java
 *  desc:    class for storing digests (hashes) of a file
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;


import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Digests {
    private Map<String, String> digests = new LinkedHashMap<>();

    public Digests() {
    }

    public Digests(final String[] algorithms, final String initialValue) {
        for (String algo : algorithms) {
            this.put(algo, initialValue);
        }
    }

    public void put(String algo, String digestVal) {
        digests.put(algo, digestVal);
    }

    public String combined() {
        //return digests.values().stream().collect(Collectors.joining());
        String[] keys = digests.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        StringBuilder allValues = new StringBuilder();
        for (String key: keys){
            allValues.append(digests.get(key));
        }
        return allValues.toString();
    }

    @Override
    public String toString() {
        String lineSeparator = System.getProperty("line.separator");
        StringBuilder result = new StringBuilder();
        String[] keys = digests.keySet().toArray(new String[0]);
        Arrays.sort(keys);
        boolean firstLine = true;
        for (String key: keys){
            if (firstLine){firstLine = false;}
            else {result.append(lineSeparator);}
            result.append(key);
            result.append(": ");
            result.append(digests.get(key));
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        Digests other = (Digests) o;
        Map<String, String> otherDigests = other.getDigests();
        Set<String> thisKeySet = digests.keySet();
        Set<String> otherKeySet = otherDigests.keySet();
        boolean result = true;
        for (String key: thisKeySet){
            if ((otherKeySet.contains(key)) && (!otherDigests.get(key).equals(digests.get(key)))){
                result = false;
                break;
            }
        }
        return result;
    }

    protected Map<String, String> getDigests(){
        return digests;
    }

    @Override
    public int hashCode(){
        return combined().hashCode();
    }

}
