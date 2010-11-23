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
 * StubGenerator will create a specific stub for the SpotSHOUT RMI library with
 * Java Reflection.
 */
public class StubGenerator {

    private String wrapperPkg = "Serial";
    private String tab = "\t";

    public String makeClass(File jarFile, String pkgName, String iName, String bindName) throws Exception {
        File spotJar = new File("lib/spotSHOUT-0.0.1.jar");
        URLClassLoader urlLoader = new URLClassLoader(
                new URL[]{jarFile.toURI().toURL(), spotJar.toURI().toURL()});

        Class remoteInterface = urlLoader.loadClass(pkgName + "." + iName);

        StringBuilder classText = new StringBuilder();
        classText.append(header(remoteInterface));
        classText.append(body(remoteInterface, bindName));

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

    private String body(Class remoteInterface, String bindName) {
        StringBuilder sb = new StringBuilder();

        sb.append("public class " + remoteInterface.getSimpleName() + "_Stub ");
        sb.append("extends Stub " + "implements " + remoteInterface.getSimpleName() + " {\n\n");
        sb.append(emptyConstructor(remoteInterface));

        Method[] methods = sort(remoteInterface.getDeclaredMethods());
        for (int i = 0; i < methods.length; i++)
            sb.append(makeMethod(methods[i], i, bindName));

        sb.append("\n}");
        return sb.toString();
    }

    private String emptyConstructor(Class remoteInteface) {
        StringBuilder sb = new StringBuilder();
        sb.append(tab + "public " + remoteInteface.getSimpleName() + "_Stub() {}\n\n");
        return sb.toString();
    }

    /**
     * Creates a stub method for the stub class, this method will wrap the
     * arguments and then create a InvokeRequest and send on to a
     * RMIUnicastConnection.
     *
     * @param interf - the class interface
     * @param m - the method to create the stub
     * @throws NotFoundException - if the method cannot be loaded/founded
     */
    private String makeMethod(Method m, int methodNumber, String bindName) {
        StringBuilder methodText = new StringBuilder();
        try {
            // Access Modifier
            methodText.append(tab + "public ");

            // Return type
            methodText.append(m.getReturnType().getName() + " ");

            // Method name
            methodText.append(m.getName() + "(");

            // Parameters
            Class parTypes[] = m.getParameterTypes();
            for (int i = 0; i < parTypes.length; i++) {
                methodText.append(parTypes[i].getName() + " p" + i);
                if (i < parTypes.length - 1) {
                    methodText.append(", ");
                }
            }
            methodText.append(") throws RemoteException {\n");

            // Body -----------------------------------------------------------
            methodText.append(tab + tab + "try {\n");

            // Let's check if the argument number is different then zero,
            // if it's equal we don't need the overhead to serialize the
            // arguments.
            methodText.append(tab + tab + tab + "Serializable[] args = null;\n");
            if (m.getParameterTypes().length != 0) {
                // Making vector of arguments (objects)
                methodText.append(tab + tab + tab + "args = new Serializable ["
                        + parTypes.length + "];\n");

                for (int i = 0; i < parTypes.length; i++) {
                    methodText.append(tab + tab + tab + "args[" + i + "] = ");
                    methodText.append("new " + wrapper(parTypes[i].getName()) + "(");
                    methodText.append("p" + i + ");\n");
                }
            }

            // Creating TargetMethod
            methodText.append("\n" + tab + tab + tab + "TargetMethod m ");
            methodText.append("= new TargetMethod(");
            methodText.append(methodNumber + ", args);\n");

           // Creating InvokeRequest
            methodText.append(tab + tab + tab + "InvokeRequest invReq = new InvokeRequest(");
            methodText.append("\"" + bindName + "\", m);\n");

            // Creating Connection
            methodText.append(tab + tab + tab + "RMIUnicastConnection conn ");
            methodText.append("= RMIUnicastConnection.makeClientConnection(");
            methodText.append("ProtocolOpcode.INVOKE_REQUEST, getTargetAddr(), RMIProperties.RMI_SPOT_PORT);\n");

            // Writting Request
            methodText.append(tab + tab + tab + "conn.writeRequest(invReq);\n\n");

            // Listen to reply and return ONLY if method has return
            if (hasReturn(m.getReturnType().getName())) {
                // Listen to reply
                methodText.append(tab + tab + tab + "InvokeReply invReply ");
                methodText.append("= (InvokeReply) conn.readReply();\n");

                // Close connection
                methodText.append(tab + tab + tab + "conn.close();\n");

                // Checking for exception
                methodText.append(tab + tab + tab + "if (invReply.exceptionHappened())");
                methodText.append(" throw new RemoteException();\n\n");

                // Return value (Unwrapping)
                methodText.append("\n" + tab + tab + tab + "return ((");
                if (m.getReturnType().isPrimitive())
                    methodText.append(wrapper(m.getReturnType().getName()));
                else if (m.getReturnType().getName().equals("java.lang.String"))
                    methodText.append("SerialString");
                else
                    methodText.append(m.getReturnType().getName());
                methodText.append(")invReply.getReturnValue()).getValue();\n");
            } else {
                // Close connection
                methodText.append(tab + tab + tab + "conn.close();\n");
            }

            // Exceptions --'
            methodText.append(tab + tab + "} catch (IOException ex) {\n");
            methodText.append(tab + tab + tab + "throw new RemoteException(\"Remote Exception on ");
            methodText.append(m.getName() + "()\");");
            methodText.append(tab + tab + "\n" + tab + tab + "}\n " + tab + "}\n");
        } catch (Exception ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
        methodText.append("\n");
        return methodText.toString();
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
