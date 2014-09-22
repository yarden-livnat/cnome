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
package edu.utah.sci.cyclist.core;

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
public class Resources1 {

	public static final String ICONS_DIR = "../assets/icons/";
	public static final String UNKNOWN_ICON	= "unknown.png";
	
	private static Map<String, Image> _icons = new HashMap<String, Image>();
	
	public static String getCSS(String name) {
		return Resources1.class.getResource(name).toExternalForm();
	}
	
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
			InputStream is = Resources1.class.getResourceAsStream(ICONS_DIR+fullname);
			if (is == null)
				is = Resources1.class.getResourceAsStream(ICONS_DIR+UNKNOWN_ICON);
			if (width > 0)
				image = new Image(is, width, height, true, true);
			else 
				image = new Image(is);
			
			_icons.put(fullname, image);
		}
		return image;
	}
	
	/**
	 * Gets the full path in which the application is running.
	 * @return String of the full path.
	 */
	public static String getCurrentPath(){
		String path = "";
		URL url = Resources1.class.getResource("");
		if(url != null){
			//The full path of the current class (e.g "path/edu/utah/sci/cyclist/core" - since that's the location of the Resources1 class)
			path = url.getPath();
			
			//The specific part of the current class location has to be removed.
			//Get if from the package name (e.g "edu.utah.sci.cyclist.core")
			String relativePath = Resources1.class.getPackage().getName().replace(".", "/");
			int lastIndex = path.lastIndexOf(relativePath);
			if(lastIndex >= 0){
				path = path.substring(0,lastIndex-1);
			}
			
			//There is always a leading "/" that should be removed. (When running from a jar it appears as a leading "file:/"
			int index = path.indexOf("/");
			if(index >=0){
				path = path.substring(index+1,path.length());
			}
		}
		return path;
	}	
}
