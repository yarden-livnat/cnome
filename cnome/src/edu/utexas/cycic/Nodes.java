package edu.utexas.cycic;

import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.ui.tools.Tool;
import edu.utexas.cycic.tools.FormBuilderTool;
import edu.utexas.cycic.tools.FormBuilderToolFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.Light.Distant;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * The class used to build a new Prototype Facility node.
 * @author Robert
 *
 */
public class Nodes{

	protected static double mousey;
	protected static double mousex;
	protected static double x;
	protected static double y;
	
	/**
	 * Function to build a prototype facility node. This node will contain
	 * the facilities available to institutions and regions. All children 
	 * built from this node will mimic its structure. 
	 * @param name Name of the new prototype facility.
	 */
	static FacilityCircle addNode(String name) {
		final facilityNode circleNode = new facilityNode();
		final FacilityCircle circle = circleNode.cycicCircle;
		circle.setId(name);
		circle.setRadius(30);
		circle.setCenterX(40);
		circle.setCenterY(40);
		circle.type = "Parent";
		circle.childrenShow = true;
		
		//Setting up the name and nameing structure of the circle.
		circle.text = new Text(name);
		circle.name = name;
		circle.text.setX(circle.getCenterX()-circle.getRadius()*0.6);
		circle.text.setY(circle.getCenterY());	
		circle.text.setWrappingWidth(circle.getRadius()*1.6);
		circle.text.setMouseTransparent(true);
		
		// Setting the circle color //
		circle.setStroke(Color.BLACK);
		circle.rgbColor=VisFunctions.stringToColor(circle.getId());
		circle.setFill(Color.rgb(circle.rgbColor.get(0), circle.rgbColor.get(1), circle.rgbColor.get(2)));
		
		// Setting font color for visibility //
		if(VisFunctions.colorTest(circle.rgbColor) == true){
			circle.text.setFill(Color.WHITE);
		}else{
			circle.text.setFill(Color.WHITE);
		}
		
		// Really cool effect, not ready for testing yet.
		/*Distant light = new Distant();
		light.setAzimuth(-120.0f);
		final Lighting l = new Lighting();
		l.setLight(light);
		l.setSurfaceScale(1.0f);
		circle.setEffect(l);*/
		
		// Adding the menu and it's menu items.
		final Menu menu1 = new Menu("Options");
		MenuItem facForm = new MenuItem("Facility Form");
		MenuItem delete = new MenuItem("Delete");
		
		delete.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				deleteNode(circleNode);
			}
		});
		
		final Menu clonesList = new Menu("Children");
		
		CustomMenuItem cloneNode = new CustomMenuItem(new Label("Add Child"));
		cloneNode.setHideOnClick(false);
		cloneNode.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				Clones.addClone("", circleNode, circleNode.cycicCircle.childrenShow);
			}
		});
		
		clonesList.getItems().add(cloneNode);
		
		MenuItem showImage = new MenuItem("Show Image");
		showImage.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				circle.image.setVisible(true);
				circle.image.toBack();
				circle.setOpacity(0);			
			}
		});
		
		MenuItem hideImage = new MenuItem("Hide Image");
		hideImage.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				circle.image.setVisible(false);
				circle.setOpacity(100);
			}
		});
		
		menu1.getItems().addAll(facForm, clonesList, delete, showImage, hideImage);
		circle.menu.getMenus().add(menu1);
		circle.menu.setLayoutX(circle.getCenterX());
		circle.menu.setLayoutY(circle.getCenterY());
		circle.menu.setVisible(false);
		
		// Piece of test code for changing the look of the facility circles.
		//circle.image.setImage(new Image("reactor.png"));
		circle.image.setLayoutX(circle.getCenterX()-60);
		circle.image.setLayoutY(circle.getCenterY()-60);
		circle.image.setMouseTransparent(true);
		circle.image.setVisible(false);
		
		// Mouse pressed to add in movement of the facilityCircle.
		circle.onMousePressedProperty().set(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){
				Cycic.workingNode = circleNode;
				circle.childrenDeltaX.clear();
				circle.childrenDeltaY.clear();
				for(int i = 0; i < circle.childrenList.size(); i++){
					circle.childrenDeltaX.add(circle.getCenterX() - circle.childrenList.get(i).getCenterX());
					circle.childrenDeltaY.add(circle.getCenterY() - circle.childrenList.get(i).getCenterY());
				}
				x = circle.getCenterX() - event.getX();
				y = circle.getCenterY() - event.getY();
				mousex = event.getX();
				mousey = event.getY();
			}
		});
		
		// To allow the facilityCircle to be moved through the pane and setting bounding regions.
		circle.onMouseDraggedProperty().set(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){
				circle.setCenterX(mousex+x);
				circle.setCenterY(mousey+y);
				
				if(circle.getCenterX() <= Cycic.pane.getLayoutBounds().getMinX()+circle.getRadius()){
					circle.setCenterX(Cycic.pane.getLayoutBounds().getMinX()+circle.getRadius());
				}
				if(circle.getCenterY() <= Cycic.pane.getLayoutBounds().getMinY()+circle.getRadius()){
					circle.setCenterY(Cycic.pane.getLayoutBounds().getMinY()+circle.getRadius());
				}
				if(circle.getCenterY() >= Cycic.pane.getLayoutBounds().getMaxY()-circle.getRadius()){
					circle.setCenterY(Cycic.pane.getLayoutBounds().getMaxY()-circle.getRadius());
				}
				if(circle.getCenterX() >= Cycic.pane.getLayoutBounds().getMaxX()-circle.getRadius()){
					circle.setCenterX(Cycic.pane.getLayoutBounds().getMaxX()-circle.getRadius());
				}
				
				circle.menu.setLayoutX(circle.getCenterX());
				circle.menu.setLayoutY(circle.getCenterY());
				
				circle.image.setLayoutX(circle.getCenterX()-60);
				circle.image.setLayoutY(circle.getCenterY()-50);
				
				circle.text.setX(circle.getCenterX()-circle.getRadius()*0.6);
				circle.text.setY(circle.getCenterY());
				
				for(int i = 0; i < CycicScenarios.workingCycicScenario.Links.size(); i++){
					if(CycicScenarios.workingCycicScenario.Links.get(i).source == circle){
						CycicScenarios.workingCycicScenario.Links.get(i).line.setStartX(circle.getCenterX());
						CycicScenarios.workingCycicScenario.Links.get(i).line.setStartY(circle.getCenterY());
					}
				}
				for(int i = 0; i < CycicScenarios.workingCycicScenario.hiddenLinks.size(); i++){
					if(CycicScenarios.workingCycicScenario.hiddenLinks.get(i).source == circle){
						CycicScenarios.workingCycicScenario.hiddenLinks.get(i).line.setStartX(circle.getCenterX());
						CycicScenarios.workingCycicScenario.hiddenLinks.get(i).line.setStartY(circle.getCenterY());
					}
				}
				for(int i = 0; i < circle.childrenLinks.size(); i++){
					circle.childrenLinks.get(i).line.setStartX(circle.getCenterX());
					circle.childrenLinks.get(i).line.setStartY(circle.getCenterY());
					circle.childrenList.get(i).setCenterX(mousex-circle.childrenDeltaX.get(i)+x);
					circle.childrenList.get(i).setCenterY(mousey-circle.childrenDeltaY.get(i)+y);
					circle.childrenLinks.get(i).line.setEndX(circle.childrenList.get(i).getCenterX());
					circle.childrenLinks.get(i).line.setEndY(circle.childrenList.get(i).getCenterY());
					circle.childrenList.get(i).menu.setLayoutX(circle.childrenList.get(i).getCenterX());
					circle.childrenList.get(i).menu.setLayoutY(circle.childrenList.get(i).getCenterY());
					circle.childrenList.get(i).text.setX(circle.childrenList.get(i).getCenterX()-circle.childrenList.get(i).getRadius()*0.6);
					circle.childrenList.get(i).text.setY(circle.childrenList.get(i).getCenterY());
					for(int ii = 0; ii < CycicScenarios.workingCycicScenario.Links.size(); ii++){
						if(circle.childrenList.get(i) == CycicScenarios.workingCycicScenario.Links.get(ii).source){
							CycicScenarios.workingCycicScenario.Links.get(ii).line.setStartX(circle.childrenList.get(i).getCenterX());
							CycicScenarios.workingCycicScenario.Links.get(ii).line.setStartY(circle.childrenList.get(i).getCenterY());
						}
					}
				}
				mousex = event.getX();
				mousey = event.getY();
			}
		});
		// Double click functionality to show/hide children. As well as a bloom feature to show which node is selected.
		circle.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){
				if(event.getButton().equals(MouseButton.SECONDARY)){
					circle.menu.setVisible(true);
				}
				if (event.getClickCount() >= 2){
					if(circle.childrenShow == true){
						circle.childrenShow = false;
						VisFunctions.reloadPane();
					}else{
						circle.childrenShow = true;
						VisFunctions.reloadPane();
					}
				}
				if(event.getButton().equals(MouseButton.PRIMARY)){
					for(int i = 0; i < Cycic.pane.getChildren().size(); i++){
						Cycic.pane.getChildren().get(i).setEffect(null);
					}
					circle.setEffect(VisFunctions.colorAdjust);
				}
			}
		});
		
		Cycic.workingScenario.FacilityNodes.add(circleNode);
		
		// Code for allow a shift + (drag and drop) to start a new facility form for this facilityCircle.
		circle.setOnDragDetected(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){
				if(event.isShiftDown() == true){
					DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
					clipboard.put(DnD.TOOL_FORMAT, Tool.class, new FormBuilderTool());
					
					Dragboard db = circle.startDragAndDrop(TransferMode.COPY);
					ClipboardContent content = new ClipboardContent();				
					content.put( DnD.TOOL_FORMAT, "Facility Form");
					db.setContent(content);
					
//					DnDIcon.getInstance().show(icon, title);
					event.consume();
				}
			}
		});
		
		// Adding facilityCircle to the pane. 
		Cycic.pane.getChildren().add(circle);
		Cycic.pane.getChildren().add(circle.menu);
		Cycic.pane.getChildren().add(circle.text);
		Cycic.pane.getChildren().add(circle.image);
		circle.image.toBack();
		
		return circle;
	}
	
	/**
	 * Removes a facilityCircle from the simulation.
	 * @param circle The facilityCircle to be removed. 
	 */
	static void deleteNode(facilityNode node){
		for(int i = 0; i < CycicScenarios.workingCycicScenario.Links.size(); i++){
			if(CycicScenarios.workingCycicScenario.Links.get(i).source == node.cycicCircle){
				CycicScenarios.workingCycicScenario.Links.remove(i);
			}
		}
		for(int i = 0; i < CycicScenarios.workingCycicScenario.hiddenLinks.size(); i++){
			if(CycicScenarios.workingCycicScenario.hiddenLinks.get(i).source == node.cycicCircle){
				CycicScenarios.workingCycicScenario.hiddenLinks.remove(i);
			}
		}
		for(int i = 0; i < CycicScenarios.workingCycicScenario.FacilityNodes.size(); i++){
			if(CycicScenarios.workingCycicScenario.FacilityNodes.get(i) == node){
				for(int ii = 0; ii < CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle.childrenList.size();ii++){
					for(int iii = 0; iii < CycicScenarios.workingCycicScenario.Links.size(); iii++){
						if(CycicScenarios.workingCycicScenario.Links.get(i).source == CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle.childrenList.get(iii)){
							CycicScenarios.workingCycicScenario.Links.remove(i);
						}
					}
				}
				CycicScenarios.workingCycicScenario.FacilityNodes.remove(i);
			}
		}
		for(int i = 0; i < CycicScenarios.workingCycicScenario.FacilityNodes.size(); i++){
			for(int ii = 0; ii < CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle.childrenList.size(); ii++){
				CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle.childrenList.get(ii).parentIndex = i;
			}
		}
		VisFunctions.reloadPane();
	}
}