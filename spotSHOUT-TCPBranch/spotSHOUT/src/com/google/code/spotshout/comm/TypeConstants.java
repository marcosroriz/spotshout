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

package com.google.code.spotshout.comm;

/**
 * This class represents constants values that are attributed to identify
 * simple data types (and the Object type) that are used in the protocol.
 */
public final class TypeConstants {

    public static final int BOOLEAN = 1;
    public static final int BYTE = 2;
    public static final int CHAR = 4;
    public static final int DOUBLE = 8;
    public static final int FLOAT = 16;
    public static final int INT = 32;
    public static final int LONG = 64;
    public static final int OBJECT = 128;
    public static final int SHORT = 256;
}
