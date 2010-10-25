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

package com.google.code.spotshout.lang;

import java.io.IOException;
import ksn.io.KSNSerializableInterface;
import ksn.io.ObjectInputStream;
import ksn.io.ObjectOutputStream;

/**
 * This class represent a Byte value allowing to be serializable.
 */
public class SerialByte implements KSNSerializableInterface {

    byte value;

    public SerialByte() {
        value = '0';
    }

    public SerialByte(byte v) {
        value = v;
    }

    public byte getValue() {
        return value;
    }

    public void writeObjectOnSensor(ObjectOutputStream stream) throws IOException {
        stream.writeByte(value);
    }

    public void readObjectOnSensor(ObjectInputStream stream) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        value = stream.readByte();
    }
}
