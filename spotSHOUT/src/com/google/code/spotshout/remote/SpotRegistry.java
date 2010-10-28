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

package com.google.code.spotshout.remote;

import com.google.code.spotshout.comm.BindReply;
import com.google.code.spotshout.comm.BindRequest;
import com.google.code.spotshout.comm.ListReply;
import com.google.code.spotshout.comm.ListRequest;
import com.google.code.spotshout.comm.LookupReply;
import com.google.code.spotshout.comm.LookupRequest;
import com.google.code.spotshout.comm.ProtocolOpcode;
import com.google.code.spotshout.comm.RMIUnicastConnection;
import java.io.IOException;
import spot.rmi.AlreadyBoundException;
import spot.rmi.NotBoundException;
import spot.rmi.Remote;
import spot.rmi.RemoteException;
import spot.rmi.registry.Registry;
import java.util.Hashtable;

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

    public SpotRegistry(String srvAddress, int srvPort) {
        ourAddress = System.getProperty("IEEE_ADDRESS");
        this.srvAddress = srvAddress;
        this.srvPort = srvPort;
    }

    /**
     * (non-javadoc)
     * @see java.rmi.registry.Registry#bind(java.lang.String, java.rmi.Remote)
     */
    public void bind(String name, String remoteFullName, Remote obj)
            throws AlreadyBoundException, NullPointerException, RemoteException {

        // Exceptions
        if (name == null) throw new NullPointerException("Bind name is null.");
        if (obj == null) throw new NullPointerException("Remote object is null.");

        try {
            RMIUnicastConnection conn = RMIUnicastConnection.
                    makeClientConnection(ProtocolOpcode.REGISTRY_REQUEST, srvAddress, srvPort);
            BindRequest request = new BindRequest(name, remoteFullName);
            conn.writeRequest(request);
            BindReply reply = (BindReply) conn.readReply();

            if (reply.exceptionHappened()) throw new AlreadyBoundException(SpotRegistry.class, "AlreadyBound on Bind");

            // Initiating Skel and it's Thread
            Class skelClass = Class.forName(remoteFullName + "_Skel");
            Skel skel = (Skel) skelClass.newInstance();
            skel.setRemote(obj);
            skel.setPort(request.getSkelPort());
            (new Thread(skel)).start();
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
        System.out.println("INITIATED LIST REQUEST");
        try {
            RMIUnicastConnection conn = RMIUnicastConnection.
                    makeClientConnection(ProtocolOpcode.REGISTRY_REQUEST, srvAddress, srvPort);
            ListRequest request = new ListRequest();
            conn.writeRequest(request);
                    System.out.println("ENDED LIST REQUEST");
        System.out.println("WAITING LIST REPLY");
        ListReply reply = (ListReply) conn.readReply();
        System.out.println("ENDED LIST REPLY");

            return reply.getNames();
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
            RMIUnicastConnection conn = RMIUnicastConnection.
                    makeClientConnection(ProtocolOpcode.REGISTRY_REQUEST, srvAddress, srvPort);
            LookupRequest request = new LookupRequest(name);
            conn.writeRequest(request);
            LookupReply reply = (LookupReply) conn.readReply();

            // Creating Stub
            System.out.println("Stub name : " + reply.getRemoteFullName() + "_Stub");
            Class stubClass = Class.forName(reply.getRemoteFullName() + "_Stub");
            Stub stub = (Stub) stubClass.newInstance();
            stub.setTargetAddr(reply.getRemoteAddr());
            stub.setTargetPort(reply.getRemotePort());

            return (Remote) stub;
        } catch (InstantiationException ex) {
            ex.printStackTrace();
            throw new RemoteException(SpotRegistry.class, "Error on Stub initialization");
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            throw new RemoteException(SpotRegistry.class, "Stub not found");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new RemoteException(SpotRegistry.class, "Stub not found");
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RemoteException(SpotRegistry.class, "Error on lookup()");
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
}
