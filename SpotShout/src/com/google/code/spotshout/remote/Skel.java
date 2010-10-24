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

package com.google.code.spotshout.remote;

import com.google.code.spotshout.comm.RMIReply;
import com.google.code.spotshout.comm.RMIRequest;
import java.rmi.Remote;

/**
 * This class represent the structure of a abstract Skeleton. Each Skeleton has
 * to inherit this class.
 */
public abstract class Skel extends Thread {

    /**
     * Remote object that the skell will dispatch the request.
     */
    private Remote remote;

    /**
     * The port which this Skel will listen (non reliable), to make reliable
     * connections with Stubs.
     */
    private int port;

    /**
     * Empty constructor for reflection.
     */
    public Skel() {
    }

    /**
     * Process a receiving call/invoke RMI request.
     * @param method - the method meta-data and it's arguments.
     * @return - the RMI Reply object, if the request doesn't have a return, i.e.
     *           void, return null
     */
    public abstract RMIReply service(RMIRequest method);

    public int getPort() {
        return port;
    }

    public Remote getRemote() {
        return remote;
    }

    public void setRemote(Remote remote) {
        this.remote = remote;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
