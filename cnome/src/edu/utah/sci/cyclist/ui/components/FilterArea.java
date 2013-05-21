package edu.utah.sci.cyclist.ui.components;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ToolBar;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import edu.utah.sci.cyclist.event.dnd.DnD;
import edu.utah.sci.cyclist.model.Field;
import edu.utah.sci.cyclist.model.Filter;

public class FilterArea extends ToolBar {
	
	private ObjectProperty<ObservableList<Filter>> _filtersProperty = new SimpleObjectProperty<>();
	
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
	
	private void build() {
//		HBoxBuilder.create()
//		.spacing(0)
//		.padding(new Insets(2))
//		.minWidth(30)
//		.prefHeight(27)
//		.styleClass("drop-area")
//		.applyTo(this);
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
					getFilters().add(createFilter(field));
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
		return glyph;
	}
	
	private void init() {
		filtersProperty().set(FXCollections.<Filter>observableArrayList());
	}
	
	private DnD.LocalClipboard getLocalClipboard() {
		return DnD.getInstance().getLocalClipboard();
	}
}
