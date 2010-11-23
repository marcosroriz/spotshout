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

package com.google.code.spotshout.remote;

import com.google.code.spotshout.RMIProperties;
import java.io.IOException;
import java.io.Serializable;
import ksn.io.KSNSerializableInterface;
import ksn.io.ObjectInputStream;
import ksn.io.ObjectOutputStream;

/**
 * Contains the meta-data of a called/invoked method and it's arguments. 
 * A object of this class will be the data that is going to be sent within
 * the peers of the protocol.
 */
public final class TargetMethod implements KSNSerializableInterface {

    /**
     * The method ordered number (generated in stubs/skel).
     */
    private int methodNumber;

    /**
     * Number of arguments
     */
    private int numberArgs;

    /**
     * Method arguments (values). If argument are primitive they
     * will be Wrapped.
     */
    private Serializable[] args;

    /**
     * Empty Constructor for serialization.
     */
    public TargetMethod() {
    }

    public TargetMethod(int mNumber, Serializable[] argList) {
        methodNumber = mNumber;
        if (argList != null) {
            args = argList;
            numberArgs = args.length;
        } else {
            argList = null;
            numberArgs = 0;
        }
    }

    public void writeObjectOnSensor(ObjectOutputStream stream) throws IOException {
        RMIProperties.log("TargetMethod Writting Started");
        stream.writeInt(methodNumber);
        stream.writeInt(numberArgs);

        for (int i = 0; i < numberArgs; i++) {
            stream.writeObject(args[i]);
        }
        
        RMIProperties.log("TargetMethod Writting Finished");
    }

    public void readObjectOnSensor(ObjectInputStream stream) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        RMIProperties.log("TargetMethod Reading Started");

        methodNumber = stream.readInt();
        numberArgs = stream.readInt();

        args = new Serializable[numberArgs];

        for (int i = 0; i < numberArgs; i++) {
            args[i] = (Serializable) stream.readObject();
        }
        RMIProperties.log("TargetMethod Reading Finished");
    }

    public Object[] getArgs() {
        return args;
    }

    public int getArgsLenght() {
        return numberArgs;
    }

    public int getMethodNumber() {
        return methodNumber;
    }
}
