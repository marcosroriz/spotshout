/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.spotshout.generator;

import java.io.IOException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javax.management.NotCompliantMBeanException;

/**
 * SkelGenerator will create a specific skel for the SpotSHOUT RMI library with
 * the help of Javassist a bytecode library from JBoss.
 */
public class SkelGenerator {

    private CtClass cc;
    private String wrapperPkg = "Serial";
    private String tab = "\t";

    /**
     * Creates a Skel {@link Class} file from a {@link Remote} interface.
     *
     * @param iName - the name of the remote interface
     */
    public void makeClass(String jarName, String pkgName, String iName) {
        try {
            ClassPool pool = ClassPool.getDefault();

            // Inserting user jar in classpath
            pool.insertClassPath(jarName);

            // Importing stuff we might need
            pool.importPackage(pkgName);
            pool.importPackage("java.io");
            pool.importPackage("ksn.io");
            pool.importPackage("java.rmi");
            pool.importPackage("com.google.code.spotshout.comm");
            pool.importPackage("com.google.code.spotshout.lang");
            pool.importPackage("com.google.code.spotshout.remote");

            // Making the class
            cc = pool.makeClass(pkgName + iName + "_Skel");

            // Getting and Setting(?) the Remote Interface
            CtClass interf = pool.get(pkgName + iName);
            // cc.setInterfaces(new CtClass[]{interf});

            // Setting SuperClass (Skel)
            CtClass skelGeneric = pool.get("com.google.code.spotshout.remote.Skel");
            cc.setSuperclass(skelGeneric);

            // Making empty constructor
            cc.makeClassInitializer();

            // Making serving method
            makeServingMethod(interf);

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
    private void makeServingMethod(CtClass interf) {
        try {
            // Method Signature
            StringBuffer methodText = new StringBuffer("public RMIReply service(RMIRequest request) {\n");

            // Basic data
            methodText.append(tab + "try {\n");
            methodText.append(tab + tab + "TargetMethod method = ((InvokeRequest) request).getMethod();\n");
            methodText.append(tab + tab + "KSNSerializableInterface returnValue = null;\n");
            methodText.append(tab + tab + interf.getSimpleName() + " remoteObj = (" + interf.getSimpleName() + ") remote;\n\n");

            // Finding right method to call
            CtMethod methods[] = interf.getDeclaredMethods();

            for (int i = 0; i < methods.length; i++) {
                if (i == 0) methodText.append(tab + tab + "if ");
                else methodText.append(tab + tab + "else if ");
                
                methodText.append("((method.getMethodName().equals(\"");
                methodText.append(methods[i].getName() + "\"))");
                methodText.append(" && (method.getMethodSignature().equals(\"");
                methodText.append(methods[i].getSignature() + "\"))) {\n");

                // Unwrapping parameters
                CtClass parTypes[] = methods[i].getParameterTypes();

                if (methods[i].getParameterTypes().length != 0) {
                    for (int j = 0; j < parTypes.length; j++) {
                        if (parTypes[j].isPrimitive()) methodText.append(tab + tab + tab + parTypes[j].getName());
                        else methodText.append(tab + tab + tab + parTypes[j].getName());

                        methodText.append(" p" + j + " = ((");
                        if (parTypes[j].isPrimitive()) {
                            methodText.append(wrapper(parTypes[j].getName()));
                        } else {
                            methodText.append(parTypes[j].getName());
                        }
                        methodText.append(")method.getArgs()[" + j + "]).getValue();\n");
                    }
                }

                // Invoking the target remote object
                methodText.append(tab + tab + tab);
                if (hasReturn(methods[i].getReturnType().getName())) {
                    methodText.append("returnValue = new ");
                    methodText.append(wrapper(methods[i].getReturnType().getName()) + "(");
                }
                methodText.append("remoteObj." + methods[i].getName());
                methodText.append("(");
                for (int j = 0; j < parTypes.length; j++) {
                    methodText.append("p" + j);
                    if (j < parTypes.length - 1) methodText.append(", ");
                }
                methodText.append(")");
                if (hasReturn(methods[i].getReturnType().getName())) methodText.append(")");
                methodText.append(";\n\n");

                // Returning
                if (hasReturn(methods[i].getReturnType().getName()))
                    methodText.append(tab + tab + tab + "return new InvokeReply(returnValue);\n");
                else
                    methodText.append(tab + tab + tab + "return null;\n");

                methodText.append(tab + tab + "}\n");
            }
            methodText.append(tab + tab + "return null;\n");
            
            // Exceptions --'
            methodText.append(tab + "} catch (Exception ex) {\n");
            methodText.append(tab + tab + "InvokeReply reply = new InvokeReply();\n");
            methodText.append(tab + tab + "reply.setOperationStatus(ProtocolOpcode.OPERATION_NOK);\n");
            methodText.append(tab + tab + "reply.setException(ProtocolOpcode.EXCEPTION_REMOTE);\n");
            methodText.append(tab + tab + "return reply;\n");
            methodText.append(tab + "}\n");
            methodText.append("} ");
            
            System.out.println("----------------------\n\nImprimindo: \n\n\n" + methodText.toString());
            CtMethod ctM = CtNewMethod.make(methodText.toString(), cc);
            cc.addMethod(ctM);
        } catch (Exception e) {
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
