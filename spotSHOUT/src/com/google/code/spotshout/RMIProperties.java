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

    /**
     * The port where a client can discover a RMI server.
     */
    public static final int DISCOVER_PORT = 90;

    /**
     * RMI Server port
     */
    public static final int SERVER_PORT = 100;

    /**
     * Timeout in milliseconds before throwing a TimeoutException
     */
    public static final int TIMEOUT = 10000;

    /**
     * Reliable protocol
     */
    public static final String RELIABLE_PROTOCOL = "radiostream";


    /**
     * Unreliable protocol
     */
    public static final String UNRELIABLE_PROTOCOL = "radiogram";
}
