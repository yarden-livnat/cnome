package edu.utah.sci.cyclist.view.panels;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import edu.utah.sci.cyclist.Resources;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.model.Field;

public class SchemaPanel extends Panel {
		
	private ObservableList<Field> _fields;
	private List<Entry> _entries;
	
	public SchemaPanel(String title) {
		super(title);
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
						.graphic(new ImageView(Resources.getIcon(field.getDataTypeName())))
						.build();
		
		entry.label.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {					
				
				DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
				clipboard.put(DnD.FIELD_FORMAT, Field.class, entry.field);
				
				Dragboard db = entry.label.startDragAndDrop(TransferMode.COPY);
				
				ClipboardContent content = new ClipboardContent();
				content.putString(entry.label.getText());
//				content.putImage(Resources.getIcon("field"));
				
				db.setContent(content);
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
	
	
	class Entry {
		Label label;
		Field field;
	}
}
