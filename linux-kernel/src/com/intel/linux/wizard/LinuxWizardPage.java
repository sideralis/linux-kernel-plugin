package com.intel.linux.wizard;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class LinuxWizardPage extends WizardPage {
	
	/** The Location of the makefile */
	private Text locationPath;
	/** The name of the project */
	private Text projectName;
	/** The browse button */
	private Button browse;

	protected LinuxWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setDescription("Creates a Linux project");
	}

	@Override
	public void createControl(Composite parent) {
		GridData gd;
		
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(2, false));
		
//		Label prjTypeLabel = new Label(container, SWT.NONE);
//		prjTypeLabel.setText("1- Select the project type");
//		gd = new GridData();
//		prjTypeLabel.setLayoutData(gd);
		
//		projectType = new List(container, SWT.SINGLE | SWT.BOLD | SWT.BORDER);
//		for (String e:PROJECT_TYPE)
//			projectType.add(e);
//		projectType.setSelection(0);
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.horizontalSpan = 2;
//		projectType.setLayoutData(gd);

		Label loc = new Label(container, SWT.NONE);
		loc.setText("1- Browse for the Linux makefile's location:");
		gd = new GridData();
		loc.setLayoutData(gd);

		locationPath = new Text(container, SWT.BOLD | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		locationPath.setLayoutData(gd);

		browse = new Button(container, SWT.NONE);
		browse.setText("Browse");
		gd = new GridData();
		browse.setLayoutData(gd);
		
		Label name = new Label(container, SWT.NONE);
		name.setText("2- Enter the project's name:");
		gd = new GridData();
		name.setLayoutData(gd);
		
		projectName = new Text(container, SWT.BOLD | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		projectName.setLayoutData(gd);

		addListeners();

		setControl(container);
	}

	private void addListeners() {
		browse.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				DirectoryDialog dirDialog = new DirectoryDialog(browse.getShell());
				dirDialog.setText("Select the directory where is located the makefile.");
				String startPath = "/home/git.view/"+System.getProperty("user.name");
				if (startPath != null) {
					File f = new File(startPath);
					if (f.exists())
						dirDialog.setFilterPath(startPath);
				}
				String path = dirDialog.open();
				locationPath.setText(path);
				IPath iPath = new Path(path);
				projectName.setText(iPath.lastSegment());
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
			}
		});	
	}

	public String getLocationPath() {
		return locationPath.getText();
	}

	public String getProjectName() {
		return projectName.getText();
	}
	
}
