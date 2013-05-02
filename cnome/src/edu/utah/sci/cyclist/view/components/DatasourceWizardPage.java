package edu.utah.sci.cyclist.view.components;

import edu.utah.sci.cyclist.model.CyclistDatasource;
import javafx.scene.Node;

public interface DatasourceWizardPage {

	String getURL();
	CyclistDatasource getDataSource();
	Node getNode();
}