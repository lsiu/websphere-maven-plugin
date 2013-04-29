Welcome to websphere-maven-plugin for Apache Maven 2

This plugin is developed against Websphere 8.5.

## Available goals
  * create-ejb-stubs

## create-ejb-stubs usage

### Sample configuration

```xml
<plugin>
    <groupId>com.github.lsiu.maven.plugins</groupId>
    <artifactId>websphere-maven-plugin</artifactId>
	<version>1.0-SNAPSHOT</version>
	<configuration>
        <!-- list of interface/classes to create ejb stubs -->
		<classes>
	    	<class>java.lang.Appendable</class>
		    <class>java.lang.Readable</class>
		</classes>
        <!-- following are optional -->
        <websphereHome/> <!-- default to /IBM/WebSphere/AppServer -->
        <outputDirectory/> <!-- default to generated-sources-stubs -->
        <classpath/> <!-- default to test scope classpath -->
	</configuration>
  </plugin>
```

### Usage
```sh 
mvn websphere:create-ejb-stubs
```