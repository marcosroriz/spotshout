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
 * This class represent the list reply of the RMI Protocol.
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
    }

    /**
     * List Reply Protocol
     * ------------------------------------------------------------------------
     * Byte:        Opcode
     * Byte:        Status
     * Int:         List Size
     * Strings:     Element Name
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIReply#readData(java.io.DataInput)
     */
    protected void readData(DataInput input) throws RemoteException {
        try {
            operation = input.readByte();
            status = input.readByte();
            int listSize = input.readInt();

            names = new String[listSize];
            for (int i = 0; i < listSize; i++)
                names[i] = input.readUTF();
        } catch (IOException ex) {
            throw new RemoteException(ListReply.class, "Error on list reply");
        }
    }

    // Getters
    public int getListSize() {
        return names.length;
    }

    public String[] getNames() {
        return names;
    }
}
