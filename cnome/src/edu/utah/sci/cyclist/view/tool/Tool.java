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

import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.presenter.Presenter;
import edu.utah.sci.cyclist.view.View;
import javafx.scene.image.Image;

public interface Tool {
	Image getIcon();
	String getName();
	View getView();
	Presenter getPresenter(EventBus bus);
}
