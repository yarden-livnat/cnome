package edu.utah.sci.cyclist.ui.components;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.model.Field;

public class DropArea extends HBox {
	
	public enum Policy {SINGLE, MUTLIPLE};
	
	private ObjectProperty<ObservableList<Field>> _fieldsProperty = new SimpleObjectProperty<>();
	private Policy _policy;
	
	public DropArea(Policy policy) {
		_policy = policy;
		build();
		init();
	}
	
	public ObjectProperty<ObservableList<Field>> fieldsProperty() {
		return _fieldsProperty;
	}
	
	
	public Policy getPolicy() {
		return _policy;
	}
	
	public void setPolicy(Policy policy) {
		// TODO: deal with the case where policy == MULTIPLE and there are already more than one Filter
		_policy = policy;
	}
	
	public ObservableList<Field> getFields() {
		return _fieldsProperty.get();
	}
	
	private void build() {		
		HBoxBuilder.create()
			.spacing(0)
			.padding(new Insets(2))
			.minWidth(30)
			.prefHeight(27)
			.styleClass("drop-area")
			.applyTo(this);
				
		/*
		 * DnD handlers
		 */
		setOnDragEntered(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {	
//				if (_policy == Policy.MUTLIPLE || getFilters().size() == 0) {
					if (getLocalClipboard().hasContent(DnD.FIELD_FORMAT))
						setStyle("-fx-border-color: #c0c0c0");
					event.consume();
//				}
				
			}
		});
		
		setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
//				if (_policy == Policy.MUTLIPLE || getFilters().size() == 0) {
					if (getLocalClipboard().hasContent(DnD.FIELD_FORMAT)) {
						event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
					}
					event.consume();
//				}
			}
		});
		
		setOnDragExited(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				if (getLocalClipboard().hasContent(DnD.FIELD_FORMAT)) {
					setEffect(null);
					setStyle("-fx-border-color: -fx-body-color");
					event.consume();
				}
				
			}
		});
		
		setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				boolean status = false;
				Field field = getLocalClipboard().get(DnD.FIELD_FORMAT, Field.class);
				if (field != null) {
					if (_policy == Policy.MUTLIPLE || getFields().size() == 0) {
						getFields().add(field);
					} else {
						getFields().set(0, field);
					}
					
					status = true;			
				}
				event.setDropCompleted(status);
				event.consume();				
			}
		});
		
		/*
		 * Handle changes in the list of Filters
		 */
		
		fieldsProperty().addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable observable) {
				
				getFields().addListener(new InvalidationListener() {
			
						@Override
			            public void invalidated(Observable observable) {				
							getChildren().clear();
							for (final Field field : getFields()) {
								final FieldGlyph glyph = new FieldGlyph(field);
								getChildren().add(glyph);
								
								glyph.setOnDragDetected(new EventHandler<MouseEvent>() {

									@Override
									public void handle(MouseEvent event) {
										Dragboard db = glyph.startDragAndDrop(TransferMode.MOVE);
										
										DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
										clipboard.put(DnD.FIELD_FORMAT, Field.class, field);
										
										ClipboardContent content = new ClipboardContent();
										content.putString(field.getName());
										db.setContent(content);
										
//										glyph.setManaged(false);
//										glyph.setVisible(false);
										getChildren().remove(glyph);
										getFields().remove(field);
									}
								});
								
								glyph.setOnDragDone(new EventHandler<DragEvent>() {

									@Override
									public void handle(DragEvent event) {
//										glyph.setCursor(Cursor.DEFAULT);
//										System.out.println("Filter drag done:"+event.isAccepted()+"  compeleted:"+event.isDropCompleted()+"  mode:"+event.getTransferMode());
//										if (/*event.isDropCompleted()*/ event.getAcceptedTransferMode() == TransferMode.COPY) {
//											glyph.setVisible(true);
//											glyph.setManaged(true);
//										} else {
//											getChildren().remove(glyph);
//											getFilters().remove(Filter);
//										}
									}
								});
							}
						}
				});
			}
		});
	}
	
	private void init() {
		fieldsProperty().set(FXCollections.<Field>observableArrayList());
	}

	private DnD.LocalClipboard getLocalClipboard() {
		return DnD.getInstance().getLocalClipboard();
	}
}
