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
 * INT:         Client Reliable Port
 *
 *
 * Server HandShake Reply
 * ----------------------------------------------------------------------------
 * INT:         Server Reliable Port
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
        RadiogramConnection rCon = null;
        Datagram dg = null;
        Connection connect = null;
        int numberTry = 0;

        while (numberTry < RMIProperties.NUMBER_OF_TRIES) {
            try {
                String uri = RMIProperties.UNRELIABLE_PROTOCOL + "://" + targetAddr + ":" + targetPort;
                rCon = (RadiogramConnection) Connector.open(uri, Connector.READ_WRITE, true);

                System.out.println("TOU AKI?");

                rCon.setTimeout(RMIProperties.TIMEOUT);
                dg = rCon.newDatagram(rCon.getMaximumLength());
                dg.reset();

                System.out.println("TOU AKI?v2");

                // Writting protocol data
                // We're going to reuse the port for unreliable and then reliable connection.
                int port = RemoteGarbageCollector.getFreePort();
                dg.write(operation);
                dg.writeInt(port);
                rCon.send(dg);

                System.out.println("TOU AKI?v3");

                // Closing the Connection
                dg.reset();
                rCon.close();

                // Waiting for unreliable Reply
                int clientUnreliablePort = 0;
                switch (operation) {
                    case ProtocolOpcode.HOST_ADDR_REQUEST:
                        clientUnreliablePort = RMIProperties.UNRELIABLE_DISCOVER_CLIENT_PORT;
                        break;
                    case ProtocolOpcode.INVOKE_REQUEST:
                        clientUnreliablePort = RMIProperties.UNRELIABLE_INVOKE_CLIENT_PORT;
                        break;
                    case ProtocolOpcode.REGISTRY_REQUEST:
                        clientUnreliablePort = RMIProperties.UNRELIABLE_REGISTRY_CLIENT_PORT;
                        break;
                    default:
                        clientUnreliablePort = RMIProperties.UNRELIABLE_REGISTRY_CLIENT_PORT;
                }

                System.out.println("TOU AKI?v4");

                // Waiting for answer
                uri = RMIProperties.UNRELIABLE_PROTOCOL + "://:" + clientUnreliablePort;
                rCon = (RadiogramConnection) Connector.open(uri);
                dg = (Radiogram) rCon.newDatagram(rCon.getMaximumLength());
                rCon.setTimeout(RMIProperties.TIMEOUT);

                // Reading protocol answer
                rCon.receive(dg);
                int serverReliablePort = dg.readInt();
                System.out.println("TOU AKI?v5");

                // Closing Unreliable connection
                rCon.close();

                // Opening and returning Reliable Connection
                uri = RMIProperties.RELIABLE_PROTOCOL + "://" + targetAddr + ":" + serverReliablePort;
                connect = Connector.open(uri);
                System.out.println("Making RELIABLE CLIENT CONNECTION WITH SERVER ON:");
                System.out.println(targetAddr + ":" + serverReliablePort);
                break;
            } catch (Exception e) {
                try {
                    numberTry++;
                    rCon.close();
                    dg.reset();
                    if (numberTry == RMIProperties.NUMBER_OF_TRIES) {
                        throw new IOException();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        System.out.println("NUMBER OF RETRY ON CLIENT CONNECTION: " + numberTry);
        return connect;
    }
}
