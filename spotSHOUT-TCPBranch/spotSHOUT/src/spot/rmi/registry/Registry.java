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

package spot.rmi.registry;

import spot.rmi.AlreadyBoundException;
import spot.rmi.NotBoundException;
import spot.rmi.Remote;
import spot.rmi.RemoteException;

/**
 * Registry is the heart of the RMI, it's this class that will make the basic
 * operations of it: bind,list,lookup,rebind and unbind remote references to
 * a name server.
 */
public interface Registry {

    /**
     * Binds a remote reference to a given name.
     * @param name - name choosed to represent the remote reference.
     * @param remoteFullName - the remote interface full name (including package)
     * @param obj - the remote reference.
     * @throws AlreadyBoundException - if the given name is already bound in
     *                                 the registry.
     * @throws NullPointerException - if the remote object or name is null.
     * @throws RemoteException - if there is a connection problem during
     *                           the operation.
     */
    public void bind(String name, String remoteFullName, Remote obj)
            throws AlreadyBoundException, NullPointerException, RemoteException;

    /**
     * List the names that are bounded on the registry.
     * @return a list of names bounded.
     * @throws RemoteException - if there is a connection problem during
     *                           the operation.
     */
    public String[] list() throws RemoteException;

    /**
     * Returns a remote reference of the given name.
     * @param name - the name of the remote reference.
     * @return a reference to the remote object.
     * @throws NotBoundException - if the give name is not binded in the registry
     * @throws NullPointerException - if name is null.
     * @throws RemoteException - if there is a connection problem during
     *                           the operation.
     */
    public Remote lookup(String name) throws NotBoundException,
            NullPointerException, RemoteException;

    /**
     * Replaces the name in the registry to the given object. The difference
     * between this operation and <i>bind></i> is that this one will not throw
     * a {@link AlreadyBoundException} if there is already the given name on
     * the registry.
     * @param name - name choosed to represent the remote reference.
     * @param remoteFullName - the remote interface full name (including package)
     * @param obj - the remote reference.
     * @throws NullPointerException - if the remote object or name is null.
     * @throws RemoteException - if there is a connection problem during
     *                           the operation.
     */
    public void rebind(String name, String remoteFullName, Remote obj)
            throws NullPointerException, RemoteException;

    /**
     * Removes the binding of <i>name</b> on the registry.
     * @param name - name of the remote reference.
     * @throws NotBoundException - if the give name is not binded in the registry
     * @throws NullPointerException - if name is null.
     * @throws RemoteException - if there is a connection problem during
     *                           the operation.
     */
    public void unbind(String name) throws NotBoundException,
            NullPointerException, RemoteException;
}
