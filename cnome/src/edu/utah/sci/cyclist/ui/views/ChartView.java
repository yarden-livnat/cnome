package edu.utah.sci.cyclist.ui.views;

import edu.utah.sci.cyclist.ui.components.ViewBase;

public class ChartView extends ViewBase {
	public static final String TITLE = "Chart";

	public ChartView() {
		super();
		build();
	}
	
	
	private void build() {
		setTitle(TITLE);
	}
}
