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
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * This class represent the rebind reply of the RMI Protocol.
 */
public class RebindReply extends RMIReply {

    /**
     * Protocol exception
     * @see ProtocolOpcode
     */
    private byte exception;

    /**
     * Empty constructor for dependency injection and "manual" reflection.
     */
    public RebindReply() {
    }

    /**
     * Rebind Reply Protocol
     * ------------------------------------------------------------------------
     * Byte:        Opcode
     * Byte:        Status
     * (Opt) Byte:  Exception
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIReply#readData(java.io.DataInput)
     */
    protected DataInput readData(DataInput input) throws RemoteException {
        try {
            operation = input.readByte();
            status = input.readByte();

            if (status != ProtocolOpcode.OPERATION_OK)
                exception = input.readByte();

            return input;
        } catch (IOException ex) {
            throw new RemoteException(RebindReply.class, "Error on rebind reply");
        }
    }

    // Getters

    /**
     * Gets the exception Opcode (if happened).
     * @return true if an exception occured, false otherwise.
     */
    public byte getException() {
        return exception;
    }
}
