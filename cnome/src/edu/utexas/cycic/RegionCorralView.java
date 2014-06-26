package edu.utexas.cycic;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import edu.utah.sci.cyclist.core.ui.components.ViewBase;
import edu.utexas.cycic.tools.RegionViewTool;

public class RegionCorralView extends ViewBase {

	{
		setMinHeight(530);
		setMaxHeight(530);
		setMinWidth(630);
		setMaxWidth(630);
	}

	static regionNode workingRegion = null; 

	static Pane corralPane = new Pane(){
		{
			setMinHeight(375);
			setMaxHeight(375);
			setMinWidth(630);
			setMaxWidth(630);
		}
	};

	static GridPane regionCorralGrid = new GridPane(){
		{
			setHgap(10);
			setVgap(5);
		}
	};

	public RegionCorralView() {

		/* Create content for RegionCorralView header */

		final Label regionLabel = new Label("Region Name:");
		regionLabel.setFont(new Font(12));
		regionCorralGrid.add(regionLabel, 0, 0);

		final TextField regionText = new TextField();
		regionCorralGrid.add(regionText, 1, 0);

		ObservableList<String> typeList = FXCollections.observableArrayList("Growth Region", "Other");
		final ComboBox typeOptions = new ComboBox(typeList);
		regionCorralGrid.add(typeOptions, 2, 0);

		final Button corralButton = new Button();
		corralButton.setText("Add");
		regionCorralGrid.add(corralButton, 3, 0);

		final Label regionPrototypeLabel = new Label("Region Prototypes:");
		regionCorralGrid.add(regionPrototypeLabel, 4, 0);

		ScrollPane root = new ScrollPane(){
			{
				setMinHeight(50);
				setMaxHeight(50);
			}
		};
		HBox hroot = new HBox(){
			{
				setLayoutY(50);
				setSpacing(10);
			}
		};
		
		
	
		hroot.setLayoutX(corralPane.getMaxWidth()-regionLabel.getLayoutX()-regionText.getLayoutX()-typeOptions.getLayoutX()-corralButton.getLayoutX()-regionPrototypeLabel.getLayoutX());
		root.setContent(hroot);
		regionCorralGrid.add(root, 5, 0);

		/* Create content of RegionCorral footer */

		Label unassociatedFacilityTitle = new Label("Unassociated Facilities:"){
			{
				setFont(new Font(12));
			}
		};
		regionCorralGrid.add(unassociatedFacilityTitle, 4, 1);
		
		HBox unassociatedFacilityList = new HBox(10);

		ScrollPane root2 = new ScrollPane(){
			{
				setMinHeight(50);
				setMaxHeight(50);
			}
		};
		root2.setContent(unassociatedFacilityList);
		regionCorralGrid.add(root2, 5, 1);

		/* Place RegionCorralView header, corralPane, and footer on main corralVBox */

		VBox mainCorralVBox = new VBox(15);
		mainCorralVBox.getChildren().addAll(regionCorralGrid, corralPane);
		setContent(mainCorralVBox);

		EventHandler addRegion = new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event) {
				final regionNode region = new regionNode();
				region.regionStruct = PracticeRegions.growthRegion;
				region.type = typeOptions.getId();
				FormBuilderFunctions.formArrayBuilder(region.regionStruct, region.regionData);
				regionNode.regionCircle = RegionShape.addRegion(regionText.getText(), region);
				
				DataArrays.regionNodes.add(region);

				corralPane.getChildren().addAll(regionNode.regionCircle, regionNode.regionCircle.text, regionNode.regionCircle.menuBar);


			}	//ends definition of EventHandler addRegion  
		};	//ends EventHandler addRegion

		corralButton.setOnMouseClicked(addRegion);

		if (CycicScenarios.workingCycicScenario.regionStructs.size() < 1) {
			PracticeRegions.init();
		}

	}
}
