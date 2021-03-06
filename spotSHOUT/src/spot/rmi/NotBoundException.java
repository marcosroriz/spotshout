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

package spot.rmi;

/**
 * An NotBoundException is thrown if there is an attempt to unbind or lookup
 * a name that is not associated in the registry.
 * @TODO Serialize this Exception.
 */
public class NotBoundException extends RuntimeException {

    public NotBoundException() {
        super();
    }

    public NotBoundException(String msg) {
        super(msg);
    }

    public NotBoundException(Class calledBy, String msg) {
        super("===NotBoundException===\n" + "Called By: " + calledBy.getName()
                + "\n" + "Msg: " + msg);
    }
}
