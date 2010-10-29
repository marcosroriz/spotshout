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
     * Number of tries (to connect).
     */
    public static final byte NUMBER_OF_TRIES = 5;

    /**
     * Host Discovery
     */
    public static final byte UNRELIABLE_DISCOVER_CLIENT_PORT = (byte) 241;

    /**
     * The port where the server will reply the discover address of a RMI server.
     */
    public static final byte UNRELIABLE_INVOKE_SERVER_PORT = (byte) 242;

    /**
     * The port where a client can discover a RMI server.
     */
    public static final byte UNRELIABLE_DISCOVER_HOST_PORT = (byte) 243;

    /**
     * RMI Server port
     */
    public static final byte RMI_SERVER_PORT = (byte) 245;

    /**
     *
     */
    public static final int RELIABLE_TIMEOUT = 20000;

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
