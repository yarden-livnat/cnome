package edu.utexas.cycic;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class XMLReader {
	/**
	 * 
	 */
	skinSet SC2 = new skinSet(){
		{
			images.put("reactor", "reactorSC.png");
			images.put("facility", "sourceSC.png");
		}
	};
	
	/**
	 * 
	 */
	static ArrayList<String> blackList = new ArrayList<String>(){
		{
			add("agents:agents:Sink");
			add("agents:agents:Source");
			add("agents:agents:KFacility");
			add("agents:agents:NullInst");
			add("agents:agents:NullRegion");
			add("agents:agents:Prey");
			add("agents:agents:Predator");
			add("stubs:StubFacility:StubFacility");
			add("stubs:StubInst:StubInst");
			add("stubs:StubRegion:StubRegion");
			add("StubFacility/cyclus/StubInst/cyclus/StubRegion:StubRegion:StubRegion");
			add("StubFacility/cyclus/StubInst:StubInst:StubInst");
			add("StubFacility:StubFacility:StubFacility");
			
		}
	};
	
	/**
	 * 
	 */
	static ArrayList<String> facilityList = new ArrayList<String>(){
		{
			/*add(":Brightlite:ReactorFacility");
			add(":Brightlite:FuelfabFacility");
			add(":Brightlite:ReprocessFacility");
			add(":cycaless:BatchReactor");
			add(":cycamore:EnrichmentFacility");
			add(":cycamore:Sink");
			add(":cycamore:Source");
			add(":agents:Source");
			add(":agents:Sink");
			add(":agents:KFacility");
			add(":agents:Prey");
			add(":agents:Predator");*/
		}
	};
	
	/**
	 * 
	 */
	static ArrayList<String> regionList = new ArrayList<String>(){
		{
			/*add(":cycamore:GrowthRegion");
			add(":agents:NullRegion");*/
			
		}
	};
	
	/**
	 * 
	 */
	static ArrayList<String> institutionList = new ArrayList<String>(){
		{
			/*add(":cycaless:DeployInst");
			add(":cycamore:ManagerInst");
			add(":agents:NullInst");*/
		}
	};
	
	/**
	 * 
	 * @param xmlSchema
	 * @return
	 */
	static ArrayList<Object> readSchema(String xmlSchema){
		ArrayList<Object> schema = new ArrayList<Object>();
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xmlSchema));
			Document doc = dBuilder.parse(is);
			
			NodeList top = doc.getChildNodes();
			if(top.item(0).getNodeName() == "interleave"){
				for(int i = 0; i < top.getLength(); i++){
					schema = nodeListener(top.item(i), schema);			
				}
			} else {
				for(int i = 0; i < doc.getChildNodes().getLength(); i++){
					schema = nodeListener(doc, schema);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return schema;
	}
	
	/**
	 * 
	 * @param jsonSchema
	 * @param xmlschema
	 * @return
	 */
	static ArrayList<Object> annotationReader(String jsonSchema, ArrayList<Object> xmlschema){
		Reader schema = new StringReader(jsonSchema);
		JsonReader jsonReader = Json.createReader(schema);
		JsonObject jsonObject = jsonReader.readObject();
		jsonReader.close();
		JsonObject vars = jsonObject.getJsonObject("vars");
		for(int i = 0; i < xmlschema.size(); i++){
			//System.out.println(xmlschema);
			combiner((ArrayList<Object>)xmlschema.get(i), vars);		
		}
		return xmlschema;
	}
	
	/**
	 * 
	 * @param jsonSchema
	 * @return
	 */
	static String entityReader(String jsonSchema){
		Reader schema = new StringReader(jsonSchema);
		JsonReader jsonReader = Json.createReader(schema);
		JsonObject jsonObject = jsonReader.readObject();
		jsonReader.close();
		JsonString string = jsonObject.getJsonString("entity");		
		return string.toString();
	}
	
	/**
	 * 
	 * @param jsonSchema
	 * @return
	 */
	static String nicheReader(String jsonSchema){
		Reader schema = new StringReader(jsonSchema);
		JsonReader jsonReader = Json.createReader(schema);
		JsonObject jsonObject = jsonReader.readObject();
		jsonReader.close();
		JsonString string = jsonObject.getJsonString("niche");		
		return string.toString();
	}
	/**
	 * 
	 * @param dataArray
	 * @param json
	 */
	@SuppressWarnings("unchecked")
	static void combiner(ArrayList<Object> dataArray, JsonObject json){
		JsonObject json_pass;
		if(dataArray.get(0) instanceof ArrayList){
			for(int i = 0; i < dataArray.size(); i++){
				combiner((ArrayList<Object>)dataArray.get(i), json);
			}
		} else if(dataArray.get(1) instanceof ArrayList){
			if(json.get((String)dataArray.get(0)) instanceof JsonString){
				json_pass = json.getJsonObject(json.getJsonString((String)dataArray.get(0)).toString().replaceAll("\"", ""));
			} else {
				json_pass = json.getJsonObject((String)dataArray.get(0));
			}
			cycicResize(dataArray);
			if(dataArray.get(2) == "oneOrMore" || dataArray.get(2) == "zeroOrMore" ){
				cycicResize((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(0));
				if(json_pass.get("uitype") instanceof JsonArray){
					JsonArray array = json_pass.getJsonArray("uitype");
					for(int i = 0; i < ((ArrayList<Object>) dataArray.get(1)).size(); i++){
						String string = array.get(i+1).toString().replaceAll("\"", "");
						cycicResize((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(i));
						((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(i)).set(2, string);
					}
				} else if(json_pass.get("uitype") != null){
					((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(0)).set(2, json_pass.get("uitype").toString().replace("\"", ""));
				}
				//cycicInfoControl(json_pass, (ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(0));
			}
			combiner((ArrayList<Object>)dataArray.get(1), json);
			try{
				cycicInfoControl(json_pass, dataArray);
			} catch (Exception ex){
				
			}
		} else {	
			cycicResize(dataArray);
			if(json.get((String)dataArray.get(0)) instanceof JsonString){
				json_pass = json.getJsonObject(json.getJsonString((String)dataArray.get(0)).toString().replaceAll("\"", ""));
			} else {
				json_pass = json.getJsonObject((String)dataArray.get(0));
			}
			try{
				cycicInfoControl(json_pass, dataArray);
			} catch (Exception ex) {
				//ex.printStackTrace();
			}	
		}
	}
	
	/**
	 * 
	 * @param dataArray
	 * @return
	 */
	static ArrayList<Object> cycicResize(ArrayList<Object> dataArray){
		while(dataArray.size() < 9){
			if(dataArray.size() == 6){
				dataArray.add(0);
			}
			dataArray.add(null);
		}
		return dataArray;
	}
	
	
	/**
	 * 
	 * @param jsonPass
	 * @param dataArray
	 * @return
	 */
	static ArrayList<Object> cycicInfoControl(JsonObject jsonPass, ArrayList<Object> dataArray){
		if(dataArray.get(2) == null){
			dataArray.set(2, "");
			if(jsonPass.get("uitype") != null){
				dataArray.set(2, jsonPass.get("uitype").toString().replace("\"", ""));
			}
		}
		if(jsonPass.get("units") != null){
			dataArray.set(3, jsonPass.get("units").toString());
		}
		if(jsonPass.get("range") != null){
			dataArray.set(4, jsonPass.get("range").toString());
		}
		if(jsonPass.get("default") != null){
			dataArray.set(6, 1);
			dataArray.set(5, jsonPass.get("default").toString());
		}
		if(jsonPass.get("userlevel") != null){
			dataArray.set(6, Integer.parseInt(jsonPass.get("userlevel").toString()));
		}
		if(jsonPass.get("tooltip") != null){
			dataArray.set(7, jsonPass.get("tooltip").toString());
		}
		if(jsonPass.get("doc") != null){
			dataArray.set(8, jsonPass.get("doc").toString());
		}
		return dataArray;
	}
	
	/**
	 * 
	 * @param node
	 * @param array
	 * @return
	 */
	static ArrayList<Object> nodeListener(Node node, ArrayList<Object> array){
		NodeList nodes = node.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++){
			if(nodes.item(i).getNodeName() == "oneOrMore" || nodes.item(i).getNodeName() == "zeroOrMore"){
				try{
					if(nodes.item(i).getParentNode().getParentNode().getNodeName().equalsIgnoreCase("config")){
						ArrayList<Object> newArray = new ArrayList<Object>();
						newArray = nodeListener(nodes.item(i), newArray);
						array.add(newArray);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
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
		}
		return array;
	}
	
	static ArrayList<Object> orMoreInfoControl(JsonObject jsonPass, ArrayList<Object> dataArray){
		
		
		return dataArray;
		
	}
}
