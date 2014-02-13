package edu.utexas.cycic;

import java.io.*;
import java.util.ArrayList;

import javafx.scene.control.Label;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Node;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
/**
 * Output class for the CYCIC GUI.
 * @author Robert
 *
 */
public class OutPut {
	/**
	 * Function to convert the information stored in the CYCIC
	 * simulation into a cylcus input file. 
	 */
	public static void output(File file){
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder= docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Simulation");
			doc.appendChild(rootElement);
			
			Element control = doc.createElement("control");
			rootElement.appendChild(control);
			
			// General Simulation Information
			controlSetup(doc, control);
			
			// Commodities
			for(Label commod: CycicScenarios.workingCycicScenario.CommoditiesList){
				commodityBuilder(doc, rootElement, commod);
			}
			
			// Markets
			for(MarketCircle market: CycicScenarios.workingCycicScenario.marketNodes){
				Element marketID = doc.createElement("market");
				
				rootElement.appendChild(marketID);
				marketBuilder(doc, marketID, market);
			}
			
			// Facilities
			for(facilityNode facility : CycicScenarios.workingCycicScenario.FacilityNodes){
				Element facID = doc.createElement("facility");
				facilityBuilder(doc, facID, facility);
				rootElement.appendChild(facID);
			}
			
			// Regions
			for(regionNode region : CycicScenarios.workingCycicScenario.regionNodes) {
				Element regionID = doc.createElement("region");
				rootElement.appendChild(regionID);
				
				Element regionName = doc.createElement("Name");
				regionID.appendChild(regionName);
				
				for(String facility: region.availFacilities){
					Element allowedFac = doc.createElement("allowedfacility");
					allowedFac.appendChild(doc.createTextNode(facility));
					regionID.appendChild(allowedFac);
				}
				
				regionBuilder(doc, regionID, region.regionStruct, region.regionData, "GrowthRegion");
				// Building the institutions within regions.
				for (instituteNode institution: CycicScenarios.workingCycicScenario.institNodes){
					for (String instit: region.institutions){
						if (institution.name == instit) {
							Element institID = doc.createElement("institution");
							regionID.appendChild(institID);
							for(String facility: institution.availPrototypes) {
								Element allowedProto = doc.createElement("availableprototype");
								allowedProto.appendChild(doc.createTextNode(facility));
								institID.appendChild(allowedProto);								
							}
							Element initFacList = doc.createElement("initialfacilitylist");
							for(facilityItem facility: institution.availFacilities) {
								Element entry = doc.createElement("entry");
								Element initProto = doc.createElement("prototype");
								initProto.appendChild(doc.createTextNode(facility.name));
								entry.appendChild(initProto);
								Element number = doc.createElement("number");
								number.appendChild(doc.createTextNode(facility.number));
								entry.appendChild(number);
								initFacList.appendChild(entry);
							}
							institID.appendChild(initFacList);
							regionBuilder(doc, institID, institution.institStruct, institution.institData, "DeployInstit");
						}
					}
				}
			}
			
			//Recipes
			for(Nrecipe recipe : CycicScenarios.workingCycicScenario.Recipes){
				recipeBuilder(doc, rootElement, recipe);
			}
			
			saveFile(doc, rootElement);
			
			// Writing out the xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(file);
			
			transformer.transform(source, result);
			
		} catch (ParserConfigurationException pce){
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
	/**
	 * Sets up the control information for the simulation.
	 * @param doc The xml.parser document that controls the cyclus 
	 * input document.
	 * @param control Element in the xml.parser document the simulation 
	 * information will be added to.
	 */
	public static void controlSetup(Document doc, Element control){
		
		Element simDuration = doc.createElement("duration");
		simDuration.appendChild(doc.createTextNode(CycicScenarios.workingCycicScenario.simulationData.duration));
		control.appendChild(simDuration);
		
		Element simStartMon = doc.createElement("startmonth");
		simStartMon.appendChild(doc.createTextNode(CycicScenarios.workingCycicScenario.simulationData.startMonth));
		control.appendChild(simStartMon);
		
		Element simStartYear = doc.createElement("startyear");
		simStartYear.appendChild(doc.createTextNode(CycicScenarios.workingCycicScenario.simulationData.startYear));
		control.appendChild(simStartYear);
		
		Element simStart = doc.createElement("simstart");
		simStart.appendChild(doc.createTextNode(CycicScenarios.workingCycicScenario.simulationData.simStart));
		control.appendChild(simStart);
		
		Element decay = doc.createElement("decay");
		decay.appendChild(doc.createTextNode(CycicScenarios.workingCycicScenario.simulationData.decay));
		control.appendChild(decay);
	}
	
	/**
	 * Function used to create commodities in the Cyclus input xml.
	 * @param doc The xml.parser document that controls the cyclus input document.
	 * @param rootElement The element that will serve as the heading for 
	 * substructures built in this function.
	 * @param commodity Label containing the commodity name.
	 */
	public static void commodityBuilder(Document doc, Element rootElement, Label commodity){
		Element commod = doc.createElement("commodity");
		Element commodName = doc.createElement("name");
		commodName.appendChild(doc.createTextNode(commodity.getText()));
		commod.appendChild(commodName);
		
		rootElement.appendChild(commod);
	}
	
	/**
	 * Function used to add recipes to the Cyclus input xml.
	 * @param doc The xml.parser document that controls the cyclus input document.
	 * @param rootElement The element that will serve as the heading for 
	 * substructures built in this function.
	 * @param recipe The Cyclus recipe being added to the input xml.
	 */
	public static void recipeBuilder(Document doc, Element rootElement, Nrecipe recipe){
		Element recipeEle = doc.createElement("recipe");
		rootElement.appendChild(recipeEle);
		
		Element recipeName = doc.createElement("name");
		recipeName.appendChild(doc.createTextNode(recipe.Name));
		recipeEle.appendChild(recipeName);
		
		Element recipeBasis = doc.createElement("basis");
		recipeBasis.appendChild(doc.createTextNode(recipe.Basis));
		recipeEle.appendChild(recipeBasis);
		
		for(isotopeData iso : recipe.Composition){
			Element isotope = doc.createElement("isotope");
			recipeEle.appendChild(isotope);
			
			Element isoID = doc.createElement("id");
			isoID.appendChild(doc.createTextNode(iso.Name));
			isotope.appendChild(isoID);
			
			Element isoComp = doc.createElement("comp");
			if (recipe.Basis == "mass"){
				isoComp.appendChild(doc.createTextNode(String.format("%f2", iso.mass)));
			} else {
				isoComp.appendChild(doc.createTextNode(String.format("%f2", iso.atom)));
			}
			isotope.appendChild(isoComp);
		}
	}
	
	/**
	 * Function responsible for adding facilities to the cyclus xml input. 
	 * @param doc The xml.parser document that controls the cyclus 
	 * input document.
	 * @param rootElement The element that will serve as the heading for 
	 * substructures built in this function.
	 * @param facArray The ArrayList<Object> containing the facility structure.
	 * @param dataArray The ArrayList<Object> containing the facility data. 
	 * @param facType A string that indicates the type of the facility. 
	 */
	@SuppressWarnings("unchecked")
	public static void facilityBuilder(Document doc, Element rootElement, facilityNode facility){
		String facType = facility.facilityType;
		ArrayList<Object> facArray = facility.facilityStructure;
		ArrayList<Object> dataArray = facility.facilityData;
		
		Element name = doc.createElement("name");
		
		rootElement.appendChild(name);
		
		Element model = doc.createElement("model");
		rootElement.appendChild(model);
		
		Element modelType = doc.createElement(facType.replace(" ", "").toString());
		model.appendChild(modelType);
		
		for(int i = 1; i < dataArray.size(); i++){
			if (dataArray.get(i) instanceof ArrayList){
				facilityDataElement(doc, modelType, (ArrayList<Object>) facArray.get(i), (ArrayList<Object>) dataArray.get(i));
			} else {
				// Adding the label
				Element heading = doc.createElement((String) facArray.get(0));
				heading.appendChild(doc.createTextNode((String) dataArray.get(0)));
				modelType.appendChild(heading);
			}
		}
	}
	
	/**
	 * Special function for adding the <name> field to the facility 
	 * input definition.
	 * @param doc The xml.parser document that controls the cyclus 
	 * input document.
	 * @param dataArray ArrayList<Object> that contains the name 
	 * information for the facility.
	 * @return Element added to the facility rootElement.
	 */
	
	public static Element facilityNameElement(Document doc, ArrayList<Object> dataArray){
		Element nameElement = doc.createElement("name");
		nameElement.appendChild(doc.createTextNode((String) dataArray.get(0)));
		return nameElement;
	}
	
	/**
	 * This function produces a xml element that contains the 
	 * information for a facility input.
	 * @param doc The xml.parser document that controls the cyclus 
	 * input document.
	 * @param rootElement The element that will serve as the heading for 
	 * substructures built in this function.
	 * @param structArray ArrayList<Object> containing the 
	 * facility input field information.
	 * @param dataArray ArrayList<Object> containing the data associated 
	 * with the input field information.
	 */
	@SuppressWarnings("unchecked")
	public static void facilityDataElement(Document doc, Element rootElement, ArrayList<Object> structArray, ArrayList<Object> dataArray){
		for (int i = 0; i < dataArray.size(); i++){
			if (dataArray.get(i) instanceof ArrayList){
				if (structArray.size() > 2 && !(structArray.get(2) instanceof ArrayList)){ 
					if (indentCheck((String) structArray.get(2))){
						Element tempElement = doc.createElement((String) structArray.get(0).toString().replace(" ", ""));
						facilityDataElement(doc, tempElement, (ArrayList<Object>) structArray.get(1), (ArrayList<Object>) dataArray.get(i));
						rootElement.appendChild(tempElement);
					}
				} else {
					facilityDataElement(doc, rootElement, (ArrayList<Object>) structArray.get(i), (ArrayList<Object>) dataArray.get(i));
				}
			} else {
				Element name = doc.createElement((String) structArray.get(0).toString().replace(" ", ""));
				name.appendChild(doc.createTextNode((String)dataArray.get(0)));
				rootElement.appendChild(name);
			}
		}
	}
	
	/**
	 * Function for doing proper indentation for the special 
	 * oneOrMore, zeroOrMore input fields. 
	 * @param string String used to test for indentation.
	 * @return Boolean to indicate whether a indent is required. 
	 */
	public static boolean indentCheck(String string){
		if(string == "oneOrMore" || string == "zeroOrMore" || string == "input" || string == "output"){
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @param doc The xml.parser document that controls the 
	 * cyclus input document.
	 * @param rootElement The element that will serve as the heading for 
	 * substructures built in this function.
	 * @param structArray Region or Institution array used to 
	 * indicate the name for the xml tag.
	 * @param dataArray ArrayList<Object> containing the data 
	 * to be input into the xml tag. 
	 * @param nodeType String containing information about 
	 * the type of the node. 
	 */
	@SuppressWarnings("unchecked")
	public static void regionBuilder(Document doc, Element rootElement, ArrayList<Object> structArray, ArrayList<Object> dataArray, String nodeType){
		rootElement.appendChild(facilityNameElement(doc, (ArrayList<Object>)dataArray.get(0)));
		Element model = doc.createElement("model");
		rootElement.appendChild(model);
		
		Element modelType = doc.createElement(nodeType.replace(" ", "").toString());
		model.appendChild(modelType);
		
		for(int i = 1; i < dataArray.size(); i++){
			if (dataArray.get(i) instanceof ArrayList){
				facilityDataElement(doc, modelType, (ArrayList<Object>) structArray.get(i), (ArrayList<Object>) dataArray.get(i));
			} else {
				// Adding the label
				Element heading = doc.createElement((String) structArray.get(0));
				heading.appendChild(doc.createTextNode((String) dataArray.get(0)));
				modelType.appendChild(heading);
			}
		}
	}
	
	/**
	 * This function is a quick hack that builds the markets in the simulation. 
	 * All markets are forced to be TestMarkets for now.
	 * @param rootElement The element that will serve as the heading for 
	 * substructures built in this function.
	 * @param market marketCircle being written to the xml file. 
	 */
	public static void marketBuilder(Document doc, Element rootElement, MarketCircle market){
		Element marketName = doc.createElement("name");
		marketName.appendChild(doc.createTextNode((String) market.name));
		rootElement.appendChild(marketName);
		
		Element marketCommod = doc.createElement("mktcommodity");
		marketCommod.appendChild(doc.createTextNode(market.commodity));
		rootElement.appendChild(marketCommod);
				
		Element marketModel = doc.createElement("model");
		Element marketType = doc.createElement("TestMarket");
		marketModel.appendChild(marketType);
		rootElement.appendChild(marketModel);	
	}
	
	public static void saveFile(Document doc, Element rootElement){
			Element cycicElement = doc.createElement("CycicSimulation");
			rootElement.appendChild(cycicElement);
			
			for (facilityNode facility: CycicScenarios.workingCycicScenario.FacilityNodes){				
				cycicElement.appendChild(outputFacility(doc, facility));
			}
			
			for (MarketCircle market: CycicScenarios.workingCycicScenario.marketNodes){
				cycicElement.appendChild(outputMarket(doc, market));
			}
	}
	
	public static void loadFile(File file){
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			
			doc.getDocumentElement().getNodeName();
			Cycic.workingScenario = CycicScenarios.workingCycicScenario;
			NodeList facList = doc.getElementsByTagName("facilityNode");
			
			for (int i = 0; i < facList.getLength(); i++){
				org.w3c.dom.Node facNode = facList.item(i);
				
				facilityNode tempNode = new facilityNode();
				Element element = (Element) facNode;
				tempNode.name = element.getElementsByTagName("name").item(0).getTextContent();
				tempNode.cycicCircle = CycicCircles.addNode((String) tempNode.name, tempNode);
				double radius = Double.parseDouble(element.getElementsByTagName("radius").item(0).getTextContent());
				double xPosition = Double.parseDouble(element.getElementsByTagName("xPosition").item(0).getTextContent());
				tempNode.cycicCircle.setCenterX(xPosition);
				tempNode.cycicCircle.text.setLayoutX(xPosition-radius*0.6);
				tempNode.cycicCircle.menu.setLayoutX(xPosition);
				double yPosition = Double.parseDouble(element.getElementsByTagName("yPosition").item(0).getTextContent());
				tempNode.cycicCircle.setCenterY(yPosition);
				tempNode.cycicCircle.text.setLayoutY(yPosition-radius*0.6);
				tempNode.cycicCircle.menu.setLayoutY(yPosition);
			}
			
			NodeList marketList = doc.getElementsByTagName("marketNode");
			
			for (int i = 0; i < marketList.getLength(); i++){
				org.w3c.dom.Node marketNode = marketList.item(i);
				
				Element element = (Element) marketNode;
				MarketNodes.addMarket(element.getElementsByTagName("name").item(0).getTextContent());
				MarketCircle tempNode = Cycic.workingScenario.marketNodes.get(Cycic.workingScenario.marketNodes.size() - 1);
				tempNode.setCenterX(Double.parseDouble(element.getElementsByTagName("xPosition").item(0).getTextContent()));
				tempNode.setCenterY(Double.parseDouble(element.getElementsByTagName("yPosition").item(0).getTextContent()));
			}		
			VisFunctions.reloadPane();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	static Element outputFacility(Document doc, facilityNode facility){
		Element facElement = doc.createElement("facilityNode");
		// Name
		Element facName = doc.createElement("name");
		facName.appendChild(doc.createTextNode((String) facility.name));
		facElement.appendChild(facName);
		// X position
		Element xPosition = doc.createElement("xPosition");
		xPosition.appendChild(doc.createTextNode(String.format("%.2f", facility.cycicCircle.getCenterX())));
		facElement.appendChild(xPosition);
		// Y position
		Element yPosition = doc.createElement("yPosition");
		yPosition.appendChild(doc.createTextNode(String.format("%.2f", facility.cycicCircle.getCenterY())));
		facElement.appendChild(yPosition);
		// radius
		Element radius = doc.createElement("radius");
		radius.appendChild(doc.createTextNode(String.format("%.2f", facility.cycicCircle.getRadius())));
		facElement.appendChild(radius);
		
		for (String commodity: facility.cycicCircle.incommods){
			Element commodityObj = doc.createElement("inCommod");
			commodityObj.appendChild(doc.createTextNode(commodity));
			facElement.appendChild(commodityObj);
		}
		for (String commodity: facility.cycicCircle.outcommods){
			Element commodityObj = doc.createElement("outCommod");
			commodityObj.appendChild(doc.createTextNode(commodity));
			facElement.appendChild(commodityObj);
		}
		
		return facElement;
	}
	
	static Element outputMarket(Document doc, MarketCircle market){
		Element markElement = doc.createElement("marketNode");
		Element marketName = doc.createElement("name");
		marketName.appendChild(doc.createTextNode((String) market.name));
		markElement.appendChild(marketName);
		// X position
		Element xPosition = doc.createElement("xPosition");
		xPosition.appendChild(doc.createTextNode(String.format("%.2f", market.getCenterX())));
		markElement.appendChild(xPosition);
		// Y position
		Element yPosition = doc.createElement("yPosition");
		yPosition.appendChild(doc.createTextNode(String.format("%.2f", market.getCenterY())));
		markElement.appendChild(yPosition);
		
		Element commodityObj = doc.createElement("inCommod");
		commodityObj.appendChild(doc.createTextNode(market.commodity));
		markElement.appendChild(commodityObj);
		return markElement;
	}
	
	static Element outputRegion(Document doc, regionNode region){
		Element regionElement = doc.createElement("regionNode");
		Element regionName = doc.createElement("name");
		regionName.appendChild(doc.createTextNode((String) region.name));
		regionElement.appendChild(regionName);
		// X position
		Element xPosition = doc.createElement("xPosition");
		xPosition.appendChild(doc.createTextNode(String.format("%.2f", region.regionCircle.getCenterX())));
		regionElement.appendChild(xPosition);
		// Y position
		Element yPosition = doc.createElement("yPosition");
		yPosition.appendChild(doc.createTextNode(String.format("%.2f", region.regionCircle.getCenterY())));
		
		regionElement.appendChild(yPosition);
		
		
		return regionElement;
	}

	public static void loadNewFile(File file){
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			
			doc.getDocumentElement().getNodeName();
			Cycic.workingScenario = CycicScenarios.workingCycicScenario;
			NodeList facList = doc.getElementsByTagName("facility");
			
			for (int i = 0; i < facList.getLength(); i++){
				org.w3c.dom.Node facNode = facList.item(i);
				
				facilityNode tempNode = new facilityNode();
				Element element = (Element) facNode;
				tempNode.name = element.getElementsByTagName("name").item(0).getTextContent();
				tempNode.cycicCircle = CycicCircles.addNode((String) tempNode.name, tempNode);
			}
			
			NodeList marketList = doc.getElementsByTagName("market");
			
			for (int i = 0; i < marketList.getLength(); i++){
				org.w3c.dom.Node marketNode = marketList.item(i);
				
				Element element = (Element) marketNode;
				MarketNodes.addMarket(element.getElementsByTagName("name").item(0).getTextContent());
				MarketCircle tempNode = Cycic.workingScenario.marketNodes.get(Cycic.workingScenario.marketNodes.size() - 1);
			}		
			VisFunctions.reloadPane();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
}

