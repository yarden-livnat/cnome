package edu.utexas.cycic;

import edu.utah.sci.cyclist.core.event.dnd.DnD;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class MemoryScroll extends ScrollPane {
	double currentY;
	double baseY;
	double currentX;
	double baseX;
	double increment;
	
	public MemoryScroll(double x, double y, double increment){
		this.currentX = x;
		this.baseX = x;
		this.currentY = y;
		this.baseY= y;
		this.increment = increment;
	}
	
	public MemoryScroll(){
		
	}
	
	void updateFac(){
		boolean test;
		currentX = baseX;
		currentY = baseY;
		this.getChildren().clear();
		for(facilityNode fac:DataArrays.FacilityNodes){
			test = false;
			for(instituteNode inst:DataArrays.institNodes){
				for(facilityItem fac_item:inst.availFacilities){
					if((fac.cycicCircle.text.getText().equalsIgnoreCase(fac_item.name))){
						test = true;
					}
				}
			}
			if(test == false){
				FacilityCircle circle = new FacilityCircle();
				circle.setRadius(30);
				circle.setStroke(Color.BLACK);
				circle.rgbColor=VisFunctions.stringToColor(fac.facilityType);
				circle.setFill(Color.rgb(circle.rgbColor.get(0), circle.rgbColor.get(1), circle.rgbColor.get(2), 0.9));
				circle.setCenterX(currentX);
				circle.setCenterY(currentY);
				circle.text.setText(fac.cycicCircle.text.getText());
				circle.text.setWrapText(true);
				circle.text.setMaxWidth(25);
				circle.text.setLayoutX(circle.getCenterX()-circle.getRadius()*0.7);
				circle.text.setLayoutY(circle.getCenterY()-circle.getRadius()*0.6);	
				circle.text.setTextAlignment(TextAlignment.CENTER);
				circle.text.setMouseTransparent(true);
				circle.text.setMaxWidth(circle.getRadius()*1.4);
				circle.text.setMaxHeight(circle.getRadius()*1.2);
				circle.text.toFront();
				circle.setOnDragDetected(new EventHandler<MouseEvent>(){
					public void handle(MouseEvent e){
						Dragboard db = circle.startDragAndDrop(TransferMode.COPY);
						ClipboardContent content = new ClipboardContent();				
						content.put(CycICDnD.UNASSOC_FAC, fac.cycicCircle.text.getText());
						db.setContent(content);
						e.consume();
					}
				});
				getChildren().addAll(circle.text, circle);
				currentY += increment;
			}
		}
	}
}
