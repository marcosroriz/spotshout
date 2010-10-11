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

/**
 * This class represents the operation codes that identify the RMI protocol
 * operations.
 */
public class ProtocolOpcode {

    // RMI Opcodes
    public static final byte BIND_REQUEST = 1;
    public static final byte BIND_REPLY = 2;
    public static final byte LIST_REQUEST = 3;
    public static final byte LIST_REPLY = 4;
    public static final byte LOOKUP_REQUEST = 5;
    public static final byte LOOKUP_REPLY = 6;
    public static final byte REBIND_REQUEST = 7;
    public static final byte REBIND_REPLY = 8;
    public static final byte UNBIND_REQUEST = 9;
    public static final byte UNBIND_REPLY = 10;
    // RMI Operation Status
    public static final byte OPERATION_OK = 11;
    public static final byte OPERATION_NOK = 12;
    // RMI Exceptions
    public static final byte EXCEPTION_ALREADY_BOUND = 13;
    public static final byte EXCEPTION_NOT_BOUND = 14;
    public static final byte EXCEPTION_NULL_POINT = 15;
    public static final byte EXCEPTION_REMOTE = 16;
}
