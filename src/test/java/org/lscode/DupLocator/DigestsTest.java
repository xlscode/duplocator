package org.lscode.DupLocator;

import static org.junit.jupiter.api.Assertions.*;

class DigestsTest {

    @org.junit.jupiter.api.Test
    void combined1() {

        Digests digests = new Digests();

        digests.put("algo1", "a");
        digests.put("algo2", "b");
        digests.put("algo3", "c");
        digests.put("algo4", "d");
        digests.put("algo5", "e");

        assertEquals(digests.combined(), "abcde");
    }


    @org.junit.jupiter.api.Test
    void combined2() {

        String[] algos = {"algo1", "algo2", "algo3"};

        Digests digests = new Digests(algos, "zero");

        assertEquals(digests.combined(), "zerozerozero");


    }

}