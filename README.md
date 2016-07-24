## Change Log

### 1.1.2
* Check for French and German version of the "Command Successful" to indicate CreateEjbStub command completed successfully
* Merged changes from [apenvern](https://github.com/apenvern/websphere-maven-plugin) - french support and support for specifying Remote interface

## Overview

Maven plugin for generate the \_XXX\_Stub.class file required in order for stand alone java application to call Remote EJB on Websphere Application Server.

Add the parameter FileSet to replace the class parameter and don't put all the remote class but just **/*Remote.class.

For example

```xml
<plugin>
    <groupId>com.github.lsiu.maven.plugins</groupId>
    <artifactId>websphere-maven-plugin</artifactId>
    <version>1.1.2-SNAPSHOT</version>
    <configuration>
        <!-- list of interface/classes to create ejb stubs -->
        <fileSets>
          <fileSet>
            <outputDirectory>${project.build.outputDirectory}</outputDirectory>
			<includes>
				<include>**/*Remote.class</include>
			</includes>
          </fileSet>
        </fileSets>
        <!-- following are optional -->
        <websphereHome/> <!-- default to /IBM/WebSphere/AppServer -->
        <outputDirectory/> <!-- default to target/classes -->
        <classpath/> <!-- default to test scope classpath -->
    </configuration>
</plugin>
```

##Contributions
[apenvern](https://github.com/apenvern/websphere-maven-plugin)