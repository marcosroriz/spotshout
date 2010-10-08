/*
 * SpotShout - A RMI library for the SunSPOT Platform.
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

import java.io.IOException;
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
     * Method Name
     */
    private String methodName;

    /**
     * Method Signature (According to JVM Spec).
     */
    private String methodSignature;

    /**
     * Boolean value that identify if the method has a return.
     */
    private boolean returnable;

    /**
     * Method arguments (values). If argument are primitive they
     * will be Wrapped.
     */
    private Object[] args;

    /**
     * Empty Constructor for serialization.
     */
    public TargetMethod() {
    }

    public TargetMethod(String mName, String mSig, boolean hasReturn, Object[] args) {
        setMethodName(mName);
        setMethodSignature(mSig);
        setReturnable(hasReturn);
        setArgs(args);
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        if (args != null) this.args = args;
    }

    public boolean isReturnable() {
        return returnable;
    }

    public void setReturnable(boolean returnable) {
        this.returnable = returnable;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public void setMethodSignature(String methodSignature) {
        this.methodSignature = methodSignature;
    }

    public void writeObjectOnSensor(ObjectOutputStream stream) throws IOException {
    }

    public void readObjectOnSensor(ObjectInputStream stream) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
    }
    
}
