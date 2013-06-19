package edu.utah.sci.cyclist.ui.panels;

import org.mo.closure.v1.Closure;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.model.DataType;
import edu.utah.sci.cyclist.model.Field;

public class SchemaPanel extends Panel {
		
	private String _id;
	private ObservableList<Field> _fields;
	private List<Entry> _entries;
	private Closure.V1<Field> _onFieldDropAction = null;
	
	public SchemaPanel(String title) {
		super(title);
		_id = title;
		addListeners();
	}
	
	public void setOnFieldDropAction(Closure.V1<Field> action) {
		_onFieldDropAction = action;
	}
	
	public void setFields(ObservableList<Field> fields) {
		if (_fields != fields) {
			if (_fields != null) {
				_fields.removeListener(_invalidationListener);
			}
			
			_fields = fields;
			_fields.addListener(_invalidationListener);
		}
		
		resetContent();
	}
	
	private void resetContent() {
		VBox vbox = (VBox) getContent();
		vbox.getChildren().clear();
		
		_entries = new ArrayList<>();
		for (Field field : _fields) {
			Entry entry = createEntry(field);
			_entries.add(entry);
			vbox.getChildren().add(entry.label);
		}
	}
	
	private Entry createEntry(Field field) {
		final Entry entry = new Entry();
		entry.field = field;
		
		entry.label = LabelBuilder.create()
						.text(field.getName())
						.graphic(new ImageView(Resources.getIcon(_type2Icon.get(field.getType()))))
						.build();
		
		entry.label.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {					
				
				DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
				clipboard.put(DnD.FIELD_FORMAT, Field.class, entry.field);
				clipboard.put(DnD.DnD_SOURCE_FORMAT, Node.class, SchemaPanel.this);
				
				Dragboard db = entry.label.startDragAndDrop(TransferMode.COPY);
				
				ClipboardContent content = new ClipboardContent();
				content.putString(_id);
				
				SnapshotParameters snapParams = new SnapshotParameters();
//	            snapParams.setFill(Color.TRANSPARENT);
	            snapParams.setFill(Color.AQUA);
	            
	            content.putImage(entry.label.snapshot(snapParams, null));	            
				
				db.setContent(content);
				event.consume();
			}
		});
		
		entry.label.setOnDragDone(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {			
			}
		});
		return entry;
	}
	

	private InvalidationListener _invalidationListener = new InvalidationListener() {
		
		@Override
		public void invalidated(Observable observable) {
			resetContent();
		}
	};
	
	
	private void addListeners() {
		getPane().setOnDragEntered(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {		
				event.consume();
			}
		});
		
		getPane().setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				DnD.LocalClipboard clipboard = getLocalClipboard();
				
				Node src = clipboard.get(DnD.DnD_SOURCE_FORMAT, Node.class);
				if (src != null && src != SchemaPanel.this) {
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
				Field field = clipboard.get(DnD.FIELD_FORMAT, Field.class);
				if (_onFieldDropAction != null) 
					_onFieldDropAction.call(field);
				event.setDropCompleted(true);
			}
		});
	}
	
	
	private DnD.LocalClipboard getLocalClipboard() {
		return DnD.getInstance().getLocalClipboard();
	}
	
	class Entry {
		Label label;
		Field field;
	}
	
	private static Map<DataType.Type, String> _type2Icon = new HashMap<>();
	
	static {
		_type2Icon.put(DataType.Type.NUMERIC, "field/number");
		_type2Icon.put(DataType.Type.TEXT, "field/Ab");
		_type2Icon.put(DataType.Type.DATE, "field/date");
		_type2Icon.put(DataType.Type.DATETIME, "field/date");		
	}
}
