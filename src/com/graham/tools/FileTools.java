/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileTools {
	
	public static final int CONVERT_NONE = 1;
	public static final int CONVERT_PC_UNIX = 2;
	public static final int CONVERT_UNIX_PC = 3;

	public static String makeStringPathSafe(String path) {
		StringBuilder ret = new StringBuilder(path.length());
		for (int i = 0; i < path.length(); i++) {
			char c = path.charAt(i);
			if (Character.isLetterOrDigit(c) || c == '-') {
			} else {
				c = '_';
			}
			ret.append(c);
		}
		return ret.toString();
	}
	
	public static void copyFile(File src, File dst, int conversion) throws IOException {
		FileReader in = new FileReader(src);
		FileWriter out = new FileWriter(dst);
		int c;
		
		switch (conversion) {
		case CONVERT_NONE:
			while ((c = in.read()) != -1) {
				out.write(c);
			}
			break;
		case CONVERT_PC_UNIX:
			while ((c = in.read()) != -1) {
				if (c != 13) {
					out.write(c);
				}
			}
			break;
		case CONVERT_UNIX_PC:
			int lastc = 0;
			while ((c = in.read()) != -1) {
				if (c == 10 && lastc != 13) {
					out.write(13);
				}
				out.write(c);
				lastc = c;
			}
			break;
		}

		in.close();
		out.close();
	}
	
	/**
	 * if f is currently and empty directory, do nothing
	 * if f is a directory with stuff in it, delete all files inside it
	 * if f is a file, throw exception
	 * @param f
	 */
	public static void makeEmptyDir(File f) {
		if (f.exists()) {
			if (f.isFile()) {
				throw new IllegalArgumentException("file exists where directory is supposed to be:" + f.getAbsolutePath());
			} else if (f.isDirectory()) {
				cleanDirectory(f);
			}
		} else {
			if (f.mkdirs()) {
				return;
			} else {
				throw new IllegalArgumentException("mkdir failed");
			}
		}
	}
	
	/**
	 * delete this directory and all file and directories in it
	 */
	  static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
	  
	  /**
	   * delete all files and directories in this directory
	   */
	  static public void cleanDirectory(File path) {
			if (path.exists()) {
				File[] files = path.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						deleteDirectory(files[i]);
					} else {
						files[i].delete();
					}
				}
			}
			
		}

	
}
