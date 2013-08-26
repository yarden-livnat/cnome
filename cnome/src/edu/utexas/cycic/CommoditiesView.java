package edu.utexas.cycic;

import edu.utah.sci.cyclist.ui.components.ViewBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Commodities Class extends the Cyclist.View class and is used for
 * controlling the commodities of a simulation.
 * @author Robert
 *
 */
public class CommoditiesView extends ViewBase{
	/**
	 * Initiates the commodity view.
	 */
	public CommoditiesView(){
		super();
		init();
		addNewCommod.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				addNewCommodity();
			}
		});
		addNewCommod.setText("Add New Commodity");
		setContent(commodGrid);
	}
	
	private static GridPane commodGrid = new GridPane();
	private static Button addNewCommod = new Button();
	
	/**
	 * Function for building the gridpane used by the commodity views.		
	 */
	public static void init(){
		commodGrid.getChildren().clear();
		for (int i = 0; i < cycicScenarios.workingCycicScenario.CommoditiesList.size(); i++){
			TextField commodity = new TextField();
			commodity.setText(cycicScenarios.workingCycicScenario.CommoditiesList.get(i).getText());
			commodGrid.add(commodity, 0, i );
			final int index = i;
			commodity.textProperty().addListener(new ChangeListener<String>(){
				public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
					cycicScenarios.workingCycicScenario.CommoditiesList.get(index).setText(newValue);
				}
			});
			Button removeCommod = new Button();
			removeCommod.setText("Delete Commodity");
			removeCommod.setOnAction(new EventHandler<ActionEvent>(){
				public void handle(ActionEvent e){
					cycicScenarios.workingCycicScenario.CommoditiesList.remove(index);
					init();
				}
			});	
			commodGrid.add(removeCommod, 1, index);
		}
		commodGrid.add(addNewCommod, 0, cycicScenarios.workingCycicScenario.CommoditiesList.size());
	}
	
	/**
	 * Adds a new TextField to the commodity GridPane tied to a new commodity in the 
	 * simulation.
	 */
	static public void addNewCommodity(){
		Label commodity = new Label();
		commodity.setText("");
		cycicScenarios.workingCycicScenario.CommoditiesList.add(commodity);
		TextField newCommod = new TextField();
		newCommod.setText(cycicScenarios.workingCycicScenario.CommoditiesList.get(cycicScenarios.workingCycicScenario.CommoditiesList.size()-1).getText());
		newCommod.textProperty().addListener(new ChangeListener<String>(){
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
				cycicScenarios.workingCycicScenario.CommoditiesList.get(cycicScenarios.workingCycicScenario.CommoditiesList.size()-1).setText(newValue);
			}
		});
		init();
	}
}
