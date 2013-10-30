package edu.utexas.cycic;

import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.runtime.Name;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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

	static void addRegion(String name) {
		final RegionCircle circle = new RegionCircle();
		circle.setRadius(15);
		circle.setCenterX(50);
		circle.setCenterY(50);
		circle.setStroke(Color.BLACK);
		circle.setFill(Color.PINK);

		circle.name = name;
		circle.text = new Text(name);
		// why does putting this in the brackets not work??

		//Adding the circle's menu and its functions.

		MenuItem regionForm = new MenuItem("Region Form");
		
		MenuItem delete = new MenuItem("Delete");

		EventHandler exitEvent = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent exitEvent) {
				System.exit(0);
			}
		};
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction(exitEvent);

		final Menu menu = new Menu();
		menu.getItems().addAll(regionForm, delete, exit);

		/*circle.menu.getMenus().add(menu);
		circle.menu.setLayoutX(circle.getCenterX());
		circle.menu.setLayoutY(circle.getCenterY());
		circle.menu.setVisible(false);*/
		
	};

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
					setCenterX(mousex+x);
					setCenterY(mousey+y);

					deltax = getCenterX()-mousex;
					deltay = getCenterY()-mousey;


					setCenterX(deltax+event.getX());
					setCenterY(deltay+event.getY());

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
				}
			});

		}


	};
