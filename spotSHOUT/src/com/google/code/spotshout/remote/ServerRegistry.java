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
import com.google.code.spotshout.comm.ListReply;
import com.google.code.spotshout.comm.ListRequest;
import com.google.code.spotshout.comm.LookupReply;
import com.google.code.spotshout.comm.LookupRequest;
import com.google.code.spotshout.comm.ProtocolOpcode;
import com.google.code.spotshout.comm.RMIReply;
import com.google.code.spotshout.comm.RMIRequest;
import com.google.code.spotshout.comm.Server;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class represents the Server Registry.
 * @TODO More doc
 */
public class ServerRegistry extends Server {

    /**
     * Registry table
     *
     * Structure:
     * | Interface Name | Full Interface Remote Name | Remote Address | Remote Port |
     */
    private Hashtable registryTable;

    public ServerRegistry() {
        setPort(RMIProperties.SERVER_PORT);
        registryTable = new Hashtable();
    }

    private RMIReply bind(BindRequest request) {
        BindReply reply = new BindReply();
        String interfaceName = request.getRemoteInterfaceName();

        if (registryTable.containsKey(interfaceName)) {
            reply.setOperationStatus(ProtocolOpcode.OPERATION_NOK);
            reply.setException(ProtocolOpcode.EXCEPTION_ALREADY_BOUND);
        } else {
            reply.setOperationStatus(ProtocolOpcode.OPERATION_OK);

            // Adding to table
            Vector v = new Vector(3);
            v.addElement(request.getRemoteFullName());
            v.addElement(request.getRemoteAddress());
            v.addElement(new Integer(request.getSkelPort()));
            registryTable.put(interfaceName, v);
        }
        
        return reply;
    }

    private RMIReply list(ListRequest request) {
        String[] names = new String[registryTable.size()];
        Enumeration e = registryTable.keys();

        int i = 0;
        while (e.hasMoreElements()) {
            names[i++] = (String) e.nextElement();
        }
        
        ListReply reply = new ListReply(names);
        reply.setOperationStatus(ProtocolOpcode.OPERATION_OK);
        return reply;
    }

    private RMIReply lookup(LookupRequest request) {
        String interfaceName = request.getRemoteInterfaceName();

        if (!registryTable.contains(interfaceName)) {
            return new LookupReply(ProtocolOpcode.OPERATION_NOK, ProtocolOpcode.EXCEPTION_NOT_BOUND);
        } else {
            Vector v = (Vector) registryTable.get(interfaceName);
            String remoteAddr = (String) v.elementAt(1);
            int remotePort = ((Integer) v.elementAt(2)).intValue();
            String remoteFullName = (String) v.elementAt(0);

            return new LookupReply(ProtocolOpcode.OPERATION_OK,
                    remoteAddr, remotePort, remoteFullName);
        }
    }

    public RMIReply service(RMIRequest request) {
        switch (request.getOperation()) {
            case ProtocolOpcode.BIND_REQUEST:
                return bind((BindRequest) request);
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
        }
        return null;
    }

}
