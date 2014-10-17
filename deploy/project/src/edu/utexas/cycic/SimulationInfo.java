package edu.utexas.cycic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import edu.utah.sci.cyclist.core.ui.components.ViewBase;
/**
 * View for the Simulation Control Information
 * @author Robert
 *
 */
public class SimulationInfo extends ViewBase{
	/**
	 * Initialization function for the view.
	 */
	public SimulationInfo(){
		super();
		if (monthList.size() == 0){
			months();
		}
		init();
	}
	
	public static final String TITLE = "Commodities and Details";
	HashMap<String, String> months = new HashMap<String, String>();
	ArrayList<String> monthList = new ArrayList<String>();
	static Window window;
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
	static GridPane commodGrid = new GridPane(){
		{
			setVgap(5);
			setHgap(5);
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
	static HBox simControlBox = new HBox(){
		{
			setStyle("-fx-font-size: 12;");
			getChildren().addAll(simDetailBox);
		}
	};

	
	/**
	 * Adds the GridPane and input nodes to the simulationInfo view.
	 */
	public void init(){
		setTitle(TITLE);
		TextField duration = VisFunctions.numberField();
		duration.setMaxWidth(150);
		duration.setPromptText("Length of Simulation");
		duration.setText(Cycic.workingScenario.simulationData.duration);
		duration.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				Cycic.workingScenario.simulationData.duration = newValue;
			}
		});
		simInfo.add(new Label("Duration (Months)"), 0, 0);
		simInfo.add(duration, 1, 0);
		

		final ComboBox<String> startMonth = new ComboBox<String>();
		startMonth.setValue(months.get(Cycic.workingScenario.simulationData.startMonth));
		for(int i = 0; i < 12; i++ ){
			startMonth.getItems().add(monthList.get(i));
		}
		
		startMonth.setValue(monthList.get(Integer.parseInt(Cycic.workingScenario.simulationData.startMonth)));
		startMonth.valueProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				Cycic.workingScenario.simulationData.startMonth = months.get(newValue);
			}
		});
		startMonth.setPromptText("Select Month");
		simInfo.add(new Label("Start Month"), 0, 1);
		simInfo.add(startMonth, 1, 1);
		
		TextField startYear = VisFunctions.numberField();
		startYear.setText(Cycic.workingScenario.simulationData.startYear);
		startYear.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				Cycic.workingScenario.simulationData.startYear = newValue;
			}
		});
		startYear.setPromptText("Starting Year");
		startYear.setMaxWidth(150);
		simInfo.add(new Label("Start Year"), 0, 2);
		simInfo.add(startYear, 1, 2);
				
		TextArea description = new TextArea();
		description.setMaxSize(350, 250);
		description.setWrapText(true);
		description.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				Cycic.workingScenario.simulationData.description = newValue;
			}
		});
		simInfo.add(new Label("Description"), 0, 3);
		simInfo.add(description, 1, 3);
		
		TextArea notes = new TextArea();
		notes.setMaxSize(350, 250);
		notes.setWrapText(true);
		notes.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				Cycic.workingScenario.simulationData.notes = newValue;
			}
		});
		simInfo.add(new Label("Notes"), 0, 4);
		simInfo.add(notes, 1, 4);
		
	
		// Prints the Cyclus input associated with this simulator. 
		Button output = new Button();
		output.setText("Generate Cyclus Input");
		output.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event){
	              FileChooser fileChooser = new FileChooser();
	              
	              //Set extension filter
	              FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
	              fileChooser.getExtensionFilters().add(extFilter);
	              fileChooser.setTitle("Please save as Cyclus input file.");
	              
	              //Show save file dialog
	              File file = fileChooser.showSaveDialog(window);
				OutPut.output(file);
			}
		});
		simInfo.add(output, 0, 5);
	
		
		setContent(simControlBox);
	}

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
		
		months.put("January", "0");
		months.put("Febuary", "1");
		months.put("March", "2");
		months.put("April", "3");
		months.put("May", "4");
		months.put("June", "5");
		months.put("July", "6");
		months.put("August", "7");
		months.put("September", "8");
		months.put("October", "9");
		months.put("November", "10");
		months.put("December", "11");
	}
}
