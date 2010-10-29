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
 * This class represents the RMI Server. It uses datagram (non reliable) data
 * to receive connection request from a client to establish a reliable connection.
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
     * The ourPort which this Server will listen (non reliable), to establish
     * reliable connections.
     */
    protected int ourPort;

    /**
     * Our server address.
     */
    protected String ourAddr;

    public Server(int listeningPort) {
        ourPort = listeningPort;
        ourAddr = IEEEAddress.toDottedHex(RadioFactory.getRadioPolicyManager().getIEEEAddress());
        RMIProperties.log("Started this RMI Server -- I'm: " + ourAddr + ":" + ourPort);
    }

    /*
     * Process a receiving call/invoke RMI request.
     * @param request - the request meta-data and it's arguments.
     * @return - the RMI Reply object, if the request doesn't have a return, i.e.
     *           void, return null
     */
    public abstract RMIReply service(RMIRequest request);

    /**
     * Send the address of this server to a client
     * @param clientAddr - the client addr
     * @throws IOException - if there is a error on opening this connection
     */
    private void sendAddrReply(String clientAddr) throws IOException {
        try {
            String tempuri = "radiogram://" + clientAddr + ":" + RMIProperties.UNRELIABLE_DISCOVER_CLIENT_PORT;
            RadiogramConnection tmp = (RadiogramConnection) Connector.open(tempuri);
            Datagram tmpDg = tmp.newDatagram(20);
            
            Thread.sleep(RMIProperties.LITTLE_SLEEP_TIME);
            tmpDg.writeByte(ProtocolOpcode.HOST_ADDR_REPLY);
            tmp.send(tmpDg);
            tmp.close();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Listen forever for unreliable connections and process the requests.
     */
    public void run() {
        try {
            String uri = RMIProperties.UNRELIABLE_PROTOCOL + "://:" + ourPort;
            RadiogramConnection rCon = (RadiogramConnection) Connector.open(uri);
            Datagram dg = (Datagram) rCon.newDatagram(rCon.getMaximumLength());
            Datagram dgReply = (Datagram) rCon.newDatagram(rCon.getMaximumLength());

            while (true) {
                dg.reset();
                try {
                    // Receive Unreliable Request
                    rCon.receive(dg);
                    byte operation = dg.readByte();

                    if (operation == ProtocolOpcode.HOST_ADDR_REQUEST) {
                        sendAddrReply(dg.getAddress());
                    } else {
                        dgReply.reset();
                        dgReply.setAddress(dg.getAddress());

                        Thread.sleep(RMIProperties.LITTLE_SLEEP_TIME);
                        int connectionPort = RemoteGarbageCollector.getFreePort();
                        dgReply.writeInt(connectionPort);
                        rCon.send(dgReply);

                        // Initiate reliable connection on this point
                        Tunnel tunnel = new Tunnel(dg.getAddress(), connectionPort);
                        (new Thread(tunnel)).start();
                    }
                } catch (Exception ex) {
                    // TimeoutException
                    dg.reset();
                    dgReply.reset();
                    rCon.close();

                    rCon = (RadiogramConnection) Connector.open(uri);
                    dg = (Radiogram) rCon.newDatagram(rCon.getMaximumLength());
                    dgReply = (Datagram) rCon.newDatagram(rCon.getMaximumLength());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Fatal error!");
        }
    }

    /**
     * This tunnel represent a reliable connection which will be used to
     * treat requests. The classes that inherit Server will need to treat the
     * dispatch method.
     */
    public class Tunnel implements Runnable {

        private String tunnelAddress;
        private int tunnelPort;
        private RMIUnicastConnection reliableCon;

        public Tunnel(String addr, int port) throws IOException {
            tunnelAddress = addr;
            tunnelPort = port;
            reliableCon = RMIUnicastConnection.makeServerConnection(addr, port);
        }

        public void run() {
            try {
                RMIProperties.log("Initiated Tunnel with: " + tunnelAddress + ":" + tunnelPort);
                RMIProperties.log("This is " + ourAddr + ":" + ourPort);

                RMIRequest req = reliableCon.readRequest();
                Thread.sleep(RMIProperties.LITTLE_SLEEP_TIME);
                RMIReply reply = service(req);
                if (reply != null)
                    reliableCon.writeReply(reply);

                reliableCon.close();
                RMIProperties.log("Finished Tunnel with: " + tunnelAddress + ":" + tunnelPort);
            } catch (Exception ex) {
                // TimeoutException
            }
        }
    }
}
