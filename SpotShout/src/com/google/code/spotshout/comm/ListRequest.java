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

import java.io.DataOutput;
import java.io.IOException;
import java.rmi.RemoteException;

/**
 * This class represent the list request of the RMI Protocol.
 */
public class ListRequest extends RMIRequest {

    /**
     * The list request of the RMI Protocol.
     */
    public ListRequest() {
        super(ProtocolOpcode.LIST_REQUEST);
    }

    /**
     * List Request Protocol
     * ------------------------------------------------------------------------
     * Byte:        Opcode
     * UTF:         Address
     * INT:         Reply Port
     *
     * For method explanation:
     * @see com.google.code.spotshout.comm.RMIRequest#writeData(java.io.DataOutput)
     */
    protected void writeData(DataOutput output) throws RemoteException {
        try {
            output.write(getOperation());
            output.writeUTF(getOurAddr());
            output.writeInt(getReplyPort());
        } catch (IOException ex) {
            throw new RemoteException(ListRequest.class, "Error on list()");
        }
    }
}
