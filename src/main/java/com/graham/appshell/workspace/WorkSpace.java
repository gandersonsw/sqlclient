/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.workspace;

import java.io.File;

import com.graham.appshell.data.*;

/**
 * WorkSpaceSettings get saved as a singleton in a specific workspace
 * WorkSpace gets saved in a list in the application scope 
 * @author ganderson
 *
 */
@AppDataClassAnn(
	classID = "WorkSpace"
)
public class WorkSpace extends AppDataAbstract implements AppDataScope {

	@AppDataFieldAnn(
		uiLabel = "Path",
		primaryKeyFlag = true
	)
	public String path;

	@AppDataFieldAnn(
		uiLabel = "Active"
	)
	public boolean activeFlag; // if this work space is the current active one

	@Override
	public File getScopeDirectory() {
		return new File(path);
	}

	@Override
	public String getScopeKey() {
		return "W" + path;
	}
	
	@Override
	public String toString() {
		return "Workspace";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		WorkSpace workSpace = (WorkSpace) o;

		if (path != null ? !path.equals(workSpace.path) : workSpace.path != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return path != null ? path.hashCode() : 0;
	}

}
