package edu.utah.sci.cyclist.ui.panels;

import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.event.ui.FilterEvent;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.Filter;
import edu.utah.sci.cyclist.ui.components.FilterGlyph;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class FiltersListPanel extends TitledPanel {
	public static final String ID 		= "filters-list--panel";
	public static final String TITLE	= "Filters";
	
	public FiltersListPanel() {
		super(TITLE);
		
		configure();
	}
	
	private void configure() {
		getPane().setOnDragEntered(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {		
				event.consume();
			}
		});
		
		getPane().setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				DnD.LocalClipboard clipboard = getLocalClipboard();
				
				if (clipboard.hasContent(DnD.FILTER_FORMAT)) {
					event.acceptTransferModes(TransferMode.MOVE);
				}
				event.consume();
			}
		});
		
		getPane().setOnDragExited(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				event.consume();
			}
		});
			
		getPane().setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				DnD.LocalClipboard clipboard = getLocalClipboard();
				Filter filter = clipboard.get(DnD.FILTER_FORMAT, Filter.class);
				if (filter != null) {
					FilterGlyph glyph = new FilterGlyph(filter);
					addGlyph(glyph);
				}
				event.setDropCompleted(true);
			}
		});
	}
	
	private void addGlyph(final FilterGlyph glyph) {
		getContent().getChildren().add(glyph);
		
		glyph.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				Filter filter  = glyph.getFilter();
				Dragboard db = glyph.startDragAndDrop(TransferMode.MOVE);
				
				DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
				clipboard.put(DnD.FILTER_FORMAT, Filter.class, filter);
				
				ClipboardContent content = new ClipboardContent();
				content.putString(filter.getName());
				
				SnapshotParameters snapParams = new SnapshotParameters();
	            snapParams.setFill(Color.TRANSPARENT);
	            
	            content.putImage(glyph.snapshot(snapParams, null));	
				
				db.setContent(content);
				
				getContent().getChildren().remove(glyph);
			}
		});
		
		glyph.setOnDragDone(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
			}
		});
		
		
//		glyph.setOnAction(new EventHandler<FilterEvent>() {
//			@Override
//			public void handle(FilterEvent event) {
//				if (event.getEventType() == FilterEvent.SHOW) {
//					if (getOnAction() != null) {
//						getOnAction().handle(event);
//					}
//				}
//			}
//		});
	}
	
	private DnD.LocalClipboard getLocalClipboard() {
		return DnD.getInstance().getLocalClipboard();
	}
}
