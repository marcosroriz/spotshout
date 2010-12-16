/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.spotshout.generator;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * SkelGenerator will create a specific skel for the SpotSHOUT RMI library with
 * the help of Java Reflection.
 */
public class SkelGenerator {

    private String wrapperPkg = "Serial";
    private String tab = "\t";

    public String makeClass(File jarFile, String pkgName, String iName) throws Exception {
        File spotJar = new File("lib/spotSHOUT-0.0.1.jar");
        URLClassLoader urlLoader = new URLClassLoader(
                new URL[]{jarFile.toURI().toURL(), spotJar.toURI().toURL()});

        Class remoteInterface = urlLoader.loadClass(pkgName + "." + iName);

        StringBuilder classText = new StringBuilder();
        classText.append(header(remoteInterface));
        classText.append(body(remoteInterface));
        
        return classText.toString();
    }

    private String header(Class remoteInterface) {
        StringBuilder sb = new StringBuilder();

        if (remoteInterface.getPackage().getName() != "")
            sb.append("package " + remoteInterface.getPackage().getName() + ";\n\n");

        sb.append("import java.io.*;\n");
        sb.append("import ksn.io.*;\n");
        sb.append("import spot.rmi.*;\n");
        sb.append("import spot.rmi.registry.*;\n");
        sb.append("import com.google.code.spotshout.*;\n");
        sb.append("import com.google.code.spotshout.comm.*;\n");
        sb.append("import com.google.code.spotshout.lang.*;\n");
        sb.append("import com.google.code.spotshout.remote.*;\n\n");

        return sb.toString();
    }

    private String body(Class remoteInterface) {
        StringBuffer sb = new StringBuffer();

        sb.append("public class " + remoteInterface.getSimpleName() + "_Skel implements Skel {\n\n");
        sb.append("\n" + tab + "private " + remoteInterface.getSimpleName() + " remote;\n\n");
        sb.append(emptyConstructor(remoteInterface));
        sb.append(serviceMethod(remoteInterface));
        sb.append(setremote(remoteInterface));
        sb.append("\n}");
        return sb.toString();
    }

    private String emptyConstructor(Class remoteInteface) {
        StringBuilder sb = new StringBuilder();
        sb.append(tab + "public " + remoteInteface.getSimpleName() + "_Skel() {}\n\n");
        return sb.toString();
    }
    
    private String setremote(Class remoteInterface) {
        StringBuilder sb = new StringBuilder();

        // Method Signature
        sb.append(tab + "public void setRemote(Remote remote) {\n");

        // Basic data
        sb.append(tab + tab + "this.remote = (");
        sb.append(remoteInterface.getSimpleName() + ") remote;\n");

        sb.append(tab + "}\n");

        return sb.toString();
    }

    private String serviceMethod(Class remoteInterface) {
        StringBuilder sb = new StringBuilder();

        // Method Signature
        sb.append(tab + "public RMIReply service(RMIRequest request) {\n");

        // Basic data
        sb.append(tab + tab + "try {\n");
        sb.append(tab + tab + tab + "TargetMethod method = ((InvokeRequest) request).getMethod();\n");
        sb.append(tab + tab + tab + "KSNSerializableInterface returnValue = null;\n");
        sb.append(tab + tab + tab + remoteInterface.getSimpleName() + " remoteObj = (" + remoteInterface.getSimpleName() + ") remote;\n\n");

        // Finding right method to call
        Method methods[] = sort(remoteInterface.getDeclaredMethods());

        for (int i = 0; i < methods.length; i++) {
                if (i == 0) sb.append(tab + tab + tab + "if ");
                else sb.append(tab + tab + tab + "else if ");

                sb.append("(method.getMethodNumber() == " + i +") {\n");

                // Unwrapping parameters
                Class[] parTypes = methods[i].getParameterTypes();

                if (methods[i].getParameterTypes().length != 0) {
                    for (int j = 0; j < parTypes.length; j++) {
                        if (parTypes[j].isPrimitive()) sb.append(tab + tab + tab + tab + parTypes[j].getName());
                        else sb.append(tab + tab + tab + tab + parTypes[j].getName());

                        sb.append(" p" + j + " = ((");
                        if (parTypes[j].isPrimitive()) sb.append(wrapper(parTypes[j].getName()));
                        else sb.append(parTypes[j].getName());
                        
                        sb.append(")method.getArgs()[" + j + "]).getValue();\n");
                    }
                }

                // Invoking the target remote object
                sb.append(tab + tab + tab + tab);
                if (hasReturn(methods[i].getReturnType().getName())) {
                    sb.append("returnValue = new ");
                    sb.append(wrapper(methods[i].getReturnType().getName()) + "(");
                }
                sb.append("remoteObj." + methods[i].getName());
                sb.append("(");
                for (int j = 0; j < parTypes.length; j++) {
                    sb.append("p" + j);
                    if (j < parTypes.length - 1) sb.append(", ");
                }
                sb.append(")");
                if (hasReturn(methods[i].getReturnType().getName())) sb.append(")");
                sb.append(";\n\n");

            // Returning
            if (hasReturn(methods[i].getReturnType().getName()))
                sb.append(tab + tab + tab + tab + "return new InvokeReply(returnValue);\n");
            else
                sb.append(tab + tab + tab + tab + "return null;\n");

            sb.append(tab + tab + tab + "}\n");
        }
        sb.append(tab + tab + tab + "return null;\n");

        // Exceptions --'
        sb.append(tab + tab + "} catch (Exception ex) {\n");
        sb.append(tab + tab + tab + "InvokeReply reply = new InvokeReply();\n");
        sb.append(tab + tab + tab + "reply.setOperationStatus(ProtocolOpcode.OPERATION_NOK);\n");
        sb.append(tab + tab + tab + "reply.setException(ProtocolOpcode.EXCEPTION_REMOTE);\n");
        sb.append(tab + tab + tab + "return reply;\n");
        sb.append(tab + tab + "}\n");
        sb.append(tab + "} ");

        return sb.toString();
    }

    private Method[] sort(Method[] methods) {
        int leastGuy = 0;
        String leastMethod = methods[0].toGenericString();
        String currentMethod = leastMethod;
        for (int i = 0; i < methods.length; i++) {
            leastMethod = methods[i].toGenericString();
            leastGuy = i;
            for (int j = i; j < methods.length; j++) {
                currentMethod = methods[j].toGenericString();
                if (leastMethod.compareTo(currentMethod) > 0) {
                    leastMethod = currentMethod;
                    leastGuy = j;
                }
            }
            Method aux = methods[i];
            methods[i] = methods[leastGuy];
            methods[leastGuy] = aux;
        }
        return methods;
    }

    /**
     * Gets the name of a wrapper of a given type.
     *
     * @param keyword - the name of the primitive type
     * @return the wrapper name
     */
    private String wrapper(String keyword) {
        String objType = "Object";

        if (keyword.equals("boolean")) {
            objType = "Boolean";
        } else if (keyword.equals("byte")) {
            objType = "Byte";
        } else if (keyword.equals("char")) {
            objType = "Char";
        } else if (keyword.equals("double")) {
            objType = "Double";
        } else if (keyword.equals("int")) {
            objType = "Int";
        } else if (keyword.equals("float")) {
            objType = "Float";
        } else if (keyword.equals("long")) {
            objType = "Long";
        } else if (keyword.equals("short")) {
            objType = "Short";
        } else if ((keyword.equals("java.lang.String")) || (keyword.equals("String"))) {
            objType = "String";
        }

        return wrapperPkg + objType;
    }

    private boolean hasReturn(String returnType) {
        return !returnType.equals("void");
    }
}
