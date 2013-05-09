/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Yarden Livnat  
 *******************************************************************************/
package edu.utah.sci.cyclist.view.wizard;

import edu.utah.sci.cyclist.model.CyclistDatasource;
import javafx.scene.Node;

public interface DatasourceWizardPage {

	String getURL();
	CyclistDatasource getDataSource();
	Node getNode();
}