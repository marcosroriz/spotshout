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
import java.io.IOException;

/**
 * This class represent the Rebind Reply of the RMI Protocol. It implements
 * the writeData and the readData methods necessary to send and read the request
 * from the Spot to the NameServer. The reply data is the following:
 *
 * Rebind Reply Protocol
 * ----------------------------------------------------------------------------
 * Byte:        Opcode
 * Byte:        Status
 * (Opt) Byte:  Exception
 */
public class RebindReply extends RMIReply {

    /**
     * Empty constructor for dependency injection and "manual" reflection.
     */
    public RebindReply() {
        super(ProtocolOpcode.REBIND_REPLY);
    }

    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.RebindReply
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIOperation#readData(java.io.DataInput)
     */
    protected void readData(DataInput input) throws IOException {
        operation = input.readByte();
        operationStatus = input.readByte();

        if (operationStatus != ProtocolOpcode.OPERATION_OK)
            exception = input.readByte();
    }
    
    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.RebindReply
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIOperation#writeData(java.io.DataOutput)
     */
    protected void writeData(DataOutput output) throws IOException {
        output.write(getOperation());
        output.write(getOperationStatus());

        if (operationStatus != ProtocolOpcode.OPERATION_OK)
            output.write(getException());
    }
}
