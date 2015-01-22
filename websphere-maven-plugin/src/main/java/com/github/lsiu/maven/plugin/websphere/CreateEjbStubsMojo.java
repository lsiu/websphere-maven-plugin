package com.github.lsiu.maven.plugin.websphere;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
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

    @Parameter(property = "inputFile")
    private File inputFile;

    @Parameter(defaultValue = "${project.build.outputDirectory}", property = "outputDirectory")
    private File outputDirectory;

    @Parameter(property = "updateFile")
    private File updateFile;
    
    @Parameter(property = "filesets")
    private List<FileSet> filesets;

    @Parameter(required = true, readonly = true, property = "project.testClasspathElements")
    protected List<String> classpath;

    public void execute() throws MojoExecutionException {
        if (websphereHome == null) {
            throw new MojoExecutionException(
                    "Missing <websphereHome> configuration");
        }
        if (websphereHome.exists() == false) {
            throw new MojoExecutionException(
                    "Directory specified in <websphereHome> '"
                    + websphereHome.getAbsolutePath() + "' not found");
        }
        if (websphereHome.isDirectory() == false) {
            throw new MojoExecutionException(
                    "Value specified in <websphereHome> '"
                    + websphereHome.getAbsolutePath()
                    + "' is not a directory");
        }

        if (outputDirectory == null) {
            throw new MojoExecutionException("Output Directory cannot be null");
        }

        if (outputDirectory.exists() == false) {
            boolean isDirMade = outputDirectory.mkdirs();
            if (isDirMade == false) {
                throw new MojoExecutionException(String.format("Cannot create output directory '%s'", outputDirectory));
            }
        }

        if (inputFile != null) {
            createEjbStubsForInputFile();
        } else if (classes != null && classes.length > 0) {
            createEjbStubsForClasses();
        } else if (filesets != null && filesets.size() > 0) {
        	createEjbStubsForClassesSet();
        } else {
            throw new MojoExecutionException(
                    "Must specify <inputFile> or at least one <class> in configuration");
        }
    }

    private void createEjbStubsForClasses() throws MojoExecutionException {
        String[] command = new String[4];
        command[0] = new File(websphereHome, getExecutable()).getAbsolutePath();
        command[2] = "-cp";
        command[3] = StringUtils.join(classpath.toArray(), File.pathSeparator);

        for (String clazz : classes) {
            command[1] = clazz;
            executeCreateEjbStubs(command);
        }
    }
    
    private void createEjbStubsForClassesSet() throws MojoExecutionException {

    	FileSetManager fileSetManager = new FileSetManager();
    	String[] command = new String[4];
        command[0] = new File(websphereHome, getExecutable()).getAbsolutePath();
        command[2] = "-cp";
        command[3] = StringUtils.join(classpath.toArray(), File.pathSeparator);
        
        for (FileSet fileset : filesets) {
        	String[] includedFiles = fileSetManager.getIncludedFiles( fileset );
        	for (String clazz : includedFiles) {
                clazz = FilenameUtils.removeExtension(clazz);
                command[1] = clazz.replace(File.separator, ".");
                executeCreateEjbStubs(command);
			}
        }
    }

    private void createEjbStubsForInputFile() throws MojoExecutionException {
        if (!inputFile.exists()) {
            throw new MojoExecutionException("File specified in <inputFile> '"
                    + inputFile.getAbsolutePath() + "' not found");
        }

        final String[] command;

        if (updateFile != null) {
            if (!updateFile.exists()) {
                throw new MojoExecutionException(
                        "File specified in <updateFile> '"
                        + updateFile.getAbsolutePath() + "' not found");
            }

            command = new String[6];
            command[4] = "-updatefile";
            command[5] = updateFile.getAbsolutePath();
        } else {
            command = new String[4];
        }

        command[0] = new File(websphereHome, getExecutable()).getAbsolutePath();
        command[1] = inputFile.getAbsolutePath();
        command[2] = "-cp";
        command[3] = StringUtils.join(classpath.toArray(), File.pathSeparator);

        executeCreateEjbStubs(command);
    }

    private void executeCreateEjbStubs(final String[] command)
            throws MojoExecutionException {
        try {
            if (getLog().isDebugEnabled()) {
                getLog().info(StringUtils.join(command, " "));
            }

            Process p = null;
            int retry = 0;
            boolean cmdSuccess = false;
            while (!cmdSuccess) {
                try {
                    p = new ProcessBuilder().directory(outputDirectory)
                            .redirectErrorStream(true).command(command).start();
                    final StringBuffer buf = new StringBuffer();
                    StreamPumper outputPumper = new StreamPumper(
                            p.getInputStream(), new StreamConsumer() {
                                public void consumeLine(String line) {
                                    getLog().info(line);
                                    buf.append(line);
                                }
                            });
                    StreamPumper errorPumper = new StreamPumper(
                            p.getErrorStream(), new StreamConsumer() {
                                public void consumeLine(String line) {
                                    getLog().error(line);
                                }
                            });

                    outputPumper.start();
                    errorPumper.start();

                    int exitCode = p.waitFor();

                    if (getLog().isDebugEnabled()) {
                        getLog().info("Exit Code: '" + exitCode + "'");
                    }

                    if (exitCode != 0) {
                        throw new MojoExecutionException(
                                "Create EJB Stub exit with code: '" + exitCode
                                + "'");
                    }
                    
                    // looks like exit code is always zero from createEjbStub
                    // Problem with build in other language not really a good correction ...
                    if (!buf.toString().endsWith("Command Successful") &&  
                    		!buf.toString().endsWith("ussite de la commande")) {
                        throw new MojoExecutionException(
                                "Error during the creation of EJB Stub failed:\n" +
                        "1. Le language use for the build isn't English or French\n" + 
                        "2. Other error : "+ buf.toString());
                    }
                    cmdSuccess = true;
                } catch (IOException e) {
                    getLog().info(
                            String.format("cmd failed:%s, retry:%s",
                                    StringUtils.join(command, " "), retry), e);
                    if (++retry == 5) {
                        throw new Exception(e);
                    }
                }
            }
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to run command: '"
                    + StringUtils.join(command, " ") + "'", e);
        }
    }

    private static String getExecutable() {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return "bin/createEJBStubs.bat";
        } else {
            return "bin/createEJBStubs.sh";
        }
    }
}
