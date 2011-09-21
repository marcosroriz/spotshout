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
package com.google.code.spotshout.lang;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import ksn.io.KSNSerializableInterface;
import ksn.io.ObjectInputStream;
import ksn.io.ObjectOutputStream;

/**
 * This class represent a Date  value allowing to be serializable.
 */
public class SerialDate implements KSNSerializableInterface {

    Date value;

    public SerialDate() {
        value = new Date();
    }

    public SerialDate(Date v) {
        value = v;
    }

    public Date getValue() {
        return value;
    }

    public void writeObjectOnSensor(ObjectOutputStream stream) throws IOException {
        // Write order YEAR - MONTH - DAY - HOUR - MINUTE - SECOND
        Calendar c = Calendar.getInstance();
        c.setTime(value);
        stream.writeInt(c.get(Calendar.YEAR));
        stream.writeInt(c.get(Calendar.MONTH));
        stream.writeInt(c.get(Calendar.DAY_OF_MONTH));
        stream.writeInt(c.get(Calendar.HOUR));
        stream.writeInt(c.get(Calendar.MINUTE));
        stream.writeInt(c.get(Calendar.SECOND));
    }

    public void readObjectOnSensor(ObjectInputStream stream) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        // Read order YEAR - MONTH - DAY - HOUR - MINUTE - SECOND
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, stream.readInt());
        c.set(Calendar.MONTH, stream.readInt());
        c.set(Calendar.DAY_OF_MONTH, stream.readInt());
        c.set(Calendar.HOUR, stream.readInt());
        c.set(Calendar.MINUTE, stream.readInt());
        c.set(Calendar.SECOND, stream.readInt());

        value = c.getTime();
    }
}
