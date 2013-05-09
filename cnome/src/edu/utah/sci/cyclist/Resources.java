/*******************************************************************************
 * Copyright (c) 2013 SCI Institute, University of Utah.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Yarden Livnat  
 *******************************************************************************/
package edu.utah.sci.cyclist;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;

/**
 * Global resources repository
 * @author yarden
 *
 */
public class Resources {

	public static final String ICONS_DIR = "assets/icons/";
	public static final String UNKNOWN_ICON	= "unknown.png";
	
	private static Map<String, Image> _icons = new HashMap<String, Image>();
	
	/**
	 * Find an icon
	 * @param name
	 * @return an Image of the icon
	 */
	public static Image getIcon(String name) {
		return getIcon(name, -1, -1);
	}
	
	/**
	 * Find an icon with a specified size
	 * @param name 
	 * @param width 
	 * @param height
	 * @return an Image of the icon or a default icon if it was not found
	 */
	public static Image getIcon(String name, double width, double height) {
		String fullname =  name.contains(".") ? name : name+".png";
		Image image = _icons.get(fullname);
		if (image == null) {
			InputStream is = Resources.class.getResourceAsStream(ICONS_DIR+fullname);
			if (is == null)
				is = Resources.class.getResourceAsStream(ICONS_DIR+UNKNOWN_ICON);
			if (width > 0)
				image = new Image(is, width, height, true, true);
			else 
				image = new Image(is);
			
			_icons.put(fullname, image);
		}
		return image;
	}
	
//	public static void clean() {
//		_icons = new HashMap<String, Image>();
//	}
}
