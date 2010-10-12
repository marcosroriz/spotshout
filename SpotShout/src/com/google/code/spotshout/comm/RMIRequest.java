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
import com.google.code.spotshout.remote.SpotRegistry;
import java.io.DataOutput;
import java.rmi.RemoteException;

/**
 * This class represent a generic RMI Request (Protocol). Each operation will
 * inherit this class and specify it's detail, overwriting the writeData method
 * with the specific data of it's protocol.
 */
public abstract class RMIRequest {

    /**
     * Protocol Opcode.
     * @see ProtocolOpcode
     */
    private byte operation;

    /**
     * Our address (MAC).
     */
    private String ourAddr;

    /**
     * The operation reply port.
     */
    private int replyPort;

    public RMIRequest(byte op) {
        this.operation = op;
        this.ourAddr = System.getProperty("IEEE_ADDRESS");
        this.replyPort = RemoteGarbageCollector.getFreePort();
    }

    /**
     * This method define the order and fields that it's going to be written
     * by each operation [i.e. -- Protocol] on the output.
     * 
     * @param output - the outputStream that the request data should be written.
     * @throws RemoteException - in case of a failure in communication or if the
     *                           data comes corrupted.
     */
    protected abstract void writeData(DataOutput output) throws RemoteException;
            
    // Getters
    public int getReplyPort() {
        return replyPort;
    }

    public byte getOperation() {
        return operation;
    }

    public String getOurAddr() {
        return ourAddr;
    }
}
