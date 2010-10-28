/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.spotshout.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcos Roriz
 */
public class Main {

    public static void main(String[] args) throws Exception {

        String jarName = "Client_1.0.0.jar";
        String pkgName = "lol";
        String iName = "Calculator";


        // Skel and Stub generation
        StubGenerator sg = new StubGenerator();
        String stub = sg.makeClass(jarName, pkgName, iName);
        
        SkelGenerator sk = new SkelGenerator();
        String skel = sk.makeClass(jarName, pkgName, iName);

        // Writting to file
        writeToFile(pkgName, iName + "_Stub.java", stub);
        writeToFile(pkgName, iName + "_Skel.java", skel);

       /* String usr = System.getProperty("user.home");
        String pathSeparator = System.getProperty("path.separator");
        File spotProp = new File(usr + "/.sunspot.properties");
        FileInputStream fis = new FileInputStream(spotProp);
        System.out.println(spotProp.exists());
        System.out.println(spotProp.toURI().toURL());
        Properties prop = new Properties();
        prop.load(fis);

        System.out.println(prop.getProperty("sunspot.home"));
        System.out.println(spotBootClassPath(prop.getProperty("sunspot.home"), pathSeparator));
 




        compileClass(skel, jarName, spotBootClassPath(prop.getProperty("sunspot.home"), pathSeparator));
        createJar(null, null, "Calculator");
        /*
        StubGenerator sg = new StubGenerator();
        SkelGenerator sk = new SkelGenerator();
        sg.makeClass("Client_1.0.0.jar", "lol", "Calculator");
        sk.makeClass("Client_1.0.0.jar", "lol", "Calculator");

        createJar(null, null, "Calculator");*/
    }

    public static void writeToFile(String pkg, String fileName, String source) throws IOException {
        String pkgNameToDir;
        if (pkg == "") pkgNameToDir = "";
        else pkgNameToDir = pkg.replace(".", "/") + "/";
        File dir = new File("tmp/" + pkgNameToDir);
        dir.mkdirs();
        
        File file = new File(dir, fileName);
        FileWriter fileSource = new FileWriter(file);
        fileSource.write(source);
        fileSource.flush();
    }

    public static String spotBootClassPath(String spotHome, String separator) {
        StringBuffer sb = new StringBuffer();
        sb.append(spotHome + "/multihop_common.jar" + separator);
        sb.append(spotHome + "/lib/transducer_device.jar" + separator);
        sb.append(spotHome + "/lib/spotlib_device.jar" + separator);
        sb.append(spotHome + "/lib/spotlib_common.jar" + separator);
        sb.append("lib/spotSHOUT-0.0.1.jar");

        return sb.toString();
    }

    public static void compileClass(File classFile, String classpath, String bootClassPath) {
        File wd = new File(".");

        String[] command = new String[11];
        command[0] = "cmd";
        command[1] = "/c";
        command[2] = "javac";
        command[3] = "-classpath";
        command[4] = classpath + System.getProperty("path.separator") + "tmp";
        command[5] = "-target";
        command[6] = "1.2";
        command[7] = "-source";
        command[8] = "1.3";
        command[9] = "-Xbootclasspath/a:" + bootClassPath;
        command[10] = classFile.getPath();

        for (String s : command) {
            System.out.print(s + " ");
        }
        System.out.println(classFile.getPath());
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(command, null, wd);
            BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String line = null;

            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }

            int exitVal = proc.waitFor();
            System.out.println("Exited with error code " + exitVal);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createJar(String jarName, String pkgName, String iName) {
        File wd = new File(".");

        String[] command = new String[8];
        command[0] = "cmd";
        command[1] = "/c";
        command[2] = "jar";
        command[3] = "cf";
        command[4] = iName + ".jar";
        command[5] = "-C";
        command[6] = "tmp";
        command[7] = ".";
        Process proc = null;
        try {
            proc = Runtime.getRuntime().exec(command, null, wd);
            BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String line = null;

            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }

            int exitVal = proc.waitFor();
            System.out.println("Exited with error code " + exitVal);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
/*
        if (proc != null) {
            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(proc.getOutputStream())), true);

            // Let's rock

            pkgName = pkgName.replaceAll("\\.", "/");

            out.println("/usr/bin/jar uf " + jarName + " -C /tmp " + pkgName
                    + iName + "_Stub.class");

            try {
                proc.wait(5 * 1000);
                out.close();
                proc.destroy();
            } catch (Exception e) {
                // TODO: UNDERSTAND AND FIX THIS EXCEPTION
                // e.printStackTrace();
            }
        }*/
    }
}
