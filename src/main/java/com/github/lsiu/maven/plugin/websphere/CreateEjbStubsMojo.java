package com.github.lsiu.maven.plugin.websphere;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.codehaus.plexus.util.cli.StreamPumper;

/**
 * Webspehre Create EJB Stubs Mojo
 * 
 * @author Leonard Siu
 * 
 */
@Mojo(name = "create-ejb-stubs", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.TEST)
public class CreateEjbStubsMojo extends AbstractMojo {

	/**
	 * Location of the file.
	 */
	@Parameter(defaultValue = "/IBM/WebSphere/AppServer", property = "websphereHome", required = true)
	private File websphereHome;

	@Parameter(property = "class")
	private String[] classes;

	@Parameter(defaultValue = "target/generated-sources/stubs", property = "outputDirectory")
	private File outputDirectory;

	@Parameter(required = true, readonly = true, property = "project.testClasspathElements")
	protected List<String> classpath;

	public void execute() throws MojoExecutionException {
		if (websphereHome == null)
			throw new MojoExecutionException(
					"Missing <websphereHome> configuration");
		if (websphereHome.exists() == false)
			throw new MojoExecutionException(
					"Directory specificed in <websphereHome> not found");
		if (websphereHome.isDirectory() == false)
			throw new MojoExecutionException(
					"Value specified in <websphereHome> is not a directory");

		if (classes == null || classes.length == 0)
			throw new MojoExecutionException(
					"Must specify at least one <class> in configuration");
		
		if (outputDirectory == null)
			throw new MojoExecutionException("Output Directory cannot be null");
		
		if (outputDirectory.exists() == false)
			outputDirectory.mkdirs();

		String[] command = new String[4];
		command[0] = new File(websphereHome, getExecutable())
				.getAbsolutePath();
		command[2] = "-cp";
		command[3] = StringUtils.join(classpath.toArray(), File.pathSeparator);

		for (String clazz : classes) {
			command[1] = clazz;
			try {
				if (getLog().isDebugEnabled()) 
					getLog().info(StringUtils.join(command, " "));
				
				Process p = new ProcessBuilder().directory(outputDirectory)
						.redirectErrorStream(true).command(command).start();
				
				StreamPumper outputPumper = new StreamPumper(p.getInputStream(),
	                    new StreamConsumer() {
	                        public void consumeLine(String line) {
                                getLog().info(line);
	                        }
	                    });
	            StreamPumper errorPumper = new StreamPumper(p.getErrorStream(),
	                    new StreamConsumer() {
	                        public void consumeLine(String line) {
                                getLog().error(line);
	                        }
	                    });

	            outputPumper.start();
	            errorPumper.start();
				
				int exitCode = p.waitFor();
				if (getLog().isDebugEnabled())
					getLog().info("Exit Code: '" + exitCode + "'");
				if (exitCode != 0)
					throw new MojoExecutionException("Create EJB Stub exit with code: '" + exitCode + "'");
			} catch (IOException e) {
				throw new MojoExecutionException("Failed to run command: '"
						+ StringUtils.join(command, " ") + "'", e);
			} catch (InterruptedException e) {
				throw new MojoExecutionException("Failed to run command: '"
						+ StringUtils.join(command, " ") + "'", e);
			}
		}
	}

	private static final String getExecutable() {
		if (Os.isFamily(Os.FAMILY_WINDOWS)) {
			return "bin/createEJBStubs.bat";
		} else {
			return "bin/createEJBStubs.sh";
		}
	}
}
