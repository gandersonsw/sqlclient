/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient.ui.databrowser;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.graham.appshell.data.AppData;
import com.graham.appshell.data.ChangeEventData;

public class RelChangeWatcher implements ChangeListener {
	private JPanel buttonPanel;
	private Map<DataBrowserDefinedRelationship,JButton> buttons = new HashMap<DataBrowserDefinedRelationship,JButton>();
	private DBContext context;
	
	public RelChangeWatcher(
			JPanel buttonPanelParam, 
			DBContext contextParam) {
		context = contextParam;
		if (context.mainPanel == null || context.queryResultsPanel == null || context.queryResults == null) {
			throw new IllegalArgumentException();
		}

		buttonPanel = buttonPanelParam;
		
		// initilize the button panel
		Collection<AppData> r = contextParam.app.appsh.getDataManagerList(DataBrowserDefinedRelationship.class).getList();
		for (AppData item : r) {
			addButton((DataBrowserDefinedRelationship)item);
		}
	}
	
	public void addButton(DataBrowserDefinedRelationship r) {
		boolean b1 = false;
		if (r.allowAnyTable) {
			for (List<Object> col : context.queryResults) {
				if (r.fromColumn.equalsIgnoreCase((String)col.get(0)) &&
							!r.toTable.equalsIgnoreCase(context.t.tableName)) {
					b1 = true;
					break;
				}
			}
		}
		
		if (b1 || r.fromTable.equalsIgnoreCase(context.t.tableName)) {
			JButton b = new JButton(new RelClickedAction(r, context));
			
			b.setToolTipText(r.toTable + "." + r.toColumn + " = this." + r.fromColumn);
			
			buttons.put(r, b);
			buttonPanel.add(b);
			buttonPanel.validate(); // this will force the panel to re do it layout.  otherwise the new button will not be drawn

			//cellRender.addRelationship(r);
			context.relshipTracker.addRelationship(r);
		}
	}
	
	public void removeButton(DataBrowserDefinedRelationship r) {
		System.out.println("not done yet");
	}

	/**
	 * called when the list of relastionships changes
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		ChangeEventData ced = (ChangeEventData)e.getSource();
		if (ced.type.equals(ChangeEventData.EditType.DNEW)) {
			addButton((DataBrowserDefinedRelationship)ced.item);
			context.relshipTracker.addRelationship((DataBrowserDefinedRelationship)ced.item);
		} else if (ced.type.equals(ChangeEventData.EditType.DDELETE)) {
			removeButton((DataBrowserDefinedRelationship)ced.item);
			context.relshipTracker.removeRelationship((DataBrowserDefinedRelationship)ced.item);
		}
	}
}