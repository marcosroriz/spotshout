/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.spotshout.nb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.util.JavaParser;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Marcos
 */
public class Generator {

    private File srcFile;
    private JavaSourceFactory jsf;
    private JavaParser jparser;
    private JavaSource javaSource;
    private JavaSource stubSource;
    private JavaSource skelSource;
    private boolean parseable;

    public Generator(FileObject fo) {
        this.srcFile = new File(fo.getPath());
        this.jsf = new JavaSourceFactory();
        this.jparser = new JavaParser(jsf);
    }

    public boolean isParseable() {
        try {
            if (javaSource == null) {
                jparser.parse(srcFile);
                for (Iterator iter = jsf.getJavaSources(); iter.hasNext();) {
                    this.javaSource = (JavaSource) iter.next();
                }
            }
            this.parseable = javaSource.isExtending(Class.forName("spot.rmi.Remote"));
            return parseable;
        } catch (Exception ex) {
            return false;
        }
    }

    public void generate() {
        this.skelSource = generateSkeleton();
        this.stubSource = generateStub();
    }

    private JavaSource generateSkeleton() {
        SkelGen sg = new SkelGen(javaSource);
        return sg.generate();
    }

    private JavaSource generateStub() {
        StubGen sg = new StubGen(javaSource);
        return sg.generate();
    }

    public boolean writeFiles(File directory, String iName) {
        try {
            FileWriter skel = new FileWriter(new File(directory, iName + "_Skel.java"));
            FileWriter stub = new FileWriter(new File(directory, iName + "_Stub.java"));

            skel.write("/** DO NOT EDIT THIS FILE - IT'S GENERATED BY SPOTSHOUT */\n\n");
            stub.write("/** DO NOT EDIT THIS FILE - IT'S GENERATED BY SPOTSHOUT */\n\n");

            skelSource.write(skel);
            stubSource.write(stub);

            skel.flush();
            stub.flush();
            
            skel.close();
            stub.close();

            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        sb.append("\nClass: ").append(javaSource.getClassName());
        sb.append("\nPackage: ").append(javaSource.getPackageName());
        sb.append("\nParseable: ").append(parseable);
        return sb.toString();
    }
}
