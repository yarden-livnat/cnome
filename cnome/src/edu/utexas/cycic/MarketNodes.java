package edu.utexas.cycic;

import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utexas.cycic.tools.MarketViewTool;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * This class builds the marketCircles used to represent markets in the 
 * CYCIC visualization. 
 * @author Robert
 *
 */
public class MarketNodes{

	protected static double mousey;
	protected static double mousex;
	protected static double x;
	protected static double y;
	
	/**
	 * Function initiates a new marketCircle.
	 * @param name Name of the new market.
	 */
	static void addMarket(String name) {
		final MarketCircle circle = new MarketCircle();
		circle.setRadiusX(40);
		circle.setRadiusY(30);
		circle.setCenterX(60);
		circle.setCenterY(60);
		
		// Adding the text to the circle.
		circle.name = name;
		circle.text.setText(name);
		
		circle.text.setLayoutX(circle.getCenterX() - circle.getRadiusX()*0.6);
		circle.text.setLayoutY(circle.getCenterY() - circle.getRadiusY()*0.6);
		
		circle.text.setTextAlignment(TextAlignment.CENTER);
		circle.text.setWrapText(true);
		circle.text.setMaxWidth(circle.getRadiusX()*1.4);
		circle.text.setMouseTransparent(true);
		circle.text.setMaxHeight(circle.getRadiusY()*1.2);
		
		// Setting the circle color //
		circle.setStroke(Color.BLACK);
		circle.setFill(Color.rgb(100, 150, 200));
		circle.text.setTextFill(Color.WHITE);
		circle.text.setFont(new Font(14));
		
		for(int i = 0; i < Cycic.pane.getChildren().size(); i++){
			if(Cycic.pane.getChildren().get(i).getId() == "cycicNode"){
				((Shape) Cycic.pane.getChildren().get(i)).setStroke(Color.BLACK);
				((Shape) Cycic.pane.getChildren().get(i)).setStrokeWidth(1);
			}
		}
		circle.setEffect(VisFunctions.lighting);
		circle.setStrokeWidth(5);
		circle.setStroke(Color.DARKGRAY);
		
		//Adding the circle's menu and it's functions.
		final Menu menu1 = new Menu(circle.getId());
		MenuItem facForm = new MenuItem("Market Form");
		MenuItem delete = new MenuItem("Delete");
		delete.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent e){
				deleteMarket(circle);
			}
		});
		menu1.getItems().addAll(facForm, delete);
		circle.menu.getMenus().add(menu1);
		circle.menu.setLayoutX(circle.getCenterX());
		circle.menu.setLayoutY(circle.getCenterY());
		circle.menu.setVisible(false);
		
		// Allows the shift drag event to load a new marketView.
		circle.setOnDragDetected(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){
				if(event.isShiftDown() == true){
					DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
					clipboard.put(DnD.TOOL_FORMAT, Tool.class, new MarketViewTool());
					
					Dragboard db = circle.startDragAndDrop(TransferMode.COPY);
					ClipboardContent content = new ClipboardContent();
					content.put( DnD.TOOL_FORMAT, "Market Form");
					db.setContent(content);
					event.consume();
				}
			}
		});
	
		// Code for moving the marketCircle
		circle.onMousePressedProperty().set(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){
				Cycic.workingMarket = circle;
				x = circle.getCenterX() - event.getX();
				y = circle.getCenterY() - event.getY();
				mousex = event.getX();
				mousey = event.getY();
			}
		});
		circle.onMouseDraggedProperty().set(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){
				circle.setCenterX(mousex+x);
				circle.setCenterY(mousey+y);
				if(circle.getCenterX() <= Cycic.pane.getLayoutBounds().getMinX()+circle.getRadiusX()){
					circle.setCenterX(Cycic.pane.getLayoutBounds().getMinX()+circle.getRadiusX());
				}
				if(circle.getCenterX() >= Cycic.pane.getLayoutBounds().getMaxX()-circle.getRadiusX()){
					circle.setCenterX(Cycic.pane.getLayoutBounds().getMaxX()-circle.getRadiusX());
				}
				if(circle.getCenterY() <= Cycic.pane.getLayoutBounds().getMinY()+circle.getRadiusY()){
					circle.setCenterY(Cycic.pane.getLayoutBounds().getMinY()+circle.getRadiusY());
				}
				if(circle.getCenterY() >= Cycic.pane.getLayoutBounds().getMaxY()-circle.getRadiusY()){
					circle.setCenterY(Cycic.pane.getLayoutBounds().getMaxY()-circle.getRadiusY());
				}

				circle.menu.setLayoutX(circle.getCenterX());
				circle.menu.setLayoutY(circle.getCenterY());
				
				circle.text.setLayoutX(circle.getCenterX() - circle.getRadiusX()*0.6);
				circle.text.setLayoutY(circle.getCenterY() - circle.getRadiusY()*0.6);
				
				for(int i = 0; i < CycicScenarios.workingCycicScenario.Links.size(); i++){
					if(CycicScenarios.workingCycicScenario.Links.get(i).target == circle){
						CycicScenarios.workingCycicScenario.Links.get(i).line.setEndX(circle.getCenterX());
						CycicScenarios.workingCycicScenario.Links.get(i).line.setEndY(circle.getCenterY());
						CycicScenarios.workingCycicScenario.Links.get(i).line.updatePosition();
					}
					if(CycicScenarios.workingCycicScenario.Links.get(i).source == circle){
						CycicScenarios.workingCycicScenario.Links.get(i).line.setStartX(circle.getCenterX());
						CycicScenarios.workingCycicScenario.Links.get(i).line.setStartY(circle.getCenterY());
						CycicScenarios.workingCycicScenario.Links.get(i).line.updatePosition();
					}
				}				
				mousex = event.getX();
				mousey = event.getY();
			}
		});
		circle.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event){
				if(event.getButton().equals(MouseButton.SECONDARY)){
					circle.menu.setVisible(true);
				}
				if(event.getButton().equals(MouseButton.PRIMARY)){
					for(int i = 0; i < Cycic.pane.getChildren().size(); i++){
						if(Cycic.pane.getChildren().get(i).getId() == "cycicNode"){
							((Shape) Cycic.pane.getChildren().get(i)).setStroke(Color.BLACK);
							((Shape) Cycic.pane.getChildren().get(i)).setStrokeWidth(1);
						}
					}
					circle.setStrokeWidth(5);
					circle.setStroke(Color.DARKGRAY);
				}
			}
		});
		CycicScenarios.workingCycicScenario.marketNodes.add(circle);
		Cycic.pane.getChildren().add(circle);
		Cycic.pane.getChildren().add(circle.menu);
		Cycic.pane.getChildren().add(circle.text);
	}

	/**
	 * Function used to delete a given market.
	 * @param name MarketCircle to be deleted.
	 */
	static void deleteMarket(MarketCircle circle){
		for(int i = 0; i < CycicScenarios.workingCycicScenario.Links.size(); i++){
			if(CycicScenarios.workingCycicScenario.Links.get(i).target == circle){
				CycicScenarios.workingCycicScenario.Links.remove(i);
				i = i-1;
			}
		}
		for(int i = 0; i < CycicScenarios.workingCycicScenario.marketNodes.size(); i++){
			if(CycicScenarios.workingCycicScenario.marketNodes.get(i) == circle){
				CycicScenarios.workingCycicScenario.marketNodes.remove(i);
			}
		}
		VisFunctions.reloadPane();
	}
}