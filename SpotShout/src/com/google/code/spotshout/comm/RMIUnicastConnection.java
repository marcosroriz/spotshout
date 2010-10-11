/*
 * SpotSHOUT - A RMI Middleware for the SunSPOT Platform.
 * Copyright (C) 2010 Marcos Paulino Roriz Junior
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.google.code.spotshout.comm;

import javax.microedition.io.Connection;

/**
 * This class abstract the details of creating a unicast connection between SPOTs
 * for the RMI protocol. We'll inject the RMIRequest and create the given RMIReply
 * reducint the coupling.
 */
public class RMIUnicastConnection {

    /**
     * The wrapped connection.
     */
    private Connection connection;
    /**
     * The protocol used (radiogram, radiostream, tcp or udp).
     */
    private String protocol;
    /**
     * The target address (MAC).
     */
    private String targetAddr;
    /**
     * The target port.
     */
    private int port;
    /**
     * The RMIRequest of this connection.
     */
    private RMIRequest request;
}
