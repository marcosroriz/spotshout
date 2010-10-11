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

import com.google.code.spotshout.remote.SpotRegistry;
import java.io.DataOutput;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * This class represent the rebind request of the RMI Protocol.
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
     * The bind request of the rmi protocol.
     * @param remoteInterfaceName - the remote name (in the NameServer)
     * @param remoteFullName - the remote interface full qualified name
     *                         (including package).
     * @TODO check for exceptions?
     */
    public RebindRequest(String remoteInterfaceName, String remoteFullName) {
        super(ProtocolOpcode.REBIND_REQUEST);
        this.remoteInterfaceName = remoteInterfaceName;
        this.remoteFullName = remoteFullName;
        this.skelPort = SpotRegistry.getFreePort();
    }

    /**
     * Rebind Request Protocol
     * ------------------------------------------------------------------------
     * Byte:        Opcode
     * UTF:         Address
     * INT:         Reply Port
     * INT:         Skeleton Port
     * UTF:         Remote Interface Desired Name
     * UTF:         Remote Interface Full Qualified Name
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIRequest#writeData(java.io.DataOutput)
     */
    protected DataOutput writeData(DataOutput output) {
        try {
            output.write(getOperation());
            output.writeUTF(getOurAddr());
            output.writeInt(getReplyPort());
            output.writeInt(getSkelPort());
            output.writeUTF(remoteInterfaceName);
            output.writeUTF(remoteFullName);
            return output;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RemoteException(RebindRequest.class,
                    "Error on rebind(" + remoteInterfaceName + ")");
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
