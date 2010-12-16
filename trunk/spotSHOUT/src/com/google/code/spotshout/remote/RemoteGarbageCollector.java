/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.spotshout.remote;

import java.util.Random;

/**
 *
 * @author Marcos Roriz
 */
public class RemoteGarbageCollector {

    public static int registedPorts[] = new int[10];
    public static int registerPortCounter = 0;
    public static int currentPort = 40;

    /**
     * This method return a free port for listening.
     * @TODO metodo de alocar portas melhores
     */
    public static synchronized int getFreePort() {
        while (true) {
            Random rand = new Random();
            currentPort = 35 + rand.nextInt(200);
            if ((currentPort < 230) && (isFree(currentPort))) {
                registerPort(currentPort);
                return currentPort;
            }
        }
    }

    private static synchronized boolean isFree(int port) {
        if (port < 30) return false;
        if (port > 240) return false;

        for (int i = 0; i < registedPorts.length; i++) {
            if (port == registedPorts[i]) return false;
        }

        return true;
    }

    private static synchronized void registerPort(int port) {
        if (registerPortCounter >= 10) registerPortCounter = 0;
        registedPorts[registerPortCounter++] = port;
    }
}
