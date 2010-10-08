/*
 * SpotShout - A RMI library for the SunSPOT Platform.
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

package com.google.code.spotshout.remote;

import com.google.code.spotshout.comm.ProtocolOpcode;
import com.sun.spot.io.j2me.tcp.TCPConnection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Hashtable;
import javax.microedition.io.Connector;

/**
 * Implements the RMI Registry for the Spot side.
 */
public class SpotRegistry implements Registry {

    /**
     * Our address.
     */
    private String ourAddress;

    /**
     * Registry (Server) address.
     */
    private String srvAddress;

    /**
     * Registry (Server) Port.
     */
    private int srvPort;

    /**
     * Hashtable to locally binded remote objects.
     */
    private Hashtable localBindTable;

    /**
     * (non-javadoc)
     * @see java.rmi.registry.Registry#bind(java.lang.String, java.rmi.Remote)
     * @TODO rola de passar o nome completo (incluindo pacote) da ref remota?
     */
    public void bind(String name, String remoteFullName, Remote obj)
            throws AlreadyBoundException, NullPointerException, RemoteException {

        // Exceptions
        if (name == null) throw new NullPointerException("Bind name is null.");
        if (obj == null) throw new NullPointerException("Remote object is null.");
        
        try {
            TCPConnection con = (TCPConnection) Connector.open(
                    "tcp://" + srvAddress + ":" + srvPort);
            DataOutputStream conOut = con.openDataOutputStream();
            DataInputStream conIn = con.openDataInputStream();

            /*
             * Bind Request Protocol
             * ---------------------------------------------------------------
             * Byte:        Opcode
             * UTF:         Address
             * UTF:         Remote Interface Desired Name
             * UTF:         Remote Interface Full Qualified Name
             * @TODO Define the port that this guy will listen
             */
            conOut.write(ProtocolOpcode.BIND_REQUEST);
            conOut.writeUTF(ourAddress);
            conOut.writeUTF(name);
            conOut.writeUTF(remoteFullName);

            /*
             * Bind Reply Protocol
             * ---------------------------------------------------------------
             * Byte:        Opcode
             * Byte:        Status
             * (Opt) Byte:  Exception
             */
            byte opcode = conIn.readByte();
            byte status = conIn.readByte();

            if (opcode != ProtocolOpcode.BIND_REPLY) {
                throw new RemoteException(SpotRegistry.class, "Error binding at nameserver");
            }

            if (status == ProtocolOpcode.OPERATION_NOK) {
                byte exception = conIn.readByte();
                throw new AlreadyBoundException(SpotRegistry.class, "AlreadyBound on Bind");
            }

            // Binding Locally
            // TODO -- fazer cada skel em uma thread, ou uma thread toda pro registro.
            Class skelClass = Class.forName(remoteFullName + "_Skel");
            Skel skel = (Skel) skelClass.newInstance();
            skel.setRemote(obj);
            localBindTable.put(name, skel);
        } catch (InstantiationException ex) {
            ex.printStackTrace();
            throw new RemoteException(SpotRegistry.class, "Skeleton not found");
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new RemoteException(SpotRegistry.class, "Skeleton not found");
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RemoteException(SpotRegistry.class, "Error on bind()");
        }
    }

    /**
     * (non-javadoc)
     * @see java.rmi.registry.Registry#list() 
     */
    public String[] list() throws RemoteException {
        try {
            TCPConnection con = (TCPConnection) Connector.open(
                "tcp://" + srvAddress + ":" + srvPort);
            DataOutputStream conOut = con.openDataOutputStream();
            DataInputStream conIn = con.openDataInputStream();

            /*
             * List Request Protocol
             * ---------------------------------------------------------------
             * Byte:        Opcode
             * UTF:         Address
             */
            conOut.write(ProtocolOpcode.LIST_REQUEST);
            conOut.writeUTF(ourAddress);

            /*
             * List Reply Protocol
             * ---------------------------------------------------------------
             * Byte:        Opcode
             * Int:         List Size
             * String:      Elements name
             */
            byte opcode = conIn.readByte();
            int listSize = conIn.readInt();
            String list[] = new String[listSize];

            for (int i = 0; i < listSize; i++) {
                list[i] = conIn.readUTF();
            }

            return list;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RemoteException(SpotRegistry.class, "Error on list()");
        }
    }

    /**
     * (non-javadoc)
     * @see java.rmi.registry.Registry#lookup(java.lang.String) 
     */
    public Remote lookup(String name) throws NotBoundException,
            NullPointerException, RemoteException {

        // Exceptions
        if (name == null) throw new NullPointerException("Lookup name is null.");

        try {
            TCPConnection con = (TCPConnection) Connector.open(
                    "tcp://" + srvAddress + ":" + srvPort);
            DataOutputStream conOut = con.openDataOutputStream();
            DataInputStream conIn = con.openDataInputStream();

            /*
             * Lookup Request Protocol
             * ---------------------------------------------------------------
             * Byte:        Opcode
             * UTF:         Address
             * UTF:         Remote Interface Name
             */
            conOut.write(ProtocolOpcode.LOOKUP_REQUEST);
            conOut.writeUTF(ourAddress);
            conOut.writeUTF(name);

            /*
             * Lookup Reply Protocol
             * ---------------------------------------------------------------
             * Byte:        Opcode
             * Byte:        Status
             * (Opt) Byte:  Exception
             * String:      Remote Reference Address
             * Int:         Remote Reference Port
             * String:      Remote Full Qualified Name
             */
            byte opcode = conIn.readByte();
            byte status = conIn.readByte();

            if (opcode != ProtocolOpcode.LOOKUP_REPLY) {
                throw new RemoteException(SpotRegistry.class, "Error looking up at nameserver");
            }

            if (status == ProtocolOpcode.OPERATION_NOK) {
                byte exception = conIn.readByte();
                throw new NotBoundException(SpotRegistry.class, "NotBound on nameserver");
            }

            String addr = conIn.readUTF();
            int port = conIn.readInt();
            String remoteFullName = conIn.readUTF();

            Class stubClass = Class.forName(remoteFullName + "_Stub");
            Stub stub = (Stub) stubClass.newInstance();

            stub.setTargetAddr(addr);
            stub.setTargetPort(port);
            stub.setTargetName(name);

            return (Remote) stub;
        } catch (InstantiationException ex) {
            ex.printStackTrace();
            throw new RemoteException(SpotRegistry.class, "Error on Skeleton initialization");
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            throw new RemoteException(SpotRegistry.class, "Skeleton not found");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new RemoteException(SpotRegistry.class, "Skeleton not found");
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RemoteException(SpotRegistry.class, "Error on bind()");
        }
    }

    /**
     * (non-javadoc)
     * @see java.rmi.registry.Registry#rebind(java.lang.String, java.rmi.Remote) 
     */
    public void rebind(String name, String remoteFullName, Remote obj)
            throws NullPointerException, RemoteException {
    }

    /**
     * (non-javadoc)
     * @see java.rmi.registry.Registry#unbind(java.lang.String) 
     */
    public void unbind(String name) throws NotBoundException,
            NullPointerException, RemoteException {
    }

    /**
     * This method clean up a remote name returning the package that contains
     * this interface.
     * Ex: foo.bar.RemoteInterface
     * Returns: foo.bar
     * @param fullName - the name of the remote interface
     * @return the cleaned up name of the package that holds this interface.
     */
    private String getRemotePackage(String fullName) {
        int lastIdx = fullName.lastIndexOf('.');
        return fullName.substring(0, lastIdx);
    }

}
