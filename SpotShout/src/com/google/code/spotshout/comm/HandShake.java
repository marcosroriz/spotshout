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

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;

/**
 * This class represents the handshake to make a reliable connection between
 * two points on PAN. It uses datagram (non reliable) data to announce to the
 * other point the data necessary to establish the connection.
 */
public class HandShake {

    public Connection connect(byte operation,  String addr, int targetPort,
            ) throws IOException {
        RadiogramConnection rCon = (RadiogramConnection)
                Connector.open("radiogram://" + addr + ":" + port,
                Connector.READ_WRITE, true);
        Datagram dg = rCon.newDatagram(150);
        dg.reset();

        dg.write(operation);
        dg.write());
        rCon.send(dg);


        // Closing the connection
        dg.reset();
        rCon.close();
        return null;
    }
}
