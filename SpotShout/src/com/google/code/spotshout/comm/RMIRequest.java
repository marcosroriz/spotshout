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

import java.io.DataOutput;

/**
 * This class represent a generic RMI Request (Protocol).
 */
public abstract class RMIRequest {

    /**
     * Protocol Opcode
     */
    private byte operation;

    /**
     * Our address (MAC).
     */
    private String ourAddr;

    public RMIRequest(byte op) {
        setOperation(op);
        this.ourAddr = System.getProperty("IEEE_ADDRESS");
    }

    /**
     * This method define the order and fields that it's going to be written
     * by each operation [i.e. -- Protocol] on the output.
     * @param output - the outputstream that the request data should be written.
     */
    protected abstract void writeData(DataOutput output);

    // Getters and Setters
    public byte getOperation() {
        return operation;
    }

    public void setOperation(byte operation) {
        if (((operation & 0x01) == 0) && (operation > 10))
            throw new UnsupportedProtocolException(RMIRequest.class, "Unsupported Operation");
        this.operation = operation;
    }

    public String getOurAddr() {
        return ourAddr;
    }

}
