/* Copyright (C) 2016 Graham Anderson gandersonsw@gmail.com - All Rights Reserved */
package com.graham.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

public class UITools {

    public static void setFrameSizeAndCenter(java.awt.Component c, int width, int height) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = dim.getWidth();//1024;
        double screenHeight = dim.getHeight();//768;
        
        c.setSize(width, height);
        c.setLocation((int)((screenWidth - width) / 2), (int)((screenHeight - height) / 2));
    }
    
    public static void center(java.awt.Component c) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = dim.getWidth();
        double screenHeight = dim.getHeight();
        Dimension d = c.getSize();
        c.setLocation((int)((screenWidth - d.getWidth()) / 2), (int)((screenHeight - d.getHeight()) / 2));
    }
    

    public static void showStopDialog(Frame owner, String message, Exception e) {
        UITools u = new UITools();
        if (message == null)
            message = "An error has occured";
        u.showStopDialog2(owner, message, e);
    }
    
    private void showStopDialog2(Frame owner, String message, Exception e) {
        JDialog d;
        if (owner == null)
            d = new JDialog();
        else
            d = new JDialog(owner, true);
        
        d.getContentPane().setLayout(new BorderLayout());
        JTextArea txt = new JTextArea(message,4,30);
        txt.setBorder(new EmptyBorder(5,5,5,5));
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setEditable(false);
        d.getContentPane().add(txt, BorderLayout.NORTH);
        
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout());
        cancelActionClass cancelAction = new cancelActionClass("Cancel", d);
        p.add(new JButton(cancelAction));
        if (e != null) {
            showDetailsActionClass showDetailsAction = new showDetailsActionClass("Show Details", d, e);
            p.add(new JButton(showDetailsAction));
            
            quitActionClass quitAction = new quitActionClass("Quit", d);
            p.add(new JButton(quitAction));
        }
        d.getContentPane().add(p, BorderLayout.SOUTH);
        setFrameSizeAndCenter(d,400,160);
        d.pack();
        d.setVisible(true);
    }


    public class cancelActionClass extends AbstractAction {
        private static final long serialVersionUID = 1L;
        JDialog d;
        public cancelActionClass(String text, JDialog paramd) {
            super(text);
            d = paramd;
        }
        public void actionPerformed(ActionEvent e) {
            d.dispose();
        }
    }
    
    public class quitActionClass extends AbstractAction {
        private static final long serialVersionUID = 1L;
        JDialog d;
        public quitActionClass(String text, JDialog paramd) {
            super(text);
            d = paramd;
        }
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
            //d.dispose();
        }
    }
    
    public class showDetailsActionClass extends AbstractAction {
        private static final long serialVersionUID = 1L;
        JDialog d;
        Exception exp;
        public showDetailsActionClass(String text, JDialog paramd, Exception e) {
            super(text);
            d = paramd;
            exp = e;
        }
        public void actionPerformed(ActionEvent evt) {
            JTextArea txt = new JTextArea(StringTools.getExceptionTrace(exp));
            txt.setBorder(new EmptyBorder(5,5,5,5));
            txt.setLineWrap(false);
            txt.setEditable(false);
            d.getContentPane().add(txt, BorderLayout.CENTER);
            setEnabled(false);
            d.pack();
        }
    }
    
    /**
     * If there is a selection, return that text. Otherwise, return the current paragraph. 
     * The text where the selection is, up to the next blank newline, and back to the 
     * previous blank newline.
     * 
     * delete1 should be true if the user just pressed the "delete" button.
     * @return
     */
	public static String getSelectedTextOrBlockAtInsert(JTextArea ta, boolean delete1) {
		
		try {
			String selectedText = ta.getSelectedText();
			if (selectedText != null && selectedText.length() != 0) {
				return selectedText;
			}
		} catch (Exception e) {
			// ignore this Illegal Argument Exception
		}
		
		int i = ta.getSelectionStart();
		String s = ta.getText();
		int begin = i;
		int end = i;
		int lastNewline = -1;
		
		while (begin >= s.length()) { // this can happen is the selection is changing
			begin--;
		}
		
		if (delete1) {
			if (begin > 1) { // if the user just deleted a single character, the selection may have not been updated yet
				if (Character.isWhitespace(s.charAt(begin))) {
					begin = begin - 2;
					end = end - 1;
				}
			}
		}
		
		while (begin > 0) {
			if (s.charAt(begin) == '\n') {
				if (lastNewline == -1) {
					lastNewline = begin;
				} else {
					if (s.substring(begin, lastNewline).trim().length() == 0) {
						begin = lastNewline + 1;
						break;
					} else {
						lastNewline = begin;
					}
				}
			}
			begin--;
		}
		
		lastNewline = -1;
		while (end < s.length()) {
			if (s.charAt(end) == '\n') {
				if (lastNewline == -1) {
					lastNewline = end;
				} else {
					if (s.substring(lastNewline, end).trim().length() == 0) {
						end = lastNewline;
						break;
					} else {
						lastNewline = end;
					}
				}
			}
			end++;
		}
		
		if (end >= s.length()) {
			end = s.length() - 1;
		}
		
		if (end < 0) {
			end = 0;
		}
		if (begin < 0) {
			begin = 0;
		}
		
		if (begin > end) {
			begin = end;
		}
		
		if (end == s.length() - 1) {
			end = s.length();
		}
		
		return s.substring(begin, end);
	}

	static public class SubString {
		public String s;
		public int startIndex;
		public int endIndex;
	}

	/**
	 * delete1 should be true if the user just pressed the "delete" button.
	 */
	public static SubString getTokenAtInsert(JTextArea ta, boolean delete1) {

		int i = ta.getSelectionStart();
		String s = ta.getText();
		int begin = i;
		int end = i;
		
		while (begin >= s.length()) { // this can happen is the selection is changing
			begin--;
		}
		
		char c;
		if (delete1) {
			if (begin > 1) { // if the user just deleted a single character, the selection may have not been updated yet
				c = s.charAt(begin);
				if (Character.isWhitespace(c)) {
					begin = begin - 2;
					end = end - 1;
				}
			}
		}

		while (begin > 0) {
			c = s.charAt(begin);

			if (Character.isLetterOrDigit(c) || c == '_' || c == '.') {
			} else {
				//begin++;
				break;
			}
			begin--;
		}

		// commenting this out will cause the selection to always stop at the current selection point

		while (end < s.length()) {
			c = s.charAt(end);
			if (Character.isLetterOrDigit(c) || c == '_' || c == '.') {
			} else {
				break;
			}
			end++;
		}

		if (end >= s.length()) {
			end = s.length() - 1;
		}

		if (begin < end) {
			begin++; // the character we are at is bad, but we need it in some cases
		}
		
		if (begin < 0) {
			begin = 0;
		}
		if (end < 0) {
			end = 0;
		}
		
		if (end == s.length() - 1) {
			end = s.length();
		}

		SubString ret = new SubString();
		ret.startIndex = begin;
		ret.endIndex = end;
		ret.s = s.substring(begin, end);
		while (ret.s.length() > 0 && Character.isWhitespace(ret.s.charAt(0))) {
			ret.startIndex++;
			ret.s = s.substring(ret.startIndex, end);
		}
		try {
			if (ret.startIndex == ret.endIndex && Character.isWhitespace(s.substring(ret.startIndex, ret.startIndex+1).charAt(0))) {
				ret.startIndex++;
				ret.endIndex++;
			}
		} catch (Exception e) {
			// ignore this
			//e.printStackTrace();
		}
		if (ret.s.endsWith("\n")) {
			ret.s = ret.s.substring(0, ret.s.length() - 1);
			ret.endIndex--;
		}
		return ret;
	}

	static public boolean findTextInTextArea(JTextArea te, SearchContext params) {
		if (params.isReplaceAll()) {
			String t = te.getText();
			String newT = StringTools.replaceAll(t, params);
			te.setText(newT);
			return true;
		}

		String editorText = te.getText();
		String searchText;
		if (params.isCaseSensitive()) {
			searchText = params.getSearchText();
		} else {
			editorText = editorText.toUpperCase();
			searchText = params.getSearchText().toUpperCase();
		}

		int foundIndex;
		if (params.isStartFromBegining()) {
			foundIndex = editorText.indexOf(searchText);
		} else {
			foundIndex = editorText.indexOf(searchText, te.getSelectionEnd());
		}

		if (foundIndex == -1) {
			return false;
		} else {
			te.setSelectionStart(foundIndex);
			te.setSelectionEnd(foundIndex + searchText.length());

			try {
				Rectangle viewRect = te.modelToView(foundIndex);
				te.scrollRectToVisible(viewRect);
			} catch (BadLocationException e) {
				// TODO
				e.printStackTrace();
			}

			return true;
		}

	}

}
