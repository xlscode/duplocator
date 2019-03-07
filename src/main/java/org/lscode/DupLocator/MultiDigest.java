/**
 *  file:    MultiDigest.java
 *  desc:    class for calculating multiple digests (hashes) of a file at once
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DupLocator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class MultiDigest {
    private Map<String, MessageDigest> digestMachines = new HashMap<>();
    private ByteBuffer buffer;
    private String[] algorithms;
    private static final String zeroDigest = "zero";

    public MultiDigest(String[] algorithms, int bufferSize) throws java.security.NoSuchAlgorithmException, IllegalArgumentException{
        this.algorithms = algorithms;
        for (String algo: algorithms){
            digestMachines.put(algo, MessageDigest.getInstance(algo));
        }
        buffer = ByteBuffer.allocate(bufferSize);
    }

    public Digests calculate(String path) throws IOException {
        return calculate(new File(path));
    }

    public Digests calculate(FileData fdata) throws IOException {
        if (fdata.getSize() == 0){
            return new Digests(algorithms, zeroDigest);
        }
        return calculate(fdata.getFullPath());
    }

    public Digests calculate(File file) throws IOException {
        Digests digests = new Digests();
        try {
            FileInputStream inStream = new FileInputStream(file);
            FileChannel inChannel = inStream.getChannel();

            while (inChannel.read(buffer) > 0) {
                for (Map.Entry<String, MessageDigest> entry : digestMachines.entrySet()) {
                    buffer.flip();
                    entry.getValue().update(buffer);
                }
                buffer.clear();
            }

            for (Map.Entry<String, MessageDigest> entry : digestMachines.entrySet()) {
                digests.put(entry.getKey(), bytesToHex(entry.getValue().digest()));
                entry.getValue().reset();
            }

        }
        finally {
            for (Map.Entry<String, MessageDigest> entry : digestMachines.entrySet()) {
                entry.getValue().reset();
            }
            buffer.clear();

        }

        return digests;
    }

    private String bytesToHex(byte[] bytes){
        StringBuilder result = new StringBuilder();
        for (byte oneByte : bytes){
            String hex=Integer.toHexString(0xff & oneByte);
            if(hex.length()==1) result.append('0');
            result.append(hex);
        }
        return result.toString();
    }
}
