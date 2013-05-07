package com.github.lsiu.m2e.connector.websphere.maven.plugin;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.jdt.AbstractJavaProjectConfigurator;

public class WebsphereProjectConfigurator extends
		AbstractJavaProjectConfigurator {
	
    @Override
    protected String getOutputFolderParameterName() {
        return "outputDirectory";
    }

	@Override
	public AbstractBuildParticipant getBuildParticipant(
			IMavenProjectFacade projectFacade, MojoExecution execution,
			IPluginExecutionMetadata executionMetadata) {

		return new WebsphereBuildParticipant(execution);
	}
}
