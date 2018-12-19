/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.workspace;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.graham.appshell.App;
import com.graham.appshell.AppExtensions;
import com.graham.appshell.data.AppScopeImpl;
import com.graham.appshell.handlers.AppQuit;
import com.graham.appshell.handlers.HandlerIfSuccess;
import com.graham.appshell.handlers.SysWindow2;
import com.graham.tools.UITools;

/**
 * UI to select the "Work Space Directory"
 * 
 * @author graham
 *
 */
public class WorkSpaceDirFrame {
	
	private JTextField workspaceDir;
	private JTextField appDir;
	private JFrame wsFrame;
	private JLabel errorLabel;
	
	/**
	 * call if application is already running
	 */
	public WorkSpaceDirFrame(
			String workspaceDefaultPath, 
			App app, 
			AppExtensions app2) {
		init(workspaceDefaultPath, app, app2, null);
	}
	
	/**
	 * call if application is starting up for the first time
	 */
	public WorkSpaceDirFrame(
			String workspaceDefaultPath, 
			AppExtensions app2, 
			String appDefaultPath) {
		init(workspaceDefaultPath, null, app2, appDefaultPath);
	}
	
	/**
	 * @param workspaceDefaultPath
	 * @param app can be null
	 * @param app2
	 * @param appDefaultPath if not null, show the app dir text, and quit if user cancels
	 */
	private void init(
			String workspaceDefaultPath, 
			App app, 
			AppExtensions app2, 
			String appDefaultPath) {
		
		JPanel mainGrid;
		AbstractAction cancelAction;
		wsFrame = new JFrame();
	
		if (appDefaultPath == null) {
			cancelAction = new cancelActionClass("Cancel", wsFrame);
			
			mainGrid = new JPanel(new GridLayout(4, 1, 4, 4));
		} else {
			cancelAction = new quitActionClass("Quit");
			
			mainGrid = new JPanel(new GridLayout(7, 1, 4, 4));
			mainGrid.add(new JLabel(" Application Work Directory:"));
			appDir = new JTextField(appDefaultPath,30);
			JPanel tmp1 = new JPanel();
			tmp1.add(appDir);
			mainGrid.add(tmp1);
			
			JPanel bp1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			bp1.add(new JButton(new chooseDirActionClass(appDir)));
			bp1.add(new JButton(new defaultActionClass(appDir, appDefaultPath)));
			mainGrid.add(bp1);
		}
		mainGrid.add(new JLabel(" Workspace Directory:"));
		workspaceDir = new JTextField(workspaceDefaultPath,30);
		JPanel tmp2 = new JPanel();
		tmp2.add(workspaceDir);
		mainGrid.add(tmp2);
		JPanel bp2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bp2.add(new JButton(new chooseDirActionClass(workspaceDir)));
		bp2.add(new JButton(new defaultActionClass(workspaceDir, workspaceDefaultPath)));
		mainGrid.add(bp2);
		
		JPanel bp3 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		errorLabel = new JLabel("");
		bp3.add(errorLabel);
		bp3.add(new JButton(cancelAction));
		
		// todo
		bp3.add(new JButton(new WorkSpaceDirOK(app, app2)));
		mainGrid.add(bp3);

		JPanel panelWithText = new JPanel(new BorderLayout());
		panelWithText.add(mainGrid, BorderLayout.CENTER);
		JTextArea helpText = new JTextArea();
		if (appDefaultPath != null) {
			helpText.setText("Choose a location on the file system to store all your work. Your work will be automatically saved here. Create one workspace now to get started. You may create additional workspaces if needed later.");
		} else {
			helpText.setText("Choose an existing workspace path. Or choose an new directory or empty directory to create a new workspace.");
		}
		helpText.setEditable(false);
		helpText.setFont(helpText.getFont().deriveFont(11.0f));
		helpText.setPreferredSize(new Dimension(300, 60));
		helpText.setBorder(new EmptyBorder(new Insets(3, 3, 3, 3)));
		helpText.setLineWrap(true);
		helpText.setWrapStyleWord(true);
		panelWithText.add(helpText, BorderLayout.SOUTH);
		wsFrame.setContentPane(panelWithText);
		
		wsFrame.setTitle("SQLClient");

		//wsFrame.setContentPane(mainGrid);
		wsFrame.addWindowListener(new SysWindow2(cancelAction));
		wsFrame.pack();
		UITools.center(wsFrame);
		wsFrame.setResizable(false);
		wsFrame.setVisible(true);
	}

	public class defaultActionClass extends AbstractAction {
        private static final long serialVersionUID = 1L;
        String defaultPath;
        JTextField tf;
        public defaultActionClass(JTextField tfParam, String defaultPathParam) {
			super("Default");
			tf = tfParam;
			defaultPath = defaultPathParam;
		}
		public void actionPerformed(ActionEvent e) {
			tf.setText(defaultPath);
		}
	}
	
	public class chooseDirActionClass extends AbstractAction {
        private static final long serialVersionUID = 1L;
        JTextField tf;
        public chooseDirActionClass(JTextField tfParam) {
			super("Choose Directory...");
			tf = tfParam;
		}
		public void actionPerformed(ActionEvent e) {
		    JFileChooser chooser = new JFileChooser();
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    int returnVal = chooser.showOpenDialog(wsFrame);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		    	tf.setText(chooser.getSelectedFile().getPath());
		    }
		}
	}
	
	public class quitActionClass extends AbstractAction {
        private static final long serialVersionUID = 1L;
        public quitActionClass(String text) {
			super(text);
		}
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}
	
	public class cancelActionClass extends AbstractAction {
        private static final long serialVersionUID = 1L;
        JFrame f;
        public cancelActionClass(String text, JFrame fParam) {
			super(text);
			f = fParam;
		}
		public void actionPerformed(ActionEvent e) {
			f.setVisible(false);
		}
	}
	

	public class WorkSpaceDirOK  extends AbstractAction {
	    private static final long serialVersionUID = 1L;

	    App curApp;
	    AppExtensions app2;
	    
	    /**
	     * currentApp may be null if we are just starting
	     * @param workSpaceDirParam
	     * @param dirParam
	     * @param currentApp
	     */
	    public WorkSpaceDirOK(App currentAppParam, AppExtensions app2Param) {
			super("OK");
			curApp = currentAppParam;
			app2 = app2Param;
		}
	    
		public void actionPerformed(ActionEvent e) {
			ActionEvent ae2 = new ActionEvent(e.getSource(), 0, AppQuit.NOQUIT);

			{
				// make sure the workspace is a valid directory
				File workSpaceFile = new File(workspaceDir.getText());
				if (!workSpaceFile.isDirectory()) {
					boolean dirSuccess = workSpaceFile.mkdirs();
					if (!dirSuccess) {
						errorLabel.setText("Can't create directory there");
						errorLabel.setToolTipText("there was an error. try a different location: " + workSpaceFile.getAbsolutePath());
						return;
					}
				}
			}

			if (curApp == null) { // first time startup
				// make sure the application workspace is a valid directory
				File appFile = new File(appDir.getText());
				if (!appFile.isDirectory()) {
					boolean dirSuccess = appFile.mkdirs();
					if (!dirSuccess) {
						errorLabel.setText("Can't create directory there");
						errorLabel.setToolTipText("there was an error. try a different location: " + appFile.getAbsolutePath());
						return;
					}
				}
				
				AppScopeImpl.setPrefsAppHomePath(appFile.getAbsolutePath());
			}
			
			wsFrame.setVisible(false);
			WorkSpace ws = new WorkSpace();
			ws.path = workspaceDir.getText();
			CreateNewWorkspaceAndStartApp cn = new CreateNewWorkspaceAndStartApp(curApp, app2, ws);
			if (curApp == null) {
				cn.actionPerformed(ae2);
			} else {
				new HandlerIfSuccess(curApp.getAppQuitHandler(), cn).start(ae2);
			}
		}
	}

}
