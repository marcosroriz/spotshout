/*
 * spotSHOUT - A RMI Middleware for the SunSPOT Platform.
 * Copyright (C) 2010-2011 Marcos Paulino Roriz Junior
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
 * This class represent the List Reply of the RMI Protocol. It implements
 * the writeData and the readData methods necessary to send and read the request
 * from the Spot to the NameServer. The reply data is the following:
 *
 * List Reply Protocol
 * ----------------------------------------------------------------------------
 * Byte:        Status
 * Int:         List Size
 * Strings:     Element Name
 */
public class ListReply extends RMIReply {
    
    /**
     * The list with the names bounded on a given Remote {@link Registry}.
     */
    private String[] names;

    /**
     * Empty constructor for dependency injection and "manual" reflection.
     */
    public ListReply() {
        super(ProtocolOpcode.LIST_REPLY);
    }

    /**
     * The list reply of the RMI protocol. This constructor should be used by
     * the Server.
     *
     * @param namesList - the list of bounded names on the server.
     */
    public ListReply(String[] namesList) {
        super(ProtocolOpcode.LIST_REPLY);
        names = namesList;
    }

    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.ListReply
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIOperation#readData(java.io.DataInput)
     */
    protected void readData(DataInput input) throws IOException {
        operationStatus = input.readByte();
        int listSize = input.readInt();

        names = new String[listSize];
        for (int i = 0; i < listSize; i++)
            names[i] = input.readUTF();
    }

    /**
     * For the protocol data:
     * @see com.google.code.spotshout.comm.ListReply
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIOperation#writeData(java.io.DataOutput)
     */
    protected void writeData(DataOutput output) throws IOException {
        output.write(getOperationStatus());
        output.writeInt(getListSize());

        for (int i = 0; i < names.length; i++)
            output.writeUTF(names[i]);
    }

    // Getters
    public int getListSize() {
        return names.length;
    }

    public String[] getNames() {
        return names;
    }
}
