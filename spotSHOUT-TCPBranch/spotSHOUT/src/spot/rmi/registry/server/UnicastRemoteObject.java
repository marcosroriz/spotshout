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

package spot.rmi.registry.server;

import com.google.code.spotshout.remote.TargetMethod;
import spot.rmi.Remote;

/**
 * Defines the basic structure of a export {@link Remote} object.
 */
public abstract class UnicastRemoteObject implements Remote {

    public UnicastRemoteObject() {
    }

    /**
     * Process a receiving call/invoke method request.
     * @param method - the method meta-data and it's arguments.
     * @return the return value as a Object:
     *    - if it's a simple type it will be Wrapped and UnWrapped on the Stub
     *    - if the method has no return (void) it will return null to the
     *      protocol and won't send the answer/reply to the caller.
     */
    public abstract Object invokeRequest(TargetMethod method);
}
