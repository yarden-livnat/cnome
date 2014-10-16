package edu.utexas.cycic;

import java.awt.Dialog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import org.controlsfx.dialog.Dialogs;

import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.ui.components.ViewBase;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
import javafx.stage.Window;


public class Cycic extends ViewBase{
	/**
	 * Function for building the CYCIC Pane and GridPane of this view. 
	 */
	public Cycic(){
		super();
		String string;
		for(int i = 0; i < XMLReader.facilityList.size(); i++){
			StringBuilder sb = new StringBuilder();
			StringBuilder sb1 = new StringBuilder();
			Process proc;
			try {
				proc = Runtime.getRuntime().exec("cyclus --agent-schema "+XMLReader.facilityList.get(i)); 
				BufferedReader read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				while((string = read.readLine()) != null){
					sb.append(string);
				}
				Process proc1 = Runtime.getRuntime().exec("cyclus --agent-annotations "+XMLReader.facilityList.get(i));
				BufferedReader read1 = new BufferedReader(new InputStreamReader(proc1.getInputStream()));
				while((string = read1.readLine()) != null){
					sb1.append(string);
				}
				facilityStructure test = new facilityStructure();
				test.facilityName = XMLReader.facilityList.get(i).replace(":", " ").trim();
				test.facStruct = XMLReader.annotationReader(sb1.toString(), XMLReader.readSchema(sb.toString()));
				DataArrays.simFacilities.add(test);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (CycicScenarios.cycicScenarios.size() < 1){
			DataArrays scenario = new DataArrays();
			workingScenario = scenario;
			CycicScenarios.cycicScenarios.add(scenario);
		}
		init();
	}
	public static final String TITLE = "Cycic";
	static Pane pane = new Pane(){
		{
		}
	};
	static facilityNode workingNode = null;
	static DataArrays workingScenario;
	static boolean marketHideBool = true;
	static Window window;
	
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
			setStyle("-fx-font-size: 9;");
		}
	};
	static VBox commodBox = new VBox(){
		{
			getChildren().add(new HBox(){
				{
					getChildren().add(new Label(){
						{
							setText("Simulation Commodities");
							setOnMouseClicked(new EventHandler<MouseEvent>(){
								public void handle(MouseEvent event){
									Dialogs.create()
										.title("Help")
										.message("Commodities facilitate the transfer of materials from one facility to another."
												+ "Facilities with the same commodities are allowed to trade with each other.")
										.showInformation();
								}
							});
							setTooltip(new Tooltip("Commodities to be traded in the simulation"));
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
						}
					}
					event.consume();
					Optional<String> response =  Dialogs.create()
							.title("Name Facility")
							.message("Enter Facility Name")
							.showTextInput();
					facility.name = response.orElse("");
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
		grid.setStyle("-fx-background-color: #d6d6d6;");
		grid.setHgap(10);
		grid.setVgap(5);
		
		// Adding a new Facility //
		Text scenetitle1 = new Text("Add Prototype");
		scenetitle1.setFont(new Font(20));
		grid.add(scenetitle1, 0, 0);
		Label facName = new Label("Name");
		grid.add(facName, 1, 0);
		// Name Field
		final TextField facNameField = new TextField();

		grid.add(facNameField, 2, 0);
		// Facility Type
		final ComboBox<String> structureCB = new ComboBox<String>();
		for(int i = 0; i < DataArrays.simFacilities.size(); i++){
			structureCB.getItems().add((String) DataArrays.simFacilities.get(i).facilityName);	
		}
		structureCB.valueProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				structureCB.setValue(newValue);
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
		Pane nodesPane = new Pane();
		for(int i = 0; i < DataArrays.simFacilities.size(); i++){
			FacilityCircle circle = new FacilityCircle();
			buildDnDCircle(circle, i, DataArrays.simFacilities.get(i).facilityName);
			nodesPane.getChildren().addAll(circle,circle.text);
		}
		scroll.setContent(nodesPane);
		cycicBox.getChildren().addAll(grid, scroll, pane);
		
		HBox mainView = new HBox(){
			{
				setSpacing(5);
			}
		};
		
		mainView.getChildren().addAll(commodBox, cycicBox);
		
		setContent(mainView);
	}
	
	/**
	 * 
	 */
	public void retrieveSchema(){
		try {
			StringBuilder sb = new StringBuilder();
			StringBuilder sb1 = new StringBuilder();
			String string;
			Process readproc = Runtime.getRuntime().exec("cd ~/Bright-lite2 && cyclus -a");
			BufferedReader schema = new BufferedReader(new InputStreamReader(readproc.getInputStream()));
			while(schema.readLine() != null){
				System.out.println(schema.readLine());
				/*Process proc = Runtime.getRuntime().exec("cyclus --agent-schema ");
				BufferedReader read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				while((string = read.readLine()) != null){
					sb.append(string);
				}
				Process proc1 = Runtime.getRuntime().exec("cyclus --agent-annotations ");
				BufferedReader read1 = new BufferedReader(new InputStreamReader(proc1.getInputStream()));
				while((string = read1.readLine()) != null){
					sb1.append(string);
				}*/
			}
			System.out.println(sb.toString());
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
			TextField commodity = new TextField();
			commodity.setText(Cycic.workingScenario.CommoditiesList.get(i).getText());
			commodGrid.add(commodity, 0, i );
			final int index = i;
			commodity.setPromptText("Enter Commodity Name");
			commodity.textProperty().addListener(new ChangeListener<String>(){
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
					Cycic.workingScenario.CommoditiesList.get(index).setText(newValue);
				}
			});
			Button removeCommod = new Button();
			removeCommod.setGraphic(GlyphRegistry.get(AwesomeIcon.TRASH_ALT, "10px"));
			removeCommod.setOnAction(new EventHandler<ActionEvent>(){
				public void handle(ActionEvent e){
					Cycic.workingScenario.CommoditiesList.remove(index);
					buildCommodPane();
				}
			});	
			commodGrid.add(removeCommod, 1, index);
		}
	}
	
	/**
	 * Adds a new TextField to the commodity GridPane tied to a new commodity in the 
	 * simulation.
	 */
	static public void addNewCommodity(){
		Label commodity = new Label();
		commodity.setText("");
		Cycic.workingScenario.CommoditiesList.add(commodity);
		TextField newCommod = new TextField();
		newCommod.autosize();
		newCommod.setPromptText("Enter Commodity Name");
		newCommod.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				Cycic.workingScenario.CommoditiesList.get(Cycic.workingScenario.CommoditiesList.size()-1).setText(newValue);
			}
		});
		buildCommodPane();
	}
}