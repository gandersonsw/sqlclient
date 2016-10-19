/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.appshell.data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AppDataFieldAnn {
	
	/**
	 * The short name that should be displayed in a UI to identify this field.
	 */
	String uiLabel();
	boolean primaryKeyFlag() default false;

}
