package edu.utexas.cycic;

import java.util.ArrayList;

import org.controlsfx.control.RangeSlider;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;

import edu.utah.sci.cyclist.core.ui.components.ViewBase;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.animation.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;


public class TimelineDisplay extends ViewBase {

	public TimelineDisplay(){
		super();
		setWidth(900);
		setMaxWidth(900);
		setHeight(600);
		setMaxHeight(900);
		init ();
	}

	static BorderPane pane= new BorderPane();
	static ArrayList <Object> structure = new ArrayList<>();
	static ArrayList <Object> facilityInformation = new ArrayList <>();
	static ArrayList <Object> facilityParentNode = new ArrayList <>();
	static Integer MaxRange = 0;
	static Integer MinRange = 0;
	
	static ArrayList <Object> displayNodes = new ArrayList <> ();
	/*
	 * [
	 * 		[node, start time, end time]
	 * 		[node, start time, end time]
	 * ]
	 */
	
	static GridPane outputPanel=new GridPane(){
		{
			setWidth(getWidth());
			setMaxWidth (900);
			setMaxHeight(500);
			setMinHeight(200);
			setHeight(250);
			setVgap (1);
			setHgap (1);
			
		}
	};

	private void init (){
		ArrayList <Object> structureOrder = new ArrayList<>();
		ArrayList <Object> displayData = new ArrayList<>();
		
		for (int i = 0; i < CycicScenarios.workingCycicScenario.FacilityNodes.size();i++){
			structure.add(CycicScenarios.workingCycicScenario.FacilityNodes.get(i).facilityStructure);
			System.out.println(structure.get(i));
			facilityParentNode.add((String)CycicScenarios.workingCycicScenario.FacilityNodes.get(i).name
					+ "  ("+CycicScenarios.workingCycicScenario.FacilityNodes.get(i).facilityType +")");
		}

		VBox panel=new VBox(){
			{
				setWidth(getWidth());
				setHeight(getHeight());
			}
		};
		
		HBox interAction =new HBox(){
			{
				setWidth(getWidth());
				setSpacing(20);
			}
		};

		Label output=new Label();
		output.setText("OutputPanel");
		outputPanel.getChildren().add(output);
		final RangeSlider yearRange = new RangeSlider(0,2200,1910,1990);
		yearRange.setMin(1900);
		yearRange.setShowTickMarks(true);
		yearRange.setShowTickLabels(true);
		yearRange.setBlockIncrement(10);
		
		final ObservableList<String> displayType = FXCollections.observableArrayList();
		if (displayType.isEmpty()){
			displayType.add("All Nodes");
		}
		for (int ii = 0; ii < facilityParentNode.size(); ii++){
			
			displayType.add((String) facilityParentNode.get(ii));
		}
		HBox minField=new HBox();
		minField.setSpacing(5);
		Label min=new Label();
		min.setText("Min");
		HBox maxField=new HBox();
		maxField.setSpacing(5);
		Label max=new Label();
		max.setText("Max");

		final TextField minNumber = new TextField();
		final TextField maxNumber = new TextField();
		minNumber.textProperty().bind(yearRange.lowValueProperty().asString("%.0f"));
		MinRange= Integer.parseInt(minNumber.getText ());
		maxNumber.textProperty().bind(yearRange.highValueProperty().asString("%.0f"));
		MaxRange= Integer.parseInt(maxNumber.getText ());
		minField.getChildren().add(min);
		minField.getChildren().add(minNumber);
		maxField.getChildren().add(max);
		maxField.getChildren().add(maxNumber);
		final CheckComboBox<String> checkBox = new CheckComboBox<String>(displayType);
		checkBox.setMaxWidth(200);
		checkBox.setMinWidth(200);
		checkBox.getCheckModel().getSelectedItems().addListener(new ListChangeListener<String>() {
			public void onChanged(ListChangeListener.Change<? extends String> c) {
				System.out.println(checkBox.getCheckModel().getSelectedItems());
				setPane();

			}
		});

		interAction.getChildren().add(checkBox);
		interAction.getChildren().add(minField);
		interAction.getChildren().add(maxField);
		panel.getChildren().add(yearRange);
		panel.getChildren().add(interAction);
		
		yearRange.setOnMouseDragged(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){
				MinRange= Integer.parseInt(minNumber.getText ());
				MaxRange= Integer.parseInt(maxNumber.getText ());
				System.out.println (MinRange+"+"+MaxRange);
				setPane();
				
			}
		});

		
		pane.setTop(outputPanel);
		pane.setBottom(panel);
		setContent(pane);
	}
	protected void setPane () {
		outputPanel.getChildren().clear();
		int yearDifference = MaxRange-MinRange;
		int firstQV = yearDifference/4+MinRange;
		int thirdQV = MaxRange- yearDifference/4;
		int midV = MinRange+ yearDifference/2;
		Text firstQ = new Text (Integer.toString(firstQV));
		Text thirdQ = new Text (Integer.toString(thirdQV));
		Text mid = new Text (Integer.toString(midV));
		Text min = new Text (MinRange.toString());
		Text max = new Text (MaxRange.toString());
		outputPanel.add (min,0,220,30,5);
		outputPanel.add (firstQ, (int)outputPanel.getWidth()/4-15,220,30,5);
		outputPanel.add (mid, (int)outputPanel.getWidth()/2-15,220, 30, 5);
		outputPanel.add (thirdQ, (int) ((int)3*outputPanel.getWidth()/4-15),220,30,5);
		outputPanel.add (max, (int) outputPanel.getWidth()-30, 220,30,5);
		Line xLine= new Line (0,240,(int)outputPanel.getWidth()-1,240);
		outputPanel.add(xLine, 0, 220,(int)outputPanel.getWidth(),1);
		for (int ii = 10; ii < outputPanel.getWidth() ; ii=ii+10){
			Line line = new Line (0,0,0,220);
			line.getStrokeDashArray().addAll(5d, 15d);
			outputPanel.add (line,0,0,1,230);
			
		}
		
		displayNodes ();
		
		
	}
	
	protected void displayNodes () {
		
		for (int i = 0; i < displayNodes.size(); i++){
			ArrayList <Object> singleEntry = (ArrayList<Object>) displayNodes.get(i);
			if (Integer.parseInt((String) singleEntry.get(1)) <= MinRange) {
				
			} else {
				
				
			}
			
			if (Integer.parseInt((String) singleEntry.get(2)) <= MaxRange) {
				
			} else {
				
				
			}
			 
		}
		
	}
}

