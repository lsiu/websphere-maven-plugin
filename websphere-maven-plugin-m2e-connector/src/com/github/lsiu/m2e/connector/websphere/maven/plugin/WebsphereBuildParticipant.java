package com.github.lsiu.m2e.connector.websphere.maven.plugin;

import java.io.File;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

public class WebsphereBuildParticipant extends MojoExecutionBuildParticipant {

	public WebsphereBuildParticipant(MojoExecution execution) {
		super(execution, true);
	}

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor)
			throws Exception {
        IMaven maven = MavenPlugin.getMaven();
        BuildContext buildContext = getBuildContext();
        System.out.println("build executed");
        
//        String[] classes = maven.getMojoParameterValue(getSession(), getMojoExecution(), "classes", String[].class);
        
        Set<IProject> result = super.build(kind, monitor);
        
        File generated = maven.getMojoParameterValue(getSession(), getMojoExecution(), "outputDirectory", File.class);
        if (generated != null) {
            buildContext.refresh( generated );
        }
        
        return result;
	}

}
