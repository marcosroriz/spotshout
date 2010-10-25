/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.spotshout.remote;

import com.google.code.spotshout.comm.InvokeReply;
import com.google.code.spotshout.comm.InvokeRequest;
import com.google.code.spotshout.comm.RMIUnicastConnection;
import com.google.code.spotshout.lang.SerialFloat;
import com.google.code.spotshout.lang.SerialInt;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 *
 * @author Marcos Roriz
 */
public class SampleStub extends Stub implements SampleRemIF {

    public int add(int x, int y) throws RemoteException {
        try {
            Serializable[] args = new Serializable[2];
            args[0] = new SerialInt(x);
            args[1] = new SerialInt(x);
            TargetMethod m = new TargetMethod("add", "(II)I", args);
            InvokeRequest invReq = new InvokeRequest(m);

            RMIUnicastConnection conn = RMIUnicastConnection.makeClientConnection(getTargetAddr(), getTargetPort());
            conn.writeRequest(invReq);

            InvokeReply invReply = (InvokeReply) conn.readReply();

            if (invReply.exceptionHappened()) throw new RemoteException();
            
            conn.close();

            return ((SerialInt) invReply.getReturnValue()).getValue();
        } catch (IOException ex) {
            throw new RemoteException("LOL");
        }
    }

    public void run() {
        System.out.println("LOL");
    }

    public long addPrecision(int x, float y) {
        Object[] args = new Object[2];
        args[0] = new SerialInt(x);
        args[1] = new SerialFloat(y);


        
        return 1;
    }

    public void divide(int x, int y) {
    }
}
