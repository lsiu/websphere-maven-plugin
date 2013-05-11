package com.github.lsiu.m2e.connector.websphere.maven.plugin;

import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;

public class WebsphereBuildParticipant extends MojoExecutionBuildParticipant {

	public WebsphereBuildParticipant(MojoExecution execution) {
		super(execution, false, false);
	}

	@Override
	public Set<IProject> build(int kind, IProgressMonitor monitor)
			throws Exception {
		return super.build(kind, monitor);
	}

}
