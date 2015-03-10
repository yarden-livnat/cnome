package edu.utexas.cycic;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.shape.Ellipse;

public class InstitutionEllipse extends Ellipse{
	Object name;
	Label text = new Label();
	ArrayList<String> institModel = new ArrayList<String>();
	ArrayList<String> containedFacilities = new ArrayList<String>();
	{
		setId((String)name);
	}
}
