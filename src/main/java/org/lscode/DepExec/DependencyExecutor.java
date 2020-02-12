/*
 *  file:    DependencyExecutor.java
 *  desc:    class for storing dependencies for Runnables
 *           and executing them in order
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DepExec;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class DependencyExecutor<T extends Enum<T>> {

    private Map<T, Optional<T>> deps = new HashMap<>();
    private Map<T, Runnable> steps = new HashMap<>();
    private Map<T, Boolean> done = new HashMap<>();

    public void addProcess(final T processID, final Runnable process, final T dependencyID){
        deps.put(processID, Optional.of(dependencyID));
        steps.put(processID, process);
        done.put(processID, false);
    }

    public void addLastProcess(final T processID, final Runnable process){
        deps.put(processID, Optional.empty());
        steps.put(processID, process);
        done.put(processID, false);
    }

    public List<T> getProcessChain(final T processID)throws NoSuchProcException {
        LinkedList<T> pChain = new LinkedList<>();
        boolean endOfChain = false;
        T currentProcID = processID;
        T previousProcID = processID;

        if (!deps.containsKey(currentProcID)){
            throw new NoSuchProcException("Process ID: " + currentProcID.toString() + " has not been registered.");
        }

        pChain.addFirst(processID);
        while (!endOfChain){
            if (deps.containsKey(currentProcID)){
                if (deps.get(currentProcID).isPresent()){
                    T nextProcID = deps.get(currentProcID).get();
                    pChain.addFirst(nextProcID);
                    previousProcID = currentProcID;
                    currentProcID = nextProcID;
                }
                else{
                    endOfChain = true;
                }
            }
            else{
                throw new NoSuchProcException("Process ID: " + currentProcID.toString() +
                        "on which process " + previousProcID.toString() + " depends, has not been registered.");
            }
        }
        return new ArrayList<>(pChain);
    }

    public void runProcessChain(final T processID){
        List<T> pChain = getProcessChain(processID);
        for (T aProcID : pChain) {
            if (!done.get(aProcID)) {
                steps.get(aProcID).run();
                done.put(aProcID, true);
            }
        }
    }
}
