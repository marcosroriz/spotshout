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

import com.google.code.spotshout.remote.RemoteGarbageCollector;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * This class represent the Rebind Request of the RMI Protocol. It implements
 * the writeData and the readData methods necessary to send and read the request
 * from the Spot to the NameServer. The request data is the following:
 *
 * Rebind Request Protocol
 * ----------------------------------------------------------------------------
 * Byte:        Opcode
 * UTF:         Address
 * INT:         Reply Port
 * INT:         Skeleton Port
 * UTF:         Remote Interface Desired Name
 * UTF:         Remote Interface Full Qualified Name
 */
public class RebindRequest extends RMIRequest {

    /**
     * Remote Interface Name on NameServer.
     */
    private String remoteInterfaceName;

    /**
     * Remote Interface Full Qualified Name (including package).
     * Ex: foo.bar.IWeather
     */
    private String remoteFullName;

    /**
     * Skeleton port.
     */
    private int skelPort;

    /**
     * Empty constructor for dependency injection and "manual" reflection.
     */
    public RebindRequest() {
    }
    
    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.RebindRequest
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIRequest#readData(java.io.DataInput)
     */
    protected void readData(DataInput input) throws RemoteException {
        try {
            // We have already readed operation for the manual reflection
            ourAddr = input.readUTF();
            replyPort = input.readInt();
            skelPort = input.readInt();
            remoteInterfaceName = input.readUTF();
            remoteFullName = input.readUTF();
        } catch (IOException ex) {
            throw new RemoteException(RebindRequest.class,
                    "Error on reading rebind()");
        }
    }

    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.RebindRequest
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIRequest#writeData(java.io.DataOutput)
     */
    protected void writeData(DataOutput output) throws RemoteException {
        try {
            output.write(getOperation());
            output.writeUTF(getOurAddr());
            output.writeInt(getReplyPort());
            output.writeInt(getSkelPort());
            output.writeUTF(remoteInterfaceName);
            output.writeUTF(remoteFullName);
        } catch (IOException ex) {
            throw new RemoteException(BindRequest.class,
                    "Error on writting rebind(" + remoteInterfaceName + ")");
        }
    }

    /**
     * Get the port which the skeleton will listen.
     * @return the port of the skeleton.
     */
    public int getSkelPort() {
        return skelPort;
    }
}
