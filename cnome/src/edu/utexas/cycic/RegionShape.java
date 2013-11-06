package edu.utexas.cycic;

import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.runtime.Name;

import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.ui.tools.Tool;
import edu.utexas.cycic.tools.FormBuilderTool;
import edu.utexas.cycic.tools.RegionViewTool;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * This class extends the Java Circle class. Used to represent a region
 * on the CYCIC pane and contains all of the information associated with
 * the region.
 * @author birdybird
 *
 */

public class RegionShape extends Circle {

	protected static double mousey;
	protected static double mousex;
	protected static double x;
	protected static double y;
	protected static double deltax;
	protected static double deltay;
	Object name;
	Text text;
	MenuBar menuBar = new MenuBar();
	ArrayList<Integer> rgbColor = new ArrayList<Integer>();
	ListView<String> facilityList = new ListView<String>();
	ListView<String> institutionList = new ListView<String>();

	static RegionShape addRegion(final String name) {
		final RegionShape circle = new RegionShape(){
			{
				setRadius(25);
			}
		};
		circle.setCenterX(50);
		circle.setCenterY(50);
		circle.setStroke(Color.DARKGRAY);
		circle.setStrokeWidth(3);

		circle.name = name;
		circle.text = new Text(name);
		circle.text.setX(circle.getCenterX()-circle.getRadius()*0.7);
		circle.text.setY(circle.getCenterY());	
		circle.text.setWrappingWidth(circle.getRadius()*1.6);
		circle.text.setMouseTransparent(true);
		circle.text.setFont(new Font(14));

		// Set circle color
		circle.rgbColor=VisFunctions.stringToColor((String)circle.name);
		circle.setFill(Color.rgb(circle.rgbColor.get(0), circle.rgbColor.get(1), circle.rgbColor.get(2)));

		// Setting font color for visibility //
		if(VisFunctions.colorTest(circle.rgbColor) == true){
			circle.text.setFill(Color.BLACK);
		}else{
			circle.text.setFill(Color.WHITE);
		}
		for(int i = 0; i < Cycic.pane.getChildren().size(); i++){
			if(Cycic.pane.getChildren().get(i).getId() == "cycicNode"){
				((Shape) Cycic.pane.getChildren().get(i)).setStroke(Color.BLACK);
				((Shape) Cycic.pane.getChildren().get(i)).setStrokeWidth(1);
			}
		}
		circle.setEffect(VisFunctions.lighting);


		//Adding the circle's menu and its functions.

		MenuItem regionForm = new MenuItem("Region Form");

//		deleteEvent not working
//		EventHandler deleteEvent = new EventHandler<ActionEvent>() {
//			public void handle(ActionEvent deleteEvent) {
//				deleteRegion(circle);
//			}
//		};
		MenuItem delete = new MenuItem("Delete");
//		delete.setOnAction(deleteEvent);

		EventHandler exitEvent = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent exitEvent) {
				circle.menuBar.setVisible(false);
			}
		};
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(exitEvent);
		
		final Menu menu = new Menu("Options");
		menu.getItems().addAll(regionForm, delete, exit);		

		circle.menuBar.getMenus().add(menu);
		circle.menuBar.setLayoutX(circle.getCenterX());
		circle.menuBar.setLayoutY(circle.getCenterY());
		circle.menuBar.setVisible(false);

		circle.onMouseClickedProperty().set(new EventHandler <MouseEvent>(){
			@Override
			public void handle(MouseEvent menuEvent){
				if(menuEvent.getButton().equals(MouseButton.SECONDARY)){
					circle.menuBar.setVisible(true);
					circle.menuBar.setLayoutX(circle.getCenterX());
					circle.menuBar.setLayoutY(circle.getCenterY());
				}
			}
		});
		
		// Allows a shift + (drag and drop) to start a new RegionView for this RegionShape.
		circle.setOnDragDetected(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent regionViewEvent){
				if(regionViewEvent.isShiftDown() == true){
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

//	static void deleteRegion(RegionShape circle){
//		CycicScenarios.workingCycicScenario.regionNodes.remove(circle);
//		VisFunctions.reloadPane();
//	};

	{	

		onMousePressedProperty().set(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){
				x = getCenterX() - event.getX();
				y = getCenterY() - event.getY();
				mousex = event.getX();
				mousey = event.getY();
			}
		});

		// To allow the facilityCircle to be moved through the pane and setting bounding regions.
		onMouseDraggedProperty().set(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){

				setCenterX(x+event.getX());
				setCenterY(y+event.getY());

				if(getCenterX() <= RegionCorralView.corralPane.getLayoutBounds().getMinX()+getRadius()){
					setCenterX(RegionCorralView.corralPane.getLayoutBounds().getMinX()+getRadius());
				}
				if(getCenterY() <= RegionCorralView.corralPane.getLayoutBounds().getMinY()+getRadius()){
					setCenterY(RegionCorralView.corralPane.getLayoutBounds().getMinY()+getRadius());
				}
				if(getCenterY() >= RegionCorralView.corralPane.getLayoutBounds().getMaxY()-getRadius()){
					setCenterY(RegionCorralView.corralPane.getLayoutBounds().getMaxY()-getRadius());
				}
				if(getCenterX() >= RegionCorralView.corralPane.getLayoutBounds().getMaxX()-getRadius()){
					setCenterX(RegionCorralView.corralPane.getLayoutBounds().getMaxX()-getRadius());
				}

				text.setX(getCenterX()-getRadius()*0.6);
				text.setY(getCenterY());

				menuBar.setLayoutX(getCenterX());
				menuBar.setLayoutY(getCenterY());

			}
		});

	}


};
