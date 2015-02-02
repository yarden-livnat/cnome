package edu.utexas.cycic;

import java.util.ArrayList;
import java.util.Arrays;

import org.controlsfx.control.RangeSlider;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.shape.Circle;

public class TimelineDisplay extends ViewBase {

	public TimelineDisplay(){
		super();
		setWidth(900);
		setMaxWidth(2000);
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
			//setMaxHeight(800);
			//setMinHeight(800);
			setHeight(500);
			setVgap (1);
			setHgap (1);

		}
	};

	static ScrollPane sp = new ScrollPane() {
		{
			setPrefViewportHeight(300);
			setFitToWidth(true);
			setHbarPolicy(ScrollBarPolicy.NEVER);
			//setPrefViewportWidth(2000);
			setVbarPolicy(ScrollBarPolicy.ALWAYS);
			setContent(outputPanel);
		}

	};

	static GridPane tlp = new GridPane(){
		{
			setWidth(outputPanel.getWidth());
			setHeight(40);
			setVgap(1);
			setHgap(1);
			setMaxHeight(40);
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
		// for controlsfx 8.20.8
		checkBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
			public void onChanged(ListChangeListener.Change<? extends String> c) {
				System.out.println(checkBox.getCheckModel().getCheckedItems());
				setPane();

			}
		});
		// for controlsfx 8.0.6
//		checkBox.getCheckModel().getSelectedItems().addListener(new ListChangeListener<String>() {
//			public void onChanged(ListChangeListener.Change<? extends String> c) {
//				System.out.println(checkBox.getCheckModel().getSelectedItems());
//				setPane();
//
//			}
//		});
		
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

		//useless code
		Line useless = new Line (0,0,0,39);
		useless.setVisible(false);
		tlp.add(useless, 0, 0, 1, 39);




		//useless

		pane.setTop(sp);
		pane.setCenter(tlp);
		pane.setBottom(panel);
		setContent(pane);

	}

	protected void setPane () {
		outputPanel.getChildren().clear();
		tlp.getChildren().clear();
		int yearDifference = MaxRange-MinRange;
		double unitWidth = tlp.getWidth()/yearDifference;

		Line xLine= new Line (0,0,(int)tlp.getWidth()-1,0);


		tlp.add(xLine, 0, 35,(int)tlp.getWidth()-1,2);
		for (int ii = MinRange; ii < MaxRange-1 ; ii ++){
			/*Line line = new Line (0,0,0,220);
			line.getStrokeDashArray().addAll(5d, 15d);
			outputPanel.add (line,0,0,1,230);
			 */
			if (ii%50 == 0) {
				Line fiftyTick = new Line (0,0,0,35);
				int loc = (int) ((ii-MinRange)*unitWidth);
				Text fifties = new Text (Integer.toString(ii));
				if (loc <= tlp.getWidth()-2){
					tlp.add (fiftyTick,loc,0,2,35);
				}
				else {
					tlp.add (fiftyTick,loc-2,0,2,35);
				}
				if (loc <=15){
					tlp.add (fifties ,loc, 36, 29, 4);
				} else if((loc > 15) && 
						(tlp.getWidth()-loc >= 15)){
					tlp.add (fifties, loc-15, 36, 29, 4);

				} else {
					tlp.add (fifties ,(int)(outputPanel.getWidth()-30), 36, 29, 4);

				}

			} else if (ii%10 == 0) {

				Line tenTick = new Line (0, 0, 0, 15);
				int loc =  (int) ((ii-MinRange)*unitWidth);
				if (loc <= tlp.getWidth()-2){

					tlp.add(tenTick, loc, 20,2,15);
				}
				else {
					tlp.add(tenTick, loc-2, 20,2, 15);
				}
				if (yearDifference <= 100) {

					Text ten = new Text (Integer.toString(ii));

					if (loc <= 15){
						tlp.add (ten ,loc, 36, 29, 4);
					} else if((loc > 15) && 
							(tlp.getWidth()-loc >=15)){
						tlp.add (ten, loc-15, 36, 29, 4);

					} else {
						tlp.add (ten ,(int)(outputPanel.getWidth()-30), 36, 29, 4);

					}

				}

			} 
			if (yearDifference <=20) {

				if ((ii%2 ==0)&&(ii%10 !=0)) {
					Line twoTick = new Line (0, 0, 0, 10);
					int loc = (int) ((ii- MinRange)*unitWidth);
					if (loc <= tlp.getWidth()-2){

						tlp.add(twoTick, loc, 25,2,10);
					}
					else {
						tlp.add(twoTick, loc-2, 25,2, 10);
					}

					Text two = new Text (Integer.toString(ii));

					if (loc <= 15){
						tlp.add (two ,loc, 36, 29, 4);
					} else if((loc > 15) && 
							(tlp.getWidth()-loc >= 15)){
						tlp.add (two, loc- 15, 36, 29, 4);

					} else {
						tlp.add (two ,(int)(outputPanel.getWidth()-30), 36, 29, 4);

					}

				}

			}
		}

		displayNodes ();


	}
	protected void addEvents(Object event, final int y) {
		ArrayList <Object> facilityEvent = (ArrayList<Object>) event; 
		int radius = 4; 
		int yearDifference = MaxRange-MinRange;
		double unitWidth = outputPanel.getWidth()/yearDifference;


		for (int i = 0; i < facilityEvent.size(); i++) {
			ArrayList <Object> singleEvent = (ArrayList<Object>) facilityEvent.get(i);
			int eventYear = Integer.parseInt( singleEvent.get(1).toString());
			String eventName = (String) singleEvent.get(0);
			Circle eventCircle = new Circle();
			
			final Rectangle eventInformation = new Rectangle (0,0,75,45);
			eventInformation.setVisible(false);
			eventInformation.setFill(Color.WHEAT);
			
			eventCircle.setFill(Color.DARKORANGE);
			int x;
			final int xx;
			

			if ((eventYear < MinRange)||(eventYear > MaxRange)){
				x = 0;
				xx = 0;
				
			} else if (((eventYear-4)<= MinRange)&&(eventYear-4)>0) {
				x = 4;
				xx = 4;
			

			} else if ((eventYear+75)>= MaxRange) {
				
				if ((eventYear+4)>= MaxRange){
					x = (int) (outputPanel.getWidth()-5);
				}
				else {
					x = (int) ((eventYear-MinRange)*unitWidth-4);
				}
				
				xx = (int) (outputPanel.getWidth()-76);
				

			} else {

				x = (int) ((eventYear-MinRange)*unitWidth-4);
				xx = (int) (outputPanel.getWidth()-76);
				

			}

			if (x !=0){
				//eventCircle.setCenterX(x);
				//eventCircle.setCenterY(y);
				eventCircle.setRadius (radius);
				//Text newLabel = new Text (eventName);
				outputPanel.add(eventCircle,x-4,y-12,8,8);
			//	outputPanel.add(newLabel,x-20,y-20,40,40);
				//outputPanel.add(info,xx-20,y-60, 74, 45);
				
				eventCircle.setOnMouseEntered(new EventHandler <MouseEvent>(){
					@Override
					public void handle(MouseEvent e) {
						outputPanel.add(eventInformation,xx-20,y-30, 75, 45);
					}
				});

				eventCircle.setOnMouseExited(new EventHandler <MouseEvent>(){
					@Override
					public void handle(MouseEvent e) {
						outputPanel.getChildren().remove(eventInformation);


					}
				});
/*
				newLabel.setOnMouseEntered(new EventHandler <MouseEvent>(){
					@Override
					public void handle(MouseEvent e) {
					

					}
				});

				newLabel.setOnMouseExited(new EventHandler <MouseEvent>(){
					@Override
					public void handle(MouseEvent e) {

				
					}
				});
*/

			}
		}
	}
	protected void displayNodes () {

		int spacing = 40;
		int yearDifference = MaxRange-MinRange;
		double unitWidth = outputPanel.getWidth()/yearDifference;
		double width = outputPanel.getWidth();
		//System.out.println(unitWidth);
		//test	
		//	grid add(Node child, int columnIndex, int rowIndex, int colspan, int rowspan)
		ArrayList <Object> sampleEntry = new ArrayList <>();
		ArrayList <Object> event1 = new ArrayList(Arrays.asList("event 1", "2010", "refuel"));
		ArrayList <Object> event2 = new ArrayList(Arrays.asList("event 2", "2050","emergency shutdown"));
		ArrayList <Object> data1event = new ArrayList (Arrays.asList(event1, event2));

		ArrayList <Object> event3 = new ArrayList(Arrays.asList("event 3", "1950","fire drill"));
		ArrayList <Object> data2event = new ArrayList (Arrays.asList(event3));
		ArrayList <Object> data3event = new ArrayList<>();
		ArrayList <Object> event4 = new ArrayList(Arrays.asList("event 4", "2007", "new fuel arrived"));
		ArrayList <Object> event5 = new ArrayList(Arrays.asList("event 5", "2049","refuel"));
		ArrayList <Object> event6 = new ArrayList(Arrays.asList("event 6", "2087", "facility remodel"));
		ArrayList <Object> data4event = new ArrayList (Arrays.asList(event4,event5,event6));

		ArrayList <Object> data1 = new ArrayList(Arrays.asList("sample 1", "1995", "2150",data1event));
		ArrayList <Object> data2 = new ArrayList(Arrays.asList("sample 2", "1900", "1993",data2event));
		ArrayList <Object> data3 = new ArrayList(Arrays.asList("sample 3", "1946", "2090",data3event));
		ArrayList <Object> data4 = new ArrayList(Arrays.asList("sample 4", "2000", "2100",data4event));
		sampleEntry.add(data1);
		sampleEntry.add(data2);
		sampleEntry.add(data3);
		sampleEntry.add(data4);
		/*Rectangle(double x,
        double y,
        double width,
        double height)
		 */
		Line useless = new Line (0,0,50,0);
		useless.setVisible(false);

		for (int i = 0; i < sampleEntry.size(); i++){
			ArrayList <Object> singleEntry = (ArrayList<Object>) sampleEntry.get(i);

			int reserveSpace = spacing*(i+1)-10;
			if ((Integer.parseInt((String) singleEntry.get(1)) <= MinRange) 
					&& (Integer.parseInt((String) singleEntry.get(2))>= MaxRange)){

				Rectangle facilityLine = new Rectangle (0,0,width-1,4);
				facilityLine.setFill(Color.LIGHTGRAY);
				outputPanel.add(facilityLine,0,reserveSpace,(int)width-1,4);
				addEvents (singleEntry.get(3),spacing*(i+1));

			} else if ((Integer.parseInt((String) singleEntry.get(1)) <= MinRange)&&
					((Integer.parseInt((String)singleEntry.get(2))<= MaxRange)
							&& (Integer.parseInt((String) singleEntry.get(2))> MinRange))){

				int range = Integer.parseInt((String)singleEntry.get(2))-MinRange;
				int High = (int) (range*unitWidth)+1;
				Rectangle facilityLine = new Rectangle (0, 0, High-1, 4);
				facilityLine.setFill(Color.LIGHTGRAY);
				outputPanel.add(facilityLine, 0, reserveSpace,High,4);
				addEvents (singleEntry.get(3),spacing*(i+1));

			} else if ((Integer.parseInt((String) singleEntry.get(1)) > MinRange) 
					&&(Integer.parseInt((String) singleEntry.get(2)) >= MaxRange
							&&(Integer.parseInt((String)singleEntry.get(1)) < MaxRange))){
				int Low = (int) (Integer.parseInt((String) singleEntry.get(1))-MinRange);
				int startLocation = (int) (Low * unitWidth);
				Rectangle facilityLine = new Rectangle (0,0,width-startLocation-1, 4);
				facilityLine.setFill(Color.LIGHTGRAY);
				int range = (int) (width-startLocation)+1;
				outputPanel.add(facilityLine, startLocation, reserveSpace, range,4);
				addEvents (singleEntry.get(3),spacing*(i+1));

			} else if ((Integer.parseInt((String) singleEntry.get(1)) > MinRange)
					&&(Integer.parseInt((String) singleEntry.get(2)) < MaxRange
					&&(Integer.parseInt((String)singleEntry.get(1)) < MaxRange))) {

				int High = Integer.parseInt((String) singleEntry.get(2))-MinRange;
				int Low = Integer.parseInt((String) singleEntry.get(1))-MinRange;
				int startLocation = (int) ( Low*unitWidth);
				int range = (int) ((High-Low)*unitWidth)+1;
				Rectangle facilityLine = new Rectangle (0,0,range-1, 4);
				facilityLine.setFill(Color.LIGHTGRAY);
				outputPanel.add (facilityLine, startLocation, reserveSpace, range, 4);
				addEvents (singleEntry.get(3),spacing*(i+1));



			}



		}



	}
}

