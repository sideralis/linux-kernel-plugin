package com.intel.linux.nature;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import com.intel.linux.Activator;

public class NatureLinkedRessources implements IProjectNature {
	/** The ID of this project nature */
	public static final String NATURE_ID = Activator.PLUGIN_ID + ".NatureLinkedRessources";

	@Override
	public void configure() throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deconfigure() throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public IProject getProject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProject(IProject project) {
		// TODO Auto-generated method stub

	}

}
