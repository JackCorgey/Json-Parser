package Fiddle;

import java.util.ArrayList;
import java.util.HashMap;

import wip.Jxn;
import wip.JxnObject;

public class test {

	public static void main(String[] args) {
		String json = 
				"'zero': 0, one: {'letter': 'a', 'number': { '2':'foo', '3': 'tree'}, 'g':'4' }, 'two': 43 }"
				.replace("\'", "\"");
		
		Jxn jxn = new Jxn(json, true);

		jxn.parse();
		
		JxnObject r = jxn.root;
		System.out.println( r.type() );
		String i = r.get("one").get("letter").value();
		int num = r.get("zero").value();
		System.out.println( i + " " + r.get("one").get("g").value() );
		
		
		Jxn json1 = new Jxn("src//test.json");
		json1.parse();
		JxnObject root = json1.root;
		
		String url = root.get("widget").get("text").get("nested").get("egg").get("bird").value();
		String url1 = root.get("widget=>image=>src", true).value();
		
		System.out.println(url + " " + url1);
				
		
//		JxnObject root = new JxnObject(null, JxnObject.Type.Object);
//		JxnObject person = new JxnObject(null, JxnObject.Type.Object);
//		person.add( "name", new JxnObject("John", JxnObject.Type.String) );
//		person.add( "age", new JxnObject("19", JxnObject.Type.Numeric) );
//		
//		root.add("person", person);
//		
//		JxnObject objP = root.get("person");
//		int age = objP.get("age").value();
//		System.out.println(age);
	}

}
