/*
 * SpotSHOUT - A RMI Middleware for the SunSPOT Platform.
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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * This class represent the Lookup Request of the RMI Protocol. It implements
 * the writeData and the readData methods necessary to send and read the request
 * from the Spot to the NameServer. The request data is the following:
 *
 * Lookup Request Protocol
 * ----------------------------------------------------------------------------
 * Byte:        Opcode
 * UTF:         Address
 * INT:         Reply Port
 * UTF:         Remote Interface Name
 */
public class LookupRequest extends RMIRequest {

    /**
     * Remote Interface Name on NameServer.
     */
    private String remoteInterfaceName;

    /**
     * Empty constructor for dependency injection and "manual" reflection.
     */
    public LookupRequest() {
        super(ProtocolOpcode.LOOKUP_REQUEST);
    }

    /**
     * The lookup request of the RMI protocol.
     * @param remoteInterfaceName - the remote name (in the NameServer)
     */
    public LookupRequest(String remoteInterfaceName) {
        super(ProtocolOpcode.LOOKUP_REQUEST);
        this.remoteInterfaceName = remoteInterfaceName;
    }

    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.LookupRequest
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIRequest#readData(java.io.DataInput)
     */
    protected void readData(DataInput input) throws RemoteException {
        try {
            // We have already readed operation for the manual reflection
            ourAddr = input.readUTF();
            replyPort = input.readInt();
            remoteInterfaceName = input.readUTF();
        } catch (IOException ex) {
            throw new RemoteException(LookupRequest.class,
                    "Error on reading lookup()");
        }
    }

    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.LookupRequest
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIRequest#writeData(java.io.DataOutput)
     */
    protected void writeData(DataOutput output) throws RemoteException {
        try {
            output.write(getOperation());
            output.writeUTF(getOurAddr());
            output.writeInt(getReplyPort());
            output.writeUTF(remoteInterfaceName);
        } catch (IOException ex) {
            throw new RemoteException(LookupRequest.class,
                    "Error on lookup(" + remoteInterfaceName + ")");
        }
    }
}
