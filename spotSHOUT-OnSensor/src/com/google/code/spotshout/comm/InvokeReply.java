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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import ksn.io.KSNSerializableInterface;
import ksn.io.ObjectInputStream;
import ksn.io.ObjectOutputStream;

/**
 * This class represent a Invoke Reply of the RMI Protocol. It implements
 * the writeData and the readData methods necessary to send and read the reply
 * from the Spots. The request data is the following:
 *
 * Invoke Reply Protocol
 * ----------------------------------------------------------------------------
 * Byte:        Operation Status
 * (Opt) Byte:  Exception
 * Int:         The size of a byte vector to serialize the reply.
 * Obj:         Return value (wrapped) -
 *              One of the Serial* objects in com.google.code.spotshout.lang
 */
public class InvokeReply extends RMIReply {

    /**
     * Reply return value (wrapped).
     */
    private KSNSerializableInterface returnValue;

    /**
     * Empty constructor for dependency injection and "manual" reflection.
     */
    public InvokeReply() {
        super(ProtocolOpcode.INVOKE_REPLY);
    }

    /**
     * The Invoke reply of the RMI protocol. This constructor should be used by
     * the SPOT.
     * 
     * @param returnValue - the method return value (Wrapped) in one of the
     *                      Serialized Classes.
     * @see com.google.code.spotshout.lang
     */
    public InvokeReply(KSNSerializableInterface v) {
        super(ProtocolOpcode.INVOKE_REPLY);
        returnValue = v;
    }

    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.InvokeRequest
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIOperation#readData(java.io.DataInput) 
     */
    protected void readData(DataInput input) throws IOException {
        try {
            operationStatus = input.readByte();

            if (operationStatus != ProtocolOpcode.OPERATION_OK) {
                exception = input.readByte();
            } else {
                // We have already readed operation for the manual reflection
                int length = input.readInt();

                byte[] data = new byte[length];
                input.readFully(data);

                ByteArrayInputStream bin = new ByteArrayInputStream(data);
                ObjectInputStream oin = new ObjectInputStream(bin);
                returnValue = (KSNSerializableInterface) oin.readObject();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.InvokeReply
     * 
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIOperation#writeData(java.io.DataOutput)
     */
    protected void writeData(DataOutput output) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bout);
        oos.writeObject(returnValue);

        if (operationStatus != ProtocolOpcode.OPERATION_OK) {
            output.write(getException());
        } else {
            output.write(bout.size());
            output.write(bout.toByteArray());
        }
    }

    // Getter
    public KSNSerializableInterface getReturnValue() {
        return returnValue;
    }
}
