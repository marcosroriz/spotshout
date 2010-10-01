/*
 * SpotShout - A RMI library for the SunSPOT Platform.
 * Copyright (C) 2010 Marcos Paulino Roriz Junior
 *
 * This file is part of SpotShout.
 *
 * SpotShout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SpotShout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SpotShout.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.google.code.spotshout.remote;

/**
 * Contains the meta-data of a called/invoked method and it's arguments. 
 * A object of this class will be the data that is going to be sent within
 * the peers of the protocol.
 */
public final class TargetMethod {

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
    
}
