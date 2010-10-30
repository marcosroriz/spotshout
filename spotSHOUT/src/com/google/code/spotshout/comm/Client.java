/*
 * spotSHOUT - A RMI Middleware for the SunSPOT Platform.
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

import com.google.code.spotshout.RMIProperties;
import com.google.code.spotshout.remote.RemoteGarbageCollector;
import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;

/**
 * This class represents the client handshake to make a reliable connection
 * between two points on PAN. It uses datagram (non reliable) data to announce
 * to the other point the data necessary to establish the connection.
 *
 * Protocol for HandShake:
 *
 * Client HandShake Request
 * ----------------------------------------------------------------------------
 * Byte:        Opcode
 *
 *
 * Server HandShake Reply
 * ----------------------------------------------------------------------------
 * INT:         Connection Reliable Port
 */
public class Client {

    /**
     * Connect two devices in PAN. First it sends a unreliable package to the
     * other point of the connection sending the address and port which will
     * be created the reliable connection. The other point will then send the
     *
     * @param operation - the given announce operation
     * @param targetAddr - the target address (MAC)
     * @param targetPort - the target announce port
     * @return a reliable {@link Connection}.
     * @throws IOException - if there is an error during connection such as
     *                       timeout or data corruption.
     */
    public static Connection connect(byte operation, String targetAddr, int targetPort)
            throws IOException {
        RadiogramConnection unrCon = null;
        Datagram dg = null;
        Connection connect = null;
        int numberTry = 0;

        while (numberTry < RMIProperties.NUMBER_OF_TRIES) {
            try {
                String uri = RMIProperties.UNRELIABLE_PROTOCOL + "://" + targetAddr + ":" + targetPort;
                unrCon = (RadiogramConnection) Connector.open(uri, Connector.READ_WRITE, true);

                unrCon.setTimeout(RMIProperties.TIMEOUT);
                dg = unrCon.newDatagram(unrCon.getMaximumLength());
                dg.reset();

                // Writting Request
                dg.write(operation);
                unrCon.send(dg);

                // Receiving Reply
                int reliablePort = 0;
                
                if (operation == ProtocolOpcode.INVOKE_REQUEST) {
                    reliablePort = invokeConnection(targetAddr);
                } else {
                    unrCon.receive(dg);
                    reliablePort = dg.readInt();
                }

                // Closing the Connection
                dg.reset();
                unrCon.close();
                
                // Opening and Returning Reliable Connection
                RemoteGarbageCollector.registerPort(reliablePort);
                uri = RMIProperties.RELIABLE_PROTOCOL + "://" + targetAddr + ":" + reliablePort;
                connect = Connector.open(uri);
                break;
            } catch (IOException ex) {
                numberTry++;
                unrCon.close();
                if (numberTry == RMIProperties.NUMBER_OF_TRIES) throw new IOException();
            }
        }
        return connect;
    }

    private static int invokeConnection(String hostAddr) throws IOException {
        int port = 0;
        RadiogramConnection invokeCon = null;
        Datagram datagram = null;
        try {
            // Waiting for answer
            String uri = RMIProperties.UNRELIABLE_PROTOCOL + "://" + hostAddr + ":" + RMIProperties.UNRELIABLE_INVOKE_CLIENT_PORT;
            invokeCon = (RadiogramConnection) Connector.open(uri);
            datagram = (Radiogram) invokeCon.newDatagram(invokeCon.getMaximumLength());

            invokeCon.setTimeout(RMIProperties.TIMEOUT);
            invokeCon.receive(datagram);
            port = datagram.readInt();

            invokeCon.close();
        } catch (IOException ex) {
            invokeCon.close();
        }
        return port;
    }
}
