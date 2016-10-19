/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.data;

import java.io.File;

import com.graham.tools.FileTools;

@AppDataClassAnn(
	classID = "DataGroup"
)
public class DataGroup extends AppDataAbstract implements AppDataScope {

	@AppDataFieldAnn(
		uiLabel = "Name"
	)
	public String groupName;

	@Override
	public String getPrimaryKey() {
		return FileTools.makeStringPathSafe(groupName);
	}
	
	@Override
	public String toString() {
		return groupName;
	}

	@Override
	public String verifyAfterUIOK() {
		if (groupName == null || groupName.length() == 0) {
			return "group name is required";
		}
		return null;
	}

	@Override
	public File getScopeDirectory() {
		return new File(AppScopeImpl.getPrefsAppHomePath(), "GROUP_" + FileTools.makeStringPathSafe(groupName));
	}

	@Override
	public String getScopeKey() {
		return "G" + groupName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DataGroup dataGroup = (DataGroup) o;

		if (groupName != null ? !groupName.equals(dataGroup.groupName) : dataGroup.groupName != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return groupName != null ? groupName.hashCode() : 0;
	}
}
