package edu.utexas.cycic;

import edu.utah.sci.cyclist.core.ui.components.ViewBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class Cycic extends ViewBase{
	/**
	 * Function for building the CYCIC Pane and GridPane of this view. 
	 */
	public Cycic(){
		super();
		if (CycicScenarios.cycicScenarios.size() < 1){
			DataArrays scenario = new DataArrays();
			workingScenario = scenario;
			CycicScenarios.cycicScenarios.add(scenario);
		}
		init();
	}
	public static final String TITLE = "Cycic";
	static Pane pane = new Pane();
	static facilityNode workingNode = null;
	static MarketCircle workingMarket = null;
	static DataArrays workingScenario;
	static ToggleGroup group = new ToggleGroup();
	static ToggleButton toggle = new ToggleButton("RANDOM TEXT"){
		{
			setToggleGroup(group);
		}
	};
	
	/**
	 * Initiates the Pane and GridPane.
	 */
	private void init(){
		setTitle(TITLE);
		setOnMousePressed(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent e){
				CycicScenarios.workingCycicScenario = workingScenario;
			}
		});
		if (RealFacs.alfredStructs.size() < 1){
			RealFacs.init();
		}
		
		VBox cycicBox = new VBox();
		Cycic.pane.setId("cycicPane");
		Cycic.pane.setPrefSize(800, 600);
		Cycic.pane.setStyle("-fx-background-color: white;");
		
		// Temp Toolbar //
		final GridPane grid = new GridPane();
		grid.setStyle("-fx-background-color: #d6d6d6;");
		grid.setHgap(10);
		grid.setVgap(5);
		
		// Adding a new Facility //
		Text scenetitle1 = new Text("Add Facility");
		scenetitle1.setFont(new Font(20));
		grid.add(scenetitle1, 0, 0);
		Label facName = new Label("Name");
		grid.add(facName, 1, 0);
		// Name Field
		final TextField facNameField = new TextField();
		grid.add(facNameField, 2, 0);
		// Facility Type
		final ComboBox<String> structureCB = new ComboBox<String>();
		for(int i = 0; i < RealFacs.alfredStructs.size(); i++){
			structureCB.getItems().add((String) RealFacs.alfredStructsNames.get(i));	
		}
		structureCB.valueProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				structureCB.setValue(newValue);
			}
		});
		grid.add(structureCB, 3, 0);
		//Submit Button
		Button submit1 = new Button("Submit");
		submit1.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				facilityNode tempNode = new facilityNode();
				tempNode.facilityType = structureCB.getValue();
				for (int i = 0; i < RealFacs.alfredStructs.size(); i++){
					if (RealFacs.alfredStructsNames.get(i) == structureCB.getValue()){
						tempNode.facilityStructure = RealFacs.alfredStructs.get(i);
					}				
				}
				tempNode.name = facNameField.getText();
				tempNode.cycicCircle = CycicCircles.addNode(facNameField.getText(), tempNode);
				tempNode.sorterCircle = SorterCircles.addNode(facNameField.getText(), tempNode, tempNode);
				FormBuilderFunctions.formArrayBuilder(tempNode.facilityStructure, tempNode.facilityData);
			}
		});
		grid.add(submit1, 4, 0);
		
		// Adding a new Market
		Text scenetitle2 = new Text("Add Market");
		scenetitle2.setFont(new Font(20));
		grid.add(scenetitle2, 0, 1);
		Label markName = new Label("Name");
		grid.add(markName, 1, 1);
		// Name Field
		final TextField markNameField = new TextField();
		grid.add(markNameField, 2, 1);
		Button submit2 = new Button("Submit");
		submit2.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event){
				MarketNodes.addMarket(markNameField.getText());
				Cycic.workingMarket = workingScenario.marketNodes.get(workingScenario.marketNodes.size() - 1);
			}
		});
		grid.add(submit2, 3, 1);
		pane.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){
				for(int i = 0; i < pane.getChildren().size(); i ++){
					if(event.getButton().equals(MouseButton.PRIMARY)){
						if(pane.getChildren().get(i).getStyleClass().toString() == "menu-bar"){
							pane.getChildren().get(i).setVisible(false);
						}
					}
				}			
			}
		});
		
		// Prints the Cyclus input associated with this simulator. 
		Button output = new Button();
		output.setText("Generate Cyclus Input");
		output.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event){
				OutPut.output();
			}
		});
		grid.add(output, 0, 2);
		grid.add(toggle, 0, 3);
		/*
		Button save = new Button();
		save.setText("Save");
		save.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event){
				OutPut.save(workingScenario);
			}
		});
		
		Button load = new Button();
		load.setText("Load");
		load.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event){
				OutPut.load();
				workingScenario = cycicScenarios.cycicScenarios.get(cycicScenarios.cycicScenarios.size()-1);
				cycicScenarios.workingCycicScenario = workingScenario;
				VisFunctions.reloadPane();			
			}
		});
		grid.add(save, 1, 2);
		grid.add(load, 2, 2);
		*/
		cycicBox.getChildren().addAll(grid, pane);
		setContent(cycicBox);
	}
}