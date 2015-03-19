package edu.utexas.cycic;

import edu.utah.sci.cyclist.core.event.dnd.DnD;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class MemoryScroll extends ScrollPane {
	double currentY;
	double currentX;
	double increment;
	
	public MemoryScroll(double x, double y, double increment){
		this.currentX = x;
		this.currentY = y;
		this.increment = increment;
	}
	
	public MemoryScroll(){
		
	}
	
	void updateFac(){
		boolean test;
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
				circle.setRadius(20);
				circle.setStroke(Color.BLACK);
				circle.setFill(Color.web("#CF5300"));
				circle.setCenterX(currentX);
				circle.setCenterY(currentY);
				circle.text.setText(fac.cycicCircle.text.getText());
				circle.text.setWrapText(true);
				circle.text.setMaxWidth(60);
				circle.text.setLayoutX(circle.getCenterX()-circle.getRadius()*0.7);
				circle.text.setLayoutY(circle.getCenterY()-circle.getRadius()*0.6);	
				circle.text.setTextAlignment(TextAlignment.CENTER);
				circle.text.setMouseTransparent(true);
				circle.text.setMaxWidth(circle.getRadius()*1.4);
				circle.text.setMaxHeight(circle.getRadius()*1.2);
				circle.setOnDragDetected(new EventHandler<MouseEvent>(){
					public void handle(MouseEvent e){
						Dragboard db = circle.startDragAndDrop(TransferMode.COPY);
						ClipboardContent content = new ClipboardContent();				
						content.put(CycICDnD.UNASSOC_FAC, fac.cycicCircle.text.getText());
						db.setContent(content);
						e.consume();
					}
				});
				this.getChildren().addAll(circle, circle.text);
				currentY += increment;
			}
		}
	}
}
