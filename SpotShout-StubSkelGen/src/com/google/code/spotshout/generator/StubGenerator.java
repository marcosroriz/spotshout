/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.spotshout.generator;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

/**
 * StubGenerator will create a specific stub for the SpotSHOUT RMI library with
 * the help of Javassist a bytecode library from JBoss.
 */
public class StubGenerator {

    private CtClass cc;
    private String wrapperPkg = "Serial";
    private String tab = "\t";

    /**
     * Creates a Stub {@link Class} file from a {@link Remote} interface.
     *
     * @param iName - the name of the remote interface
     */
    public void makeClass(String jarName, String pkgName, String iName) {
        try {
            ClassPool pool = ClassPool.getDefault();

            // Inserting user jar in classpath
            pool.insertClassPath(jarName);

            // Importing stuff we might need
            pool.importPackage("java.io");
            pool.importPackage("java.rmi");
            pool.importPackage("com.google.code.spotshout.comm");
            pool.importPackage("com.google.code.spotshout.lang");
            pool.importPackage("com.google.code.spotshout.remote");

            // Making the class
            cc = pool.makeClass(pkgName + iName + "_Stub");

            // Setting the interface
            CtClass interf = pool.get(pkgName + iName);
            cc.setInterfaces(new CtClass[]{interf});

            // Setting SuperClass (Stub)
            CtClass stubGeneric = pool.get("com.google.code.spotshout.remote.Stub");
            cc.setSuperclass(stubGeneric);

            // Making each method
            CtMethod methods[] = interf.getDeclaredMethods();
            for (int i = 0; i < methods.length; i++) {
                makeMethod(interf, methods[i]);
            }

            cc.writeFile();
            //cc.writeFile("tmp/");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
    private void makeMethod(CtClass interf, CtMethod m) throws NotFoundException {
        try {
            // Access Modifier
            StringBuffer methodText = new StringBuffer("public ");

            // Return type
            methodText.append(m.getReturnType().getName() + " ");

            // Method name
            methodText.append(m.getName() + "(");

            // Parameters
            CtClass parTypes[] = m.getParameterTypes();
            for (int i = 0; i < parTypes.length; i++) {
                methodText.append(parTypes[i].getName() + " p" + i);
                if (i < parTypes.length - 1) {
                    methodText.append(", ");
                }
            }
            methodText.append(") {\n");

            // Body -----------------------------------------------------------
            methodText.append(tab + "try {\n");

            // Let's check if the argument number is different then zero,
            // if it's equal we don't need the overhead to serialize the
            // arguments.
            methodText.append(tab + tab + "Serializable[] args = null;\n");
            if (m.getParameterTypes().length != 0) {
                // Making vector of arguments (objects)
                methodText.append(tab + tab + "args = new Serializable ["
                        + parTypes.length + "];\n");

                for (int i = 0; i < parTypes.length; i++) {
                    methodText.append(tab + tab + "args[" + i + "] = ");
                    methodText.append("new " + wrapper(parTypes[i].getName()) + "(");
                    methodText.append("p" + i + ");\n");
                }
            }

            // Creating TargetMethod
            methodText.append("\n" + tab + tab + "TargetMethod m ");
            methodText.append("= new TargetMethod(");
            methodText.append("\"" + m.getName() + "\"" + ", \"" + m.getSignature() + "\", "
                    + hasReturn(m.getReturnType().getName()) + ", args);\n");


           // Creating InvokeRequest
            methodText.append(tab + tab + "InvokeRequest invReq = new InvokeRequest(m);\n");

            // Creating Connection
            methodText.append(tab + tab + "RMIUnicastConnection conn ");
            methodText.append("= RMIUnicastConnection.makeClientConnection(");
            methodText.append("getTargetAddr(), getTargetPort());\n");

            // Writting Request
            methodText.append(tab + tab + "conn.writeRequest(invReq);\n\n");

            // Listen to reply and return ONLY if method has return
            if (hasReturn(m.getReturnType().getName())) {
                // Listen to reply
                methodText.append(tab + tab + "InvokeReply invReply ");
                methodText.append("= (InvokeReply) conn.readReply();\n");

                // Close connection
                methodText.append(tab + tab + "conn.close();\n");

                // Return value (Unwrapping)
                methodText.append("\n" + tab + tab + "return ((");
                if (m.getReturnType().isPrimitive()) {
                    methodText.append(wrapper(m.getReturnType().getName()));
                } else {
                    methodText.append(m.getReturnType().getName());
                }
                methodText.append(")invReply.getReturnValue()).getValue();\n");
            } else {
                // Close connection
                methodText.append(tab + tab + "conn.close();\n");
            }

            // Exceptions --'
            methodText.append(tab + "} catch (java.io.IOException ex) {\n");
            methodText.append(tab + tab + "throw new java.rmi.RemoteException(\"Remote Exception on ");
            methodText.append(m.getName() + "()\");");
            methodText.append(tab + "\n" + tab + "}\n}\n\n");

            System.out.println("----------------------\n\nImprimindo: \n" + methodText.toString());
            CtMethod ctM = CtNewMethod.make(methodText.toString(), cc);
            cc.addMethod(ctM);
        } catch (CannotCompileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
        }

        return wrapperPkg + objType;
    }

    private boolean hasReturn(String returnType) {
        return !returnType.equals("void");
    }
}
