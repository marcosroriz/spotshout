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

package java.rmi.registry;

import com.google.code.spotshout.RMIProperties;
import com.google.code.spotshout.remote.ServerRegistry;
import com.google.code.spotshout.remote.SpotRegistry;
import java.rmi.RemoteException;

/**
 * LocateRegistry is the class used to obtain/create a reference to a given
 * registry (host + port).
 */
public final class LocateRegistry {

    private static Registry reg;
    
    private LocateRegistry() {}

    public static Registry createRegistry() {
        reg = new ServerRegistry();
        return reg;
    }

    public static Registry getRegistry() {
        //TODO discover :/
        return null;
    }

    public static Registry getRegistry(int port) throws RemoteException {
        if (reg == null) throw new RemoteException("No registry running here");
        else return reg;
    }

    public static Registry getRegistry(String host) {
        reg = new SpotRegistry(host, RMIProperties.SERVER_PORT);
        return reg;
    }

    public static Registry getRegistry(String host, int port) {
        reg = new SpotRegistry(host, RMIProperties.SERVER_PORT);
        return reg;
    }
}
