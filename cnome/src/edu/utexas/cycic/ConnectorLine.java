package edu.utexas.cycic;

import javafx.scene.shape.Line;

public class ConnectorLine extends Line {
	Line right = new Line(){
		{
			setStrokeWidth(2);
		}
	};
	Line left = new Line(){
		{
			setStrokeWidth(2);
		}
	};
	
	public void updatePosition(){
		double x1 = getStartX();
		double y1 = getStartY();
		
		double x2 = getEndX();
		double y2 = getEndY();
		
		right.setStartX(x1 + (x2-x1)*0.5);
		right.setStartY(y1 + (y2-y1)*0.5);
		
		right.setEndX((x1 + (x2-x1)*0.55)+5.0*(y2-y1)/Math.sqrt(Math.pow((y2-y1), 2)+Math.pow(x2-x1, 2)));
		right.setEndY((y1 + (y2-y1)*0.55)-5.0*(x2-x1)/Math.sqrt(Math.pow((y2-y1), 2)+Math.pow(x2-x1, 2)));
		
		left.setStartX(x1 + (x2-x1)*0.5);
		left.setStartY(y1 + (y2-y1)*0.5);

		left.setEndX((x1 + (x2-x1)*0.55)-5.0*(y2-y1)/Math.sqrt(Math.pow((y2-y1), 2)+Math.pow(x2-x1, 2)));
		left.setEndY((y1 + (y2-y1)*0.55)+5.0*(x2-x1)/Math.sqrt(Math.pow((y2-y1), 2)+Math.pow(x2-x1, 2)));
	}

}
