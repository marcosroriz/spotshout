/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.spotshout.remote;

import com.google.code.spotshout.lang.SerialInt;

/**
 *
 * @author Marcos Roriz
 */
public class SampleStub extends Stub {

    public int add(int x, int y) {
        Object[] args = new Object[2];
        args[0] = new SerialInt(x);
        args[1] = new SerialInt(x);
        TargetMethod m = new TargetMethod("add", "(II)I", true, args);

        return 1;
    }
    public void run() {
        System.out.println("LOL");
    }
}
