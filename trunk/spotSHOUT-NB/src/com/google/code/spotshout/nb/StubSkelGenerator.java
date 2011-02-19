/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.spotshout.nb;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Set;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;

public final class StubSkelGenerator implements ActionListener {

    private final DataObject context;

    public StubSkelGenerator(DataObject context) {
        this.context = context;
    }

    public void actionPerformed(ActionEvent ev) {
        try {
            StringBuffer sb = new StringBuffer();

            // Folder of the File
            FileObject abstractFolder = context.getFolder().getPrimaryFile();
            File folder = new File(abstractFolder.getPath());

            // Selected Files (we didn't enable multi-selection on our plugin)
            Set<FileObject> files = context.files();

            // Let's generate Skeleton adn Stubs for each selected file.
            for (FileObject file : files) {
                Generator gen = new Generator(file);

                // Check if file implements spot.rmi.Registry
                if (!gen.isParseable()) {
                    String msg = "The file: " + file.getName() + " does not implement spot.rmi.Remote.";
                    showMessage(msg, NotifyDescriptor.ERROR_MESSAGE);
                    continue;
                }

                // Generate the Skeleton and Stub Source Files
                gen.generate();

                if (!gen.writeFiles(folder, file.getName())) {
                    String msg = "There was an error on generating the Skeleton and Stub source files.\n"
                               + "Check if your username has permission to write to the source folder.";
                    showMessage(msg, NotifyDescriptor.ERROR_MESSAGE);
                    continue;
                }

                String msg = "Both files were generated successfully!";
                showMessage(msg, NotifyDescriptor.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showMessage(String msgContent, int msgType) {
        NotifyDescriptor nd = new NotifyDescriptor.Message(msgContent, msgType);
        DialogDisplayer.getDefault().notify(nd);
    }
}
