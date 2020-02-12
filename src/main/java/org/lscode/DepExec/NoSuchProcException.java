/*
 *  file:    NoSuchProcException.java
 *  desc:    exception for a missing process ID
 *  author:  lscode
 *  license: GNU General Public License v3.0
 */

package org.lscode.DepExec;

public class NoSuchProcException extends RuntimeException{
    public NoSuchProcException() { super(); }
    public NoSuchProcException(String message) { super(message); }
}
