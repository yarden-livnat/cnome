/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved.
 *
 * License for the specific language governing rights and limitations under Permission
 * is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: The above copyright notice 
 * and this permission notice shall be included in all copies  or substantial portions of the Software. 
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR 
 *  A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *     Yarden Livnat  
 *******************************************************************************/
package edu.utah.sci.cyclist.core.ui.tools;

import javafx.scene.image.Image;
import edu.utah.sci.cyclist.core.Resources;
import edu.utah.sci.cyclist.core.event.notification.EventBus;
import edu.utah.sci.cyclist.core.presenter.TablePresenter;
import edu.utah.sci.cyclist.core.presenter.ViewPresenter;
import edu.utah.sci.cyclist.core.ui.View;
import edu.utah.sci.cyclist.core.ui.views.SimpleTableView;

public class TableTool implements Tool {

	public static final String ID 			= "edu.utah.sci.cyclist.TableTool";
	public static final String TOOL_NAME 	= "Table";
	public static final String ICON_NAME 	= "table";
	
	private View _view = null;
	private ViewPresenter _presenter = null;
	
	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public Image getIcon() {
		return Resources.getIcon(ICON_NAME, 16, 16);	
	}

	@Override
	public String getName() {
		return TOOL_NAME;
	}

	@Override
	public View getView() {
		if (_view == null) 
			_view = new SimpleTableView();
		return _view;
	}

	@Override
	public ViewPresenter getPresenter(EventBus bus) {
		if (_presenter == null)
			_presenter = new TablePresenter(bus);
		return _presenter;
	}

}
