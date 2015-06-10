# spotshout
spotSHOUT implements my undergrad project, Distributed Object Middleware for Wireless Sensor Network, for the SunSPOT platform. This work proposes a RMI-like Middleware for WSN. The middleware will enable abstraction of the communications details between sensors, allowing the development of applications with a higher-level of abstraction, thus, making it easier and funnier to develop applications outside the scope of just collecting data.

# Installing

There are two ways to install spotSHOUT, as a local or system library implementation. They differ basically on how the spotSHOUT JAR is referenced.

The Local Library Mode requires that each project Sun SPOT references spotSHOUT manually in list of JARs that they are using. This method is easier to install, but is more annoying to use and maintain, since a change on the JAR version requires to recompile all applications. The method works with the simulator Solarium and will be used as the basis for the tutorials explained in the Wiki.

The System Library Mode attached the spotSHOUT Jar to the bootstrap classes of the Sun SPOTs. Allowing the project to be referenced indirectly, ie, the projects do not need to import / point to the JAR file, since it is already in the standard library. This method is easier to use and maintain, but unfortunately requires that the standard library of classes to be rebuilt and updated for each Sun SPOT sensor and this method does not work with the Simulator Solarium.

After installation **configure the simulation environment** and then read the tutorial for five minutes or go to the guide.

## Installing as Local Library
To install the library as a middleware application for sensors / hosts take the following steps:
  1. Download the JAR, SRC and Plugin spotSHOUT in [download section](http://code.google.com/p/spotshout/downloads/list).
  1. The way of how to reference the middleware depends on the nature of the project (Sensor or Host)
    * Sensor: Open `NetBeans` and in the Sensor project that uses spotSHOUT edit the `build.properties` file adding the following line:
```
utility.jars=C:/caminho/para/spotSHOUT-1.0.jar
```
    * Host: Open `NetBeans` and in the Host project that uses spotSHOUT edite the `build.properties` file adding the following line:
```
user.classpath=C:/caminho/para/spotSHOUT-1.0.jar;....
```
  1. In `NetBeans` add the spotSHOUT classpath to your project (for code completion). Do: Right Click on the Project Name **>** Properties **>** Java Classpath **>** Add JAR/folder **>** Select the JAR **AND** Source of spotSHOUT. ![http://spotshout.googlecode.com/svn/wiki/images/addJAR.png](http://spotshout.googlecode.com/svn/wiki/images/addJAR.png)
  1. Install the plug-in to generate the stubs/skeletons in `NetBeans`. Do:  Tools Menu **>** Plug-ins **>** Downloaded **>** Select "Add Plug-ins" **>** Choose the NBM-file (Plug-in) downloaded. Next click on install and it's going to show the software license (Apache v2), read it! After reading, accept it and it will be installed. ![http://spotshout.googlecode.com/svn/wiki/images/plugin.png](http://spotshout.googlecode.com/svn/wiki/images/plugin.png)

## Installing as System Library
  1. Download the JAR, SRC and Plugin spotSHOUT in [download section](http://code.google.com/p/spotshout/downloads/list).
  1. Move the spotSHOUT Jar to the `/lib` directory of the SunSPOT SDK installation folder. ![http://spotshout.googlecode.com/svn/wiki/images/libDir.png](http://spotshout.googlecode.com/svn/wiki/images/libDir.png)
  1. In your home folder modify the `.sunspot.properties` file and adapt the file to have the following lines (verify the name and version of the JAR - here we are using `spotSHOUT-1.0.jar`). ![http://spotshout.googlecode.com/svn/wiki/images/systeminstall.png](http://spotshout.googlecode.com/svn/wiki/images/systeminstall.png)
```
spot.library.name=spotSHOUT
spot.library.addin.jars=${sunspot.lib}/spotSHOUT-1.0.jar${path.separator}${sunspot.lib}/multihop_common.jar${path.separator}${sunspot.lib}/transducer_device.jar
```
  1. Rebuild the standard system class library of SunSPOT using the following ant command (in any directory that contains the `build.xml` file of SunSPOT - example: the install folder of the SunSPOT SDK): ![http://spotshout.googlecode.com/svn/wiki/images/antlibrary.png](http://spotshout.googlecode.com/svn/wiki/images/antlibrary.png)
```
$ ant library
```
  1. Deploy the new standard library on your Sun SPOT's using:
```
$ ant flashlibrary
```

# Configuring the Environment
In order to utilize the base stations in a simulated environment (a host computer) make sure they are marked as shared and not necessary. Just change the `.sunspot.properties` file adding the following lines:
```
basestation.shared=true
basestation.not.required=true
```
If you do not want to modify your file, just in time to run a host application passes the properties as arguments. Example:
```
ant host-run -Dbasestation.shared=true -Dbasestation.not.required=true
```

# References
The operation guide of the [SunSpot](http://sunspotworld.com/docs/Yellow/SunSPOT-Programmers-Manual.pdf) documentation and other middleware, such as [LooCI](http://code.google.com/p/looci/wiki/Welcome?tm=6) and [KSN](http://www.ipd.uka.de/KSN/Serialization/), were used to construct the installation guide.


