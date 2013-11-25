package edu.utah.sci.cyclist.ui.components;

import java.util.ArrayList;
import java.util.Map;

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
import edu.utah.sci.cyclist.model.Table;

public class FilterArea extends ToolBar {
        
        private ObservableList<Filter> _filters = FXCollections.observableArrayList();
        private ObservableList<Filter> _remoteFilters = FXCollections.observableArrayList();
        private ObjectProperty<EventHandler<FilterEvent>> _action = new SimpleObjectProperty<>();
        private int _lastRemoteFilter = 0;
        private ObjectProperty<Table> _tableProperty = new SimpleObjectProperty<>();
        private Map<Class<?>, TransferMode[]> _sourcesTransferModes;
        
        public FilterArea() {
                build();
        }
        
        /**
         * @name setDragAndDropModes
         * @param sourcesTransferModes - Maps for each possible source the accepted drag and drop transfer modes.
         */
        public void setDragAndDropModes( Map<Class<?>, TransferMode[]> sourcesTransferModes){
    		_sourcesTransferModes = sourcesTransferModes;
    	}
        
        public ObservableList<Filter> getFilters() {
                return _filters;
        }
        
        public ObservableList<Filter> getRemoteFilters() {
                return _remoteFilters;
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
        
        public ObjectProperty<Table> tableProperty() {
                return _tableProperty;
        }
        
        public Table getTable() {
                return _tableProperty.get();
        }
        
        public void copy(FilterArea other) {
                _filters.addAll(other._filters);
                _remoteFilters.addAll(other._remoteFilters);
        }
        
        private void build() {
//                setPrefHeight(25);
                setMinWidth(15);
                setMinHeight(15);
                getStyleClass().add("drop-area");
                setOrientation(Orientation.HORIZONTAL);
                
                /*
                 * DnD Handlers
                 */
                
                setOnDragEntered(new EventHandler<DragEvent>() {
                        public void handle(DragEvent event) {
                                if (getLocalClipboard().hasContent(DnD.FIELD_FORMAT) || getLocalClipboard().hasContent(DnD.FILTER_FORMAT) )
                                        setStyle("-fx-border-color: #c0c0c0");
                                event.consume();                        
                        }
                });

                setOnDragOver(new EventHandler<DragEvent>() {
                        public void handle(DragEvent event) {
                                if (getLocalClipboard().hasContent(DnD.FIELD_FORMAT) || getLocalClipboard().hasContent(DnD.FILTER_FORMAT)) {
                                        if(getLocalClipboard().hasContent(DnD.DnD_SOURCE_FORMAT)){
                                        	Class<?> key = getLocalClipboard().getType(DnD.DnD_SOURCE_FORMAT);
	                                	    
                                        	//Accepts the drag and drop transfer mode according to the source and the 
                                        	//predefined accepted transfer modes.
                                        	if(key != null && _sourcesTransferModes!= null &&_sourcesTransferModes.containsKey(key)){
	                                	    	event.acceptTransferModes(_sourcesTransferModes.get(key));
	                                	    }
                                        }
                                }
                                event.consume();
                        }
                });
                        
                setOnDragExited(new EventHandler<DragEvent>() {
                        public void handle(DragEvent event) {
                                if (getLocalClipboard().hasContent(DnD.FIELD_FORMAT) || getLocalClipboard().hasContent(DnD.FILTER_FORMAT)) {
                                        setEffect(null);
                                        setStyle("-fx-border-color: -fx-body-color");
                                        event.consume();
                                }
                        }
                });
                
                setOnDragDropped(new EventHandler<DragEvent>() {
                        public void handle(DragEvent event) {
                                boolean status = false;
                                if (getLocalClipboard().hasContent(DnD.FIELD_FORMAT) ) {
                                        Field field = getLocalClipboard().get(DnD.FIELD_FORMAT, Field.class);
                                        if (field != null) {
                                                Filter filter = new Filter(field);
                                                getFilters().add(filter);
                                                if (getOnAction() != null) {
                                                        getOnAction().handle(new FilterEvent(FilterEvent.SHOW, filter));
                                                }
                                                status = true;
                                        }
                                }
                                else if (getLocalClipboard().hasContent(DnD.FILTER_FORMAT)) {
                                        Filter filter = getLocalClipboard().get(DnD.FILTER_FORMAT, Filter.class);
                                        getFilters().add(filter);
                                        status = true;
                                }
                                event.setDropCompleted(status);
                                event.consume();                                
                        }
                });
                
                
                _filters.addListener(new ListChangeListener<Filter>() {

                        @Override
                        public void onChanged(ListChangeListener.Change<? extends Filter> c) {
                                while (c.next()) {
                                        if (c.wasAdded()) {
                                                for (Filter filter : c.getAddedSubList()) {
                                                        FilterGlyph glyph = createFilterGlyph(filter, false);
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
                
                _remoteFilters.addListener(new ListChangeListener<Filter>() {

                        @Override
                        public void onChanged(ListChangeListener.Change<? extends Filter> c) {
                                java.util.List<Filter> addedFilters = new ArrayList<Filter>(); 
                                while (c.next()) {
                                        if (c.wasAdded()) {
                                                for (Filter filter : c.getAddedSubList()) {
                                                        FilterGlyph glyph = createFilterGlyph(filter, true);
                                                        getItems().add(_lastRemoteFilter++, glyph);
                                                        addedFilters.add(filter);
                                                }
                                        } else if (c.wasRemoved()) {
                                                for (Filter filter : c.getRemoved()) {
                                                        for (Node node : getItems()) {
                                                                FilterGlyph glyph = (FilterGlyph) node;
                                                                if (glyph.getFilter() == filter) {
                                                                        getItems().remove(glyph);
                                                                        _lastRemoteFilter--;
                                                                        break;
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                        
                });
                
                //Change the glyph display when the selected table changes.
                tableProperty().addListener(new InvalidationListener() {
                        
                        @Override
                        public void invalidated(Observable observable) {
                                Table table = tableProperty().get();
                                if (table == null) {
                                        
                                } else {
                                        for (Node node : getItems()) {
                                                FilterGlyph glyph = (FilterGlyph) node;
                                                glyph.validProperty().set(table.hasField(glyph.getFilter().getField()));
                                        }
                                }
                                
                        }
                });
        }
        
        private FilterGlyph createFilterGlyph(Filter filter, boolean remote) {
                
                boolean filterIsValid = true;
                if(tableProperty().get() != null){
                        filterIsValid = tableProperty().get().hasField(filter.getField());
                }
                
                final FilterGlyph glyph = new FilterGlyph(filter, remote, filterIsValid);
                
                glyph.setOnDragDetected(new EventHandler<MouseEvent>() {

                        @Override
                        public void handle(MouseEvent event) {
//                                Field field = glyph.getFilter().getField();
                                Filter filter  = glyph.getFilter();
                                Dragboard db = glyph.startDragAndDrop(TransferMode.COPY);
                                
                                DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
                                clipboard.put(DnD.FILTER_FORMAT, Filter.class, filter);
                                
                                ClipboardContent content = new ClipboardContent();
                                content.putString(filter.getName());
                                
                                clipboard.put(DnD.DnD_SOURCE_FORMAT, FilterArea.class, FilterArea.this);
                                
                                SnapshotParameters snapParams = new SnapshotParameters();
                    snapParams.setFill(Color.TRANSPARENT);
                    
                    content.putImage(glyph.snapshot(snapParams, null));        
                                
                                db.setContent(content);
                                
//                                getChildren().remove(glyph);
//                                getFilters().remove(glyph.getFilter());
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
                                if (event.getEventType() == FilterEvent.SHOW ) {
                                        if (getOnAction() != null) {
                                                getOnAction().handle(event);
                                        }
                                } else if (event.getEventType() == FilterEvent.DELETE) {
//                                        if (getOnAction() != null) {
//                                                getOnAction().handle(event);
                                        getFilters().remove(glyph.getFilter());
                                } else if(event.getEventType() == FilterEvent.REMOVE_FILTER_FIELD){
                                        //If filter is connected to a numeric field - clean the field connection as well.
                                        if (getOnAction() != null) {
                                                getOnAction().handle(event);
                                        }
                                }
                        }
                });
                return glyph;
        }
        
        private DnD.LocalClipboard getLocalClipboard() {
                return DnD.getInstance().getLocalClipboard();
        }
}