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
import com.sun.spot.io.j2me.radiostream.RadiostreamConnection;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import spot.rmi.RemoteException;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;

/**
 * This class abstract the details of creating a unicast connection between SPOTs
 * for the RMI protocol. We'll inject the RMIRequest and create the given RMIReply
 * reducint the coupling.
 */
public class RMIUnicastConnection {

    /**
     * The wrapped connection.
     */
    private RadiostreamConnection connection;

    /**
     * Input Stream
     */
    private DataInputStream dis;

    /**
     * Output Stream
     */
    private DataOutputStream dos;

    /**
     * The RMI operation request of this connection.
     */
    private RMIRequest request;

    /**
     * The RMI operation reply of this connection.
     */
    private RMIReply reply;

    private RMIUnicastConnection(Connection conn) throws IOException {
        try {
            connection = (RadiostreamConnection) conn;
            dis = connection.openDataInputStream();
            dos = connection.openDataOutputStream();
            connection.setTimeout(RMIProperties.RELIABLE_TIMEOUT);
            Thread.sleep(300);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Abstract a unicast connection between two points (Spot-Spot) or (Spot-Registry).
     * @param op - the operation.
     * @param addr - the server address (MAC).
     * @param port - the server port.
     * @throws IOException - on a given remote error (timeout) or data corruption.
     */
    public static RMIUnicastConnection makeClientConnection(byte op, String addr,
            int port) throws IOException {
        RadiostreamConnection conn = (RadiostreamConnection)
                Client.connect(op, addr, port);
        return new RMIUnicastConnection(conn);
    }

    /**
     * Abstract a unicast connection between two points (Spot-Spot) or (Spot-Registry).
     * @param addr - the client address (MAC).
     * @param port - the client port.
     * @throws IOException - on a given remote error (timeout) or data corruption.
     */
    public static RMIUnicastConnection makeServerConnection(String addr, int port)
            throws IOException {
        String uri = RMIProperties.RELIABLE_PROTOCOL + "://" + addr + ":" + port;
        RadiostreamConnection conn = (RadiostreamConnection) Connector.open(uri);
        return new RMIUnicastConnection(conn);
    }

    /**
     * Close this connection.
     * @throws IOException - on a given remote error (timeout) or data corruption.
     */
    public void close() throws IOException {
        connection.close();
    }

    /**
     * Reads a RMI Request on this connection.
     * @return a specific RMI Request.
     * @throws IOException - on a given remote error (timeout) or data corruption.
     */
    public RMIRequest readRequest() throws IOException {
        RMIProperties.log("Reading Request Started");

        byte operation = readOpcode(dis);
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

        request.setOperation(operation);
        request.readData(dis);

        RMIProperties.log("Reading Request Finished");
        return request;
    }

    /**
     * Read a RMI Reply on this connection.
     * @return a specific RMI Reply.
     * @throws IOException  - on a given remote error (timeout) or data corruption.
     */
    public RMIReply readReply() throws IOException {
        RMIProperties.log("Reading Reply Started");
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

        reply.readData(dis);
        RMIProperties.log("Reading Reply Finished");
        return reply;
    }

    /**
     * Write a RMI Request on this connection.
     * @param request - the request to be written.
     * @throws IOException - on a given remote error (timeout) or data corruption.
     */
    public void writeRequest(RMIRequest request) throws IOException {
        RMIProperties.log("Writting Request Started");
        this.request = request;
        request.writeData(dos);
        dos.flush();
        RMIProperties.log("Writting Request Finished");
    }

    /**
     * Write a RMI Reply on this connection.
     * @param request - the request to be written.
     * @throws IOException - on a given remote error (timeout) or data corruption.
     */
    public void writeReply(RMIReply reply) throws IOException {
        RMIProperties.log("Writting Reply Started");
        this.reply = reply;
        reply.writeData(dos);
        dos.flush();
        RMIProperties.log("Writting Reply Finished");
    }

    /**
     * This method will read the opcode of the input so that we can manually
     * instantiate the correct operation and inject it's data by calling readData
     * on it.
     * @param input - the inputStream that the request data should be read.
     * @throws IOException - in case of a failure in communication or if the
     *                       data comes corrupted.
     */
    private byte readOpcode(DataInput input) throws IOException {
        RMIProperties.log("Reading Opcode Started");
        byte opcode = input.readByte();
        RMIProperties.log("Reading Opcode Finished");
        return opcode;
    }
}
