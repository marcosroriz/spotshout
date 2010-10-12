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

import com.sun.spot.io.j2me.radiostream.RadiostreamConnection;
import com.sun.spot.peripheral.TimeoutException;
import java.io.DataOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.microedition.io.Connector;

/**
 * This class abstract the details of creating a unicast connection between SPOTs
 * for the RMI protocol. We'll inject the RMIRequest and create the given RMIReply
 * reducint the coupling.
 */
public class RMIUnicastConnection {

    /**
     * Timeout in milliseconds before throwing a TimeoutException
     */
    private int TIMEOUT = 5000;

    /**
     * The wrapped connection.
     */
    private RadiostreamConnection connection;

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
    private int targetPort;

    /**
     * The RMI operation request of this connection.
     */
    private RMIRequest request;

    /**
     * The RMI operation reply of this connection.
     */
    private RMIReply reply;

    /**
     * Abstract a unicast connection between the Spot and the {@link Registry}.
     * @param targetAddr - the registry address (MAC).
     * @param targetPort - the registry port.
     * @throws RemoteException - on error
     */
    public RMIUnicastConnection(String addr, int port) throws RemoteException {
        protocol = "radiostream";
        targetAddr = addr;
        targetPort = port;
        String uri = protocol + "://" + targetAddr + ":" + targetPort;
        try {
            connection = (RadiostreamConnection) Connector.open(uri);
            connection.setTimeout(TIMEOUT);
        } catch (IOException ex) {
            throw new RemoteException(RMIUnicastConnection.class,
                    "Failed to comunicate with: " + uri);
        }
    }

    public void writeRequest(RMIRequest request) throws RemoteException, TimeoutException {
        try {
            this.request = request;
            DataOutputStream dos = connection.openDataOutputStream();
            request.writeData(dos);
            dos.flush();
        } catch (IOException ex) {
            throw new RemoteException("Remote Eception on Opening Data Output Stream");
        }
    }

    public RMIReply readReply() throws RemoteException {
        try {
            byte operation = request.getOperation();

            switch (operation) {
                case ProtocolOpcode.BIND_REQUEST:
                    break;
                case ProtocolOpcode.LIST_REQUEST:
                    break;
                case ProtocolOpcode.LOOKUP_REQUEST:
                    break;
                case ProtocolOpcode.REBIND_REQUEST:
                    break;
                case ProtocolOpcode.UNBIND_REQUEST:
                    break;
                default:
                    throw new RemoteException(RMIUnicastConnection.class,
                            "Unsupported Operation");
            }
            return null;
        } catch (IOException ex) {
            throw new RemoteException(RMIUnicastConnection.class,
                    "Unsupported Operation");
        }
    }
}
