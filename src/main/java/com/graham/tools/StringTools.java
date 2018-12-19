/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringTools {
	
	private static SimpleDateFormat dateFormatSQL1 = new SimpleDateFormat("MM/dd/yyyy h:mm:ss aaa");
	
	public static String notNullStr(Object txt) {
		if (txt == null)
			return "";
		return txt.toString();
	}

	/**
	 * @return True if not null, is a String object, and has something besides whitespace
	 */
	public static boolean isStringTrimSpace(Object txt) {
		if (txt == null) {
			return false;
		}
		if (txt instanceof String) {
			return ((String)txt).trim().length() > 0;
		}
		return false;
	}
	
    public static String getExceptionTrace(Exception e) {
        if (e == null)
            return "";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        e.printStackTrace(pw);
        //pw.close();
        return sw.toString();
    }
    
	/** replace exactly one occurence, if no occurence found, throw exception */
	public static String replaceOne(String src, String searchFor, String replaceWith) {
		int i = src.indexOf(searchFor);
		if (i == -1) {
			throw new IllegalArgumentException("replaceOne requires search for string to be found in source string");
		}
		
		return src.substring(0,i) + replaceWith + src.substring(i + searchFor.length());
	}
	
	public static String unescapeTabsAndNewlines(String txt) {
		txt = replaceAll(txt, SearchContext.createReplaceAllContext("\\n", "\n"));
		txt = replaceAll(txt, SearchContext.createReplaceAllContext("\\t", "	"));
		return txt;
	}
	
	public static String replaceAll(String s, SearchContext params) {

		if (s.indexOf(params.getSearchText()) == -1)
			return s;
		
		StringBuffer ret = new StringBuffer();
		
		int i = 0;
		int previ = 0;
		int replaceCount = 0;
		while ((i = s.indexOf(params.getSearchText(), i)) != -1) {
			ret.append(s.substring(previ,i));
			ret.append(params.getReplaceText());
			i += params.getSearchText().length();
			previ = i;
			replaceCount++;
		}
		params.setReplaceCount(replaceCount);
	
		ret.append(s.substring(previ));
		return ret.toString();

	}
	
	public static String getSurroundingSubstring(String txt, int index, int targetLength) {
		int startIndex = index - targetLength / 2;
		int endIndex = index + targetLength / 2;
		
		if (startIndex < 0) {
			endIndex = endIndex - startIndex;
			startIndex = 0;
		}
		
		if (endIndex > txt.length()) {
			startIndex = startIndex - (endIndex - txt.length());
		}
		
		if (startIndex < 0)
			startIndex = 0;
		
		if (endIndex > txt.length() - 1)
			endIndex = txt.length() - 1;
		
		return txt.substring(startIndex, endIndex);
	}
	

    public static void addListItem(StringBuffer sb, String newItem) {
        if (sb.length() > 0)
            sb.append(',');
        sb.append(newItem);
    }
    
    public static ArrayList<String> convertListToArray(String s) {
    	return convertListToArray(s, ",");
    	/*
        ArrayList<String> arr = new ArrayList<String>();
        
        if (s == null || s.length() == 0)
            return arr;
    
        int i = 0;
        int j;
        
        while ((j = s.indexOf(",",i)) != -1) {
            arr.add(decodeListItem(s.substring(i,j)));
            i = j+1;
        }
        
        arr.add(decodeListItem(s.substring(i)));
        
        return arr;
        */
    }
    
    public static ArrayList<String> convertListToArray(String s, String delim) {
        ArrayList<String> arr = new ArrayList<String>();
        
        if (s == null || s.length() == 0)
            return arr;
    
        int i = 0;
        int j;
        
        while ((j = s.indexOf(delim,i)) != -1) {
            arr.add(decodeListItem(s.substring(i,j)));
            i = j+1;
        }
        
        arr.add(decodeListItem(s.substring(i)));
        
        return arr;
    }
    
    public static String convertArrayToList(List<String> arr) {
    	return convertArrayToList(arr, ",");
    }
    
    
    public static String convertArrayToList(List<String> arr, String delim) {
    	StringBuilder sb = new StringBuilder();
    	for (String v : arr) {
    		if (sb.length() > 0) {
    			sb.append(delim);
    		}
    		sb.append(v);
    	}
    	return sb.toString();
    }
            
    private static String decodeListItem(String s) {
        return s;
    }

    public static String simpleEncrypt(String s) {
        int i1 = 1;
        int i2 = 2;
        int tmp;
        StringBuffer ret = new StringBuffer();

        if (s.length() > 3) {
            s = "k" + s.charAt(3) + s.charAt(1) + s.charAt(2) + s.charAt(0)
                    + s.substring(4);
        }

        for (int j = 0; j < s.length(); j++) {

            char c = s.charAt(j);
            char c2;

            if (Character.isDigit(c)) {
                c2 = (char) ('9' - c + '0');

            } else if (Character.isLetter(c)) {
                if (Character.isLowerCase(c)) {
                    c2 = (char) ('z' - c + 'a');
                } else {
                    c2 = (char) ('Z' - c + 'A');
                }
            } else {
                c2 = c;
            }

            ret.append(c2);

            if (j == i1) {
                tmp = i1;
                i1 = i2 + i1;
                i2 = tmp;
                char c3 = (char) ((67 + tmp) % 26 + 'a');
                ret.append(c3);
            }
        }

        return "se" + ret.toString();
    }

    public static String simpleDecrypt(String s) {
        int i1 = 1;
        int i2 = 2;
        int i1offset = 0;
        boolean skipThisSkip = false;
        int tmp;
        StringBuffer ret = new StringBuffer();

        if (!s.startsWith("se"))
            return "error:must start with se";

        s = s.substring(2);

        for (int j = 0; j < s.length(); j++) {

            char c = s.charAt(j);
            char c2;

            if (Character.isDigit(c)) {
                c2 = (char) ('9' - c + '0');

            } else if (Character.isLetter(c)) {
                if (Character.isLowerCase(c)) {
                    c2 = (char) ('z' - c + 'a');
                } else {
                    c2 = (char) ('Z' - c + 'A');
                }
            } else {
                c2 = c;
            }

            if (skipThisSkip) {
                skipThisSkip = false;
            } else {
                if (j == i1 + i1offset) {
                    skipThisSkip = true;
                    i1offset++;
                    tmp = i1;
                    i1 = i2 + i1;
                    i2 = tmp;
                } else {
                    //ret.append(c2);
                }
                ret.append(c2);
            }

        }

        s = ret.toString();

        if (s.length() > 4) {
            s = "" + s.charAt(4) + s.charAt(2) + s.charAt(3) + s.charAt(1)
                    + s.substring(5);
        }

        return s;
    }
    
    public static String pad2DigitNumber(int i) {
    	if (i < 10) {
    		return "0" + i;
    	}
    	return String.valueOf(i);
    }
    
    // MM/DD/YYYY HH:MI:SS AM
    public static String formatDateSQL1(Date d) {
    	return dateFormatSQL1.format(d);
    }
    
    public static boolean parseYesNo(String s) {
    	if (s == null) {
    		return false;
    	}
    	return s.toLowerCase().equals("yes");
    }
    
    public static String formatYesNo(boolean b) {
    	if (b) {
    		return "yes";
    	} else {
    		return "no";
    	}
    }
    
    /**
     * this is case sensitive
     * @param searchStr
     * @param filter
     * @return
     */
    public static int match(String searchStr, String filter) {
    	System.out.println("unimplemented: StringTools.match");
    	return 1;
    }
    
    /**
     * Look for any numerical IDs in the text, anything that is not a numeric will be treaded as a seperator.
     * 
     * @param text
     * @return
     */
	public static Set<String> parseIDs(String text) {
		int curLoc = 0;
		Set<String> ret = new HashSet<String>();
		String curOrderId = "";
		while (curLoc < text.length()) {
			if (Character.isDigit(text.charAt(curLoc))) {
				curOrderId += text.charAt(curLoc);
			} else {
				if (curOrderId.length() > 0) {
					ret.add(curOrderId);
					curOrderId = "";
				}
			}
			curLoc++;
		}
		
		if (curOrderId.length() > 0) {
			ret.add(curOrderId);
		}
		
		return ret;
	}
	
	/**
	 * Replace any digits in the string with "_".
	 * @param s
	 * @return
	 */
	public static String blankAnyDigits(String s) {
		StringBuilder sb = new StringBuilder(s.length());
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isDigit(c)) {
				sb.append('_');
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	public static boolean equalWithNullCheck(final String str1, final String str2) {
		if (str1 == null) {
			return str2 == null;
		} else {
			return str1.equals(str2);
		}
	}
    
    /**
     * 
     * @param txt
     * @param toUpper if true, change all items to upperCase
     * @return
     */
    /*
    public static Set<String> getProgrammingTokens(String txt, boolean toUpper) {
    	HashSet<String> ret = new HashSet<String>();
    	int max = txt.length();
    	boolean inToken = false;
    	int i;
    	int tokenStart = 0;
    	char c;
    	for (i = 0; i < max; i++) {
    		c = txt.charAt(i);
    		if (inToken) {
    			if (Character.isLetterOrDigit(c) || c == '_') {
    			} else {
    				inToken = false;
    				String token = txt.substring(tokenStart, i);
    				if (toUpper) {
    					token = token.toUpperCase();
    				}
    				ret.add(token);
    			}
    		} else {
    			if (Character.isLetter(c) || c == '_') {
    				inToken = true;
    				tokenStart = i;
    			}
    		}
    	}
    	
		if (inToken) {
			String token = txt.substring(tokenStart, i);
			if (toUpper) {
				token = token.toUpperCase();
			}
			ret.add(token);
		}
    	
		return ret;
    }
    */
	
}
