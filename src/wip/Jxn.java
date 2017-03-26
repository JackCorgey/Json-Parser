package wip;

import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Jxn {
	
	private String[] json;
	public JxnObject root;
	
	// this is for testing purposes only
	public Jxn(String _json, boolean yes) {	
		root = new JxnObject(null, JxnObject.Type.Object);
		json = new String[_json.length()];
		json = _json.split("");
	}
	
	public Jxn(String jsonPath) {
		root = new JxnObject(null, JxnObject.Type.Object);
		json = load(jsonPath).split("");
	}
	
	public String load(String jsonPath) {
		try {
			return new String( Files.readAllBytes( Paths.get(jsonPath) ) );
		} catch (IOException e) {
			throw new RuntimeException("Incorrect path: " + jsonPath);
		}
	}
	
	public void parse() {
		findSections(0, json.length, root);
	}
	
	public void findSections(int start, int end, JxnObject obj) {
		for(int i = start, len = end; i < len; ++i) {
			
			if( json[i].equals(":") ) { // ":" indicates a key
				JxnObject sub;
				String label = getLabel(i);
				String val = null;

				// jump to the next non-whitespace character
				// if a curl bracket, recursively call findSections
				// else it's a value
				i = getNext(i);
				if( json[i].equals("{") ) { // we found an object; dive
					sub = new JxnObject(null, JxnObject.Type.Object);
					findSections(i, i = getSectionEnd(i), sub); // push i to end of section
					//System.out.println(i);
				} else {
					sub = processValue(i);
				}
				
				obj.add(label, sub);			
			} 
			
		}	
	}
	
	public void getArray() {
		// need to implement array
	}
	
	// assumption will call when index is a bracket
	public int getSectionEnd(int i) { // doesn't take in account of escapes
		String[] bracket = getBracketType(json[i]);
		
		int complete = 1;		
		while(complete != 0) {
			String curr = json[++i];
			if( curr.equals(bracket[0]) ) {
				++complete;
			} else if ( curr.equals(bracket[1]) ) {
				--complete;
			}
		}
		
		return i;
	}
	
	public String getLabel(int i) {	
		i = getPrev(i); // just to first non-space character
		String label = "", eLabel = json[i];
		
		// if the label is surrounded by quotients 
		// get the label in-between the quotients
		if( eLabel.matches("(\"|\')") ) {
			label = getLabel(--i, eLabel);
		} else { // else  get label between two non-alphanumerics
			label = getLabel(i, " ", "/[", "{", ":");
		}
		
		return label;
	}
	
	private String getLabel(int i, String ...breakPoint) {
		int end = i+1;  // make sure we get the entire label
		
		while( i != -1 && !matches(json[i], breakPoint) ) {
			--i;
		}
			
		return toString( Arrays.copyOfRange(json, ++i, end) );
	}
	
	private JxnObject processValue(int i) {
		boolean isNum = false;
		int start = i;
		String brk = "";
		
		if( json[i].equals("\"") ) {
			brk = "\"";
			start = ++i;
		} else {
			isNum = true;
			brk = " |}|,";
		}

		while( !matches( json[i], brk.split("|") ) ) {
			++i;
		}
		
		JxnObject.Type type = JxnObject.Type.String;
		String val = toString( Arrays.copyOfRange(json, start, i) ); 
		
		if(isNum){
			valNumInput(val, start);
			type = JxnObject.Type.Numeric;
		}
		
		return new JxnObject(val, type);	
	}
	
	private void valNumInput(String num, int i) {
		try {
			Double.parseDouble(num);
		} catch (Exception e) {
			String msg = String.format("(%s) Non-numerical at character %s", num, i);
			throw new RuntimeException(msg);
		}
		
	}
	
	private boolean matches(String val, String ...args) {
		for(String arg : args) {
			if( val.equals(arg) )
				return true;
		}
		return false;
	}
	
	// jumps to the next non-whitespace character
	public int getNext(int i) {
		while( json[i].equals(":") || json[i].equals(" ") ) {
			++i;
		}
		return i;
	}
	
	// jumps to the previous non-whitespace character
	public int getPrev(int i) {
		while( json[i].equals(":") || json[i].equals(" ") ) {
			--i;
		}
		return i;
	}
	
	
	private String[] getBracketType(String type) {
		if( type.equals("{") ) {
			return new String[]{"{","}"};
		} else {
			return new String[]{"[","]"};
		}
	}
	
	public String toString(String[] str) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0, len = str.length; i < len; ++i) {
			sb.append(str[i]);
		}
		return sb.toString();
	}
	
}
