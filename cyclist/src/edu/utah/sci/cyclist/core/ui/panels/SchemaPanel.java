package edu.utah.sci.cyclist.core.ui.panels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Window;

import org.mo.closure.v1.Closure;

import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.model.DataType;
import edu.utah.sci.cyclist.core.model.Field;
import edu.utah.sci.cyclist.core.ui.wizards.FieldPropertiesEditorWizard;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;

public class SchemaPanel extends TitledPanel {
                
        private String _id;
        private ObservableList<Field> _fields;
        private List<Entry> _entries;
        private Closure.V1<Field> _onFieldDropAction = null;
        
        final SchemaPanel _panel = this;
        
        
        public SchemaPanel(String title) {
                super(title, GlyphRegistry.get(AwesomeIcon.LIST_UL)); 
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
        
        /**
         * Sets the menu for changing the fields properties. 
         * On mouse right click the user can choose the field role and its filter type.
         * For text fields - all the options are disabled.
         */
        
        public void addChangePropertiesMenu(){
			 for(final Entry entry : _entries){
				 if(entry.label.getOnMouseClicked() == null){
						entry.label.setOnMouseClicked(new EventHandler<MouseEvent>(){
							@Override
				             public void handle(MouseEvent event) {
								 if (event.getButton() == MouseButton.SECONDARY){      
									 Window window = _panel.getParent().getScene().getWindow();
									 Field field = entry.field;
									 FieldPropertiesEditorWizard wizard = new FieldPropertiesEditorWizard(field);
									 ObjectProperty<DataType> dataType = wizard.show(window);
									 dataType.addListener(new ChangeListener<DataType>(){
									 @Override
									 	public void changed(ObservableValue<? extends DataType> arg0, DataType oldVal,DataType newVal) {
											if(newVal.getRole() != field.getDataType().getRole()){
												field.getDataType().setRole(newVal.getRole());
											}
											if(newVal.getFilterType() != field.getDataType().getFilterType()){
												field.getDataType().setFilterType(newVal.getFilterType());
											}
										}	
									});
								 }
				             }
						});
				 }
			 }
        }
        
        private void resetContent() {
                VBox vbox = (VBox) getContent();
                vbox.getChildren().clear();
                
                SortedList<Field> sorted = _fields.sorted(new Comparator<Field>() {

                        @Override
                        public int compare(Field o1, Field o2) {
                                return o1.getName().compareToIgnoreCase(o2.getName());
                        }

                        @Override
                        public Comparator<Field> reversed() {
                                // TODO Auto-generated method stub
                                return null;
                        }

                        @Override
                        public Comparator<Field> thenComparing(
                                        Comparator<? super Field> other) {
                                // TODO Auto-generated method stub
                                return null;
                        }

						@Override
						public <U extends Comparable<? super U>> Comparator<Field> thenComparing(
								Function<? super Field, ? extends U> keyExtractor,
								Comparator<? super U> keyComparator) {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public <U extends Comparable<? super U>> Comparator<Field> thenComparing(
								Function<? super Field, ? extends U> keyExtractor) {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public Comparator<Field> thenComparingInt(
								ToIntFunction<? super Field> keyExtractor) {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public Comparator<Field> thenComparingLong(
								ToLongFunction<? super Field> keyExtractor) {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public Comparator<Field> thenComparingDouble(
								ToDoubleFunction<? super Field> keyExtractor) {
							// TODO Auto-generated method stub
							return null;
						}
		});
		
		_entries = new ArrayList<>();
		for (Field field : sorted) {
			Entry entry = createEntry(field);
			_entries.add(entry);
			vbox.getChildren().add(entry.label);
		}
	}
	
	private Entry createEntry(Field field) {
		final Entry entry = new Entry();
		entry.field = field;
		
		entry.label = new Label(field.getName(), GlyphRegistry.get(_type2Icon.get(field.getType())));
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
		
		entry.label.getStyleClass().add("entry");
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
        
        private static Map<DataType.Type, AwesomeIcon> _type2Icon = new HashMap<>();
        
        static {
                _type2Icon.put(DataType.Type.NUMERIC, AwesomeIcon.SORT_NUMERIC_ASC); 
                _type2Icon.put(DataType.Type.TEXT, AwesomeIcon.SORT_ALPHA_ASC); 
                _type2Icon.put(DataType.Type.DATE, AwesomeIcon.CLOCK_ALT); 
                _type2Icon.put(DataType.Type.DATETIME, AwesomeIcon.CLOCK_ALT);                 
        }
}
