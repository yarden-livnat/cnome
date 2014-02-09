/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved.
 *
 * License for the specific language governing rights and limitations under Permission
 * is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: The above copyright notice 
 * and this permission notice shall be included in all copies  or substantial portions of the Software. 
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 *  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR 
 *  A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR 
 *  COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
 *  IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *     Yarden Livnat  
 *******************************************************************************/
package edu.utah.sci.cyclist.core.ui.panels;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import javafx.event.EventHandler;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.ui.tools.Tool;
import edu.utah.sci.cyclist.core.ui.tools.ToolFactory;
import edu.utah.sci.cyclist.core.util.AwesomeIcon;
import edu.utah.sci.cyclist.core.util.GlyphRegistry;

public class ToolsPanel extends TitledPanel {
        public static final String ID = "tools-panel";
        public static final String TITLE = "Views";
        
        
        public ImageView dragView = new ImageView();
        public Pane root;
        
        public ToolsPanel() {
                super(TITLE, GlyphRegistry.get(AwesomeIcon.EYE));//"FontAwesome|EYE_OPEN"));
        }
        
        public void setToolFactories(List<ToolFactory> factories) {                
                Collections.sort(factories, new Comparator<ToolFactory>() {
                        public int compare(ToolFactory a, ToolFactory b) {
                                return a.getToolName().compareTo(b.getToolName());
                        }

                        @Override
                        public Comparator<ToolFactory> reversed() {
                                // TODO Auto-generated method stub
                                return null;
                        }

                        @Override
                        public Comparator<ToolFactory> thenComparing(
                                        Comparator<? super ToolFactory> other) {
                                // TODO Auto-generated method stub
                                return null;
                        }

                        @Override
                        public <U extends Comparable<? super U>> Comparator<ToolFactory> thenComparing(
                                        Function<? super ToolFactory, ? extends U> keyExtractor,
                                        Comparator<? super U> keyComparator) {
                                // TODO Auto-generated method stub
                                return null;
                        }

						@Override
						public <U extends Comparable<? super U>> Comparator<ToolFactory> thenComparing(
								Function<? super ToolFactory, ? extends U> keyExtractor) {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public Comparator<ToolFactory> thenComparingInt(
								ToIntFunction<? super ToolFactory> keyExtractor) {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public Comparator<ToolFactory> thenComparingLong(
								ToLongFunction<? super ToolFactory> keyExtractor) {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public Comparator<ToolFactory> thenComparingDouble(
								ToDoubleFunction<? super ToolFactory> keyExtractor) {
							// TODO Auto-generated method stub
							return null;
						}
		});
		
		VBox vbox = (VBox) getContent();
		
		for (final ToolFactory factory : factories) {
			
			final Label title = new Label(factory.getToolName(), GlyphRegistry.get(factory.getIcon()));
			title.setPrefWidth(USE_COMPUTED_SIZE);
			title.getStyleClass().add("tools-entry");
			
			title.setOnDragDetected(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event) {					
					Tool tool;
					try {
						tool = factory.create();

						DnD.LocalClipboard clipboard = DnD.getInstance().createLocalClipboard();
						clipboard.put(DnD.TOOL_FORMAT, Tool.class, tool);
						
						Dragboard db = title.startDragAndDrop(TransferMode.COPY);
						ClipboardContent content = new ClipboardContent();				
						content.put( DnD.TOOL_FORMAT, factory.getToolName());
						
						SnapshotParameters snapParams = new SnapshotParameters();
//			            snapParams.setFill(Color.TRANSPARENT);
			            snapParams.setFill(Color.AQUA);
			            
			            content.putImage(title.snapshot(snapParams, null));	            
						
						db.setContent(content);
						
					} catch (InstantiationException | IllegalAccessException e) {
						// TODO: report an error
					}					
					
//					DnDIcon.getInstance().show(icon, title);
					event.consume();
				}
			});
			
			vbox.getChildren().add(title);
		}
	}
}