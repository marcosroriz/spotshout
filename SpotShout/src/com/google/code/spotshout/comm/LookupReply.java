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

import com.google.code.spotshout.remote.Stub;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * This class represent the Lookup Reply of the RMI Protocol. It implements
 * the writeData and the readData methods necessary to send and read the request
 * from the Spot to the NameServer. The reply data is the following:
 *
 * Lookup Reply Protocol
 * ---------------------------------------------------------------
 * Byte:        Opcode
 * Byte:        Status
 * (Opt) Byte:  Exception
 * String:      Remote Reference Address
 * Int:         Remote Reference Port
 * String:      Remote Full Qualified Name
 */
public class LookupReply extends RMIReply {

    /**
     * Remote address (in MAC).
     */
    private String remoteAddr;

    /**
     * Remote (Skeleton) port.
     */
    private int remotePort;

    /**
     * Remote Interface Name (as announced).
     * @TODO Preciso do nome anunciado da classe? Mas não estou ouvihndo na thread? So se for pra GC.
     */
    //private String remoteName;

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
     * @param statusCode - operation status (OK)
     * @param remAddr - remote address
     * @param remPort - remote port
     * @param remFullName - remote interface full name (including package)
     */
    public LookupReply(byte statusCode, String remAddr, int remPort,
            String remFullName) {
        operationStatus = statusCode;
        remoteAddr = remAddr;
        remotePort = remPort;
        remoteFullName = remFullName;
    }

    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.LookupReply
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIOperation#readData(java.io.DataInput)
     */
    protected void readData(DataInput input) throws RemoteException {
        try {
            operation = input.readByte();
            operationStatus = input.readByte();

            if (operationStatus != ProtocolOpcode.OPERATION_OK) {
                exception = input.readByte();
            } else {
                remoteAddr = input.readUTF();
                remotePort = input.readInt();
                remoteFullName = input.readUTF();
            }
        } catch (IOException ex) {
            throw new RemoteException(LookupReply.class, "Error on reading lookup reply");
        }
    }

    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.LookupReply
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIOperation#writeData(java.io.DataOutput)
     */
    protected void writeData(DataOutput output) throws RemoteException {
        try {
            output.write(getOperation());
            output.write(getOperationStatus());

            if (operationStatus != ProtocolOpcode.OPERATION_OK) {
                output.write(getException());
            } else {
                output.writeUTF(remoteAddr);
                output.writeInt(remotePort);
                output.writeUTF(remoteFullName);
            }
        } catch (IOException ex) {
            throw new RemoteException(LookupReply.class, "Error on writting lookup reply");
        }
    }

    /**
     * Creates statically a stub. We'll load the remoteFullName_Stub class
     * and inject it's dependency (targetMAC, port, etc). 
     * @return stub - a proxy that implements extends {@link Stub} and implements
     *                the given Remote Interface.
     * @throws ClassNotFoundException if the class cannot be initialized.
     */
    public Stub createStub() throws ClassNotFoundException {
        try {
            Class stubClass = Class.forName(remoteFullName + "_Stub");
            Stub stub = (Stub) stubClass.newInstance();
            stub.setTargetAddr(remoteAddr);
            stub.setTargetPort(remotePort);
            //stub.setTargetName(remoteName);
            //@TODO Preciso do nome anunciado da classe? Mas não estou ouvihndo na thread? So se for pra GC.

            return stub;
        } catch (Exception ex) {
            throw new ClassNotFoundException("Error on Skeleton: "
                    + remoteFullName + "_Skel initialization");
        }
    }

    // Getters
    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getRemoteFullName() {
        return remoteFullName;
    }

    public int getRemotePort() {
        return remotePort;
    }
}
