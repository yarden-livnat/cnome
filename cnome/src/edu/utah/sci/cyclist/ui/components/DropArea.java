package edu.utah.sci.cyclist.ui.components;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.event.ui.FilterEvent;
import edu.utah.sci.cyclist.model.DataType.Role;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.FieldProperties;
import edu.utah.sci.cyclist.model.Table;

public class DropArea extends HBox implements Observable {
	
	public enum Policy {SINGLE, MULTIPLE};
	public enum AcceptedRoles{ALL,DIMENSION};
	
	private ObjectProperty<ObservableList<Field>> _fieldsProperty = new SimpleObjectProperty<>();
	private Policy _policy;
	private AcceptedRoles _acceptedRoles;
	private ObjectProperty<Table> _tableProperty = new SimpleObjectProperty<>();
	private List<InvalidationListener> _listeners = new ArrayList<>();
	private List<Field> _preOccupiedFields = new ArrayList<>();
	private ObjectProperty<EventHandler<FilterEvent>> _action = new SimpleObjectProperty<>();
	
	public DropArea(Policy policy, AcceptedRoles acceptedRoles) {
		_policy = policy;
		_acceptedRoles = acceptedRoles;
		build();
		init();
	}
	
	
	public ObjectProperty<EventHandler<FilterEvent>> onAction() {
		return _action;
	}
	
	public void setOnAction( EventHandler<FilterEvent> handler) {
		_action.set(handler);
	}
	
	public EventHandler<FilterEvent> getOnAction() {
		return _action.get();
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
	
	public ObjectProperty<Table> tableProperty() {
		return _tableProperty;
	}
	
	public Table getTable() {
		return _tableProperty.get();
	}
	public ObservableList<Field> getFields() {
		return _fieldsProperty.get();
	}
	
	public boolean isValid() {
		for (Node node : getChildren()) {
			FieldGlyph glyph = (FieldGlyph) node;
			if (glyph.isDisabled())
				return false;
		}		
		return true;
	}
	
	public void removeFilterFromGlyph(Object filter){
		for (Node node : getChildren()) {
			FieldGlyph glyph = (FieldGlyph) node;
			if(glyph.removeFieldFilter(filter)){
				break;
			}
		}
	}
	
	public String getFieldTitle(int index) {
		FieldGlyph glyph = (FieldGlyph) getChildren().get(index);
		return glyph.getTitle();
	}
	
	/* Name: updatePreOccupiedField
     * Saves all the fields already used by other drop areas.
     * (Cannot add them to the current drop area as long as they are used by the other drop areas) */
	public void updatePreOccupiedField(List<Field> fields){
		_preOccupiedFields.clear();
		_preOccupiedFields.addAll(fields);
	}
	
	private void build() {	
		setSpacing(0);
		setPadding(new Insets(2));
		setMinWidth(30);
		setPrefHeight(18);
		getStyleClass().add("drop-area");
				
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
					//setStyle("-fx-border-color: -fx-body-color");
					setStyle("-fx-box-border: transparent");
					event.consume();
				}
				
			}
		});
		
		setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				boolean status = false;

				Field field = getLocalClipboard().get(DnD.FIELD_FORMAT, Field.class);
				if (event.getAcceptedTransferMode() == TransferMode.COPY) {
					field = field.clone();
					if (field.getString(FieldProperties.AGGREGATION_FUNC) == null) {
						field.set(FieldProperties.AGGREGATION_FUNC, field.getString(FieldProperties.AGGREGATION_DEFAULT_FUNC));
					}
				}
				if (field != null) {
					if(_acceptedRoles == AcceptedRoles.DIMENSION && field.getRole() != Role.DIMENSION){
						System.out.println("Cannot add non-discrete field to this drop area");
					} else if(isPreOccupiedField(field)){
						System.out.println("Field already exists in another drop area");
					}else if (getFields().size() == 0) {
						getFields().add(field);
					} else if(_policy == Policy.SINGLE){
						getFields().set(0, field);
					} else {
						getFields().add(field);
					}
					
					status = true;			
				}
				
				event.setDropCompleted(status);
				event.consume();				
			}
			
		});
		
		tableProperty().addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable observable) {
				Table table = tableProperty().get();
				if (table == null) {
					
				} else {
					for (Node node : getChildren()) {
						if (node instanceof FieldGlyph) {
							FieldGlyph glyph = (FieldGlyph) node;
							glyph.validProperty().set(table.hasField(glyph.getField()));
						}
					}
				}
				
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
										
										SnapshotParameters snapParams = new SnapshotParameters();
							            snapParams.setFill(Color.TRANSPARENT);
							            
							            content.putImage(glyph.snapshot(snapParams, null));	
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
								
								glyph.setOnAction(new EventHandler<ActionEvent>() {

									@Override
									public void handle(ActionEvent event) {
										fireInvalidationEvent();
									}
								});
								

								glyph.setOnFilterAction(new EventHandler<FilterEvent>() {
								
									@Override
									public void handle(FilterEvent event) {
										if (getOnAction() != null) {
											getOnAction().handle(event);
										}
									}
								});
								
//								glyph.validProperty().bind(tableProperty().isEqualTo(field.tableProperty()));
								if (getTable() != null)
									glyph.validProperty().set(getTable().hasField(field));
							}
							
							fireInvalidationEvent();
						}
				});
			}
		});
	}
	
	private void fireInvalidationEvent() {
		for (InvalidationListener listener : _listeners) {
			listener.invalidated(DropArea.this);
		}
	}
	
	private void init() {
		fieldsProperty().set(FXCollections.<Field>observableArrayList());
	}

	private DnD.LocalClipboard getLocalClipboard() {
		return DnD.getInstance().getLocalClipboard();
	}
	
	/* Name: isPreOccupiedField
     * Checks if the specified field is already used by other drop areas */
	private Boolean isPreOccupiedField(Field testedField){
		for(Field field : _preOccupiedFields){
			if(testedField.getName().equals(field.getName()) && testedField.getTable().getName().equals(field.getTable().getName())){
				return true;
			}
		}
		return false;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		if (!_listeners.contains(listener))
			_listeners.add(listener);
		
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		_listeners.remove(listener);
	}
}
