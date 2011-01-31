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

import com.google.code.spotshout.RMIProperties;
import com.google.code.spotshout.comm.BindReply;
import com.google.code.spotshout.comm.BindRequest;
import com.google.code.spotshout.comm.InvokeRequest;
import com.google.code.spotshout.comm.ListReply;
import com.google.code.spotshout.comm.ListRequest;
import com.google.code.spotshout.comm.LookupReply;
import com.google.code.spotshout.comm.LookupRequest;
import com.google.code.spotshout.comm.ProtocolOpcode;
import com.google.code.spotshout.comm.RMIOperation;
import com.google.code.spotshout.comm.RMIReply;
import com.google.code.spotshout.comm.RMIRequest;
import com.google.code.spotshout.comm.RMIUnicastConnection;
import com.google.code.spotshout.comm.RebindReply;
import com.google.code.spotshout.comm.RebindRequest;
import com.google.code.spotshout.comm.Server;
import com.google.code.spotshout.comm.UnbindReply;
import com.google.code.spotshout.comm.UnbindRequest;
import java.util.Hashtable;
import spot.rmi.AlreadyBoundException;
import spot.rmi.NotBoundException;
import spot.rmi.Remote;
import spot.rmi.RemoteException;
import spot.rmi.registry.Registry;
import spot.rmi.registry.RegistryListener;

/**
 * Implements the RMI Registry for the Spot side.
 */
public class SpotRegistry extends Server implements Registry {
    /**
     * Invoke table
     *
     * Structure:
     * | Interface Name | Skel |
     */
    private Hashtable invokeTable;
    
    /**
     * RMI Registry (Server) address.
     */
    private String srvAddress;

    /**
     * RMI Registry (Server) Port.
     */
    private int srvPort;

    public SpotRegistry(String serverAddress, int serverPort) {
        super(RMIProperties.RMI_SPOT_PORT);
        srvAddress = serverAddress;
        srvPort = serverPort;
        invokeTable = new Hashtable();
    }

    /**
     * (non-javadoc)
     * @see com.google.code.spotshout.comm.Server#service(com.google.code.spotshout.comm.RMIRequest) 
     * 
     */
    public RMIReply service(RMIRequest request) {
        switch (request.getOperation()) {
            case ProtocolOpcode.INVOKE_REQUEST:
                return invoke((InvokeRequest) request);
        }
        return null;
    }
    
    /**
     * (non-javadoc)
     * @see java.rmi.registry.Registry#bind(java.lang.String, java.rmi.Remote)
     */
    public void bind(String name, String remoteFullName, Remote obj)
            throws AlreadyBoundException, NullPointerException, RemoteException {
        RMIProperties.log("Bind Request Started -- Remote Name: " + name);

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

            // Initiating Skel and saving it
            Class skelClass = Class.forName(remoteFullName + "_Skel");
            Skel skel = (Skel) skelClass.newInstance();
            skel.setRemote(obj);

            invokeTable.put(request.getRemoteInterfaceName(), skel);
            RMIProperties.log("Bind Request Finished -- Remote Name: " + name);
        } catch (Exception ex) {
            throw new RemoteException(SpotRegistry.class, "Error on bind(" +  name + ")");
        }
    }
    
    private RMIReply invoke(InvokeRequest request) {
        RMIProperties.log("Invoke Request Started -- Remote Name: " + request.getRemoteName());

        Skel skel = (Skel) invokeTable.get(request.getRemoteName());
        RMIReply reply = skel.service(request);

        RMIProperties.log("Invoke Request Finished -- Remote Name: " + request.getRemoteName());
        return reply;
    }
    
    /**
     * (non-javadoc)
     * @see java.rmi.registry.Registry#list() 
     */
    public String[] list() throws RemoteException {
        try {
            RMIProperties.log("List Request Started");

            RMIUnicastConnection conn = RMIUnicastConnection.
                    makeClientConnection(ProtocolOpcode.REGISTRY_REQUEST, srvAddress, srvPort);
            ListRequest request = new ListRequest();
            conn.writeRequest(request);
            ListReply reply = (ListReply) conn.readReply();

            RMIProperties.log("List Request Finished");
            return reply.getNames();
        } catch (Exception ex) {
            throw new RemoteException(SpotRegistry.class, "Error on list()");
        }
    }

    /**
     * (non-javadoc)
     * @see java.rmi.registry.Registry#lookup(java.lang.String) 
     */
    public Remote lookup(String name) throws NotBoundException,
            NullPointerException, RemoteException {
        RMIProperties.log("Lookup Request Started -- Remote Name: " + name);

        // Exceptions
        if (name == null) throw new NullPointerException("Lookup name is null.");

        try {
            RMIUnicastConnection conn = RMIUnicastConnection.
                    makeClientConnection(ProtocolOpcode.REGISTRY_REQUEST, srvAddress, srvPort);
            LookupRequest request = new LookupRequest(name);
            conn.writeRequest(request);
            LookupReply reply = (LookupReply) conn.readReply();

            // Creating Stub
            Stub stub = reply.createStub();
            stub.setLookupName(name);
            
            RMIProperties.log("Lookup Request Finished -- Remote Name:" + name);
            return (Remote) stub;
        } catch (Exception ex) {
            throw new RemoteException(SpotRegistry.class, "Error on lookup(" + name + ")");
        }
    }

    /**
     * (non-javadoc)
     * @see java.rmi.registry.Registry#rebind(java.lang.String, java.rmi.Remote) 
     */
    public void rebind(String name, String remoteFullName, Remote obj)
            throws NullPointerException, RemoteException {
        RMIProperties.log("Rebind Request Started -- Remote Name: " + name);

        // Exceptions
        if (name == null) throw new NullPointerException("Rebind name is null.");
        if (obj == null) throw new NullPointerException("Remote object is null.");

        try {
            RMIUnicastConnection conn = RMIUnicastConnection.
                    makeClientConnection(ProtocolOpcode.REGISTRY_REQUEST, srvAddress, srvPort);
            RebindRequest request = new RebindRequest(name, remoteFullName);
            conn.writeRequest(request);
            RebindReply reply = (RebindReply) conn.readReply();

            // Initiating Skel and saving it
            Class skelClass = Class.forName(remoteFullName + "_Skel");
            Skel skel = (Skel) skelClass.newInstance();
            skel.setRemote(obj);

            invokeTable.put(request.getRemoteInterfaceName(), skel);
            RMIProperties.log("Rebind Request Finished -- Remote Name: " + name);
        } catch (Exception ex) {
            throw new RemoteException(SpotRegistry.class, "Error on rebind(" + name + ")");
        }
    }

    /**
     * (non-javadoc)
     * @see java.rmi.registry.Registry#unbind(java.lang.String) 
     */
    public void unbind(String name) throws NotBoundException,
            NullPointerException, RemoteException {
        RMIProperties.log("Unbind Request Started -- Remote Name: " + name);

        // Exceptions
        if (name == null) throw new NullPointerException("Unbind name is null.");
        //if (!invokeTable.contains(name)) throw new NotBoundException("Unbind name is not bounded.");
        //TODO FIX THIS
        try {
            RMIUnicastConnection conn = RMIUnicastConnection.
                    makeClientConnection(ProtocolOpcode.REGISTRY_REQUEST, srvAddress, srvPort);
            UnbindRequest request = new UnbindRequest(name);
            conn.writeRequest(request);
            UnbindReply reply = (UnbindReply) conn.readReply();

            if (reply.exceptionHappened()) throw new NotBoundException(SpotRegistry.class, "Ubind name is not bounded.");

            invokeTable.remove(request.getRemoteInterfaceName());
            RMIProperties.log("Unbind Request Finished -- Remote Name: " + name);
        } catch (Exception ex) {
            throw new RemoteException(SpotRegistry.class, "Error on unbind(" + name + ")");
        }
    }

    public void addRegistryListener(RegistryListener listener) {
        // @TODO
    }

}
