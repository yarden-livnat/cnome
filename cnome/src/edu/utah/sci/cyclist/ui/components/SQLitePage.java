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
package edu.utah.sci.cyclist.ui.components;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;

import edu.utah.sci.cyclist.model.CyclistDatasource;
import edu.utah.sci.cyclist.ui.wizards.DatasourceWizardPage;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.text.TextBuilder;
import javafx.stage.FileChooser;

public class SQLitePage extends GridPane implements DatasourceWizardPage {
	static Logger log = Logger.getLogger(SQLitePage.class);
	private TextField _path;
	private CyclistDatasource _ds;
	
	public SQLitePage(CyclistDatasource ds) {
		_ds = ds;
		build();
	}

	@Override
	public String getURL() {
		StringBuilder sb = new StringBuilder();
		sb.append("jdbc:sqlite:/").append(_path.getText());
		return sb.toString();
	}

	@Override
	public CyclistDatasource getDataSource() {

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			log.warn("Can not load sqlite driver", e);
		}

		_ds.setURL(getURL());
		Properties p = _ds.getProperties();
		p.setProperty("driver", "sqlite");
		p.setProperty("type", "SQLite");
		p.setProperty("path", _path.getText());
		String name = _path.getText();
		p.setProperty("name", name.substring(name.lastIndexOf("/")+1));

		return _ds;	
	}

	@Override
	public Node getNode() {
		return this;
	}
	private void build() {
		GridPaneBuilder.create()
			.vgap(10)
			.hgap(35)
			.padding(new Insets(10,3,10,3))
			.applyTo(this);

		String path = _ds.getProperties().getProperty("path");
		if (path == null) path = "";

		 _path = TextFieldBuilder.create().text(path).prefWidth(125).build();
		 
		 HBox textBox = HBoxBuilder.create()
				 .children(
				 _path, 
				 ButtonBuilder.create()
				 .text("...")
				 .onAction(new EventHandler<ActionEvent>() {
					 @Override
					 public void handle(ActionEvent event) {
						 FileChooser chooser = new FileChooser();
						 chooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("SQLite files (*.sqlite)", "*.sqlite") );
						 File file = chooser.showOpenDialog(null);
						 if (file != null)
							 _path.setText(file.getPath());
					 }
				 })
				 .build()
				 ).build(); 
		 
		add(TextBuilder.create().text("File:").build(), 0, 0);
		add(textBox, 1, 0);
	}
}
