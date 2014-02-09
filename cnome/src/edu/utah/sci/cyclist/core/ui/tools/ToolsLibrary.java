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

import edu.utexas.cycic.tools.CommoditiesViewToolFactory;
import edu.utexas.cycic.tools.CycicToolFactory;
import edu.utexas.cycic.tools.FormBuilderToolFactory;
import edu.utexas.cycic.tools.InstitutionViewToolFactory;
import edu.utexas.cycic.tools.MarketViewToolFactory;
import edu.utexas.cycic.tools.RecipeFormToolFactory;
import edu.utexas.cycic.tools.RegionViewToolFactory;
import edu.utexas.cycic.tools.SimulationInfoToolFactory;

public class ToolsLibrary {

	public static final ToolFactory[] factories = {
		new GenericToolFactory<TableTool>(TableTool.class, TableTool.TOOL_NAME, TableTool.ICON),
		new GenericToolFactory<ChartTool>(ChartTool.class, ChartTool.TOOL_NAME, ChartTool.ICON),
		new GenericToolFactory<FlowTool>(FlowTool.class, FlowTool.TOOL_NAME, FlowTool.ICON),
		new GenericToolFactory<WorkspaceTool>(WorkspaceTool.class, WorkspaceTool.TOOL_NAME, WorkspaceTool.ICON),
		new CycicToolFactory(),
		new CommoditiesViewToolFactory(),
		new FormBuilderToolFactory(),
		new InstitutionViewToolFactory(),
		new MarketViewToolFactory(),
		new RecipeFormToolFactory(),
		new RegionViewToolFactory(),
		new SimulationInfoToolFactory()
	};
}
