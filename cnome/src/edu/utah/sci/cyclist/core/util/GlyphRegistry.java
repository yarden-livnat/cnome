package edu.utah.sci.cyclist.core.util;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import edu.utah.sci.cyclist.Cyclist;

public class GlyphRegistry {
//	static private GlyphFont font = GlyphFontRegistry.font("FontAwesome").fontSize(16);;
	public final static String FONT_AWESOME_TTF_PATH = "assets/fontawesome-webfont.ttf";
    public final static String DEFAULT_ICON_SIZE = "14.0";
    public final static String DEFAULT_FONT_SIZE = "1em";

//    public final static AwesomeIcon.ICON_OK
    static {
    	Font.loadFont(Cyclist.class.getResource(FONT_AWESOME_TTF_PATH).toExternalForm(), 10.0);
    }
    
	static public Label get(String name) {
		return get(name, DEFAULT_ICON_SIZE);
	}
	
	static public Label get(AwesomeIcon iconName) {
		return get(iconName,  DEFAULT_ICON_SIZE);
	}
	
	static public Label get(AwesomeIcon iconName, String size) {
		return get(iconName.toString(),  size);
	}
	
	static public Label get(String name, String size) {
		return get("FontAwesome", name, size);
	}
	
	static public Label get(String font, String name, String size) {
		Label label = new Label(name);
        label.getStyleClass().add("awesome-icon");
        label.setStyle("-fx-font-family:"+font+"; -fx-font-size: " + size + ";");
        
        return label;
	}
}
