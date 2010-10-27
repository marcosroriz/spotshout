/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.spotshout.remote;

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
            if (currentPort > 230) currentPort = 0;
            else if (isFree(currentPort)) {
                int temp = currentPort;
                currentPort++;
                return temp;
            } else {
                currentPort++;
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

    public static synchronized void registerPort(int port) {
        registedPorts[registerPortCounter++] = port;
    }
}
