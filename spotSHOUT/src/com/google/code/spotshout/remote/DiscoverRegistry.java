/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.spotshout.remote;

import com.google.code.spotshout.RMIProperties;
import com.google.code.spotshout.comm.RMIReply;
import com.google.code.spotshout.comm.RMIRequest;
import com.google.code.spotshout.comm.Server;
import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import java.io.IOException;
import javax.microedition.io.Connector;

/**
 *
 * @author Marcos Roriz
 */
public class DiscoverRegistry extends Server {

    private String srvAddr;

    public DiscoverRegistry() {
        setPort(RMIProperties.DISCOVER_PORT);
        srvAddr = System.getProperty("IEEE_ADDRESS");
    }

    public RMIReply service(RMIRequest request) {
        return null;
    }

    /**
     * Listen for discovery requests (unreliable) connections and send the Server Addr.
     */
    public void run() {
        try {
            String uri = RMIProperties.UNRELIABLE_PROTOCOL + "://" + getPort();
            RadiogramConnection rCon = (RadiogramConnection) Connector.open(uri);
            Radiogram dg = (Radiogram) rCon.newDatagram(rCon.getMaximumLength());

            while (true) {
                dg.reset();
                try {
                    // Receive Unreliable Request
                    rCon.receive(dg);
                    String clientAddr = dg.readUTF();
                    System.out.println("readed this bitch" + clientAddr);

                    

                    // Send server address and port
                    dg.reset();
                    dg.writeUTF(srvAddr);
                    dg.writeInt(getPort());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    rCon.close();
                    rCon = (RadiogramConnection) Connector.open(uri);
                    dg = (Radiogram) rCon.newDatagram(rCon.getMaximumLength());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
