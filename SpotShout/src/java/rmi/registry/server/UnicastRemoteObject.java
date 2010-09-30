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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package java.rmi.registry.server;

import java.io.DataOutput;
import java.rmi.registry.Remote;

/**
 * Defines the basic structure of a export {@link Remote} object.
 */
public abstract class UnicastRemoteObject implements Remote {

    public UnicastRemoteObject() {
    }

    /**
     * Process a receiving call/invoke method that has no return.
     * @param packet - the method meta-data and it's arguments.
     */
    public abstract void callWithoutReturn(DataOutput packet);

    /**
     * Process a receiving call/invoke method that has return.
     * @param packet - the method meta-data and it's arguments.
     * @return the return value as a Object, if it's a simple type it will
     *         be Wrapped and UnWrapped on the Stub.
     */
    public abstract Object callWithReturn(DataOutput packet);

    /**
     * Get the called method return type.
     * @return the method type (int constant)
     * @TODO - put link to constant table
     */
    public abstract int getReturnType();

    /**
     * Set the called method return type. (int constant)
     * @TODO - put link to constant table
     */
    public abstract void setReturnType();
}