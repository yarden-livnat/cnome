package edu.utah.sci.cyclist.view.panels;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.view.tool.Tool;

public class ToolsPanel extends TitledPane  {
	public static final String ID 		= "tools-panel";
	public static final String TITLE	= "Views";
	
	private VBox _vbox;
	
	public ImageView dragView = new ImageView();
	public Pane root;
	
	public ToolsPanel() {
		build();
	}
	
//	@Override
	public void setTitle(String title) {
		setText(title);
	}

	public void setTools(List<Tool> tools) {		
		Collections.sort(tools, new Comparator<Tool>() {
			public int compare(Tool a, Tool b) {
				return a.getName().compareTo(b.getName());
			}
		});
		
		for (final Tool tool : tools) {
			final Image icon = tool.getIcon();
			final Label title = new Label(tool.getName(), new ImageView(icon));
			title.getStyleClass().add("tools-entry");
			
			title.setOnDragDetected(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {					
					
					DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
					clipboard.put(DnD.TOOL_FORMAT, Tool.class, tool);
					
					Dragboard db = title.startDragAndDrop(TransferMode.COPY);
					ClipboardContent content = new ClipboardContent();				
					content.put( DnD.TOOL_FORMAT, tool.getName());
					db.setContent(content);
					
//					DnDIcon.getInstance().show(icon, title);
					event.consume();
				}
			});
			
			_vbox.getChildren().add(title);
		}
	}
	
	private void build() {
		setText(TITLE);
		
		_vbox = VBoxBuilder.create()
				.styleClass("tools-panel")
				.build();
		
		setContent(_vbox);
	}
}

