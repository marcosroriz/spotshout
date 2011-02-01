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
 * StubGenerator will create a specific stub for the SpotSHOUT RMI library with
 * Java Reflection.
 */
public class StubGen {

    private JavaSource remoteInterfaceSource;
    private JavaSource javaSource;
    private String pkgName;
    private String iName;

    public StubGen(JavaSource remoteInterface) {
        this.remoteInterfaceSource = remoteInterface;
        this.pkgName = remoteInterfaceSource.getPackageName();
        this.iName = remoteInterfaceSource.getClassName();

        JavaSourceFactory factory = new JavaSourceFactory();
        JavaQName className = JavaQNameImpl.getInstance(pkgName, iName + "_Stub");
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

        // Implements Remote Interface and Extends Stub
        javaSource.addImplements(JavaQNameImpl.getInstance(pkgName, iName));
        javaSource.addExtends(JavaQNameImpl.getInstance("com.google.code.spotshout.remote", "Stub"));
    }

    private void generateEmptyConstructor() {
        // Empty Constructor
        javaSource.newJavaConstructor("public");
    }

    private void generateBody() {
        // Getting Methods
        JavaMethod[] methods = sort(remoteInterfaceSource.getMethods());

        if (methods != null) {
            for (int i = 0; i < methods.length; i++) {
                generateMethod(methods[i], i);
            }
        }
    }

    private void generateMethod(JavaMethod remoteMethod, int methodNumber) {
        // Create the declaration of the method from the remoteMethod signature
        JavaMethod stubMethod = javaSource.newJavaMethod(remoteMethod);
        String[] parameterNames = remoteMethod.getParamNames();

        // Add "Connection Variable"
        stubMethod.addLine("RMIUnicastConnection conn = null;");
        stubMethod.addTry();

        // Wrapping up the arguments
        stubMethod.addLine("Serializable[] args = null;");
        if (remoteMethod.getParamTypes().length > 0) {
            stubMethod.addLine("args = new Serializable[" + remoteMethod.getParamTypes().length + "];");

            JavaQName[] parameters = remoteMethod.getParamTypes();
            for (int i = 0; i < parameters.length; i++) {
                stubMethod.addLine("args[" + i + "] = new " + wrapper(parameters[i]) + "(" + parameterNames[i] + ");");
            }
        }

        // Creating TargetMethod and InvokeRequest
        stubMethod.addLine();
        stubMethod.addLine("TargetMethod m = new TargetMethod(" + methodNumber + ", args);");
        stubMethod.addLine("InvokeRequest invReq = new InvokeRequest(getLookupName(), m);");

        // Creating Connection and Writting request
        stubMethod.addLine("conn = RMIUnicastConnection.makeClientConnection("
                + "ProtocolOpcode.INVOKE_REQUEST, getTargetAddr(), RMIProperties.RMI_SPOT_PORT);");
        stubMethod.addLine("conn.writeRequest(invReq);");

        // Listen to reply and return it only if the method is not void
        if (!remoteMethod.isVoid()) {
            stubMethod.addLine();
            stubMethod.addLine("InvokeReply invReply = (InvokeReply) conn.readReply();");
            stubMethod.addIf("invReply.exceptionHappened()");
            stubMethod.addLine(" throw new RemoteException();");
            stubMethod.addEndIf();

            // Return value and Unwrap it
            StringBuffer sb = new StringBuffer("");

            if (remoteMethod.getType().isPrimitive()) {
                sb.append("return ((" + wrapper(remoteMethod.getType()));
                sb.append(")invReply.getReturnValue()).getValue();");
            } else if (remoteMethod.getType().getClassName().equals("java.lang.String")
                    || remoteMethod.getType().getClassName().equals("String")) {
                sb.append("return ((SerialString");
                sb.append(")invReply.getReturnValue()).getValue();");
            } else {
                sb.append("return (" + remoteMethod.getType() + ")");
                sb.append("((SerialObject)invReply.getReturnValue()).getValue();");
            }

            stubMethod.addLine(sb.toString());
        }

        // Catch Any Exception
        stubMethod.addCatch(JavaQNameImpl.getInstance("java.io", "IOException"));
        stubMethod.addLine("throw new RemoteException(\"Remote Exception on" + remoteMethod.getName() + "\");");

        // Finally (Close the connection)
        stubMethod.addFinally();
        stubMethod.addLine("try { conn.close(); } catch (Exception ex) { throw new RemoteException(); };");
        stubMethod.addEndTry();
    }

    /**
     * Gets the name of a wrapper of a given type.
     *
     * @param keyword - the name of the primitive type
     * @return the wrapper name
     */
    private String wrapper(JavaQName parameter) {
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
