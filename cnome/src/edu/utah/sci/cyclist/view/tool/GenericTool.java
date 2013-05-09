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
package edu.utah.sci.cyclist.view.tool;

import javafx.scene.image.Image;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.presenter.TablePresenter;
import edu.utah.sci.cyclist.presenter.Presenter;
import edu.utah.sci.cyclist.view.SimpleTableView;
import edu.utah.sci.cyclist.view.View;

public class GenericTool implements Tool {

	@Override
	public Image getIcon() {
		return Resources.getIcon("table", 16, 16);	
	}

	@Override
	public String getName() {
		return "Table";
	}

	@Override
	public View getView() {
		return new SimpleTableView();
	}

	@Override
	public Presenter getPresenter(EventBus bus) {
		return new TablePresenter(bus);
	}

}
