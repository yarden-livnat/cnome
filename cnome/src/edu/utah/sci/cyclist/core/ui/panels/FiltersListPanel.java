package edu.utah.sci.cyclist.core.ui.panels;

import javafx.event.EventHandler;
import javafx.scene.SnapshotParameters;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.event.ui.FilterEvent;
import edu.utah.sci.cyclist.core.model.Filter;
import edu.utah.sci.cyclist.core.ui.components.FilterGlyph;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;

public class FiltersListPanel extends TitledPanel {
	public static final String ID 		= "filters-list-panel";
	public static final String TITLE	= "Filters";
	
	public FiltersListPanel() {
		super(TITLE, GlyphRegistry.get(AwesomeIcon.FILTER)); //"FontAwesome|FILTER"));
		
		configure();
	}
	
	private void configure() {
		setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
		
		getPane().setOnDragEntered(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {		
				event.consume();
			}
		});
		
		getPane().setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				DnD.LocalClipboard clipboard = getLocalClipboard();
				
				if (clipboard.hasContent(DnD.FILTER_FORMAT)) {
					event.acceptTransferModes(TransferMode.COPY);
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
				Dragboard db = glyph.startDragAndDrop(TransferMode.COPY);
				
				DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
				clipboard.put(DnD.FILTER_FORMAT, Filter.class, filter);
				
				ClipboardContent content = new ClipboardContent();
				content.putString(filter.getName());
				
				SnapshotParameters snapParams = new SnapshotParameters();
	            snapParams.setFill(Color.TRANSPARENT);
	            
	            content.putImage(glyph.snapshot(snapParams, null));	
				
				db.setContent(content);
				
//				getContent().getChildren().remove(glyph);
			}
		});
		
		glyph.setOnDragDone(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
			}
		});
		
		
		glyph.setOnAction(new EventHandler<FilterEvent>() {
			@Override
			public void handle(FilterEvent event) {
				if (event.getEventType() == FilterEvent.SHOW) {
//					if (getOnAction() != null) {
//						getOnAction().handle(event);
//					}
				} else if (event.getEventType() == FilterEvent.DELETE) {
					getContent().getChildren().remove(glyph);
				}
			}
		});
	}
	
	private DnD.LocalClipboard getLocalClipboard() {
		return DnD.getInstance().getLocalClipboard();
	}
}
