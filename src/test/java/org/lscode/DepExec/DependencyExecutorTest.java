package org.lscode.DepExec;

import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DependencyExecutorTest {

    public enum Processes {
        PROC1, PROC2, PROC3, PROC4
    }

    private DependencyExecutor<Processes> executor = new DependencyExecutor<>();
    private String result = "";

    void mproc1(){result = result + "-proc1";}
    void mproc2(){result = result + "-proc2";}
    void mproc3(){result = result + "-proc3";}
    void mproc4(){result = result + "-proc4";}

    @org.junit.jupiter.api.Test
    void test1(){
        executor.addProcess(Processes.PROC1, this::mproc1, Processes.PROC2);
        executor.addProcess(Processes.PROC2, this::mproc2, Processes.PROC3);
        executor.addProcess(Processes.PROC3, this::mproc3, Processes.PROC4);
        executor.addLastProcess(Processes.PROC4, this::mproc4);

        executor.runProcessChain(Processes.PROC1);

        assertEquals("-proc4-proc3-proc2-proc1", result);
    }

    @org.junit.jupiter.api.Test
    void exceptionTest() {
        executor.addProcess(Processes.PROC1, this::mproc1, Processes.PROC2);

        Assertions.assertThrows(NoSuchProcException.class, ()->executor.getProcessChain(Processes.PROC1));
    }

}