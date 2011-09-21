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

package spot.rmi.registry;

import com.google.code.spotshout.RMIProperties;
import com.google.code.spotshout.comm.ProtocolOpcode;
import com.google.code.spotshout.remote.HostRegistry;
import com.google.code.spotshout.remote.SpotRegistry;
import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;

/**
 * LocateRegistry is the class used to obtain/create a reference to a given
 * registry (host + port).
 */
public class LocateRegistry {

    private static Registry reg;
    private static Thread regThread;
    
    protected  LocateRegistry() {}

    /**
     * Creates a Registry (Default to Port 245)
     * @return
     */
    public static Registry createRegistry() {
        if (reg == null) {
            reg = new HostRegistry();
            regThread = new Thread((HostRegistry) reg);
            regThread.start();
        }
        return reg;
    }

    /**
     * Discover and get a Registry (by broadcast)
     * @return a registry
     * @throws IOException
     */
    public static Registry getRegistry() throws IOException {
        if (reg == null) {
            String addr = discoverSrv();
            createSpotRegistry(addr, RMIProperties.RMI_SERVER_PORT);
        }
        return reg;
    }

    /**
     * Get a Registry in a specific location
     * @param host - registry addr (MAC)
     * @return registry
     */
    public static Registry getRegistry(String host) {
        if (reg == null) {
            createSpotRegistry(host, RMIProperties.RMI_SERVER_PORT);
        }
        return reg;
    }

    /**
     * Get a Registry in a specific location and port
     * @param host - registry addr (MAC)
     * @param port - port number
     * @return registry
     */
    public static Registry getRegistry(String host, int port) {
        if (reg == null) {
            createSpotRegistry(host, port);
        }
        return reg;
    }

    private static Registry createSpotRegistry(String host, int port) {
        reg = new SpotRegistry(host, port);
        regThread = new Thread((SpotRegistry) reg);
        regThread.start();

        return reg;
    }

    /**
     * Discover the Name Server ADDR
     * @return the name server addr
     * @throws IOException
     */
    private static String discoverSrv() throws IOException {
        RadiogramConnection rCon = null;
        Datagram dg = null;
        String serverAddr = null;
        int numberTry = 0;
        
        while (numberTry < RMIProperties.NUMBER_OF_TRIES) {
            try {
                String uri = RMIProperties.UNRELIABLE_PROTOCOL + "://broadcast:" + RMIProperties.RMI_SERVER_PORT;
                rCon = (RadiogramConnection) Connector.open(uri, Connector.READ_WRITE, true);
                
                rCon.setTimeout(RMIProperties.TIMEOUT);
                dg = rCon.newDatagram(rCon.getMaximumLength());
                dg.reset();

                dg.write(ProtocolOpcode.HOST_ADDR_REQUEST);
                rCon.send(dg);

                // Closing the connection
                dg.reset();
                rCon.close();

                // Waiting for answer
                uri = RMIProperties.UNRELIABLE_PROTOCOL + "://:" + RMIProperties.UNRELIABLE_DISCOVER_CLIENT_PORT;
                rCon = (RadiogramConnection) Connector.open(uri);
                dg = (Radiogram) rCon.newDatagram(rCon.getMaximumLength());
                rCon.setTimeout(RMIProperties.TIMEOUT);

                // Reading protocol answer
                rCon.receive(dg);
                serverAddr = dg.getAddress();

                // Closing Unreliable connection
                rCon.close();
                dg.reset();
                break;
            } catch (IOException ex) {
                numberTry++;
                dg.reset();
                rCon.close();
                if (numberTry == RMIProperties.NUMBER_OF_TRIES) throw new IOException();
            }
        }
        return serverAddr;
    }
}
