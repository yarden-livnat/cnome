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
package edu.utah.sci.cyclist.core.event.dnd;


import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class DnDIcon {

	private static DnDIcon _instance = new DnDIcon();
	
	private Pane _glass;
	private ImageView _icon = null;
	private EventHandler<? super MouseEvent> draggedHandler;
	private EventHandler<? super MouseEvent> releasedHandler;
	
	public static DnDIcon getInstance() { return _instance; }
	
	public void setGlass(Pane glass) {
		_glass = glass;
	}
	
	public void show(Image image, final Node node) {
		if (_icon == null) {
			_icon = new ImageView();
			_icon.setMouseTransparent(true);
		}
		
		_icon.setImage(image);
		_glass.getChildren().add(_icon);
		_icon.startFullDrag();
		
         node.setMouseTransparent(true);
        
        draggedHandler = node.getOnMouseDragged();
		
        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
	        public void handle(MouseEvent e) {
	            Point2D localPoint = _glass.sceneToLocal(new Point2D(e.getSceneX(), e.getSceneY()));
	            _icon.relocate(
	                    (int)(localPoint.getX() - _icon.getBoundsInLocal().getWidth() / 2),
	                    (int)(localPoint.getY() - _icon.getBoundsInLocal().getHeight() / 2)
	            );
	            e.consume();
	        }
	    });
		
		releasedHandler = node.getOnMouseReleased();
	    node.setOnMouseReleased(new EventHandler<MouseEvent>() {
	        public void handle(MouseEvent e) {
	            // dragItem = null;
	        	// _icon.setMouseTransparent(false);
	        	_glass.getChildren().remove(_icon);
	        	
	            node.setMouseTransparent(false);
	            node.setOnMouseReleased(releasedHandler);
	            node.setOnMouseDragged(draggedHandler);
	       }
	    });
	}
}
