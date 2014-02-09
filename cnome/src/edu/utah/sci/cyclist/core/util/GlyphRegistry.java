package edu.utah.sci.cyclist.core.util;

import javafx.scene.Node;

import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

public class GlyphRegistry {
	static private GlyphFont font = GlyphFontRegistry.font("FontAwesome").fontSize(16);;
	
	static public Node get(String name) {
//		return GlyphFontRegistry.glyph(name);
		return font.create(name.substring(name.indexOf('|')+1));
	}
}
