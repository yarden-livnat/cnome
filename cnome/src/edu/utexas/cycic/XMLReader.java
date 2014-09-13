package edu.utexas.cycic;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.controlsfx.dialog.Dialog;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class XMLReader {
	static ArrayList<String> facilityList = new ArrayList<String>(){
		{
			add(":Brightlite:ReactorFacility");
			add(":Brightlite:FuelfabFacility");
			//add(":cycamore:BatchReactor");
			add(":cycamore:EnrichmentFacility");
			add(":cycamore:Sink");
			add(":cycamore:Source");
			add(":agents:KFacility");
			add(":agents:Prey");
			add(":agents:Predator");
		}
	};
	static ArrayList<String> regionList = new ArrayList<String>(){
		{
			add(":cycamore:GrowthRegion");
		}
	};
	static ArrayList<String> institutionList = new ArrayList<String>(){
		{
			//add(":cycamore:DeployInst");
			add(":cycamore:ManagerInst");
		}
	};
	static String test = "<interleave><element name=\"in_commods\"><oneOrMore><element name=\"val\">" +  
			"<data type=\"token\" /></element></oneOrMore></element><element name=\"capacity\">"+
			"<data type=\"double\" /></element><optional><element name=\"max_inv_size\"><data type=\"double\" />"+
			"</element></optional></interleave>";
	static String jsonTest = "{"+
		"\"doc\" : \"A minimum implementation sink facility that accepts specified amounts of commodities from other agents\","+
		"\"vars\" : {" +
		"\"capacity\" : {" +
        "\"doc\" : \"capacity the sink facility can accept at each time step\"," +
        "\"index\" : 1, \"tooltip\" : \"sink capacity\", \"type\" : \"double\"}," +
        "\"in_commods\" : {\"doc\" : \"commodities that the sink facility accepts\", \"index\" : 0," +
        "\"schematype\" : \"token\", \"tooltip\" : \"input commodities\", \"type\" : [ \"std::vector\", \"std::string\" ]" +
      	"}, \"inventory\" : {\"capacity\" : \"max_inv_size\", \"index\" : 3, \"type\" : \"cyclus::toolkit::ResourceBuff\"},"+
      	"\"max_inv_size\" : {\"default\" : 1.000000000000000e+299, \"doc\" : \"total maximum inventory size of sink facility\","+
        "\"index\" : 2, \"tooltip\" : \"sink maximum inventory size\", \"type\" : \"double\"}}}";
			
	static ArrayList<Object> readSchema(String xmlSchema){
		ArrayList<Object> schema = new ArrayList<Object>();
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xmlSchema));
			Document doc = dBuilder.parse(is);
			
			NodeList top = doc.getChildNodes();
			for(int i = 0; i < top.getLength(); i++){
				schema = nodeListener(top.item(i), schema);
			}
			//System.out.println(schema);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return schema;
	}
	
	static ArrayList<Object> annotationReader(String jsonSchema, ArrayList<Object> xmlschema){
		Reader schema = new StringReader(jsonSchema);
		JsonReader jsonReader = Json.createReader(schema);
		JsonObject jsonObject = jsonReader.readObject();
		jsonReader.close();
		JsonObject vars = jsonObject.getJsonObject("vars");
		for(int i = 0; i < xmlschema.size(); i++){
			//System.out.println(xmlschema.get(i));
			combiner((ArrayList<Object>)xmlschema.get(i), vars);		
		}
		//System.out.println(xmlschema);
		return xmlschema;
	}
	
	static void combiner(ArrayList<Object> dataArray, JsonObject json){
		if(dataArray.get(0) instanceof ArrayList){
			for(int i = 0; i < dataArray.size(); i++){
				//System.out.println(dataArray.get(i));
				combiner((ArrayList<Object>)dataArray.get(i), json);
			}
		} else if(dataArray.get(1) instanceof ArrayList){
			JsonObject json_pass = json.getJsonObject((String)dataArray.get(0));
			while(dataArray.size() < 9){
				if(dataArray.size() == 6){
					dataArray.add(0);
				}
				dataArray.add(null);
			}
			if(dataArray.get(2) == "oneOrMore"){
				if(json_pass.get("cycic") != null){
					((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(0)).add(json_pass.get("cycic").toString().replace("\"", ""));
				} else {
					//((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(0)).add("");
				}
			} else if (dataArray.get(2) == "zeroOrMore"){
				if(json_pass.get("cycic") != null){
					((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(0)).add(json_pass.get("cycic").toString().replace("\"", ""));
				} else {
					//((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(0)).add("");
				}
			}
			combiner((ArrayList<Object>)dataArray.get(1), json);
			try{
				if(json_pass.get("units") != null){
					dataArray.set(3, json_pass.get("units").toString());
				}
				if(json_pass.get("range") != null){
					dataArray.set(4, json_pass.get("range").toString());
				}
				if(json_pass.get("default") != null){
					dataArray.set(6, 1);
					dataArray.set(5, json_pass.get("default").toString());
				}
				if(json_pass.get("userlevel") != null){
					dataArray.set(6, json_pass.get("userlevel"));
				}
				if(json_pass.get("tooltip") != null){
					dataArray.set(7, json_pass.get("tooltip").toString());
				}
				if(json_pass.get("doc") != null){
					dataArray.set(8, json_pass.get("doc").toString());
				}
			} catch (Exception ex){
				//ex.printStackTrace();
			}
		} else {
			while(dataArray.size() < 9){
				if(dataArray.size() == 6){
					dataArray.add(0);
				}
				dataArray.add(null);
			}
			JsonObject json_pass = json.getJsonObject((String)dataArray.get(0));
			try{
				if(dataArray.get(2) == null){
					dataArray.set(2, "");
					if(json_pass.get("cycic") != null){
						dataArray.set(2, json_pass.get("cycic").toString().replace("\"", ""));
					}
				}
				if(json_pass.get("units") != null){
					dataArray.set(3, json_pass.get("units").toString());
				}
				if(json_pass.get("range") != null){
					dataArray.set(4, json_pass.get("range").toString());
				}
				if(json_pass.get("default") != null){
					dataArray.set(6, 1);
					dataArray.set(5, json_pass.get("default").toString());
				}
				if(json_pass.get("userlevel") != null){
					dataArray.set(6, json_pass.get("userlevel"));
				}
				if(json_pass.get("tooltip") != null){
					dataArray.set(7, json_pass.get("tooltip").toString());
				}
				if(json_pass.get("doc") != null){
					dataArray.set(8, json_pass.get("doc").toString());
				}
			} catch (Exception ex) {
				//ex.printStackTrace();
			}	
		}
	}
	
	
	static ArrayList<Object> nodeListener(Node node, ArrayList<Object> array){
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++){
			if(nodes.item(i).getNodeName() == "oneOrMore" || nodes.item(i).getNodeName() == "zeroOrMore"){
				/*if(nodes.item(i).getParentNode().getParentNode().getNodeName().equalsIgnoreCase("config")){
					Dialog dlg = new Dialog(null , "Error");
					
				}*/
				ArrayList<Object> newArray = new ArrayList<Object>();
				newArray = nodeListener(nodes.item(i), newArray);
				array.add(newArray);
				array.add(nodes.item(i).getNodeName());
			}
			if(nodes.item(i).getNodeName() == "element"){
				ArrayList<Object> newArray = new ArrayList<Object>();
				for(int j = 0; j < nodes.item(i).getAttributes().getLength(); j++){
					if (nodes.item(i).getAttributes().item(j).getNodeName() == "name"){
						newArray.add(nodes.item(i).getAttributes().item(j).getNodeValue());
					}
				}
				array.add(nodeListener(nodes.item(i), newArray));
			}
			if(nodes.item(i).getNodeName() == "optional"){
				Node newNode = nodes.item(i).getChildNodes().item(1);
				//System.out.println(newNode);
				ArrayList<Object> newArray = new ArrayList<Object>();
				for(int j = 0; j < newNode.getAttributes().getLength(); j++){
					if (newNode.getAttributes().item(j).getNodeName() == "name"){
						newArray.add(newNode.getAttributes().item(j).getNodeValue());
					}
				}
				array.add(nodeListener(newNode, newArray));
			}
			if(nodes.item(i).getNodeName() == "data"){
				for(int j = 0; j < nodes.item(i).getAttributes().getLength(); j++){
					if(nodes.item(i).getAttributes().item(j).getNodeName() == "type"){
						array.add(1, nodes.item(i).getAttributes().item(j).getNodeValue());
					}
				}
			}
			//System.out.println(array);
		}
		return array;
	}
}
