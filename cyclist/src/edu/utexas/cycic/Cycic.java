package edu.utexas.cycic;

import java.awt.Dialog;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.controlsfx.dialog.Dialogs;

import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.ui.components.ViewBase;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;
import edu.utah.sci.cyclist.core.controller.CyclistController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import edu.utah.sci.cyclist.core.services.CyclusService;

public class Cycic extends ViewBase{
	static Logger log = Logger.getLogger(Cycic.class);
	/**
	 * Function for building the CYCIC Pane and GridPane of this view. 
	 */
	public Cycic(){
		super();
		if (monthList.size() == 0){
			months();
		}
		if (CycicScenarios.cycicScenarios.size() < 1){
			DataArrays scenario = new DataArrays();
			workingScenario = scenario;
			CycicScenarios.cycicScenarios.add(scenario);
		}
		init();
	}
	public static final String TITLE = "Cycic";
	static Pane pane = new Pane();
	Pane nodesPane = new Pane();
	static facilityNode workingNode = null;
	static DataArrays workingScenario;
	static boolean marketHideBool = true;
	static Window window;
	static ToggleGroup opSwitch = new ToggleGroup();
	static ToggleButton localToggle = new ToggleButton("Local");
	static ToggleButton remoteToggle = new ToggleButton("Remote");
	
	/**
	 * 
	 */
	static GridPane simInfo = new GridPane(){
		{
			setHgap(4);
			setVgap(4);
			setPadding(new Insets(10, 10, 10, 10));			
		}
	};
	
	/**
	 * 
	 */
	static VBox simDetailBox = new VBox(){
		{
			getChildren().add(simInfo);
			setStyle("-fx-border-style: solid;"
	                + "-fx-border-width: 1;"
	                + "-fx-border-color: black");
		}
	};
	
	/**
	 * 
	 */
	static GridPane commodGrid = new GridPane(){
		{
			setVgap(5);
			setHgap(5);
		}
	};
	
	/**
	 * 
	 */
	static Button addNewCommod = new Button(){
		{
			setOnAction(new EventHandler<ActionEvent>(){
				public void handle(ActionEvent e){
					addNewCommodity();
				}
			});
			setText("+");
			setStyle("-fx-font-size: 12;");
		}
	};
	static VBox commodBox = new VBox(){
		{
			getChildren().add(new HBox(){
				{
					getChildren().add(new Label(){
						{
							setText("Simulation Commodities");
							setTooltip(new Tooltip("Commodities facilitate the transfer of materials from one facility to another."
									+ "Facilities with the same commodities are allowed to trade with each other."));
							setFont(new Font("Times", 16));
						}
					});
					getChildren().add(addNewCommod);
					setSpacing(5);
				}
			});
			getChildren().add(commodGrid);
			setPadding(new Insets(10, 10, 10, 10));
			setSpacing(5);
			setStyle("-fx-border-style: solid;"
	                + "-fx-border-width: 1;"
	                + "-fx-border-color: black");
			
		}
	};
	GridPane archetypeGrid = new GridPane(){
		{
			add(new Label(){
				{
					setText("Add Available Archetypes");
					setTooltip(new Tooltip("Use the drop down menu to select archetypes to add to the simulation."));
					setFont(new Font("Times", 16));
				}
			}, 0, 0);
			setStyle("-fx-border-style: solid;"
	                + "-fx-border-width: 1;"
	                + "-fx-border-color: black");
			setVgap(5);
			setHgap(5);
			setPadding(new Insets(10, 10, 10, 10));
		}
	};
	
	/**
	 * Initiates the Pane and GridPane.
	 */
	private void init(){
		pane.setOnDragOver(new EventHandler <DragEvent>(){
			public void handle(DragEvent event){
				event.acceptTransferModes(TransferMode.ANY);
			}
		});
		pane.setOnDragDropped(new EventHandler<DragEvent>(){
			public void handle(DragEvent event){
				if(event.getDragboard().hasContent(DnD.VALUE_FORMAT)){
					facilityNode facility = new facilityNode();
					facility.facilityType = event.getDragboard().getContent(DnD.VALUE_FORMAT).toString();
					facility.facilityType.trim();
					for (int i = 0; i < DataArrays.simFacilities.size(); i++){
						if(DataArrays.simFacilities.get(i).facilityName.equalsIgnoreCase(facility.facilityType)){
							facility.facilityStructure = DataArrays.simFacilities.get(i).facStruct;
							facility.niche = DataArrays.simFacilities.get(i).niche;
						}
					}
					event.consume();
					/*Optional<String> response =  Dialogs.create()
							.title("Name Facility")
							.message("Enter Facility Name")
							.showTextInput();
					if(response.isPresent()){
						facility.name = response.orElse("");*/
					facility.name = "";
					facility.cycicCircle = CycicCircles.addNode((String)facility.name, facility);
					facility.cycicCircle.setCenterX(event.getX());
					facility.cycicCircle.setCenterY(event.getY());
					facility.cycicCircle.text.setLayoutX(event.getX()-facility.cycicCircle.getRadius()*0.7);
					facility.cycicCircle.text.setLayoutY(event.getY()-facility.cycicCircle.getRadius()*0.6);
					facility.sorterCircle = SorterCircles.addNode((String)facility.name, facility, facility);
					FormBuilderFunctions.formArrayBuilder(facility.facilityStructure, facility.facilityData);			
				}
			}
		});
		setTitle(TITLE);
		setOnMousePressed(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e){
				CycicScenarios.workingCycicScenario = workingScenario;
			}
		});
		
		VBox cycicBox = new VBox();
		cycicBox.autosize();
		Cycic.pane.autosize();
		Cycic.pane.setId("cycicPane");
		Cycic.pane.setPrefSize(1000, 600);
		Cycic.pane.setStyle("-fx-background-color: white;");
		
		// Temp Toolbar //
		final GridPane grid = new GridPane();
		grid.setStyle("-fx-background-color: #d6d6d6;"
					+ "-fx-font-size: 14;");
		grid.setHgap(10);
		grid.setVgap(5);
		createArchetypeBar(grid);
		// Adding a new Facility //
		Text scenetitle1 = new Text("Add Prototype");
		grid.add(scenetitle1, 0, 0);
		Label facName = new Label("Name");
		grid.add(facName, 1, 0);
		// Name Field
		final TextField facNameField = new TextField();

		grid.add(facNameField, 2, 0);
		// Facility Type
		final ComboBox<String> structureCB = new ComboBox<String>();
		structureCB.setOnMousePressed(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent m){
				structureCB.getItems().clear();	
				for(int i = 0; i < DataArrays.simFacilities.size(); i++){
					structureCB.getItems().add((String) DataArrays.simFacilities.get(i).facilityName);	
				}
			}
		});

		structureCB.setPromptText("Select Facility Archetype");
		grid.add(structureCB, 3, 0);
		//Submit Button
		Button submit1 = new Button("Add");
		submit1.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				if (structureCB.getValue() == null){
					return;
				}
				facilityNode tempNode = new facilityNode();
				tempNode.facilityType = structureCB.getValue();
				for (int i = 0; i < DataArrays.simFacilities.size(); i++){
					if (DataArrays.simFacilities.get(i).facilityName == structureCB.getValue()){
						tempNode.facilityStructure = DataArrays.simFacilities.get(i).facStruct;
					}				
				}
				tempNode.name = facNameField.getText();
				tempNode.cycicCircle = CycicCircles.addNode((String)tempNode.name, tempNode);
				tempNode.sorterCircle = SorterCircles.addNode((String)tempNode.name, tempNode, tempNode);
				FormBuilderFunctions.formArrayBuilder(tempNode.facilityStructure, tempNode.facilityData);
			}
		});
		grid.add(submit1, 4, 0);
		
		ScrollPane scroll = new ScrollPane();
		scroll.setMinHeight(120);
		scroll.setContent(nodesPane);
		Button cyclusALocal = new Button("Cyclus -a");
		cyclusALocal.setTooltip(new Tooltip("Use this button to search for all local Cyclus modules."));
		cyclusALocal.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				retrieveSchema();
			}
		});
		grid.add(cyclusALocal, 6, 0);
		
		
		ComboBox<String> skins = new ComboBox<String>();
		for(int i = 0; i < DataArrays.visualizationSkins.size(); i++){
			skins.getItems().add(DataArrays.visualizationSkins.get(i).name);
		}
		skins.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				for()
			}
		});
		/*opSwitch.getToggles().addAll(localToggle, remoteToggle);
		localToggle.setSelected(true);
		grid.add(localToggle, 7, 0);
		grid.add(remoteToggle, 8, 0);
		*/
		cycicBox.getChildren().addAll(grid, scroll, pane);
		
		HBox mainView = new HBox(){
			{
				setSpacing(5);
			}
		};
		details();
		VBox sideView = new VBox(){
			{
				setSpacing(5);
				setStyle("-fx-border-style: solid;"
		                + "-fx-border-width: 1;"
		                + "-fx-border-color: black");
			}
		};
		//ScrollPane commodScroll = new ScrollPane();
		//commodScroll.setContent(commodBox); 
		sideView.getChildren().addAll(simDetailBox, archetypeGrid, commodBox);
		mainView.getChildren().addAll(sideView, cycicBox);
		
		setContent(mainView);
	}
	
	/**
	 * 
	 */
	public void retrieveSchema(){
		try {
			String string;
			Process readproc = Runtime.getRuntime().exec("cyclus -a");
			
			BufferedReader schema = new BufferedReader(new InputStreamReader(readproc.getInputStream()));
			Object[] schemaLines = schema.lines().toArray();
			schema.close();
			DataArrays.simFacilities.clear();
			DataArrays.simRegions.clear();
			DataArrays.simInstitutions.clear();
			for(int i = 0; i < schemaLines.length; i++){
				StringBuilder sb1 = new StringBuilder();
				Process proc1 = Runtime.getRuntime().exec("cyclus --agent-annotations " + schemaLines[i]);
				BufferedReader read1 = new BufferedReader(new InputStreamReader(proc1.getInputStream()));
				while((string = read1.readLine()) != null){
					sb1.append(string);
				}
				boolean test = true;
				for(int j = 0; j < XMLReader.blackList.size(); j++){
					if(((String)schemaLines[i]).equalsIgnoreCase(XMLReader.blackList.get(j))){
						test = false;
					}
				}
				if(test == false){
					continue;
				}
				switch(XMLReader.entityReader(sb1.toString()).replace("\"", "")){
				case "facility":
					facilityStructure node = new facilityStructure();
					node.facAnnotations = sb1.toString();
					node.facilityArch = schemaLines[i].toString();
					node.niche = XMLReader.nicheReader(sb1.toString().replace("\"", ""));
					node.facilityName = ((String) schemaLines[i]).replace(":", " ");
					DataArrays.simFacilities.add(node);
					log.info("Adding archetype "+schemaLines[i]);
					break;
				case "region":
					log.info("Adding archetype "+schemaLines[i]);
					regionStructure rNode = new regionStructure();
					rNode.regionAnnotations = sb1.toString();
					rNode.regionArch = schemaLines[1].toString();
					rNode.regionName = ((String) schemaLines[i]).replace(":", " ");
					DataArrays.simRegions.add(rNode);
					break;
				case "institution":
					log.info("Adding archetype "+schemaLines[i]);
					institutionStructure iNode = new institutionStructure();
					iNode.institArch = schemaLines[i].toString();
					iNode.institAnnotations = sb1.toString();
					iNode.institName = ((String) schemaLines[i]).replace(":", " ");
					DataArrays.simInstitutions.add(iNode);
					break;
				default:
					log.error(schemaLines[i]+" is not of the 'facility', 'region' or 'institution' type. "
							+ "Please check the entity value in the archetype annotation.");
					break;
				};	
			}
			log.info("Schema discovery complete");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param circle
	 * @param i
	 * @param name
	 */
	public void buildDnDCircle(FacilityCircle circle, int i, String name){
		circle.setRadius(40);
		circle.setStroke(Color.BLACK);
		circle.setFill(Color.web("#CF5300"));
		circle.setCenterX(45+(i*90));
		circle.setCenterY(50);
		circle.text.setText(name);
		circle.text.setWrapText(true);
		circle.text.setMaxWidth(60);
		circle.text.setLayoutX(circle.getCenterX()-circle.getRadius()*0.7);
		circle.text.setLayoutY(circle.getCenterY()-circle.getRadius()*0.6);	
		circle.text.setTextAlignment(TextAlignment.CENTER);
		circle.text.setMouseTransparent(true);
		circle.setOnDragDetected(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e){
				Dragboard db = circle.startDragAndDrop(TransferMode.COPY);
				ClipboardContent content = new ClipboardContent();				
				content.put(DnD.VALUE_FORMAT, circle.text.getText());
				db.setContent(content);
				e.consume();
			}
		});
	}
	
	/**
	 * 
	 */
	public static void buildCommodPane(){
		commodGrid.getChildren().clear();
		for (int i = 0; i < Cycic.workingScenario.CommoditiesList.size(); i++){
			CommodityNode commod = Cycic.workingScenario.CommoditiesList.get(i);
			TextField commodity = new TextField();
			commodity.setText(commod.name.getText());
			commodGrid.add(commodity, 0, i );
			final int index = i;
			commodity.setPromptText("Enter Commodity Name");
			commodity.textProperty().addListener(new ChangeListener<String>(){
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
					commod.name.setText(newValue);
				}
			});
			commodGrid.add(new Label("Priority"), 1, index);
			TextField priority = VisFunctions.numberField();
			priority.textProperty().addListener(new ChangeListener<String>(){
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
					commod.priority = Double.parseDouble(newValue);
				}
			});
			priority.setPromptText("Enter Commodity Prioirty");
			priority.setText("1");
			commodGrid.add(priority, 2, index);
			Button removeCommod = new Button();
			removeCommod.setGraphic(GlyphRegistry.get(AwesomeIcon.TRASH_ALT, "10px"));
			removeCommod.setOnAction(new EventHandler<ActionEvent>(){
				public void handle(ActionEvent e){
					Cycic.workingScenario.CommoditiesList.remove(index);
					buildCommodPane();
				}
			});	
			commodGrid.add(removeCommod, 3, index);
		}
	}
	
	/**
	 * Adds a new TextField to the commodity GridPane tied to a new commodity in the 
	 * simulation.
	 */
	static public void addNewCommodity(){
		CommodityNode commodity = new CommodityNode();
		commodity.name.setText("");
		Cycic.workingScenario.CommoditiesList.add(commodity);
		TextField newCommod = new TextField();
		newCommod.autosize();
		newCommod.setPromptText("Enter Commodity Name");
		newCommod.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				commodity.name.setText(newValue);
			}
		});
		buildCommodPane();
	}
	
	HashMap<String, String> months = new HashMap<String, String>();
	ArrayList<String> monthList = new ArrayList<String>();
	/**
	 * Quick hack to convert months into their integer values.
	 * i.e. January = 0, Feb = 1, etc...
	 */
	public void months(){
		
		Cycic.workingScenario.simulationData.startMonth = "0";
		
		monthList.add("January");
		monthList.add("February");
		monthList.add("March");
		monthList.add("April");
		monthList.add("May");
		monthList.add("June");
		monthList.add("July");
		monthList.add("August");
		monthList.add("September");
		monthList.add("October");
		monthList.add("November");
		monthList.add("December");
		
		months.put("January", "1");
		months.put("Febuary", "2");
		months.put("March", "3");
		months.put("April", "4");
		months.put("May", "5");
		months.put("June", "6");
		months.put("July", "7");
		months.put("August", "8");
		months.put("September", "9");
		months.put("October", "10");
		months.put("November", "11");
		months.put("December", "12");
	}
	public void details(){
		Label simDets = new Label("Simulation Details");
		simDets.setTooltip(new Tooltip("The top level details of the simulation."));
		simDets.setFont(new Font("Times", 16));
		simInfo.add(simDets, 0, 0);
		TextField duration = VisFunctions.numberField();
		duration.setMaxWidth(150);
		duration.setPromptText("Length of Simulation");
		duration.setText(Cycic.workingScenario.simulationData.duration);
		Cycic.workingScenario.simulationData.duration = "0";
		duration.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				Cycic.workingScenario.simulationData.duration = newValue;
			}
		});
		simInfo.add(new Label("Duration (Months)"), 0, 1);
		simInfo.add(duration, 1, 1);
		

		final ComboBox<String> startMonth = new ComboBox<String>();
		startMonth.setValue(months.get(Cycic.workingScenario.simulationData.startMonth));
		for(int i = 0; i < 12; i++ ){
			startMonth.getItems().add(monthList.get(i));
		}
		Cycic.workingScenario.simulationData.startMonth = "1";
		startMonth.setValue(monthList.get(Integer.parseInt(Cycic.workingScenario.simulationData.startMonth)));
		startMonth.valueProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				Cycic.workingScenario.simulationData.startMonth = months.get(newValue);
			}
		});
		startMonth.setPromptText("Select Month");
		simInfo.add(new Label("Start Month"), 0, 2);
		simInfo.add(startMonth, 1, 2);
		TextField startYear = VisFunctions.numberField();
		startYear.setText(Cycic.workingScenario.simulationData.startYear);
		Cycic.workingScenario.simulationData.startYear = "0";
		startYear.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				Cycic.workingScenario.simulationData.startYear = newValue;
			}
		});
		startYear.setPromptText("Starting Year");
		startYear.setMaxWidth(150);
		simInfo.add(new Label("Start Year"), 0, 3);
		simInfo.add(startYear, 1, 3);
				
		TextArea description = new TextArea();
		description.setMaxSize(250, 50);
		description.setWrapText(true);
		description.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				Cycic.workingScenario.simulationData.description = newValue;
			}
		});
		simInfo.add(new Label("Description"), 0, 4);
		simInfo.add(description, 1, 4);
		
		TextArea notes = new TextArea();
		notes.setMaxSize(250, 50);
		notes.setWrapText(true);
		notes.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				Cycic.workingScenario.simulationData.notes = newValue;
			}
		});
		simInfo.add(new Label("Notes"), 0, 5);
		simInfo.add(notes, 1, 5);
		
	
		// Prints the Cyclus input associated with this simulator. 
		Button output = new Button("Generate Cyclus Input");
		output.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event){
				if(OutPut.inputTest()){
					FileChooser fileChooser = new FileChooser();
					//Set extension filter
					FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
					fileChooser.getExtensionFilters().add(extFilter);
					fileChooser.setTitle("Please save as Cyclus input file.");
					fileChooser.setInitialFileName("*.xml");
					//Show save file dialog
					File file = fileChooser.showSaveDialog(window);
					OutPut.output(file);
				}
			}
		});
		simInfo.add(output, 0, 6);
		
        Button runRemote = new Button("Run Remotely");
        runRemote.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e){
            	if(OutPut.inputTest()){
            		String cycicXml = OutPut.xmlStringGen();
            		CyclistController._cyclusService.submit(cycicXml);
            	}
            }
        });;
    
        Button runInput = new Button("Run Locally");
        runInput.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e){
                if(OutPut.inputTest()){
                	String tempHash = Integer.toString(OutPut.xmlStringGen().hashCode());
                	String cycicTemp = "cycic"+tempHash;
                	try {
                		File temp = File.createTempFile(cycicTemp, ".xml");
                		FileWriter fileOutput = new FileWriter(temp);
                		BufferedWriter buffOut = new BufferedWriter(fileOutput);

                		Runtime.getRuntime().exec("cyclus -o "+cycicTemp +".sqlite "+cycicTemp); 
                	} catch (Exception e1) {
                		e1.printStackTrace();
                	}
                }   
            }
        });
        HBox runBox = new HBox();
        runBox.setSpacing(5);
        runBox.getChildren().addAll(runInput, runRemote);
        simInfo.add(runBox, 1,6);    
    }

	public void createArchetypeBar(GridPane grid){
		ComboBox<String> archetypes = new ComboBox<String>();
		for(int i = 0; i < DataArrays.simFacilities.size(); i++){
			archetypes.getItems().add(DataArrays.simFacilities.get(i).facilityName);
		}
		archetypes.setOnMousePressed(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e){
				archetypes.getItems().clear();
				for(int i = 0; i < DataArrays.simFacilities.size(); i++){
					archetypes.getItems().add(DataArrays.simFacilities.get(i).facilityName);
				}
			}
		});
		archetypes.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				for(int i = 0; i < DataArrays.simFacilities.size(); i++){
					if(DataArrays.simFacilities.get(i).facilityName.equalsIgnoreCase(archetypes.getValue())){
						if(DataArrays.simFacilities.get(i).loaded == true){
							return;
						}
						facilityStructure test = DataArrays.simFacilities.get(i);
						String string;
						StringBuilder sb = new StringBuilder();
						try {

							Process proc = Runtime.getRuntime().exec("cyclus --agent-schema "+test.facilityArch); 
							BufferedReader read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
							while((string = read.readLine()) != null){
								sb.append(string);
							}
							test.facSchema = sb.toString();
							test.loaded = true;
							test.facStruct = XMLReader.annotationReader(test.facAnnotations, XMLReader.readSchema(test.facSchema));
							FacilityCircle circle = new FacilityCircle();
							int pos = 0;
							for(int k = 0; k < DataArrays.simFacilities.size(); k++){
								if(DataArrays.simFacilities.get(k).loaded == true){
									pos+=1;
								}
							}
							buildDnDCircle(circle, pos-1, test.facilityName);
							nodesPane.getChildren().addAll(circle,circle.text);
						} catch (Exception eq) {
							
						}
					}
				}
			}
		});
		archetypeGrid.add(new Label("Add Archetype to Simulation"), 0, 1);
		archetypeGrid.add(archetypes, 1, 1);
	}
	
}