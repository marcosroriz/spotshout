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

import com.google.code.spotshout.remote.Stub;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * This class represent the Lookup Reply of the RMI Protocol. It implements
 * the writeData and the readData methods necessary to send and read the request
 * from the Spot to the NameServer. The reply data is the following:
 *
 * Lookup Reply Protocol
 * ---------------------------------------------------------------
 * Byte:        Status
 * (Opt) Byte:  Exception
 * String:      Remote Interface Name
 * String:      Remote Full Qualified Name
 * String:      Remote Reference Address
 */
public class LookupReply extends RMIReply {

    /**
     * Remote address (in MAC).
     */
    private String remoteAddr;

    /**
     * The interface name.
     */
    private String remoteName;

    /**
     * Remote Interface Full Qualified Name (including package).
     */
    private String remoteFullName;
    
    /**
     * Empty constructor for dependency injection and "manual" reflection.
     */
    public LookupReply() {
        super(ProtocolOpcode.LOOKUP_REPLY);
    }

    /**
     * Construct a LookupReply with a given exception code.
     * @param statusCode - the operation status code
     * @param exceptionCode - the exception code
     */
    public LookupReply(byte statusCode, byte exceptionCode) {
       super(ProtocolOpcode.LOOKUP_REPLY);
       operationStatus = statusCode;
       exception = exceptionCode;
    }

    /**
     * Construct a LookupReply with all the meta-data available so we can
     * manually instantiate a Stub in the Spot.
     * @param remName - the interface remote name
     * @param remFullName - remote interface full name (including package)
     * @param remAddr - remote address
     */
    public LookupReply(String remName, String remFullName, String remAddr) {
        super(ProtocolOpcode.LOOKUP_REPLY);
        remoteName = remName;
        remoteFullName = remFullName;
        remoteAddr = remAddr;
    }

    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.LookupReply
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIOperation#readData(java.io.DataInput)
     */
    protected void readData(DataInput input) throws IOException {
        // We have already readed operation for the manual reflection
        operationStatus = input.readByte();

        if (operationStatus != ProtocolOpcode.OPERATION_OK) {
            exception = input.readByte();
        } else {
            remoteName = input.readUTF();
            remoteFullName = input.readUTF();
            remoteAddr = input.readUTF();
        }
    }

    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.LookupReply
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIOperation#writeData(java.io.DataOutput)
     */
    protected void writeData(DataOutput output) throws IOException {
        output.write(getOperationStatus());

        if (operationStatus != ProtocolOpcode.OPERATION_OK) {
            output.write(getException());
        } else {
            output.writeUTF(remoteName);
            output.writeUTF(remoteFullName);
            output.writeUTF(remoteAddr);
        }
    }

    /**
     * Creates statically a stub. We'll load the remoteFullName_Stub class
     * and inject it's dependency (targetMAC, port, etc). 
     * @return stub - a proxy that implements extends {@link Stub} and implements
     *                the given Remote Interface.
     * @throws ClassNotFoundException if the class cannot be initialized.
     */
    public Stub createStub() throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        Class stubClass = Class.forName(remoteFullName + "_Stub");
        Stub stub = (Stub) stubClass.newInstance();
        stub.setTargetAddr(remoteAddr);
        stub.setInterfaceName(remoteName);
        return stub;
    }
}
