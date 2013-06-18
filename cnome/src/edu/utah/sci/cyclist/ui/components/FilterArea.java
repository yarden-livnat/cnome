package edu.utah.sci.cyclist.ui.components;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ToolBar;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.event.ui.FilterEvent;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.Filter;

public class FilterArea extends ToolBar implements Observable {
	
	private ObjectProperty<ObservableList<Filter>> _filtersProperty = new SimpleObjectProperty<>();
	private ObjectProperty<EventHandler<FilterEvent>> _action = new SimpleObjectProperty<>();
	private List<InvalidationListener> _listeners = new ArrayList<>();
	
	public FilterArea() {
		build();
		init();
	}
	
	public ObjectProperty<ObservableList<Filter>> filtersProperty() {
		return _filtersProperty;
	}
	
	public ObservableList<Filter> getFilters() {
		return _filtersProperty.get();
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
	
	@Override
	public void addListener(InvalidationListener listener) {
		if (!_listeners.contains(listener))
			_listeners.add(listener);
		
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		_listeners.remove(listener);
	}
	
	private void build() {
		setPrefHeight(25);
		getStyleClass().add("drop-area");
		setOrientation(Orientation.HORIZONTAL);
		
		/*
		 * DnD Handlers
		 */
		
		setOnDragEntered(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				if (getLocalClipboard().hasContent(DnD.FIELD_FORMAT))
					setStyle("-fx-border-color: #c0c0c0");
				event.consume();			
			}
		});

		setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				if (getLocalClipboard().hasContent(DnD.FIELD_FORMAT)) {
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				}
				event.consume();
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
					getFilters().add(createFilter(field.clone()));
					status = true;
				}
				status = true;					
				event.setDropCompleted(status);
				event.consume();				
			}
		});
		
		
		filtersProperty().addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable observable) {
				getFilters().addListener(new ListChangeListener<Filter>() {

					@Override
					public void onChanged(ListChangeListener.Change<? extends Filter> c) {
						while (c.next()) {
							if (c.wasAdded()) {
								for (Filter filter : c.getAddedSubList()) {
									FilterGlyph glyph = createFilterGlyph(filter);
									getItems().add(glyph);
								}
							} else if (c.wasRemoved()) {
								for (Filter filter : c.getRemoved()) {
									for (Node node : getItems()) {
										FilterGlyph glyph = (FilterGlyph) node;
										if (glyph.getFilter() == filter) {
											getItems().remove(glyph);
											break;
										}
									}
								}
							}
						}
					}
					
				});
			
				// inform listeners filter list is invalidated
				fireInvalidationEvent();
			}
		});
	}
	
	private Filter createFilter(Field field) {
		Filter filter = new Filter(field);
		
		return filter;
	}
	
	private FilterGlyph createFilterGlyph(Filter filter) {
		final FilterGlyph glyph = new FilterGlyph(filter);
		
		glyph.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				Field field = glyph.getFilter().getField();
				Dragboard db = glyph.startDragAndDrop(TransferMode.MOVE);
				
				DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
				clipboard.put(DnD.FIELD_FORMAT, Field.class, field);
				
				ClipboardContent content = new ClipboardContent();
				content.putString(field.getName());
				
				SnapshotParameters snapParams = new SnapshotParameters();
	            snapParams.setFill(Color.TRANSPARENT);
	            
	            content.putImage(glyph.snapshot(snapParams, null));	
				
				db.setContent(content);
				
//				getChildren().remove(glyph);
				getFilters().remove(glyph.getFilter());
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
					if (getOnAction() != null) {
						getOnAction().handle(event);
					}
				}
			}
		});
		return glyph;
	}
	
	private void fireInvalidationEvent() {
		for (InvalidationListener listener : _listeners) {
			listener.invalidated(FilterArea.this);
		}
	}
	
	private void init() {
		filtersProperty().set(FXCollections.<Filter>observableArrayList());
	}
	
	private DnD.LocalClipboard getLocalClipboard() {
		return DnD.getInstance().getLocalClipboard();
	}
}
