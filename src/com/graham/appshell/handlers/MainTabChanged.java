/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.handlers;

import com.graham.appshell.tabui.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.*;

public class MainTabChanged implements ChangeListener {

	private HashMap<AppUIPanelKey,JMenu> uiMenus = new HashMap<>();
	private TabUIManager tabManager;
	private JMenuBar mainMenuBar;
	private JMenu currentMenu;

	public MainTabChanged(TabUIManager tabManagerParam, JMenuBar mainMenuBarParam) {
		tabManager = tabManagerParam;
		mainMenuBar = mainMenuBarParam;
	}

	public synchronized void stateChanged(ChangeEvent arg0) {

		if (currentMenu != null) {
			mainMenuBar.remove(currentMenu);
			mainMenuBar.repaint();
		}

		AppUIPanel uiPanel = tabManager.getSelectedTabUIPanel();
		if (uiPanel == null) {
			return;
		}

		currentMenu = uiMenus.get(uiPanel.getKey());
		if (currentMenu == null) {
			List<Action> actions = uiPanel.getActionsForMenu();
			if (actions == null || actions.size() == 0) {
				return;
			}
			currentMenu = new JMenu(uiPanel.getTabLabel());
			for (Action a : actions) {
				currentMenu.add(a);
			}
			uiMenus.put(uiPanel.getKey(), currentMenu);
		}

		mainMenuBar.add(currentMenu, 3);
		mainMenuBar.repaint();
	}

}