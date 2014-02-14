package edu.utah.sci.cyclist.core.ui.panels;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TitledPanel extends VBox {

	private HBox _header;
	
	private ScrollPane _pane;
	
	public TitledPanel(String title, Node glyph) {
		build(title, glyph);
	}
	
	private void build(String title, Node glyph) {
		getStyleClass().add("cnome-panel");
		setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
		setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		
		// header
		Label label = new Label(title, glyph);
		label.getStyleClass().add("label");
		
		_header = new HBox();
		_header.getStyleClass().add("header");
		_header.getChildren().add(label);
		
		// pane
		_pane = new ScrollPane();
		_pane.getStyleClass().add("pane");
		_pane.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
		_pane.fitToWidthProperty();
		
		VBox vbox = new VBox();
		vbox.getStyleClass().add("panel-vbox");
//		vbox.setPrefWidth(200);
		vbox.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
		vbox.setFillWidth(true);
		_pane.setContent(vbox);
		
		// add childeren
		getChildren().addAll(_header, _pane);
		
		VBox.setVgrow(vbox, Priority.ALWAYS);
//		VBox.setVgrow(this, Priority.ALWAYS);
		VBox.setVgrow(_pane, Priority.ALWAYS);
			
	}
	
	public void setContent(Node node) {
		_pane.setContent(node);
	}
	
	public VBox getContent() {
		return (VBox)_pane.getContent();
	}
	
	public HBox getHeader() {
		return _header;
	}
	
	public Node getPane() {
		return _pane;
	}
	
	
}
