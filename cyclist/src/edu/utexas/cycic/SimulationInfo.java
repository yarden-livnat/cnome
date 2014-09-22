package edu.utexas.cycic;

import java.awt.Cursor;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.controlsfx.dialog.Dialogs;

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
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import edu.utah.sci.cyclist.Cyclist;
import edu.utah.sci.cyclist.core.ui.MainScreen;
import edu.utah.sci.cyclist.core.ui.components.ViewBase;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;
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
	 * 
	 */
	static HBox simControlBox = new HBox(){
		{
			setStyle("-fx-font-size: 12;");
			getChildren().addAll(commodBox, simDetailBox);
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
		/*Button load = new Button();
		load.setText("Load");
		load.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event){
				FileChooser fc = new FileChooser();
				fc.setTitle("Please choose a saved scenario.");
				File file = fc.showOpenDialog(window);
				OutPut.loadFile(file);
			}
		});
		simInfo.add(load, 1, 5);*/
		buildCommodPane();
		
		
		setContent(simControlBox);
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
