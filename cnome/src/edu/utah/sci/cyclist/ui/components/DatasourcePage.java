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

import java.util.Properties;

import edu.utah.sci.cyclist.model.CyclistDatasource;
import edu.utah.sci.cyclist.ui.wizards.DatasourceWizardPage;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.PasswordFieldBuilder;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.text.TextBuilder;

/*
 * A general pane that defines characteristics of all data sources
 */
public class DatasourcePage extends GridPane implements DatasourceWizardPage {

	// Fields of a data source
	protected TextField _user;
	protected TextField _password;
	protected TextField _host;
	protected TextField _port;
	protected TextField _schema;
	protected String    _driver = "";
	protected String    _type = "";

	// The data source
	protected CyclistDatasource _ds;
		
	// * * * Constructor sets the data source, builds the window & initializes * * * //
	public DatasourcePage(CyclistDatasource ds) {
		_ds = ds;
		build();
		init();
	}

	// * * * Get the URL of the data source * * * //
    public String getURL() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("jdbc:").append(_driver).append("://")
    	.append(_host.getText()).append(":").append(_port.getText())
    	.append("/").append(_schema.getText());
    	return sb.toString();
	}

    // * * * Returns the data source created or edited by this page * * * //
    @Override
	public CyclistDatasource getDataSource() {
		
		_ds.setURL(getURL());
		Properties p = _ds.getProperties();
		p.setProperty("user", _user.getText());
		p.setProperty("password", _password.getText());
		p.setProperty("host", _host.getText());
		p.setProperty("port", _port.getText());
		p.setProperty("schema", _schema.getText());
		p.setProperty("driver", _driver);
		p.setProperty("type", _type);
		_ds.setProperties(p);
		return _ds;
	}
	
	// * * * Return this GUI element * * * //
	public Node getNode() {
		return this;	
	}
	
	// * * * Build the page * * * //
	protected void build() {
		 GridPaneBuilder.create()
				.vgap(10)
				.hgap(5)
				.padding(new Insets(10,3,10,3))
				.applyTo(this);

		add(TextBuilder.create().text("User:").build(), 0, 0);
		add(_user = TextFieldBuilder.create().build(), 1, 0);

		add(TextBuilder.create().text("Password:").build(), 0, 1);
		add(_password = PasswordFieldBuilder.create().build(), 1, 1);

		add(TextBuilder.create().text("Host:").build(), 0, 2);
		add(_host = TextFieldBuilder.create().build(), 1, 2);
		
		add(TextBuilder.create().text("Schema:").build(), 0, 3);
		add(_schema = TextFieldBuilder.create().build(), 1, 3);
	
		add(TextBuilder.create().text("Port:").focusTraversable(true).build(), 2, 2);
		add(_port = TextFieldBuilder.create().build(), 3, 2);

	}

	// * * * Initialize the fields, if we have them * * * //
	protected void init() {
		Properties p = _ds.getProperties();
		_user.setText(check(p.getProperty("user")));
		_password.setText(check(p.getProperty("password")));
		_host.setText(check(p.getProperty("host")));
		_port.setText(check(p.getProperty("port")));
		_schema.setText(check(p.getProperty("schema")));
	}
	
	// * * * If we don't have a property, set the value as "" * * * //
	protected String check(String value) {
		return value == null ? "" : value;
	}	
}
