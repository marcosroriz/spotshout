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
import java.io.DataOutput;
import java.rmi.RemoteException;

/**
 * This class abstract the notion of a given RMI operation (request and reply).
 * Each operation will inherit this class and specify it's detail, overwriting
 * the readData and writeData methods with the specific data of it's protocol.
 */
public abstract class RMIOperation {

    /**
     * Protocol operation code.
     * @see ProtocolOpcode.
     */
    protected byte operation;

    /**
     * This method define the order and fields that it's going to be read
     * by each operation [i.e. -- Protocol] on the input.
     *
     * @param input - the inputStream that the request data should be read.
     * @throws RemoteException - in case of a failure in communication or if the
     *                           data comes corrupted.
     */
    protected abstract void readData(DataInput input) throws RemoteException;

    /**
     * This method will read the opcode of the input so that we can manually
     * instantiate the correct operation and inject it's data by calling readData
     * on it.
     * @param input - the inputStream that the request data should be read.
     * @throws RemoteException
     */
    protected abstract void readOpcode(DataInput input) throws RemoteException;

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
    public byte getOperation() {
        return operation;
    }
}
