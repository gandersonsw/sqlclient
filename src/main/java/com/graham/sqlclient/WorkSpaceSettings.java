/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.sqlclient;

import java.util.ArrayList;
import java.util.List;

import com.graham.appshell.data.AppDataAbstract;
import com.graham.appshell.data.AppDataClassAnn;
import com.graham.appshell.data.AppDataFieldAnn;

/**
 * WorkSpaceSettings get saved as a singleton in a specific workspace
 * WorkSpace gets saved in a list in the application scope 
 * @author ganderson
 *
 */
@AppDataClassAnn(
	classID = "WorkSpaceSettings"
)
public class WorkSpaceSettings extends AppDataAbstract {

	@AppDataFieldAnn(
		uiLabel = "Web Url"
	)
	public String webUrl;
	
	@AppDataFieldAnn(
		uiLabel = "FTP Url"
	)
	public String ftpUrl;
	
	@AppDataFieldAnn(
		uiLabel = "FTP Username"
	)
	public String ftpUserName;
	
	@AppDataFieldAnn(
		uiLabel = "FTP Password"
	)
	public String ftpPassword;
	
	@AppDataFieldAnn(
		uiLabel = "Groups"
	)
	public List<String> groups = new ArrayList<String>(); // groups that this workspace shares data with
	
	@AppDataFieldAnn(
		uiLabel = "DBList"
	)
	public List<WSDB> dbList = new ArrayList<WSDB>();

	@Override
	public String getPrimaryKey() {
		return "1";
	}
	
	@Override
	public boolean isUIEditable(String fieldName) {
		return false;
	}

}
