package com.intel.linux.wizard;

import java.io.File;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.CProjectNature;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IProjectType;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;

import com.intel.linux.nature.ToggleNature;

public class LinuxProjectManager {

	/**
	 * See http://cdt-devel-faq.wikidot.com/#toc25
	 * 
	 * @param name
	 * @param path
	 * @param monitor
	 */
	public void createLinuxProject(String name, String path, IProgressMonitor monitor) {
		IProjectType projType = null;
		IToolChain toolChain = null;
		
		// First check if makefile exist
		File makefile = new File(path + "/Makefile");
		if (!makefile.exists()) {
			// We need to warn the user
			MessageDialog.openWarning(null, "No Makefile", "There is no makefile in the project's directory. Build will not be possible!");
		}

		// Secondly check if eclipse project exist already
		File eclipse = new File(path + "/.cproject");
		if (eclipse.exists()) {
			// The project has been created already, let's ask the user if he
			// wants to overwrite it!
			// TODO: this does not work correctly as some linked folders are not added if we say yes.
			boolean answer = MessageDialog.openQuestion(null, "Eclipse project already exists", "Do you want to overwrite the existing eclipse project?\n"
					+ "If not, press no and use the File\\Import... menu, then General\\Existing projects into Workspace to open the existing project");
			if (answer == false)
				return;
		}

		// Parse makefile
//		MakefileData.parse(makefile);

		// Get workspace in order to create a project
		IWorkspaceRoot wrkSpaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject newProjectHandle = wrkSpaceRoot.getProject(name);
		monitor.beginTask("Creating Linux project", 0);
		IProjectDescription projDesc = ResourcesPlugin.getWorkspace().newProjectDescription(newProjectHandle.getName());
		if (!("".equals(path)) && path != null) {
			Path myPath = new Path(path);
			projDesc.setLocation(myPath);
		}

		try {
			IProject cdtProj = CCorePlugin.getDefault().createCDTProject(projDesc, newProjectHandle, monitor);
			
			CProjectNature.addCNature(cdtProj, monitor);
			ICProjectDescriptionManager mgr = CoreModel.getDefault().getProjectDescriptionManager();
			ICProjectDescription des = mgr.getProjectDescription(cdtProj, true);
			if (des != null)
				return; // C project description already exists
			des = mgr.createProjectDescription(cdtProj, true);

			ManagedBuildInfo info = ManagedBuildManager.createBuildInfo(cdtProj);

			projType = ManagedBuildManager.getExtensionProjectType("linux-kernel.projectType.linux");
			toolChain = ManagedBuildManager.getExtensionToolChain("linux-kernel.toolChain.linux");

			if (projType != null && toolChain != null) {
				ManagedProject mProj = new ManagedProject(cdtProj, projType);
				info.setManagedProject(mProj);

				IConfiguration[] configs = ManagedBuildManager.getExtensionConfigurations(toolChain, projType);

				for (IConfiguration icf : configs) {
					if (!(icf instanceof Configuration)) {
						continue;
					}
					Configuration cf = (Configuration) icf;

					String id = ManagedBuildManager.calculateChildId(cf.getId(), null);
					Configuration config = new Configuration(mProj, cf, id, false, true);

					ICConfigurationDescription cfgDes = des.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, config.getConfigurationData());
					// Add define
//					{
//						int size = MakefileData.getDefines().size();
//						HashMap<String, String> defines = MakefileData.getDefines();
//						Set<String> keys = defines.keySet();
//
//						for (ICFolderDescription fileDesc : cfgDes.getFolderDescriptions()) {
//							for (ICLanguageSetting lang : fileDesc.getLanguageSettings())// ;//.getLanguageSettings();
//							{
//								ICLanguageSettingEntry[] newEntries = new ICLanguageSettingEntry[size];
//								int i = 0;
//								Iterator<String> ite = keys.iterator();
//								while (ite.hasNext()) {
//									String key = ite.next();
//									newEntries[i++] = new CMacroEntry(key, defines.get(key), 0);
//								}
//								lang.setSettingEntries(ICSettingEntry.MACRO, newEntries);
//							}
//						}
//					}

					config.setConfigurationDescription(cfgDes);
					config.exportArtifactInfo();

					IBuilder bld = config.getEditableBuilder();
					if (bld != null) {
						bld.setManagedBuildOn(true);
					}

					config.setName(config.getName());
					config.setArtifactName(cdtProj.getName());

				}
				mgr.setProjectDescription(cdtProj, des);

				// Add Linux project nature
				new ToggleNature(cdtProj).start();
			}

		} catch (OperationCanceledException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
