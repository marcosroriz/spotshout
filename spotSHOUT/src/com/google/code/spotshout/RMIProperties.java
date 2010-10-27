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
     * The port where the server will reply the discover address of a RMI server.
     */
    public static final int UNRELIABLE_DISCOVER_CLIENT_PORT = 91;

    /**
     * The port where a client can discover a RMI server.
     */
    public static final int UNRELIABLE_REGISTRY_CLIENT_PORT = 92;

    /**
     * The port where the server will reply the discover address of a RMI server.
     */
    public static final int UNRELIABLE_INVOKE_CLIENT_PORT = 93;

    /**
     * The port where a client can discover a RMI server.
     */
    public static final int UNRELIABLE_DISCOVER_HOST_PORT = 90;

    /**
     * RMI Server port
     */
    public static final int RMI_SERVER_PORT = 100;

    /**
     * Timeout in milliseconds before throwing a TimeoutException
     */
    public static final int TIMEOUT = 30000;

    /**
     * Reliable protocol
     */
    public static final String RELIABLE_PROTOCOL = "radiostream";

    /**
     * Unreliable protocol
     */
    public static final String UNRELIABLE_PROTOCOL = "radiogram";
}
