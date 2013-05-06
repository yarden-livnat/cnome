package edu.utah.sci.cyclist.view.components;


import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class DnDIcon {

	private static DnDIcon _instance = new DnDIcon();
	private static Pane _root;
	private static ImageView _view = new ImageView();
	
	public static DnDIcon getInstance() { return _instance; }
	
	public void setRoot(Pane root) {
		_root = root;
	}
	
	public void show(Image image) {
		_view.setImage(image);
		_root.getChildren().add(_view);
	}
	
	public void drag(MouseEvent e) {
		 Point2D localPoint = _root.sceneToLocal(e.getSceneX(), e.getSceneY());
         _view.relocate(
                 (int)(localPoint.getX() - _view.getBoundsInLocal().getWidth() / 2),
                 (int)(localPoint.getY() - _view.getBoundsInLocal().getHeight() / 2));
	}
	
	public void hide() {
		_root.getChildren().remove(_view);
	}
	
}
