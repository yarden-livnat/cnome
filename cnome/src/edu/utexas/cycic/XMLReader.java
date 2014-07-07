package edu.utexas.cycic;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class XMLReader {
	static String test = "<interleave><element name=\"in_commods\"><oneOrMore><element name=\"val\">" +  
			"<data type=\"token\" /></element></oneOrMore></element><element name=\"capacity\">"+
			"<data type=\"double\" /></element><optional><element name=\"max_inv_size\"><data type=\"double\" />"+
			"</element></optional></interleave>";
			
	static ArrayList<Object> readSchema(String xmlSchema){
		ArrayList<Object> schema = new ArrayList<Object>();
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xmlSchema));
			Document doc = dBuilder.parse(is);
			
			NodeList top = doc.getChildNodes();
			for(int i = 0; i < top.getLength(); i++){
				nodeListener(top.item(i));
			}			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return schema;
	}
	
	static void nodeListener(Node node){
		System.out.println(node.getNodeName());
		NodeList nodes = node.getChildNodes();
		for(int i = 0; i < nodes.getLength(); i++){
			nodeListener(nodes.item(i));
		}
		/*try{

		} catch (Exception ex) {
			System.out.println(node.getNodeName());
		}*/
	}
}
