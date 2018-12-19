/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.data;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.*;

import com.graham.appshell.App;
import com.graham.appshell.handlers.FloatingFrameQuitListener;
import com.graham.appshell.handlers.SysWindow2;
import com.graham.tools.UITools;

public class DataUIList {

	private App app;
	private DataManagerList dm;
	private JFrame frame;
	private List<AppDataScope> scopes;
	private JPanel grid;
	private HashMap<String,AppDataUIData> aduiDataMap = new HashMap<>();
	private dataUIListChangeListener changeListener;
	private JFrame newAppDataEditor;

	class AppDataUIData {
		String pk;
		JLabel label;
		JCheckBox delCheck;
		JFrame itemEditor;
		AppDataUIData(String pkParam, JLabel l, JCheckBox dc) {
			pk = pkParam;
			delCheck = dc;
			label = l;
		}
	}

	public DataUIList(DataManagerList dmParam, App appParam, List<AppDataScope> scopesParam) {
		dm = dmParam;
		app = appParam;
		scopes = scopesParam;

		changeListener = new dataUIListChangeListener();
		dm.addDataChangeListener(changeListener);
	}
	
	public JPanel initEditListWithEditItemUI() {
		//createdUIFlag = true;
		
		AppData data[] = dm.getSortedArray();
		int rows = data.length;
		grid = new JPanel(new GridLayout(rows, 3, 4, 4));
		
		//delChecks = new HashMap<String,JCheckBox>();
		for (AppData d : data) {
			AppDataUIData aduid = new AppDataUIData(d.getPrimaryKey(), new JLabel(d.toString()), new JCheckBox("Delete"));
			grid.add(aduid.label);
			grid.add(new JButton(new editItemAction(aduid)));
		//	JCheckBox cb = new JCheckBox("Delete");
			grid.add(aduid.delCheck);
			aduiDataMap.put(aduid.pk, aduid);
	//		delChecks.put(d.getPrimaryKey(), cb);
		}
		
		JPanel filler = new JPanel(new BorderLayout());
		filler.add(grid, BorderLayout.NORTH);
		
		JScrollPane sp = new JScrollPane(filler);
		
		JPanel border = new JPanel(new BorderLayout());
		
		JPanel bottom = new JPanel();
		bottom.add(new JButton(new newItemAction()));
		//bottom.add(new JButton(new cancelActionClass()));
		bottom.add(new JButton(new DoneActionClass()));
		// TODO when the window is closed by the OS, DoneActionClass does not get called, and the cleanup in there doe snot get called (dm.removeDataChangeListener(changeListener))
		
		
		border.add(sp, BorderLayout.CENTER);
		border.add(bottom, BorderLayout.SOUTH);
		
		return border;
	}

	public void toFront() {
		frame.toFront();
	}

	public boolean isVisibile() {
		return frame.isVisible();
	}
	
	/* public JFrame initEditListWithEditItemUIWindow() {
		return initEditListWithEditItemUIWindow(null, null);
	} */
	
	public JFrame initEditListWithEditItemUIWindow() {
		frame = new JFrame();
		frame.setTitle(dm.templateItem.getClassId() + " List");
		frame.add(initEditListWithEditItemUI());
		
		SysWindow2 aSymWindow = new SysWindow2(new DoneActionClass());
		frame.addWindowListener(aSymWindow);
	
		frame.pack();
		UITools.center(frame);
		//frame.setResizable(false);
		frame.setVisible(true);
		app.addAppQuitListener(new FloatingFrameQuitListener(frame));
		return frame;
	}

	public class editItemAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		AppDataUIData aduid;
		public editItemAction(AppDataUIData aduidParam) {
			super("Edit");
			aduid = aduidParam;
		}
		public void actionPerformed(ActionEvent e) {
			if (aduid.itemEditor == null) {
				DataUI dg = new DataUI(app, dm.getByPrimaryKey(aduid.pk));
				aduid.itemEditor = dg.initEditUIWindow();
				dg.setWindowClosingListener(new itemEditorClosingAction(aduid));
			} else {
				aduid.itemEditor.toFront();
			}
		}
	}
	
	public class DoneActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public DoneActionClass() {
			super("Done");
		}
		public void actionPerformed(ActionEvent e) {
			dm.removeDataChangeListener(changeListener);
			List<String> kys = new ArrayList<String>();
			for (AppDataUIData aduid : aduiDataMap.values()) {
				if (aduid.itemEditor != null) {
					//	for (JFrame f : itemEditors.values()) {
					aduid.itemEditor.setVisible(false);
					aduid.itemEditor.dispose();
				}
				if (aduid.delCheck.isSelected()) {
					kys.add(aduid.pk);
				}
			}
			if (newAppDataEditor != null) {
				newAppDataEditor.setVisible(false);
				newAppDataEditor.dispose();
			}
			//for (String k : delChecks.keySet()) {
			//	if (delChecks.get(k).isSelected()) {
			//		kys.add(k);
			//	}
			//}
			dm.delete(kys);
			// todo
			frame.setVisible(false);
			frame.dispose();
		}
	}
	
	public class newItemAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public newItemAction() {
			super("New");
		}
		public void actionPerformed(ActionEvent e) {
			if (newAppDataEditor == null) {
				DataUI dg = new DataUI(app, dm.templateItem.newInstance(), scopes);
				newAppDataEditor = dg.initCreateNewUIWindow();
				dg.setWindowClosingListener(new itemEditorClosingAction(null));
			} else {
				newAppDataEditor.toFront();
			}
		}
	}
	
	public class itemEditorClosingAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		AppDataUIData add;
		public itemEditorClosingAction(AppDataUIData addParam) {
			super();
			add = addParam;
		}
		public void actionPerformed(ActionEvent e) {
			if (add == null) {
				newAppDataEditor = null;
			} else {
				add.itemEditor = null;
			}
		}
	}

	public class dataUIListChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			ChangeEventData ced = (ChangeEventData)e.getSource();

			if (ced.type == ChangeEventData.EditType.DEDIT) {
				AppDataUIData add = aduiDataMap.get(ced.oldPrimaryKey);
				if (add != null) {
					add.label.setText(ced.item.toString());
					add.pk = ced.item.getPrimaryKey();
					aduiDataMap.remove(ced.oldPrimaryKey);
					aduiDataMap.put(add.pk, add);
				}
			} else {
				frame.setVisible(false);
				frame = initEditListWithEditItemUIWindow();
			}
		}
	}

}
