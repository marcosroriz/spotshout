/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.code.spotshout.nb;

import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaQName;
import org.apache.ws.jaxme.js.JavaQNameImpl;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;

/**
 * SkelGenerator will create a specific skel for the SpotSHOUT RMI library with
 * the help of Java Reflection.
 */
public class SkelGen {

    private JavaSource remoteInterfaceSource;
    private JavaSource javaSource;
    private String pkgName;
    private String iName;
    
    public SkelGen(JavaSource remoteInterface) {
        this.remoteInterfaceSource = remoteInterface;
        this.pkgName = remoteInterfaceSource.getPackageName();
        this.iName = remoteInterfaceSource.getClassName();
        
        JavaSourceFactory factory = new JavaSourceFactory();
        JavaQName className = JavaQNameImpl.getInstance(pkgName, iName + "_Skel");
        this.javaSource = factory.newJavaSource(className, "public");
    }

    public JavaSource generate() {
        generateHeaders();
        generateEmptyConstructor();
        generateBody();
        return javaSource;
    }

    private void generateHeaders() {
        // User Needed Imports
        for (JavaQName name : remoteInterfaceSource.getImports()) {
            javaSource.addImport(name);
        }

        // Library Needed Imports
        javaSource.addImport(JavaQNameImpl.getInstance("java.io", "*"));
        javaSource.addImport(JavaQNameImpl.getInstance("ksn.io", "*"));
        javaSource.addImport(JavaQNameImpl.getInstance("spot.rmi", "*"));
        javaSource.addImport(JavaQNameImpl.getInstance("spot.rmi.registry", "*"));
        javaSource.addImport(JavaQNameImpl.getInstance("com.google.code.spotshout", "*"));
        javaSource.addImport(JavaQNameImpl.getInstance("com.google.code.spotshout.comm", "*"));
        javaSource.addImport(JavaQNameImpl.getInstance("com.google.code.spotshout.lang", "*"));
        javaSource.addImport(JavaQNameImpl.getInstance("com.google.code.spotshout.remote", "*"));

        // Implements Skeleton Interface
        javaSource.addImplements(JavaQNameImpl.getInstance("com.google.code.spotshout.remote", "Skel"));
    }

    private void generateEmptyConstructor() {
        // Empty Constructor
        javaSource.newJavaConstructor("public");
    }

    private void generateBody() {
        // Generating Remote Bean (the object that we'll dispatch the calls)
        javaSource.newJavaField("remote", JavaQNameImpl.getInstance(pkgName, iName), "private");
        JavaMethod remoteMethod = javaSource.newJavaMethod("setRemote", "void", "public");
        remoteMethod.addParam(JavaQNameImpl.getInstance("spot.rmi", "Remote"), "remote");
        remoteMethod.addLine("this.remote = (" + iName + ")remote;");

        // Generating Dispatch Method
        JavaMethod serviceMethod = javaSource.newJavaMethod("service",
                JavaQNameImpl.getInstance("com.google.code.spotshout.comm", "RMIReply"),
                "public");

        serviceMethod.addParam(JavaQNameImpl.getInstance("com.google.code.spotshout.comm", "RMIRequest"), "request");

        // Basic data of Dispatch Method
        serviceMethod.addTry();
        serviceMethod.addLine("TargetMethod method = ((InvokeRequest) request).getMethod();");
        serviceMethod.addLine("KSNSerializableInterface returnValue = null;");
        serviceMethod.addLine(iName + " remoteObj = (" + iName + ") remote;");
        serviceMethod.addLine();
        
        // Finding right method to call
        JavaMethod[] methods = sort(remoteInterfaceSource.getMethods());

        StringBuffer sb = new StringBuffer();
        for (int i  = 0; i < methods.length; i++) {
            sb.delete(0, sb.length()); // Cleaning StringBuffer Cache and Reusing it

            // Testing method id
            if (i == 0) sb.append("if ");
            else sb.append("else if ");
            sb.append("(method.getMethodNumber() == " + i +") {");
            serviceMethod.addLine(sb.toString());

            // Unwrapping method args
            JavaQName[] methodParam = methods[i].getParamTypes();
            for (int j = 0; j < methodParam.length; j++) {
                sb.delete(0, sb.length());
                
                sb.append(methodParam[j].getClassName());
                sb.append(" p" + j + " = ");
                
                if (!methodParam[j].isPrimitive()) 
                    sb.append("(" + methodParam[j].getClassName() + ")");
                
                sb.append("((" + unwrap(methodParam[j]) + ")method.getArgs()[" + j + "]).getValue();");
                serviceMethod.indent();
                serviceMethod.addLine(sb.toString());
                serviceMethod.unindent();
            }
            
            // Invoking the target remote object
            // Let's check if method has return and if true prepare return value
            sb.delete(0, sb.length());

            if (!methods[i].isVoid()) {
                sb.append("returnValue = new " + unwrap(methods[i].getType()) + "(");
            }
            
            // Make remoteObj call
            sb.append("remoteObj." + methods[i].getName() + "(");
            // List parameters in the call
            for (int j = 0; j < methodParam.length; j++) {
                    sb.append("p" + j);
                    if (j < methodParam.length - 1) sb.append(", ");
            }
            sb.append(")");
            if (!methods[i].isVoid())
                sb.append(")");
            sb.append(";");

            serviceMethod.indent();
            serviceMethod.addLine(sb.toString());
            serviceMethod.unindent();

            serviceMethod.addLine("}");
        }
        // If method has no return (we return null and the protocol won't reply it
        serviceMethod.addLine("return null;");

        // Treating Exceptions
        serviceMethod.addCatch(JavaQNameImpl.getInstance("java.lang", "Exception"), "ex");
        
        serviceMethod.addLine("InvokeReply reply = new InvokeReply();");
        serviceMethod.addLine("reply.setOperationStatus(ProtocolOpcode.OPERATION_NOK);");
        serviceMethod.addLine("reply.setException(ProtocolOpcode.EXCEPTION_REMOTE);");
        serviceMethod.addLine("return reply;");

        serviceMethod.addEndTry();
    }

    /**
     * Gets the name of the Unwrapper of a given type.
     *
     * @param keyword - the name of the primitive type
     * @return the wrapper name
     */
    private String unwrap(JavaQName parameter) {
        String keyword = parameter.getClassName();
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

        return "Serial" + objType;
    }

    private JavaMethod[] sort(JavaMethod[] methods) {
        if (methods.length == 0) return null;

        int leastGuy = 0;
        String leastMethod = methods[0].getLoggingSignature();
        String currentMethod = leastMethod;
        for (int i = 0; i < methods.length; i++) {
            leastMethod = methods[i].getLoggingSignature();
            leastGuy = i;
            for (int j = i; j < methods.length; j++) {
                currentMethod = methods[j].getLoggingSignature();
                if (leastMethod.compareTo(currentMethod) > 0) {
                    leastMethod = currentMethod;
                    leastGuy = j;
                }
            }
            JavaMethod aux = methods[i];
            methods[i] = methods[leastGuy];
            methods[leastGuy] = aux;
        }
        return methods;
    }
    
}
