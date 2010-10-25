/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.spotshout.generator;

/**
 *
 * @author Marcos Roriz
 */
public class Main {

    public static void main(String[] args) {
        StubGenerator sg = new StubGenerator();
        SkelGenerator sk = new SkelGenerator();
        sg.makeClass("SpotShout-0.0.1.jar", "com.google.code.spotshout.remote.", "SampleRemIF");
        sk.makeClass("SpotShout-0.0.1.jar", "com.google.code.spotshout.remote.", "SampleRemIF");
    }
}
