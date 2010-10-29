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
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.util.IEEEAddress;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;

/**
 * This class represents the server handshake to make a reliable connection
 * between two points on PAN. It uses datagram (non reliable) data to receive
 * connection request from a client to establish a reliable connection.
 *
 * This class abstract the structure of a server. It listen to connection on a
 * given {@link Thread} and dispatch the method. The class interested on serving
 * will inherit this class and implement the dispatch method of the request.
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
 * (Opt)String: Server Address
 * INT:         Connection Reliable Port
 */
public abstract class Server implements Runnable {

    /**
     * This tunnel represent a reliable connection which will be used to
     * treat requests. The classes that inherit Server will need to treat the
     * dispatch method.
     */
    public class Tunnel implements Runnable {

        private RMIUnicastConnection reliableCon;

        public Tunnel(String addr, int port) throws IOException {
            reliableCon = RMIUnicastConnection.makeServerConnection(addr, port);
        }

        public void run() {
            try {
                RMIRequest req = reliableCon.readRequest();
                RMIReply reply = service(req);
                if (reply != null) {
                    reliableCon.writeReply(reply);
                }
                reliableCon.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * The port which this Server will listen (non reliable), to establish
     * reliable connections.
     */
    protected byte port;

    /**
     * Our server address.
     */
    protected String srvAddr;

    /**
     * Is this a discover registry ??
     */
    protected boolean discover;
    
    public Server() {
        port = RMIProperties.RMI_SERVER_PORT;
        srvAddr = IEEEAddress.toDottedHex(RadioFactory.getRadioPolicyManager().getIEEEAddress());
    }

    /*
     * Process a receiving call/invoke RMI request.
     * @param request - the request meta-data and it's arguments.
     * @return - the RMI Reply object, if the request doesn't have a return, i.e.
     *           void, return null
     */
    public abstract RMIReply service(RMIRequest request);

    /**
     * Listen for unreliable connections and make a reliable one with the client.
     */
    public void run() {
        try {
            String uri = RMIProperties.UNRELIABLE_PROTOCOL + "://:" + port;
            RadiogramConnection rCon = (RadiogramConnection) Connector.open(uri);
            Datagram dg = (Datagram) rCon.newDatagram(rCon.getMaximumLength());
            Datagram dgReply = (Datagram) rCon.newDatagram(rCon.getMaximumLength());


            while (true) {
                dg.reset();
                try {
                    // Receive Unreliable Request
                    rCon.receive(dg);
                    byte operation = dg.readByte();

                    System.out.println("received request from " + dg.getAddress());
                    
                    if (discover) {
                        String tempuri = "radiogram://" + dg.getAddress() + ":" + RMIProperties.UNRELIABLE_DISCOVER_CLIENT_PORT;
                        RadiogramConnection tmp = (RadiogramConnection) Connector.open(tempuri);
                        Datagram tmpDg = tmp.newDatagram(tmp.getMaximumLength());

                        System.out.println("sending our addr to mothar fuckar " + tempuri);
                        // Send unreliable reply
                        tmpDg.writeByte(0);
                        tmp.send(tmpDg);
                        tmp.close();
                        System.out.println("closed this connection");
                    } else {
                        dgReply.reset();
                        dgReply.setAddress(dg.getAddress());
                        int connectionPort = RemoteGarbageCollector.getFreePort();
                        dgReply.writeInt(connectionPort);
                        rCon.send(dgReply);

                        // Initiate reliable connection on this point
                        Tunnel tunnel = new Tunnel(dg.getAddress(), connectionPort);
                        (new Thread(tunnel)).start();
                    }
                } catch (Exception ex) {
                    dg.reset();
                    rCon.close();
                    
                    rCon = (RadiogramConnection) Connector.open(uri);
                    dg = (Radiogram) rCon.newDatagram(rCon.getMaximumLength());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
