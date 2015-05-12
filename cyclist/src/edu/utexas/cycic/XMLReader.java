package edu.utexas.cycic;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javafx.scene.image.Image;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
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
	static skinSet SC2 = new skinSet(){
		{
			name = "SC2";
			images.put("reactor", new Image(new File("skinImages/reactor.png").toURI().toString()));
			images.put("facility", new Image(new File("skinImages/sourceFacSC2.jpg").toURI().toString()));
		}
	};
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	 public static skinSet loadSkin(String path){
		skinSet skin = new skinSet(){
			{
				name = "DSARR";
				images.put("abr", new Image(new File(path + "/skinImages/fuelcycle_abr.png").toURI().toString(), 100, 100, false, false));
				images.put("facility", new Image(new File(path + "/skinImages/fuelcycle_enr.png").toURI().toString()));
				images.put("fuel fabrication", new Image(new File(path + "/skinImages/fuelcycle_fab.png").toURI().toString(), 100, 100, false, false));
				images.put("repository", new Image(new File(path + "/skinImages/fuelcycle_geo.png").toURI().toString()));
				images.put("mine", new Image(new File(path + "/skinImages/fuelcycle_mine.png").toURI().toString()));
				images.put("reactor", new Image(new File(path + "/skinImages/fuelcycle_rxtr.png").toURI().toString(), true));
				images.put("reprocessing", new Image(new File(path + "/skinImages/fuelcycle_sep.png").toURI().toString()));
			}
		};
		return skin;
	}
	
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
			add(":cycaless:BatchReactor");
			add(":cycamore:BatchReactor");
			//add(":cycamore:Reactor");
			//add(":cycamore:Separations");
			//add(":cycamore:FuelFab");
			//add(":cycamore:Sink");
			//add(":cycamore:Source");
			//add(":cycamore:Enrichment");
			add("commodconverter:CommodConverter:CommodConverter");
		}
	};
	
	/**
	 * 
	 */
	static ArrayList<String> facilityList = new ArrayList<String>(){
		{
			
		}
	};
	
	/**
	 * 
	 */
	static ArrayList<String> regionList = new ArrayList<String>(){
		{
		
		}
	};
	
	/**
	 * 
	 */
	static ArrayList<String> institutionList = new ArrayList<String>(){
		{

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
					System.out.println(top.item(i));
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
	 * @param xmlSchema
	 * @return
	 */
	static ArrayList<String> readSchema_new(String xmlSchema){
		ArrayList<String> schema = new ArrayList<String>();
		try{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xmlSchema));
			Document doc = dBuilder.parse(is);
			NodeList top = doc.getChildNodes();
			NodeList node = top.item(0).getChildNodes();
			for(int i = 1; i < node.getLength(); i+=2){
				switch (node.item(i).getNodeName()){
				case "optional":
					Node temp_node = node.item(i).getChildNodes().item(1);
					for(int k = 0; k < temp_node.getAttributes().getLength(); k++){
						if (( temp_node).getAttributes().item(k).getNodeName() == "name"){
							System.out.println(temp_node.getAttributes().item(k).getNodeValue());
							schema.add((temp_node).getAttributes().item(k).getNodeValue());
						} 
					}
					break;
				default:
					for(int j = 0; j < node.item(i).getAttributes().getLength(); j++){
						if (node.item(i).getAttributes().item(j).getNodeName() == "name"){
							System.out.println(node.item(i).getAttributes().item(j).getNodeValue());
							schema.add(node.item(i).getAttributes().item(j).getNodeValue());
						} 
					}
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
		//System.out.println(dataArray);
		//System.out.println(json);
		JsonObject json_pass;		
		if(dataArray.get(0) instanceof ArrayList){
			for(int i = 0; i < dataArray.size(); i++){
				combiner((ArrayList<Object>)dataArray.get(i), json);
			}
		} else if(dataArray.get(1) instanceof ArrayList){
			if(json == null){
				cycicResize(dataArray);
				combiner((ArrayList<Object>)dataArray.get(1), json);
				return;
			} else if(json.get((String)dataArray.get(0)) instanceof JsonString){
				json_pass = json.getJsonObject(json.getJsonString((String)dataArray.get(0)).toString().replaceAll("\"", ""));
			} else {
				json_pass = json.getJsonObject((String)dataArray.get(0));
			}
			cycicResize(dataArray);
			if(dataArray.get(2).toString().equalsIgnoreCase("oneormore") || dataArray.get(2).toString().equalsIgnoreCase("zeroormore")){
				orMoreInfoControl(json_pass, dataArray);
			}
			if(json_pass == null){
				combiner((ArrayList<Object>)dataArray.get(1), json);
			} else {
				combiner((ArrayList<Object>)dataArray.get(1), json_pass);
			}
			try{
				cycicInfoControl(json_pass, dataArray);
			} catch (Exception ex){
				//ex.printStackTrace();
			}
		} else {
			cycicResize(dataArray);
			if(json == null){
				return;
			} else if(json.get((String)dataArray.get(0)) instanceof JsonString){
				json_pass = json.getJsonObject(json.getJsonString((String)dataArray.get(0)).toString().replaceAll("\"", ""));
			}else {
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
		while(dataArray.size() < 10){
			if(dataArray.size() == 6){
				dataArray.add(0);
			}
			if(dataArray.size() == 3){
				if(dataArray.get(2) == null){
					//dataArray.set(2, dataArray.get(1));
				}
			}
			dataArray.add(null);
		}
		return dataArray;
	}
	
	/**
	 * 
	 * @param dataArray
	 * @return
	 */
	static ArrayList<Object> cycicResizeNew(ArrayList<Object> dataArray){
		while(dataArray.size() < 10){
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
		if(jsonPass.get("categorical") != null){
			dataArray.set(4, jsonPass.get("categorical").toString());
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
		if(jsonPass.get("uilabel") != null){
			dataArray.set(9, jsonPass.get("uilabel").toString().replaceAll("\"", ""));
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
			switch (nodes.item(i).getNodeName()){
			case "oneOrMore":
			case "interleave":
			case "zeroOrMore":
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
				break;
			case "element":
				ArrayList<Object> newArray1 = new ArrayList<Object>();	
				for(int j = 0; j < nodes.item(i).getAttributes().getLength(); j++){
					if (nodes.item(i).getAttributes().item(j).getNodeName() == "name"){
						newArray1.add(nodes.item(i).getAttributes().item(j).getNodeValue());
					} 
				}
				array.add(nodeListener(nodes.item(i), newArray1));
				break;
			case "optional":
				Node newNode = nodes.item(i).getChildNodes().item(1);
				ArrayList<Object> newArray11 = new ArrayList<Object>();
				for(int j = 0; j < newNode.getAttributes().getLength(); j++){
					if (newNode.getAttributes().item(j).getNodeName() == "name"){
						newArray11.add(newNode.getAttributes().item(j).getNodeValue());
					}
				}
				array.add(nodeListener(newNode, newArray11));
				break;
			case "data":
				for(int j = 0; j < nodes.item(i).getAttributes().getLength(); j++){
					if(nodes.item(i).getAttributes().item(j).getNodeName() == "type"){
						array.add(1, nodes.item(i).getAttributes().item(j).getNodeValue());
					}
				}
			default: 
				break;
			}
		}
		return array;
	}
	
	/**
	 * 
	 * @param jsonPass
	 * @param dataArray
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static ArrayList<Object> orMoreInfoControl(JsonObject jsonPass, ArrayList<Object> dataArray){
		cycicResize((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(0));
        if(jsonPass == null){
        } else if(jsonPass.get("uitype") instanceof JsonArray){
			JsonArray array = jsonPass.getJsonArray("uitype");
            System.out.println(array);
            System.out.println(dataArray);
			/*for(int i = 0; i < ((ArrayList<Object>) dataArray.get(1)).size(); i++){
				String string = array.get(i+1).toString().replaceAll("\"", "");
				//cycicResize((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(i));
				((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(i)).set(2, string);
			}*/
        } else if(jsonPass.get("uitype") != null){
			((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(0)).set(2, jsonPass.get("uitype").toString().replace("\"", ""));
		}
		
        if(jsonPass == null){
        } else if(jsonPass.get("uilabel") instanceof JsonArray){
			JsonArray array = jsonPass.getJsonArray("uilabel");
			for(int i = 0; i < ((ArrayList<Object>) dataArray.get(1)).size(); i++){
				String string = array.get(i+1).toString().replaceAll("\"", "");
				cycicResize((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(i));
				((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(i)).set(9, string);
			}
		} else if(jsonPass.get("uilabel") != null){
			((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(0)).set(9, jsonPass.get("uilabel").toString().replace("\"", ""));
		}
		
        if(jsonPass == null){
        } else if(jsonPass.get("tooltip") instanceof JsonArray){
			JsonArray array = jsonPass.getJsonArray("tooltip");
			for(int i = 0; i < ((ArrayList<Object>) dataArray.get(1)).size(); i++){
				String string = array.get(i+1).toString().replaceAll("\"", "");
				cycicResize((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(i));
				((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(i)).set(7, string);
			}
		} else if(jsonPass.get("tooltip") != null){
			((ArrayList<Object>) ((ArrayList<Object>) dataArray.get(1)).get(0)).set(7, jsonPass.get("tooltip").toString().replace("\"", ""));
		}
		return dataArray;
		
	}

	static ArrayList<Object> nodeBuilder(JsonObject anno, ArrayList<Object> facArray, ArrayList<String> vars){
		System.out.println("nodeStart");
		for(int i = 0; i < vars.size(); i++){
			String var = vars.get(i);	
			System.out.println(var);
			JsonObject anno1 = anno.getJsonObject(var);
			System.out.println(anno1);
			ValueType test = anno1.get("type").getValueType();
			if(test == ValueType.ARRAY){
				JsonArray type = anno1.getJsonArray("type");
				JsonArray alias = anno1.getJsonArray("alias");
				JsonArray uitype = anno1.getJsonArray("uitype");
				ArrayList<Object> newArray = new ArrayList<Object>();
				newArray = annotationBuilder(type, alias, uitype, newArray);
				System.out.println(newArray);
				facArray.add(newArray);
			} else {
				JsonString type = anno1.getJsonString("type");
				JsonString alias = anno1.getJsonString("alias");
				JsonString uitype = anno1.getJsonString("uitype");
				String alias_s, uitype_s;
				ArrayList<Object> newArray = new ArrayList<Object>();
				if(alias == null){
					alias_s = var;
				} else {
					alias_s = alias.toString();
				}
				if (uitype == null){
					uitype_s = type.toString();
				} else {
					uitype_s = uitype.toString();
				}
				newArray = stringAnnotation(type.toString(), alias_s, uitype_s, newArray);
				System.out.println(newArray);
				facArray.add(newArray);
			}
		}
		System.out.println("nodeEnd");
		return facArray;
	}
	
	/**
	 * 
	 * @param type
	 * @param alias
	 * @param uitype
	 * @param facArray
	 * @return
	 */
	static ArrayList<Object> annotationBuilder(JsonArray type, JsonArray alias, JsonArray uitype, ArrayList<Object> facArray){
		System.out.println("annoStart");
		System.out.println(type);
		switch (type.getJsonString(0).toString().replaceAll("\"", "")){
		case "std::map":
			System.out.println("Map");
			ArrayList<Object> structArray = new ArrayList<Object>();
			for(int i = 1; i < type.size(); i++){
				if(type.get(i) instanceof JsonArray){
					ArrayList<Object> tempArray = new ArrayList<Object>();
					tempArray = annotationBuilder(type.getJsonArray(i), alias.getJsonArray(i), uitype.getJsonArray(i), tempArray);
					structArray.add(tempArray);
				} else {
					String alias_s, uitype_s;
					ArrayList<Object> newArray = new ArrayList<Object>();
					if(alias == null){
						alias_s = "key";
					} else {
						alias_s = alias.getString(i);
					}
					if (uitype == null){
						uitype_s = type.getString(i);
					} else {
						uitype_s = uitype.getString(i);
					}
					newArray = stringAnnotation(type.getString(i), alias_s, uitype_s, newArray);
					System.out.println(newArray);
					structArray.add(newArray);
				}
			}
			if(alias == null){
				facArray.add("Map");		
			} else {
				System.out.println(alias);
				facArray.add(alias.getJsonArray(0).getString(0));		
			}
			facArray.add(structArray);
			facArray.add("oneOrMore");
			cycicResize(facArray);
			break;
		case "std::pair":
			System.out.println("Pair");
			ArrayList<Object> structArrayPair = new ArrayList<Object>();
			for(int i = 1; i < type.size(); i++){
				if(type.get(i) instanceof JsonArray){
					ArrayList<Object> tempArray = new ArrayList<Object>();
					tempArray = annotationBuilder(type.getJsonArray(i), alias.getJsonArray(i), uitype.getJsonArray(i), tempArray);
					structArrayPair.add(tempArray);
				} else {
					ArrayList<Object> tempArray = new ArrayList<Object>();
					String alias_s, uitype_s;
					if(alias == null){
						alias_s = "key";
					} else {
						alias_s = alias.getString(i);
					}
					if (uitype == null){
						uitype_s = type.getString(i);
					} else {
						uitype_s = uitype.getString(i);
					}
					tempArray = stringAnnotation(type.getString(i), alias_s, uitype_s, tempArray);
					structArrayPair.add(tempArray);
				}
			}
			if(alias == null){
				facArray.add("Pair");		
			} else {
				facArray.add(alias.getString(0));		
			}
			facArray.add(structArrayPair);
			facArray.add("pair");
			cycicResize(facArray);
			break;
		case "std::vector":
			ArrayList<Object> structArrayVector = new ArrayList<Object>();
			if(type.get(1) instanceof JsonArray){
				ArrayList<Object> tempArray = new ArrayList<Object>();
				tempArray = annotationBuilder(type.getJsonArray(1), alias.getJsonArray(1), uitype.getJsonArray(1), tempArray);
				structArrayVector.add(tempArray);
			} else {
				String alias_s, uitype_s;
				ArrayList<Object> newArray = new ArrayList<Object>();
				if(alias == null){
					alias_s = "var";
				} else {
					alias_s = alias.getString(1);
				}
				if (uitype == null){
					uitype_s = type.getString(1);
				} else {
					uitype_s = uitype.getString(1);
				}
				newArray = stringAnnotation(type.getString(1), alias_s, uitype_s, newArray);
				structArrayVector.add(newArray);
			}
			if(alias == null){
				facArray.add("vector");
			} else {
				facArray.add(alias.getString(0));
			}
			facArray.add(structArrayVector);
			facArray.add("oneOrMore");
			cycicResize(facArray);
			break;
		case "std::string":
		case "int":
		case "double":
			System.out.println("singles");
			ArrayList<Object> tempArray = new ArrayList<Object>();
			tempArray = stringAnnotation(type.getJsonString(0).toString(), alias.getJsonString(0).toString(), uitype.getJsonString(0).toString(), tempArray);
			break;
		default : 
			System.out.println("Default");
			System.out.println("HEY RADIO YOU FORGOT " + type.getJsonString(0).toString());
			break;
		}
		System.out.println("annoEnd");
		return facArray;		
	}
	
	static ArrayList<Object> stringAnnotation(String type, String alias, String uitype, ArrayList<Object> facArray){
		System.out.println("String1");
		cycicResizeNew(facArray);
		facArray.set(0, alias);
		facArray.set(1, type);
		if(uitype == null){
			facArray.set(2, type);
		} else {
			facArray.set(2, uitype.toString().replaceAll("\"", ""));
		}
		System.out.println("String2");
		return facArray;
	}
}
