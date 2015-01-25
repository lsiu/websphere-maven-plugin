Forked from [lsiu/websphere-maven-plugin](https://github.com/lsiu/websphere-maven-plugin)

Maven plugin for generate the \_XXX\_Stub.class file required in order for stand alone java application to call Remote EJB on Websphere Application Server.

Add the parameter FileSet to replace the class parameter and don't put all the remote class but just **/*Remote.class.

For example

```xml
<plugin>
    <groupId>com.github.lsiu.maven.plugins</groupId>
    <artifactId>websphere-maven-plugin</artifactId>
    <version>1.0.2</version>
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
