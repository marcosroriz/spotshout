/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.spotshout;

/**
 *
 * @author Marcos Roriz
 */
public class RMIProperties {

    public static final boolean DEBUG = false;

    public static final void log(String msg) {
        if (DEBUG) System.out.println(msg);
    }

    /**
     * Number of tries (to connect).
     */
    public static final byte NUMBER_OF_TRIES = 5;

    /**
     * Host Discovery
     */
    public static final int UNRELIABLE_DISCOVER_CLIENT_PORT = 240;

    /**
     * The port where a client can discover a RMI server.
     */
    public static final int UNRELIABLE_DISCOVER_HOST_PORT = 243;

    /**
     * The port where the server will reply the invoke port of the connection.
     */
    public static final int UNRELIABLE_INVOKE_CLIENT_PORT = 242;

    /**
     * RMI Spot port
     */
    public static final int RMI_SPOT_PORT = 244;

    /**
     * RMI Server port
     */
    public static final int RMI_SERVER_PORT = 245;

    /**
     * Reliable timeout (radiostream).
     */
    public static final int RELIABLE_TIMEOUT = 10000;

    /**
     * Little sleep time.
     */
    public static final int LITTLE_SLEEP_TIME = 300;

    /**
     * Timeout in milliseconds before throwing a TimeoutException
     */
    public static final int TIMEOUT = 5000;

    /**
     * Reliable protocol
     */
    public static final String RELIABLE_PROTOCOL = "radiostream";

    /**
     * Unreliable protocol
     */
    public static final String UNRELIABLE_PROTOCOL = "radiogram";
}
