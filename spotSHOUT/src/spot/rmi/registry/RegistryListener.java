/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package spot.rmi.registry;

import com.google.code.spotshout.comm.RMIOperation;

/**
 *
 * @author Marcos Roriz
 */
public interface RegistryListener {

    public void actionPerfomed(RMIOperation operation);
}
