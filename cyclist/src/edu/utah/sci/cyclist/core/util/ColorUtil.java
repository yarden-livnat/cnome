package edu.utah.sci.cyclist.core.util;

import javafx.scene.paint.Color;

public class ColorUtil {

	static public String toString(Color color) {
		return color.toString().replace("0x", "#");
	}
}
