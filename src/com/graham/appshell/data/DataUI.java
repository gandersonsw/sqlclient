/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.data;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.graham.appshell.App;
import com.graham.appshell.handlers.FloatingFrameQuitListener;
import com.graham.appshell.handlers.SysWindow2;
import com.graham.tools.UITools;
import com.graham.tools.StringTools;

public class DataUI {
	
	private Map<String,JComponent> editors = new HashMap<String,JComponent>();
	private JLabel message;
	private AppData data;
	private boolean createdUIFlag = false;
	private JFrame frame; // this might be null if someone else already has a frame for us
	private ChangeEventData.EditType editType;
	private App app;
	private List<AppDataScope> scopes;
	private JComboBox scopeComboBox;
	private AbstractAction windowCloseAction;
	
	/**
	 * use this if you want UI to allow selection of scope
	 */
	public DataUI(App appParam, AppData dataParam, List<AppDataScope> scopesParam) {
		if (dataParam == null) {
			throw new IllegalArgumentException("AppData required");
		}
		data = dataParam;
		app = appParam;
		scopes = scopesParam;
	}
	
	/**
	 * use this if should use scope already set in data
	 */
	public DataUI(App appParam, AppData dataParam) {
		if (dataParam == null) {
			throw new IllegalArgumentException("AppData required");
		}
		data = dataParam;
		app = appParam;
	}
	
	public void setWindowClosingListener(AbstractAction windowCloseActionParam) {
		windowCloseAction = windowCloseActionParam;
	}
	
	public static JComponent getFieldLabel(AppData dataParam, final String fieldName) {
		Object currentDataField = dataParam.getField(fieldName);
		if (currentDataField instanceof Boolean) { // make a check box
			return null;
			// top.add(new JLabel(""));
		} else {
			return new JLabel(dataParam.getUILabel(fieldName) + ":");
			
		}
	}
	
	public static JComponent getFieldEditor(final AppData dataParam, final String fieldName, final boolean createChangeListener) {
		JComponent f;
		List<String> allowedValues;
		
		Object currentDataField = dataParam.getField(fieldName);
		final boolean editable = dataParam.isUIEditable(fieldName);
		if (currentDataField instanceof Boolean) { // make a check box
			f = new JCheckBox(dataParam.getUILabel(fieldName));
			((JCheckBox)f).setSelected(((Boolean) currentDataField).booleanValue());
			if (createChangeListener) {
				throw new RuntimeException("createChangeListener not supported for this type");
			}
		} else if ((allowedValues = dataParam.getUIAllowedValues(fieldName)) == null) { // make a text field
			JTextField textF = new JTextField(StringTools.notNullStr(dataParam.getField(fieldName)));
			textF.setColumns(20);
			textF.setEditable(editable);
			if (createChangeListener) {
				textF.getDocument().addDocumentListener(new AppDataFieldDocumentL(dataParam, fieldName));
			}
			f = textF;
		} else { // make a combo box
			if (true) { // sort the items
				Object l2[] = allowedValues.toArray();
				Arrays.sort(l2, 0, l2.length);
				List l3 = Arrays.asList(l2);
				allowedValues = (List<String>)l3;
			}
			
			f = new JComboBox();
			for (String i : allowedValues) {
				((JComboBox)f).addItem(i);
			}
			if (dataParam.getField(fieldName) != null) {
				((JComboBox)f).setSelectedItem(dataParam.getField(fieldName));
			}
			if (createChangeListener) {
				throw new RuntimeException("createChangeListener not supported for this type");
			}
		}
		
		final String toolTip = dataParam.getUIToolTipText(fieldName);
		if (toolTip != null) {
			f.setToolTipText(toolTip);
		}
		
		return f;
	}
	
	private JPanel initCreateNewUI() {
		editType = ChangeEventData.EditType.DNEW;
		createdUIFlag = true;
		List<String> fields = data.getFieldNames();
		int fCount = fields.size() + 1;
		boolean showScopesComboBox = scopes != null && scopes.size() > 1;
		if (showScopesComboBox) {
			fCount++;
		}
		JPanel top = new JPanel(new GridLayout(fCount,2,5,5));

		JComponent f;
		
		for (String fieldName : fields) {
			f = getFieldLabel(data, fieldName);
			if (f == null) {
				f = new JLabel("");
			}
			top.add(f);
			
			f = getFieldEditor(data, fieldName, false);
			top.add(f);
			editors.put(fieldName, f);
		}
		
		if (showScopesComboBox) {
			top.add(new JLabel("Scope:"));
			scopeComboBox = new JComboBox();
			for (AppDataScope s : scopes) {
				scopeComboBox.addItem(s);
			}
			top.add(scopeComboBox);
		}

		top.add(new JButton(new itemOkActionClass()));
		top.add(new JButton(new cancelActionClass()));
		
		JPanel ret = new JPanel(new BorderLayout());
		
		ret.add(top, BorderLayout.NORTH);
		message = new JLabel("TEXT");
		ret.add(message, BorderLayout.SOUTH);
		
		return ret;
	}

	public JFrame initCreateNewUIWindow() {
		
		frame = new JFrame();
		frame.setTitle("Define " + data.getClassId());

		frame.add(initCreateNewUI());
		
		SysWindow2 aSymWindow = new SysWindow2(new cancelActionClass());
		frame.addWindowListener(aSymWindow);
	
		frame.pack();
		UITools.center(frame);
		frame.setResizable(false);

		frame.setVisible(true);
		
		app.addAppQuitListener(new FloatingFrameQuitListener(frame));
		
		return frame;
	}
	
	public void setMessage(String txt) {
		message.setText(txt);
	}

	public JFrame initEditUIWindow() {
		JFrame f = initCreateNewUIWindow();
		editType = ChangeEventData.EditType.DEDIT;
		return f;
	}
	
	public class itemOkActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public itemOkActionClass() {
			super("OK");
		}

		public void actionPerformed(ActionEvent e) {
			for (String fieldName : editors.keySet()) {
				Object val = null;
				JComponent f = editors.get(fieldName);
				if (f instanceof JTextField) {
					val = ((JTextField)f).getText();
				} else if (f instanceof JComboBox) {
					val = ((JComboBox)f).getSelectedItem().toString();
				} else if (f instanceof JCheckBox) {
					val = Boolean.valueOf(((JCheckBox)f).isSelected());
				}
				
				if (data.getUITextTrimFlag(fieldName) && val instanceof String) {
					val = ((String)val).trim();
				}
				data.setField(fieldName, val);
			}
			String messageTxt = data.verifyAfterUIOK();
			if (messageTxt == null) {
				if (editType.equals(ChangeEventData.EditType.DNEW)) {
					if (scopes != null) {
						if (scopes.size() > 1) {
							data.setScope((AppDataScope)scopeComboBox.getSelectedItem());
						} else {
							data.setScope(scopes.get(0));
						}
					}
					try {
						app.getDataManagerList(data.getClassId()).add(data);
					} catch (IllegalArgumentException e2) {
						message.setText(e2.getMessage());
						((AppDataAbstract)data).clearScopeBecauseOfFailedPersist();
						return;
					}
				} else if (editType.equals(ChangeEventData.EditType.DEDIT)) {
					app.getDataManagerList(data.getClassId()).changed(data);
					//System.out.println("editing ***************************** ");
				} else {
					System.out.println("other type 5634");
				}

				frame.setVisible(false);

				if (windowCloseAction != null) {
					windowCloseAction.actionPerformed(e);
				}
			} else {
				message.setText(messageTxt);
			}

		}
	}
	
	public class cancelActionClass extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public cancelActionClass() {
			super("Cancel");
		}

		public void actionPerformed(ActionEvent e) {
			frame.setVisible(false);
			if (windowCloseAction != null) {
				windowCloseAction.actionPerformed(e);
			}
		}
	}

}
