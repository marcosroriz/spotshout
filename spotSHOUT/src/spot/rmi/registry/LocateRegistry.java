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

package spot.rmi.registry;

import com.google.code.spotshout.RMIProperties;
import com.google.code.spotshout.comm.ProtocolOpcode;
import com.google.code.spotshout.remote.ServerRegistry;
import com.google.code.spotshout.remote.SpotRegistry;
import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import java.io.IOException;
import spot.rmi.RemoteException;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;

/**
 * LocateRegistry is the class used to obtain/create a reference to a given
 * registry (host + port).
 */
public class LocateRegistry {

    private static Registry reg;
    
    protected  LocateRegistry() {}

    public static Registry createRegistry() {
        if (System.getProperty("IEEE_ADDRESS") == null) {
            reg = new ServerRegistry();
            (new Thread((ServerRegistry) reg)).start();
        }
        return reg;
    }

    public static Registry getRegistry() throws IOException {
        System.out.println("INIT DISCOVERY");
        String addr = discoverSrv();
        System.out.println("END DISCOVERY");
        System.out.println("Endereco dessa porra e" + addr);
        
        reg = new SpotRegistry(addr, RMIProperties.RMI_SERVER_PORT);
        (new Thread((SpotRegistry) reg)).start();
        return reg;
    }

    public static Registry getRegistry(int port) throws RemoteException {
        if (reg == null) throw new RemoteException("No registry running here");
        else return reg;
    }

    public static Registry getRegistry(String host) {
        reg = new SpotRegistry(host, RMIProperties.RMI_SERVER_PORT);
        return reg;
    }

    public static Registry getRegistry(String host, int port) {
        reg = new SpotRegistry(host, RMIProperties.RMI_SERVER_PORT);
        return reg;
    }

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

                System.out.println("making another broadcast request to " + uri);
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

                System.out.println("waitting for host server addr reply at " + uri);
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
        System.out.println("NUMBER RETRY ON DISCOVERY : " + numberTry);
        return serverAddr;
    }
}
