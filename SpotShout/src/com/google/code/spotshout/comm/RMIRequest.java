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

/**
 * This class represent a generic RMI Request (Protocol). Each operation will
 * inherit this class and specify it's detail, overwriting the writeData method
 * with the specific data of it's protocol. Also it's should overwrite the
 * readData method with it's specific protocol (in the same order that they
 * have been written).
 */
public abstract class RMIRequest extends RMIOperation {

    /**
     * Our address (MAC).
     */
    protected String ourAddr;

    /**
     * The operation reply port.
     */
    protected int replyPort;

    public RMIRequest() {
    }

    public RMIRequest(byte op) {
        operation = op;
        ourAddr = System.getProperty("IEEE_ADDRESS");
        replyPort = RemoteGarbageCollector.getFreePort();
    }

    // Getters
    public int getReplyPort() {
        return replyPort;
    }

    public String getOurAddr() {
        return ourAddr;
    }
}
