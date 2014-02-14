package edu.utexas.cycic;

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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
		
		simInfo.setHgap(4);
		simInfo.setVgap(4);
		simInfo.setPadding(new Insets(10, 10, 10, 10));
		
		months();
		init();
	}
	
	public static final String TITLE = "Simulation Details";
	GridPane simInfo = new GridPane();
	HashMap<String, String> months = new HashMap<String, String>();
	ArrayList<String> monthList = new ArrayList<String>();
	static GridPane commodGrid = new GridPane(){
		{
			
		}
	};
	static Button addNewCommod = new Button(){
		{
			setOnAction(new EventHandler<ActionEvent>(){
				public void handle(ActionEvent e){
					addNewCommodity();
				}
			});
			setText("Add New Commodity");
		}
	};
	
	/**
	 * Adds the GridPane and input nodes to the simulationInfo view.
	 */
	public void init(){
		setTitle(TITLE);
		TextField duration = VisFunctions.numberField();
		duration.setText(CycicScenarios.workingCycicScenario.simulationData.duration);
		duration.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				CycicScenarios.workingCycicScenario.simulationData.duration = newValue;
			}
		});
		simInfo.add(new Label("Duration"), 0, 0);
		simInfo.add(duration, 1, 0);
		simInfo.add(new Label("Months"), 2, 0);
		

		final ComboBox<String> startMonth = new ComboBox<String>();
		startMonth.setValue(months.get(CycicScenarios.workingCycicScenario.simulationData.startMonth));
		for(int i = 0; i < 12; i++ ){
			startMonth.getItems().add(monthList.get(i));
		}
		startMonth.valueProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				CycicScenarios.workingCycicScenario.simulationData.startMonth = months.get(newValue);
			}
		});
		simInfo.add(new Label("Start Month"), 0, 1);
		simInfo.add(startMonth, 1, 1);
		
		TextField startYear = VisFunctions.numberField();
		startYear.setText(CycicScenarios.workingCycicScenario.simulationData.startYear);
		startYear.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				CycicScenarios.workingCycicScenario.simulationData.startYear = newValue;
			}
		});
		simInfo.add(new Label("Start Year"), 0, 2);
		simInfo.add(startYear, 1, 2);
		
		TextField simStart = VisFunctions.numberField();
		simStart.setText(CycicScenarios.workingCycicScenario.simulationData.simStart);
		simStart.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				CycicScenarios.workingCycicScenario.simulationData.simStart = newValue;
			}
		});
		simInfo.add(new Label("Simulation Start"), 0 ,3);
		simInfo.add(simStart, 1, 3);
		
		TextField decay = VisFunctions.numberField();
		decay.setText(CycicScenarios.workingCycicScenario.simulationData.decay);
		decay.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				CycicScenarios.workingCycicScenario.simulationData.decay = newValue;
			}
		});
		simInfo.add(new Label("Decay Flag"), 0, 4);
		simInfo.add(decay, 1, 4);
		simInfo.add(new Label(""), 0 , 5);
		simInfo.add(new Label("Simulation Commodities"), 0, 6);
		simInfo.add(commodGrid, 0, 7);
		buildCommodPane();
		setContent(simInfo);
	}
	/**
	 * 
	 */
	public static void buildCommodPane(){
		commodGrid.getChildren().clear();
		for (int i = 0; i < CycicScenarios.workingCycicScenario.CommoditiesList.size(); i++){
			TextField commodity = new TextField();
			commodity.setText(CycicScenarios.workingCycicScenario.CommoditiesList.get(i).getText());
			commodGrid.add(commodity, 0, i );
			final int index = i;
			commodity.textProperty().addListener(new ChangeListener<String>(){
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
					CycicScenarios.workingCycicScenario.CommoditiesList.get(index).setText(newValue);
				}
			});
			Button removeCommod = new Button();
			removeCommod.setText("Delete Commodity");
			removeCommod.setOnAction(new EventHandler<ActionEvent>(){
				public void handle(ActionEvent e){
					CycicScenarios.workingCycicScenario.CommoditiesList.remove(index);
					buildCommodPane();
				}
			});	
			commodGrid.add(removeCommod, 1, index);
		}
		commodGrid.add(addNewCommod, 0, CycicScenarios.workingCycicScenario.CommoditiesList.size());
	}
	
	/**
	 * Adds a new TextField to the commodity GridPane tied to a new commodity in the 
	 * simulation.
	 */
	static public void addNewCommodity(){
		Label commodity = new Label();
		commodity.setText("");
		CycicScenarios.workingCycicScenario.CommoditiesList.add(commodity);
		TextField newCommod = new TextField();
		newCommod.setText(CycicScenarios.workingCycicScenario.CommoditiesList.get(CycicScenarios.workingCycicScenario.CommoditiesList.size()-1).getText());
		newCommod.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				CycicScenarios.workingCycicScenario.CommoditiesList.get(CycicScenarios.workingCycicScenario.CommoditiesList.size()-1).setText(newValue);
			}
		});
		buildCommodPane();
	}
	
	/**
	 * Quick hack to convert months into their integer values.
	 * i.e. January = 0, Feb = 1, etc...
	 */
	public void months(){
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
