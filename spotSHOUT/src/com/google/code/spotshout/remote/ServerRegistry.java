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
import com.google.code.spotshout.comm.RMIReply;
import com.google.code.spotshout.comm.RMIRequest;
import com.google.code.spotshout.comm.Server;
import spot.rmi.AlreadyBoundException;
import spot.rmi.NotBoundException;
import spot.rmi.Remote;
import spot.rmi.RemoteException;
import spot.rmi.registry.Registry;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class represents the Server Registry.
 * @TODO More doc
 */
public class ServerRegistry extends Server implements Registry {

    /**
     * Registry table
     *
     * Structure:
     * | Interface Name | Full Interface Remote Name | Remote Address |
     */
    private Hashtable registryTable;

    /**
     * Invoke table
     *
     * Structure:
     * | Interface Name | Skel |
     */
    private Hashtable invokeTable;

    public ServerRegistry() {
        super(RMIProperties.RMI_SERVER_PORT);
        registryTable = new Hashtable();
        invokeTable = new Hashtable();
    }

    public RMIReply service(RMIRequest request) {
        switch (request.getOperation()) {
            case ProtocolOpcode.BIND_REQUEST:
                return bind((BindRequest) request);
            case ProtocolOpcode.INVOKE_REQUEST:
                return invoke((InvokeRequest) request);
            case ProtocolOpcode.LIST_REQUEST:
                return list((ListRequest) request);
            case ProtocolOpcode.LOOKUP_REQUEST:
                return lookup((LookupRequest) request);
            /*case ProtocolOpcode.REBIND_REQUEST:
            request = new RebindRequest();
            break;
            case ProtocolOpcode.UNBIND_REQUEST:
            request = new UnbindRequest();
            break;*/
            default:
                break;
        }
        return null;
    }
   
    private RMIReply bind(BindRequest request) {
        RMIProperties.log("Bind Request Started -- Remote Name: " + request.getRemoteInterfaceName());

        BindReply reply = new BindReply();
        String interfaceName = request.getRemoteInterfaceName();

        if (registryTable.containsKey(interfaceName)) {
            reply.setOperationStatus(ProtocolOpcode.OPERATION_NOK);
            reply.setException(ProtocolOpcode.EXCEPTION_ALREADY_BOUND);
        } else {
            // Adding to table
            Vector v = new Vector(2);
            v.addElement(request.getRemoteFullName());
            v.addElement(request.getRemoteAddress());
            registryTable.put(interfaceName, v);
        }

        RMIProperties.log("Bind Request Finished -- Remote Name: " + request.getRemoteInterfaceName());
        return reply;
    }

    public void bind(String name, String remoteFullName, Remote obj)
            throws AlreadyBoundException, NullPointerException, RemoteException {
        try {
            RMIProperties.log("Bind Request Started -- Remote Name: " + name);

            // Exceptions
            if (name == null) throw new NullPointerException("Bind name is null.");
            if (obj == null) throw new NullPointerException("Remote object is null.");

            BindRequest request = new BindRequest(name, remoteFullName);
            BindReply reply = (BindReply) bind(request);

            if (reply.exceptionHappened()) {
                throw new AlreadyBoundException(SpotRegistry.class, "AlreadyBound on Bind");
            }

            // Initiating Skel and saving it
            Class skelClass = Class.forName(remoteFullName + "_Skel");
            Skel skel = (Skel) skelClass.newInstance();
            skel.setRemote(obj);

            invokeTable.put(request.getRemoteInterfaceName(), skel);
            RMIProperties.log("Bind Request Finished -- Remote Name: " + name);
        } catch (Exception ex) {
            throw new RemoteException(SpotRegistry.class, "Skeleton not found");
        }
    }

    private RMIReply invoke(InvokeRequest request) {
        RMIProperties.log("Invoke Request Started -- Remote Name: " + request.getRemoteName());

        Skel skel = (Skel) invokeTable.get(request.getRemoteName());
        RMIReply reply = skel.service(request);

        RMIProperties.log("Invoke Request Finished -- Remote Name: " + request.getRemoteName());
        return reply;
    }
    
    private RMIReply list(ListRequest request) {
        RMIProperties.log("List Request Started");
        
        String[] names = new String[registryTable.size()];
        Enumeration e = registryTable.keys();

        int i = 0;
        while (e.hasMoreElements()) {
            names[i++] = (String) e.nextElement();
        }

        ListReply reply = new ListReply(names);

        RMIProperties.log("List Request Finished");
        return reply;
    }

    public String[] list() throws RemoteException {
        RMIProperties.log("List Request Started");

        ListRequest request = new ListRequest();
        ListReply reply = (ListReply) list(request);

        RMIProperties.log("List Request Finished");
        return reply.getNames();
    }

    private RMIReply lookup(LookupRequest request) {
        RMIProperties.log("Lookup Request Started -- Remote Name: " + request.getRemoteInterfaceName());

        RMIReply reply;
        String interfaceName = request.getRemoteInterfaceName();

        if (!registryTable.containsKey(interfaceName)) {
            reply = new LookupReply(ProtocolOpcode.OPERATION_NOK, ProtocolOpcode.EXCEPTION_NOT_BOUND);
        } else {
            Vector v = (Vector) registryTable.get(interfaceName);
            String remoteFullName = (String) v.elementAt(0);
            String remoteAddr = (String) v.elementAt(1);
            reply = new LookupReply(remoteAddr, remoteFullName, remoteAddr);
        }

        RMIProperties.log("Lookup Request Finished -- Remote Name: " + request.getRemoteInterfaceName());
        return reply;
    }
 
    public Remote lookup(String name) throws NotBoundException,
            NullPointerException, RemoteException {
        RMIProperties.log("Lookup Request Started -- Remote Name: " + name);

        // Exceptions
        if (name == null) throw new NullPointerException("Lookup name is null.");

        try {
            LookupRequest request = new LookupRequest(name);
            LookupReply reply = (LookupReply) lookup(request);

            // Creating Stub
            Stub stub = (Stub) reply.createStub();

            RMIProperties.log("Lookup Request Finished -- Remote Name:" + name);
            return (Remote) stub;
        } catch (Exception ex) {
            throw new RemoteException(SpotRegistry.class, "Stub not found");
        }
    }

    public void rebind(String name, String remoteFullName, Remote obj) throws NullPointerException, RemoteException {
    }

    public void unbind(String name) throws NotBoundException, NullPointerException, RemoteException {
    }

}
