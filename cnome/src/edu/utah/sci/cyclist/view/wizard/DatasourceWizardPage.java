package edu.utah.sci.cyclist.view.wizard;

import edu.utah.sci.cyclist.model.CyclistDatasource;
import javafx.scene.Node;

public interface DatasourceWizardPage {

	String getURL();
	CyclistDatasource getDataSource();
	Node getNode();
}