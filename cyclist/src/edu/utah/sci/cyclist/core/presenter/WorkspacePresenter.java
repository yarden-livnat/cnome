package edu.utah.sci.cyclist.core.presenter;

import edu.utah.sci.cyclist.core.controller.IMemento;
import edu.utah.sci.cyclist.core.model.Context;
import edu.utah.sci.cyclist.core.tools.Tool;
import edu.utah.sci.cyclist.core.ui.View;

public interface WorkspacePresenter {
	void addTool(Tool tool);
	void save(IMemento memento);
	void restore(IMemento memento, Context ctx);
	void setDirtyFlag(Boolean flag);
	
	void setView(View view);
	Boolean getDirtyFlag();
	void clearWorkspace();
}
