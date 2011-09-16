package com.intel.linux.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class LinuxWizard extends Wizard implements INewWizard, IRunnableWithProgress {

	private LinuxWizardPage wizardPage;

	public LinuxWizard() {
		super();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, true, this);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		if (!wizardPage.getProjectName().equals("") && !wizardPage.getLocationPath().equals(""))
			new LinuxProjectManager().createLinuxProject(wizardPage.getProjectName(),wizardPage.getLocationPath(),monitor);
	}

}
