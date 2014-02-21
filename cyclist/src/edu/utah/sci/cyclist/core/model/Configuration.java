package edu.utah.sci.cyclist.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.paint.Color;

public class Configuration {

	private static Configuration _instance = new Configuration();
	
	private Map<String, Color> _typeColors = new HashMap<>();
	private List<Color> _availableColors = new ArrayList<>();

	private Configuration() {
		init();
	}
	
	public static Configuration getInstance() {
		return _instance;
	}
	
	public Color getColor(String type) {
		Color c = _typeColors.get(type);
		if (c == null) {
			if (_availableColors.size() > 0) {
				c = _availableColors.remove(0);
			} else {
				c = Color.color(Math.random(), Math.random(), Math.random());
			}
			_typeColors.put(type, c);
		}
		
		return c;
	}
	
	public static Color color(String type) {
		return _instance.getColor(type);
	}
	
	
	private  void init() {
		for (Color c : colors) {
			_availableColors.add(c);
		}
	}
	
	private  Color[] colors = {
			Color.color(141/255.0, 211/255.0, 199/255.0),
//			Color.color(255/255.0, 255/255.0, 179/255.0),
			Color.color(190/255.0, 186/255.0, 218/255.0),
			Color.color(251/255.0, 128/255.0, 114/255.0),
			Color.color(128/255.0, 177/255.0, 211/255.0),
			Color.color(253/255.0, 180/255.0,  98/255.0),
			Color.color(179/255.0, 222/255.0, 105/255.0),
			Color.color(252/255.0, 205/255.0, 229/255.0),
			Color.color(217/255.0, 217/255.0, 217/255.0), 
			Color.color(188/255.0, 128/255.0, 189/255.0)
		};
}
