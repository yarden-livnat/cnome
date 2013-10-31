package edu.utexas.cycic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import edu.utah.sci.cyclist.ui.components.ViewBase;

public class RegionCorralView extends ViewBase {

	{
		setMinHeight(620);
		setMaxHeight(620);
		setMinWidth(650);
		setMaxWidth(650);
	}

	static regionNode workingRegion = null; 

	static Pane corralPane = new Pane(){
		{
			setMinHeight(500);
			setMaxHeight(500);
			setMinWidth(650);
			setMaxWidth(650);
		}
	};

	public RegionCorralView() {

		//if (cycicScenarios.workingCycicScenario.regionStructs.size() < 1) {
			//PracticeRegions.init();
		//}

		/* Create content for RegionCorralView header */

		final Label regionLabel = new Label("Region Name:");
		regionLabel.setFont(new Font(50));
		final TextField regionText = new TextField(); 

		ObservableList<String> typeList = FXCollections.observableArrayList("Growth Region", "Other");
		final ComboBox typeOptions = new ComboBox(typeList);

		final Button corralButton = new Button();
		corralButton.setText("Add");

		final Label regionPrototypeLabel = new Label("Region Prototypes:");

		final ScrollPane root = new ScrollPane(){
			{
				setMinHeight(30);
				setMaxHeight(30);
			}
		};
		final HBox hroot = new HBox(){
			{
				setLayoutY(30);
				setSpacing(10);
			}
		};
		final RegionShape circle = new RegionShape(){
			{
				setRadius(10);
				setStroke(Color.BLACK);
				setFill(Color.GREEN);
			}
		};
		final RegionShape ncircle = new RegionShape(){
			{
				setRadius(10);
				setStroke(Color.BLACK);
				setFill(Color.RED);
			}
		};

		hroot.getChildren().addAll(circle, ncircle);
		root.setContent(hroot);

		/* Create RegionCorralView header */

		HBox regionCorralHeader = new HBox(10); 
		regionCorralHeader.getChildren().addAll(regionLabel, regionText, typeOptions, corralButton, regionPrototypeLabel, root);
		setContent(regionCorralHeader);

		/* Create content of RegionCorral footer */

		final Label unassociatedFacilityTitle = new Label("Unassociated Facilities:"){
			{
				setFont(new Font(30));
			}
		};
		final Circle unassociatedCircleFacility = new Circle(10){
			{
				setStroke(Color.BLACK);
				setFill(Color.BLUE);
			}
		};
		final Rectangle unassociatedRectangleFacility = new Rectangle(25, 15){
			{
				setStroke(Color.BLACK);
				setFill(Color.GREEN);
			}
		};
		final HBox unassociatedFacilityList = new HBox(10){
			{
				setMinWidth(30);
				getChildren().addAll(unassociatedCircleFacility, unassociatedRectangleFacility);
			}
		};
		final ScrollPane root2 = new ScrollPane(){
			{
				setMinHeight(30);
				setMaxHeight(30);
				setContent(unassociatedFacilityList);
			}
		};
		
		/* Create RegionCorralView footer */
		
		final HBox regionCorralFooter = new HBox(10){
			{
				getChildren().addAll(unassociatedFacilityTitle, root2);
			}
		};

		/* Place RegionCorralView header, corralPane, and footer on main corralVBox */

		VBox mainCorralVBox = new VBox(15);
		mainCorralVBox.getChildren().addAll(regionCorralHeader, corralPane, regionCorralFooter);
		setContent(mainCorralVBox);

		EventHandler addRegion = new EventHandler<MouseEvent>(){
			@SuppressWarnings("unchecked")
			public void handle(MouseEvent event) {
				final regionNode region = new regionNode();
				regionNode.regionCircle = RegionShape.addRegion(regionText.getText());

				//region.regionStruct = (ArrayList<Object>) cycicScenarios.workingCycicScenario.regionStructs.get(0);
				//FormBuilderFunctions.formArrayBuilder(region.regionStruct, region.regionData);
				//from line 82-83 of RegionView

				corralPane.getChildren().addAll(regionNode.regionCircle, regionNode.regionCircle.text);


			}	//ends definition of EventHandler addRegion  
		};	//ends EventHandler addRegion

		corralButton.setOnMouseClicked(addRegion);
		
		if (CycicScenarios.workingCycicScenario.regionStructs.size() < 1) {
			PracticeRegions.init();
		}

	}
}
