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

package java.rmi;

/**
 * A RemoteException is thrown if there is a problem with communication during
 * an remote operation (method invoke, binding, etc).
 * @TODO Serialize this Exception.
 */
public class RemoteException extends RuntimeException {

    public RemoteException() {
        super();
    }

    public RemoteException(String msg) {
        super(msg);
    }

    public RemoteException(Class calledBy, String msg) {
        super("===Remote Exception===\n" + "Called By: " + calledBy.getName()
                + "\n" + "Msg: " + msg);
    }
}
