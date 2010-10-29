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
import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.TimeoutException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import spot.rmi.RemoteException;

/**
 * This class abstract the details of creating a unicast connection between SPOTs
 * for the RMI protocol. We'll inject the RMIRequest and create the given RMIReply
 * reducint the coupling.
 */
public class RMIUnicastConnection {

    /**
     * The wrapped client connection.
     */
    private RadiogramConnection clientConnection;

    /**
     * The wrapped server connection.
     */
    private RadiogramConnection serverConnection;

    /**
     * The RMI operation request of this connection.
     */
    private RMIRequest request;

    /**
     * The RMI operation reply of this connection.
     */
    private RMIReply reply;

    /**
     * Number of timeout exceptions.
     */
    private int numException;

    private RMIUnicastConnection(Connection client, Connection server) {
        clientConnection = (RadiogramConnection) client;
        clientConnection.setTimeout(RMIProperties.TIMEOUT);
        serverConnection = (RadiogramConnection) server;
        serverConnection.setTimeout(RMIProperties.TIMEOUT * 2);
        numException = 0;
    }

    /**
     * Abstract a unicast connection between two points (Spot-Spot) or (Spot-Registry).
     * @param op - the operation.
     * @param addr - the server address (MAC).
     * @param port - the server port.
     * @throws IOException - on a given remote error (timeout) or data corruption.
     */
    public static RMIUnicastConnection makeClientConnection(byte op, String addr, int port) throws IOException {
        RMIProperties.log("Started Client Connection with: " + addr + ":" + port);
        Client client = Client.connect(op, addr, RMIProperties.RMI_SERVER_PORT);
        
        String uri = RMIProperties.UNRELIABLE_PROTOCOL + "://" + addr + ":" + client.getServerPort();
        RadiogramConnection conn = (RadiogramConnection) Connector.open(uri);

        uri = RMIProperties.UNRELIABLE_PROTOCOL + "://:" + client.getClientPort();
        RadiogramConnection srv = (RadiogramConnection) Connector.open(uri);
        return new RMIUnicastConnection(conn, srv);
    }

    /**
     * Abstract a unicast connection between two points (Spot-Spot) or (Spot-Registry).
     * @param addr - the client address (MAC).
     * @param clientPort - the client port.
     * @throws IOException - on a given remote error (timeout) or data corruption.
     */
    public static RMIUnicastConnection makeServerConnection(String addr, int clientPort, int serverPort)
            throws IOException {
        RMIProperties.log("Started Client Connection with: " + addr + ":" + clientPort);
        String uri = RMIProperties.UNRELIABLE_PROTOCOL + "://" + addr + ":" + clientPort;
        RadiogramConnection conn = (RadiogramConnection) Connector.open(uri);

        uri = RMIProperties.UNRELIABLE_PROTOCOL + "://:" + serverPort;
        RadiogramConnection srv = (RadiogramConnection) Connector.open(uri);
        return new RMIUnicastConnection(conn, srv);
    }

    /**
     * Close this connection.
     * @throws IOException - on a given remote error (timeout) or data corruption.
     */
    public void close() throws IOException {
        clientConnection.close();
        serverConnection.close();
    }

    public RMIRequest readRequest() throws IOException {
        RMIProperties.log("Started Reading Request");
        Datagram clientDg = clientConnection.newDatagram(clientConnection.getMaximumLength());
        Datagram serverDg = serverConnection.newDatagram(serverConnection.getMaximumLength());

        clientDg.reset();
        serverDg.reset();

        int numberTry = 0;
        while (numberTry < RMIProperties.NUMBER_OF_TRIES) {
            try {
                serverConnection.receive(serverDg);
                readRequest((Radiogram) serverDg);
                break;
            } catch (TimeoutException e) {
                clientDg.reset();
                serverDg.reset();
                numException++;
                if (numberTry >= RMIProperties.NUMBER_OF_TRIES) throw new IOException();
            }
        }
        RMIProperties.log("Finished Reading Request");
        return request;
    }

    /**
     * Reads a RMI Request on this connection.
     * @return a specific RMI Request.
     * @throws IOException - on a given remote error (timeout) or data corruption.
     */
    private RMIRequest readRequest(Radiogram serverDg) throws IOException {
        byte operation = serverDg.readByte();
        
        switch (operation) {
            case ProtocolOpcode.BIND_REQUEST:
                request = new BindRequest();
                break;
            case ProtocolOpcode.INVOKE_REQUEST:
                request = new InvokeRequest();
                break;
            case ProtocolOpcode.LIST_REQUEST:
                request = new ListRequest();
                break;
            case ProtocolOpcode.LOOKUP_REQUEST:
                request = new LookupRequest();
                break;
            case ProtocolOpcode.REBIND_REQUEST:
                request = new RebindRequest();
                break;
            case ProtocolOpcode.UNBIND_REQUEST:
                request = new UnbindRequest();
                break;
            default:
                throw new RemoteException(RMIUnicastConnection.class,
                        "Unsupported operation: " + operation);
        }

        // Reading data
        int size = serverDg.readInt();
        byte[] buff = new byte[size];
        serverDg.readFully(buff);
        ByteArrayInputStream bais = new ByteArrayInputStream(buff);
        DataInputStream dis = new DataInputStream(bais);
        request.readData(dis);

        return request;
    }


    /**
     * Read a RMI Reply on this connection.
     * @return a specific RMI Reply.
     * @throws IOException  - on a given remote error (timeout) or data corruption.
     */
    public RMIReply readReply() throws IOException {
        boolean acknowledge = false;
        RMIProperties.log("Started Reading Reply");
        Datagram clientDg = clientConnection.newDatagram(clientConnection.getMaximumLength());
        Datagram serverDg = serverConnection.newDatagram(serverConnection.getMaximumLength());
        
        clientDg.reset();
        serverDg.reset();

        byte operation = request.getOperation();
        switch (operation) {
            case ProtocolOpcode.BIND_REQUEST:
                reply = new BindReply();
                break;
            case ProtocolOpcode.INVOKE_REQUEST:
                reply = new InvokeReply();
                break;
            case ProtocolOpcode.LIST_REQUEST:
                reply = new ListReply();
                break;
            case ProtocolOpcode.LOOKUP_REQUEST:
                reply = new LookupReply();
                break;
            case ProtocolOpcode.REBIND_REQUEST:
                reply = new RebindReply();
                break;
            case ProtocolOpcode.UNBIND_REQUEST:
                reply = new UnbindReply();
                break;
            default:
                throw new RemoteException(RMIUnicastConnection.class,
                        "Unsupported Operation: " + operation);
        }

        int numberTry = 0;
        while (numberTry < RMIProperties.NUMBER_OF_TRIES) {
            try {
                serverConnection.receive(serverDg);
                
                // Reading data
                int size = serverDg.readInt();
                byte[] buff = new byte[size];
                serverDg.readFully(buff);
                ByteArrayInputStream bais = new ByteArrayInputStream(buff);
                DataInputStream dis = new DataInputStream(bais);
                reply.readData(dis);

                // Sending ack
                clientDg.write(ProtocolOpcode.ACK);
                clientConnection.send(clientDg);
                clientConnection.send(clientDg);
                clientConnection.send(clientDg);
                clientConnection.send(clientDg);
                clientConnection.send(clientDg);
                clientConnection.send(clientDg);
                clientConnection.send(clientDg);
                clientConnection.send(clientDg);
                clientConnection.send(clientDg);
                clientConnection.send(clientDg);
                clientConnection.send(clientDg);
                clientConnection.send(clientDg);

                break;
            } catch (TimeoutException e) {
                clientDg.reset();
                serverDg.reset();
                numException++;
                if (numberTry >= RMIProperties.NUMBER_OF_TRIES) throw new IOException();
            }
        }
        RMIProperties.log("Finished Reading Reply");
        return reply;
    }

    /**
     * Write a RMI Request on this connection.
     * @param request - the request to be written.
     * @throws IOException - on a given remote error (timeout) or data corruption.
     */
    public void writeRequest(RMIRequest request) throws IOException {
        RMIProperties.log("Started Writting Request");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream daos = new DataOutputStream(baos);

        Datagram clientDg = clientConnection.newDatagram(clientConnection.getMaximumLength());
        Datagram serverDg = serverConnection.newDatagram(serverConnection.getMaximumLength());

        clientDg.reset();
        serverDg.reset();

        this.request = request;
        request.writeData(daos);

        int numberTry = 0;
        while (numberTry < RMIProperties.NUMBER_OF_TRIES) {
            try {
                clientDg.writeByte(request.getOperation());
                clientDg.writeInt(baos.size());
                clientDg.write(baos.toByteArray());

                clientConnection.send(clientDg);
                serverConnection.receive(serverDg);
                if (serverDg.readByte() == ProtocolOpcode.ACK) break;
            } catch (TimeoutException ex) {
                clientDg.reset();
                serverDg.reset();
                numException++;
                if (numberTry >= RMIProperties.NUMBER_OF_TRIES) throw new IOException();
            }
        }
        RMIProperties.log("Finished Writting Request");
    }

    /**
     * Write a RMI Reply on this connection.
     * @param request - the request to be written.
     * @throws IOException - on a given remote error (timeout) or data corruption.
     */
    public void writeReply(RMIReply reply) throws IOException {
        RMIProperties.log("Started Writting Reply");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream daos = new DataOutputStream(baos);
        Datagram clientDg = clientConnection.newDatagram(clientConnection.getMaximumLength());
        Datagram serverDg = serverConnection.newDatagram(serverConnection.getMaximumLength());

        clientDg.reset();
        serverDg.reset();

        this.reply = reply;
        reply.writeData(daos);

        int numberTry = 0;
        while (numberTry < RMIProperties.NUMBER_OF_TRIES) {
            try {
                clientDg.writeByte(reply.getOperation());
                clientDg.writeInt(baos.size());
                clientDg.write(baos.toByteArray());

                clientConnection.send(clientDg);
                serverConnection.receive(serverDg);
                if (serverDg.readByte() == ProtocolOpcode.ACK) break;
            } catch (TimeoutException ex) {
                clientDg.reset();
                serverDg.reset();
                numException++;
                if (numberTry >= RMIProperties.NUMBER_OF_TRIES) throw new IOException();
            }
        }
        RMIProperties.log("Finished Writting Reply");
    }
}
