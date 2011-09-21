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

package com.google.code.spotshout.comm;

/**
 * This class represent a generic RMI Reply (Protocol). Each operation will
 * inherit this class and specify it's detail, overwriting the readData method
 * with the specific data of it's protocol.
 */
public abstract class RMIReply extends RMIOperation {

    /**
     * Operation Status - OK or NOK.
     */
    protected byte operationStatus;

    /**
     * Protocol exception (if happened).
     * @see ProtocolOpcode.
     */
    protected byte exception;

    public RMIReply() {
    }

    public RMIReply(byte op) {
        operation = op;
        operationStatus = ProtocolOpcode.OPERATION_OK;
    }

    /**
     * Verify if a exception happened on the RMI operation.
     * @return true if a exception happened during the RMI Reply, false otherwise
     */
    public boolean exceptionHappened() {
        return operationStatus == ProtocolOpcode.OPERATION_NOK;
    }

    // Getters and Setters
    public byte getException() {
        return exception;
    }

    public byte getOperationStatus() {
        return operationStatus;
    }

    public void setException(byte exception) {
        this.exception = exception;
    }

    public void setOperationStatus(byte operationStatus) {
        this.operationStatus = operationStatus;
    }
}
