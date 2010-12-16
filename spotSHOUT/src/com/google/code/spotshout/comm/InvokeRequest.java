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

import com.google.code.spotshout.remote.TargetMethod;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import ksn.io.ObjectInputStream;
import ksn.io.ObjectOutputStream;

/**
 * This class represent a Invoke Request of the RMI Protocol. It implements
 * the writeData and the readData methods necessary to send and read the request
 * from the Spots. The request data is the following:
 *
 * Invoke Request Protocol
 * ----------------------------------------------------------------------------
 * Byte:        Opcode
 * Int:         The size of a byte vector to serialize TargetMethod.
 * Obj:         TargetMethod
 */
public class InvokeRequest extends RMIRequest {

    /**
     * The remote interface name.
     */
    private String remoteName;

    /**
     * Target Method that we are invoking.
     */
    private TargetMethod method;

    /**
     * Empty constructor for dependency injection and "manual" reflection.
     */
    public InvokeRequest() {
        super(ProtocolOpcode.INVOKE_REQUEST);
    }
    
    public InvokeRequest(String remName, TargetMethod m) {
        super(ProtocolOpcode.INVOKE_REQUEST);
        remoteName = remName;
        method = m;
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
            remoteName = input.readUTF();

            // We have already readed operation for the manual reflection
            int length = input.readInt();
            byte[] data = new byte[length];
            input.readFully(data, 0, length);

            ByteArrayInputStream bin = new ByteArrayInputStream(data);
            ObjectInputStream oin = new ObjectInputStream(bin);
            method = (TargetMethod) oin.readObject();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.InvokeRequest
     * 
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIOperation#writeData(java.io.DataOutput)
     */
    protected void writeData(DataOutput output) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bout);
        oos.writeObject(method);

        output.write(getOperation());
        output.writeUTF(remoteName);
        output.writeInt(bout.size());
        byte[] buff = bout.toByteArray();
        output.write(buff);
    }

    // Getter
    public TargetMethod getMethod() {
        return method;
    }

    public String getRemoteName() {
        return remoteName;
    }
}
