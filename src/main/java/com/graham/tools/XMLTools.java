/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


public class XMLTools {

	/**
	 * 
	 * @param xml
	 * @return Null if an exception occurs
	 */
	public static Object parseBasicTypesXML(String xml) {
		try {
			final SAXBuilder builder = new SAXBuilder();
			final StringReader xmlReader = new StringReader(xml);
			Document doc = builder.build(xmlReader);
			final Element root = doc.getRootElement();
			
			return xmlConvertElement(root);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static Object xmlConvertElement(Element e) {
		String name = e.getName();
		if (name.equals("Map")) {
			Map ret;
			if ("java.util.HashMap".equals(e.getAttributeValue("type"))) {
				ret = new HashMap();
			} else {
				throw new IllegalArgumentException("unknown type:" + e.getAttribute("type"));
			}
			List childs = e.getChildren();
			Iterator iter = childs.iterator();
			while (iter.hasNext()) {
				Element e2 = (Element)iter.next();
				String key = e2.getAttributeValue("key");
				ret.put(key, xmlConvertElement(e2));
			}
			return ret;
		} else if (name.equals("String")) {
			return e.getText();
		} else if (name.equals("Collection")) {
			Collection ret;
			if ("java.util.ArrayList".equals(e.getAttributeValue("type"))) {
				ret = new ArrayList();
			} else {
				throw new IllegalArgumentException("unknown type:" + e.getAttribute("type"));
			}
			List childs = e.getChildren();
			Iterator iter = childs.iterator();
			while (iter.hasNext()) {
				Element e2 = (Element)iter.next();
				ret.add(xmlConvertElement(e2));
			}
			return ret;
		} else if (name.equals("int")) {
			return Integer.parseInt(e.getText());
		}else if (name.equals("long")) {
			return Long.parseLong(e.getText());
		} else if (name.equals("boolean")) {
			return Boolean.parseBoolean(e.getText());
		} else {
			System.out.println("xmlConvertElement:WARNING:unknown element:" + name);
			return "ERROR:" + name;
		}
	}
	
}
