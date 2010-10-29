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

import com.google.code.spotshout.remote.RemoteGarbageCollector;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.util.IEEEAddress;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * This class represent the Bind Request of the RMI Protocol. It implements
 * the writeData and the readData methods necessary to send and read the request
 * from the Spot to the NameServer. The request data is the following:
 *
 * Bind Request Protocol
 * ----------------------------------------------------------------------------
 * Byte:        Opcode
 * INT:         Skeleton Port
 * UTF:         Remote Interface Desired Name
 * UTF:         Remote Interface Full Qualified Name
 */
public class BindRequest extends RMIRequest {

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
    public BindRequest() {
        super(ProtocolOpcode.BIND_REQUEST);
    }

    /**
     * The bind request of the RMI protocol. This constructor should be used by
     * the SPOT.
     * 
     * @param remoteInterfaceName - the remote name (in the NameServer)
     * @param remoteFullName - the remote interface full qualified name
     *                         (including package).
     */
    public BindRequest(String remoteInterfaceName, String remoteFullName) {
        super(ProtocolOpcode.BIND_REQUEST);
        this.remoteInterfaceName = remoteInterfaceName;
        this.remoteFullName = remoteFullName;
        this.skelPort = RemoteGarbageCollector.getFreePort();
        long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
        remoteAddress = IEEEAddress.toDottedHex(ourAddr);
        System.out.println("OUR ADDRESS IS : " + remoteAddress);
        RemoteGarbageCollector.registerPort(skelPort);
    }

    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.BindRequest
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIOperation#readData(java.io.DataInput) 
     */
    protected void readData(DataInput input) throws IOException {
        // We have already readed operation for the manual reflection
        skelPort = input.readInt();
        remoteInterfaceName = input.readUTF();
        remoteFullName = input.readUTF();
        remoteAddress = input.readUTF();
    }

    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.BindRequest
     * 
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIOperation#writeData(java.io.DataOutput)
     */
    protected void writeData(DataOutput output) throws IOException {
        output.write(getOperation());
        output.writeInt(getSkelPort());
        output.writeUTF(remoteInterfaceName);
        output.writeUTF(remoteFullName);
        output.writeUTF(remoteAddress);
    }

    /**
     * Get the port which the skeleton will listen.
     * @return the port of the skeleton.
     */
    public int getSkelPort() {
        return skelPort;
    }

    /**
     * Get the full qualified name of the remote interface.
     * @return full qualified name of remote interface.
     */
    public String getRemoteFullName() {
        return remoteFullName;
    }

    /**
     * Get the remote interface name (as desired).
     * @return desired name.
     */
    public String getRemoteInterfaceName() {
        return remoteInterfaceName;
    }
}
