package edu.utah.sci.cyclist.view.components;

//import pnnl.cyclist.model.vo.CyclistDataSource;
import javafx.scene.Node;

public interface DatasourceWizardPage {

	String getURL();
	//CyclistDataSource getDataSource();
	String getDatasource();
	Node getNode();
}