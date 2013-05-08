package edu.utah.sci.cyclist.event.dnd;


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
