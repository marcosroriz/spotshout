/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.google.code.spotshout.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
public class Generator {

    private File jarFile;
    private File skel;
    private File stub;
    private String spotHome;
    private String pathSeparator;
    private String pkgName;
    private String iName;
    private String bindName;

    public Generator(File jar, String interfaceName, String pkg, String registryName) throws FileNotFoundException, IOException {
        jarFile = jar;
        iName = interfaceName;
        bindName = registryName;

        if (pkg.equals(".")) {
            pkgName = "";
	} else {
            if ((pkg.charAt(pkg.length() - 1)) == '.')
                pkgName = pkg.substring(0, pkg.length() - 1);
            else
                pkgName = pkg;
	}

        pathSeparator = System.getProperty("path.separator");

        String usr = System.getProperty("user.home");
        File spot = new File(usr + "/.sunspot.properties");
        FileInputStream fis = new FileInputStream(spot);
        Properties spotProp = new Properties();
        spotProp.load(fis);

        spotHome = spotProp.getProperty("sunspot.home");
 }

    public File generateSkel() throws Exception {
        // Skel generation
        SkelGenerator sk = new SkelGenerator();
        String skelSource = sk.makeClass(jarFile, pkgName, iName);
        skel = writeToFile(pkgName, iName + "_Skel.java", skelSource);
        return skel;
    }

    public File generateStub() throws Exception {
        // Stub generation
        StubGenerator sg = new StubGenerator();
        String stubSource = sg.makeClass(jarFile, pkgName, iName, bindName);
        stub = writeToFile(pkgName, iName + "_Stub.java", stubSource);
        return stub;
    }

    public boolean compile(File file) {
        return compileClass(file, spotClassPath(jarFile, spotHome, pathSeparator), spotBootClassPath(spotHome, pathSeparator));
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.out.println("Usage: $ java -jar StubSkelGenerator.jar AppJar.jar remote.pkg.name interfaceName bindName)");
            System.exit(0);
        }
        String jarName = args[0];
        String pkgName = args[1];
        String iName = args[2];
        String bindName = args[3];

        Generator gen = new Generator(new File(jarName), pkgName, iName, bindName);
        gen.compile(gen.generateSkel());
        gen.compile(gen.generateStub());

    }

    public File writeToFile(String pkg, String fileName, String source) throws IOException {
        String pkgNameToDir;
        if (pkg == "") pkgNameToDir = "";
        else pkgNameToDir = pkg.replace(".", "/") + "/";
        File dir = new File("tmp/" + pkgNameToDir);
        dir.mkdirs();
        
        File file = new File(dir, fileName);
        FileWriter fileSource = new FileWriter(file);
        fileSource.write(source);
        fileSource.flush();
        return file;
    }

    public String spotClassPath(File jar, String spotHome, String separator) {
        StringBuilder sb = new StringBuilder();
        sb.append("." + separator);
        sb.append(spotHome + "lib/multihop_common.jar" + separator);
        sb.append(spotHome + "/lib/transducer_device.jar" + separator);
        sb.append(spotHome + "/lib/ipv6lib_common.jar" + separator);
        sb.append(spotHome + "/lib/spotlib_device.jar" + separator);
        sb.append(spotHome + "/lib/spotlib_common.jar" + separator);
        sb.append(jar.getAbsolutePath() + separator);
        sb.append("lib/spotSHOUT-0.0.1.jar");

        return sb.toString();
    }

    public String spotBootClassPath(String spotHome, String separator) {
        StringBuilder sb = new StringBuilder();
        sb.append(spotHome + "/lib/squawk_device.jar");
        return sb.toString();
    }

    public boolean compileClass(File classFile, String classpath, String bootClassPath) {
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
            return true;
        } catch (InterruptedException ex) {
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createJar(String iName) {
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
            Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
