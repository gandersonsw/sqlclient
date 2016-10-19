/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.tabui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TabUIManager {

	private JTabbedPane mainTabPane;

	// TODO make this private
	public HashMap<AppUIPanelKey,AppUIPanel> uiPanels = new HashMap<>();

	public JTabbedPane buildUI() {
		mainTabPane = new JTabbedPane();
		return mainTabPane;
	}

	public void addSelectedTabChangeListener(ChangeListener tabChangedListener) {
		mainTabPane.addChangeListener(tabChangedListener);
	}

	public void addTab(AppUIPanel uiPanel) {
		if (uiPanel.getKey() == null) {
			int largestId = 0;
			for (AppUIPanel p : getUIPanels(uiPanel.getUIPanelType())) {
				int curId = p.getKey().getUIPanelId();
				if (curId > largestId)
					largestId = curId;
			}
			// TODO this line below is weird
			((AppUIPanelMultiple)uiPanel).setKey(new AppUIPanelKey((AppUIPanelMultipleType)uiPanel.getUIPanelType(), largestId + 1));
		}

		uiPanels.put(uiPanel.getKey(), uiPanel);
		mainTabPane.add(uiPanel.getTabLabel(), uiPanel.getJComponent());

	}

	public void removeTab(AppUIPanelKey k) {
		AppUIPanel p = uiPanels.get(k);
		if (p.close()) {
			mainTabPane.remove(p.getJComponent());
			uiPanels.remove(k);
		}
	}

	public List<AppUIPanel> getUIPanels(AppUIPanelType t) {
		List<AppUIPanel> uiPanelsFound = new ArrayList<>();
		for (AppUIPanelKey k : uiPanels.keySet()) {
			AppUIPanel appUi = uiPanels.get(k);
			if (appUi.getUIPanelType().equals(t)) {
				uiPanelsFound.add(appUi);
			}
		}
		return uiPanelsFound;
	}

	public void setSelectedTab(AppUIPanelKey uiKey) {
		mainTabPane.setSelectedComponent(uiPanels.get(uiKey).getJComponent());
	}

	/**
	 * @return this can return null
	 */
	public AppUIPanel getSelectedTabUIPanel() {
		Component c = mainTabPane.getSelectedComponent();
		if (c == null) {
			return null;
		}
		for (AppUIPanel s : uiPanels.values()) {
			if (s.getJComponent() == c) {
				return s;
			}
		}
		System.out.println("should not be at this line 9139");
		//throw new IllegalArgumentException("should not be at this line");
		return null;
	}

	public void closeSelectedTab() {
		Component c = mainTabPane.getSelectedComponent();
		if (c == null) {
			return;
		}
		AppUIPanelKey panelToRemove = null;
		for (AppUIPanelKey k : uiPanels.keySet()) {
			AppUIPanel p = uiPanels.get(k);
			if (p.getJComponent() == c) {
				if (p.close()) {
					mainTabPane.remove(c);
					panelToRemove = k;
				}
			}
		}
		if (panelToRemove != null) {
			uiPanels.remove(panelToRemove);
		}
	}

}
