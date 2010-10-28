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
    public static final int NUMBER_OF_TRIES = 5;

    /**
     * The port where the server will reply the discover address of a RMI server.
     */
    public static final int UNRELIABLE_DISCOVER_CLIENT_PORT = 241;

    /**
     * The port where a client can discover a RMI server.
     */
    public static final int UNRELIABLE_REGISTRY_CLIENT_PORT = 242;

    /**
     * The port where the server will reply the discover address of a RMI server.
     */
    public static final int UNRELIABLE_INVOKE_CLIENT_PORT = 243;

    /**
     * The port where a client can discover a RMI server.
     */
    public static final int UNRELIABLE_DISCOVER_HOST_PORT = 244;

    /**
     * RMI Server port
     */
    public static final int RMI_SERVER_PORT = 245;

    /**
     * Timeout in milliseconds before throwing a TimeoutException
     */
    public static final int TIMEOUT = 5000;

    /**
     * Timeout in milliseconds on a reliable connection
     */
    public static final int WAIT_LITTLE_TIME = 4000;

    /**
     * Reliable protocol
     */
    public static final String RELIABLE_PROTOCOL = "radiostream";

    /**
     * Unreliable protocol
     */
    public static final String UNRELIABLE_PROTOCOL = "radiogram";
}
