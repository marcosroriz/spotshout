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
import java.rmi.RemoteException;

/**
 * This class represent a generic RMI Reply (Protocol). Each operation will
 * inherit this class and specify it's detail, overwriting the readData method
 * with the specific data of it's protocol.
 */
public abstract class RMIReply {

    /**
     * Protocol Opcode.
     */
    protected byte operation;

    /**
     * Operation Status - OK or NOK.
     */
    protected byte status;

    /**
     * This method define the order and fields that it's going to be read
     * by each operation [i.e. -- Protocol] on the input.
     * 
     * @param input - the inputStream that the request data should be read.
     * @return DataInput - the inputStream that the data will be readed.
     * @throws RemoteException - in case of a failure in communication or if the
     *                           data comes corrupted.
     */
    protected abstract DataInput readData(DataInput input) throws RemoteException;

    /**
     * Verify if a exception happened on the RMI operation.
     * @return true if a exception happened during the RMI Reply, false otherwise
     */
    public boolean exceptionHappened() {
        return status == ProtocolOpcode.OPERATION_NOK;
    }
    
    // Getters and Setters
    public byte getOperation() {
        return operation;
    }

    public byte getStatus() {
        return status;
    }
}
