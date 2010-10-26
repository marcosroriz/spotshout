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
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;

/**
 * LocateRegistry is the class used to obtain/create a reference to a given
 * registry (host + port).
 */
public final class LocateRegistry {

    private static Registry reg;
    
    private LocateRegistry() {}

    public static Registry createRegistry() {
        reg = new ServerRegistry();
        (new Thread((ServerRegistry)reg)).start();
        return reg;
    }

    public static Registry getRegistry() {
        Vector v = discoverSrv();
        Enumeration e = v.elements();

        while (e.hasMoreElements()) {
            System.out.println(e.nextElement());
        }
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

    private static Vector discoverSrv() {
        RadiogramConnection rCon = null;
        Datagram dg = null;
        Vector v = new Vector(2);
        try {
            String uri = RMIProperties.UNRELIABLE_PROTOCOL + "://broadcast:" + RMIProperties.DISCOVER_PORT;
            rCon = (RadiogramConnection) Connector.open(uri);
            dg = rCon.newDatagram(50);  // only sending 12 bytes of data
            dg.reset();

            dg.writeUTF(System.getProperty("IEEE_ADDRESS"));
            rCon.send(dg);

            // Reading stuff here ;)
            dg.reset();

            rCon.receive(dg);
            v.addElement(dg.readUTF());
            v.addElement(new Integer(dg.readInt()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return v;
    }
}
