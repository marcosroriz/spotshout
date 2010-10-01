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

package com.google.code.spotshout.comm;

/**
 * This class represents constants values that are attributed to identify
 * simple data types (and the Object type) that are used in the protocol.
 */
public final class TypeConstants {

    public static final int BOOLEAN = 1;
    public static final int BYTE    = 2;
    public static final int CHAR    = 4;
    public static final int DOUBLE  = 8;
    public static final int FLOAT   = 16;
    public static final int INT     = 32;
    public static final int LONG    = 64;
    public static final int OBJECT  = 128;
    public static final int SHORT   = 256;
    
}
