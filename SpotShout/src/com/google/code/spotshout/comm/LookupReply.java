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
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * This class represent the lookup reply of the RMI Protocol.
 */
public class LookupReply extends RMIReply {

    /**
     * Protocol exception.
     * @see ProtocolOpcode.
     */
    private byte exception;

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
    private String remoteName;

    /**
     * Remote Interface Full Qualified Name (including package).
     */
    private String remoteFullName;
    
    /**
     * Empty constructor for dependency injection and "manual" reflection.
     */
    public LookupReply() {
    }

    /*
     * Lookup Reply Protocol
     * ---------------------------------------------------------------
     * Byte:        Opcode
     * Byte:        Status
     * (Opt) Byte:  Exception
     * String:      Remote Reference Address
     * Int:         Remote Reference Port
     * String:      Remote Full Qualified Name
     * 
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIReply#readData(java.io.DataInput)
     */
    protected DataInput readData(DataInput input) throws RemoteException {
        try {
            operation = input.readByte();
            status = input.readByte();

            if (status != ProtocolOpcode.OPERATION_OK) {
                exception = input.readByte();
            } else {
                remoteAddr = input.readUTF();
                remotePort = input.readInt();
                remoteFullName = input.readUTF();
            }

            return input;
        } catch (IOException ex) {
            throw new RemoteException(LookupReply.class, "Error on lookup reply");
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
            stub.setTargetName(remoteName);

            return stub;
        } catch (Exception ex) {
            throw new ClassNotFoundException("Error on Skeleton: "
                    + remoteFullName + "_Skel initialization");
        }
    }

    // Getters and Setters
    
    /**
     * Gets the exception Opcode (if happened).
     * @return true if an exception occured, false otherwise.
     */
    public byte getException() {
        return exception;
    }

    /**
     * Inject the target remote name (as announced).
     * @param remoteName - the remote name in the name server.
     */
    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }
}
