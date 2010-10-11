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

package com.google.code.spotshout.comm;

import com.google.code.spotshout.remote.SpotRegistry;
import java.io.DataOutput;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * This class represent a bind request.
 */
public class BindRequest extends RMIRequest {

    /**
     * Remote Interface Name on NameServer.
     */
    private String remoteInterfaceName;

    /**
     * Remote Interface full qualified name (including package).
     * Ex: foo.bar.IWeather
     */
    private String remoteFullName;

    public BindRequest(byte operation, String remoteInterfaceName, String remoteFullName) {
        super(operation);
        this.remoteInterfaceName = remoteInterfaceName;
        this.remoteFullName = remoteFullName;
    }

    /**
     * Bind Request Protocol
     * ---------------------------------------------------------------
     * Byte:        Opcode
     * UTF:         Address
     * INT:         Skeleton Port
     * UTF:         Remote Interface Desired Name
     * UTF:         Remote Interface Full Qualified Name
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIRequest#writeData(java.io.DataOutput)
     * @TODO Solve this acoplation with SpotRegistry
     */
    protected void writeData(DataOutput output) {
        try {
            output.write(ProtocolOpcode.BIND_REQUEST);
            output.writeUTF(remoteFullName);
            output.writeInt(SpotRegistry.getFreePort());
            output.writeUTF(remoteInterfaceName);
            output.writeUTF(remoteFullName);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RemoteException(BindRequest.class, "Error on bind()");
        }
    }

}
