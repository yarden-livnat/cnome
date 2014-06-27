package edu.utexas.cycic;

import java.util.ArrayList;

import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utexas.cycic.tools.RegionViewTool;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * This class extends the Java Circle class. Used to represent a region
 * in the RegionCorralView and contains all of the information associated with
 * the region.
 * @author birdybird
 *
 */

public class RegionShape extends Rectangle {

	protected static double mousey;
	protected static double mousex;
	protected static double x;
	protected static double y;
	protected static double deltax;
	protected static double deltay;
	Object name;
	Label text = new Label("");
	MenuBar menuBar = new MenuBar();
	ArrayList<Integer> rgbColor = new ArrayList<Integer>();
	ListView<String> facilityList = new ListView<String>();
	ListView<String> institutionList = new ListView<String>();
	{
		setId("this");
	}

	static RegionShape addRegion(final String name, final regionNode region) {
		final RegionShape circle = new RegionShape();
		
		RegionCorralView.workingRegion = region;
		
		// Set properties of regionNode
		region.name = name;
		
		circle.setWidth(80);
		circle.setHeight(80);
		circle.setX(50);
		circle.setY(50);
		circle.setStroke(Color.DARKGRAY);
		circle.setStrokeWidth(5);
		
		circle.name = name;
		circle.text.setText(name);
		circle.text.setLayoutX(circle.getX()+circle.getHeight()*0.2);
		circle.text.setLayoutY(circle.getY()+circle.getHeight()*0.2);	
		circle.text.setMaxWidth(circle.getWidth()*0.8);
		circle.text.setMaxHeight(circle.getHeight()*0.8);
		circle.text.setMouseTransparent(true);
		circle.text.setFont(new Font(14));
		circle.text.setWrapText(true);
		
		// Set circle color
		circle.rgbColor=VisFunctions.stringToColor((String)circle.name);
		circle.setFill(Color.rgb(circle.rgbColor.get(0), circle.rgbColor.get(1), circle.rgbColor.get(2)));

		// Setting font color for visibility //
		if(VisFunctions.colorTest(circle.rgbColor) == true){
			circle.text.setTextFill(Color.BLACK);
		}else{
			circle.text.setTextFill(Color.WHITE);
		}
		
		circle.setEffect(VisFunctions.lighting);

		//Adding the circle's menu and its functions.

		MenuItem regionForm = new MenuItem("Region Form");

		EventHandler<ActionEvent> deleteEvent = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent deleteEvent) {
				deleteRegion(circle, region);
			}
		};
		MenuItem delete = new MenuItem("Delete");
		delete.setOnAction(deleteEvent);

		EventHandler<ActionEvent> exitEvent = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent exitEvent) {
				circle.menuBar.setVisible(false);
			}
		};
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(exitEvent);
		
		final Menu menu = new Menu("Options");
		menu.getItems().addAll(regionForm, delete, exit);		

		circle.menuBar.getMenus().add(menu);
		circle.menuBar.setLayoutX(circle.getX());
		circle.menuBar.setLayoutY(circle.getY());
		circle.menuBar.setVisible(false);

		circle.onMouseClickedProperty().set(new EventHandler <MouseEvent>(){
			@Override
			public void handle(MouseEvent menuEvent){
				if(menuEvent.getButton().equals(MouseButton.SECONDARY)){
					circle.menuBar.setVisible(true);
					circle.menuBar.setLayoutX(circle.getX());
					circle.menuBar.setLayoutY(circle.getY());
				}
				
				for(int i = 0; i < RegionCorralView.corralPane.getChildren().size(); i++){
					if(RegionCorralView.corralPane.getChildren().get(i).getId() == "this"){
						((Shape) RegionCorralView.corralPane.getChildren().get(i)).setStroke(Color.BLACK);
						((Shape) RegionCorralView.corralPane.getChildren().get(i)).setStrokeWidth(1);
					}
				}
				
				circle.setEffect(VisFunctions.lighting);
				circle.setStrokeWidth(5);
				circle.setStroke(Color.DARKGRAY);
				
			}
		});
		
		// Allows a shift + (drag and drop) to start a new RegionView for this RegionShape.
		circle.setOnDragDetected(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent regionViewEvent){
				if(regionViewEvent.isShiftDown() == true){

					RegionView.workingRegion = region;
					
					DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
					clipboard.put(DnD.TOOL_FORMAT, Tool.class, new RegionViewTool());
					
					Dragboard db = circle.startDragAndDrop(TransferMode.COPY);
					ClipboardContent content = new ClipboardContent();				
					content.put( DnD.TOOL_FORMAT, "Region View");
					db.setContent(content);
					
					regionViewEvent.consume();
				}
			}
		});
		
		return circle;	
	};

	static void deleteRegion(RegionShape circle, regionNode region){
		DataArrays.regionNodes.remove(region);
		RegionCorralView.corralPane.getChildren().removeAll(circle, circle.menuBar, circle.text);
	};

	{	

		onMousePressedProperty().set(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){
				x = getX() - event.getX();
				y = getY() - event.getY();
				mousex = event.getX();
				mousey = event.getY();
			}
		});

		// To allow the facilityCircle to be moved through the pane and setting bounding regions.
		onMouseDraggedProperty().set(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){

				setX(x+event.getX());
				setY(y+event.getY());

				if(getX() <= RegionCorralView.corralPane.getLayoutBounds().getMinX()){
					setX(RegionCorralView.corralPane.getLayoutBounds().getMinX());
				}
				if(getY() <= RegionCorralView.corralPane.getLayoutBounds().getMinY()){
					setY(RegionCorralView.corralPane.getLayoutBounds().getMinY());
				}
				if(getY() >= RegionCorralView.corralPane.getLayoutBounds().getMaxY()-getHeight()){
					setY(RegionCorralView.corralPane.getLayoutBounds().getMaxY()-getHeight());
				}
				if(getX() >= RegionCorralView.corralPane.getLayoutBounds().getMaxX()-getWidth()){
					setX(RegionCorralView.corralPane.getLayoutBounds().getMaxX()-getWidth());
				}

				text.setLayoutX(getX()+getHeight()*0.2);
				text.setLayoutY(getY()+getHeight()*0.2);	

				menuBar.setLayoutX(getX()+getHeight()*0.2);
				menuBar.setLayoutY(getY()+getHeight()*0.2);

			}
		});

	}


};
