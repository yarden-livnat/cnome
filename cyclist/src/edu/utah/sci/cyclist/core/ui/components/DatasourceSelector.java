package edu.utah.sci.cyclist.core.ui.components;

import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import edu.utah.sci.cyclist.core.model.Table;
import edu.utah.sci.cyclist.core.model.Table.SourceLocation;

public class DatasourceSelector extends VBox{
	
	private TextField _localField;
	private ToggleGroup _remoteLocalGroup;
	private RadioButton _remoteRadio;
	private RadioButton _localAllRadio;
	private RadioButton _localSubsetRadio;
	private TextField _aliasField;

	public DatasourceSelector(Table table) {
		setSpacing(5);
		buildGUI(table);
	}

	// - - - Create the GUI - - - 
	private void buildGUI(Table table) {
		
		// Create the local field
		_localField = new TextField(new Integer(table.getDataSubset()).toString());
		_localField.prefWidth(150);
		_localField.minHeight(20);


		// Create the alias field
		_aliasField = new TextField(table.getAlias());
		_aliasField.prefWidth(150);
		_aliasField.minHeight(20);

		// --- Alias Box
		HBox aliasBox = new HBox();
		aliasBox.setSpacing(5);
		aliasBox.getChildren().add(new Text("Alias:"));
		
		// --- Remote box
		HBox remoteBox = new HBox();
		remoteBox.setSpacing(5);
		remoteBox.getChildren().addAll(
			_remoteRadio = new RadioButton(),
			new Text("Remote")
		);
		
		
		// --- Local box
		HBox localBox = new HBox();
		localBox.setSpacing(5);;
		localBox.getChildren().addAll(
			_localAllRadio = new RadioButton(),
			new Text("Local (all)")
		);

		// --- Local subset box
		HBox subsetBox =  new HBox();
		subsetBox.setSpacing(5);
		subsetBox.getChildren().addAll(
			_localSubsetRadio = new RadioButton(),
			new Text("Local (subset):"),
			_localField
		);
		
		// --- Toggle group
		_remoteLocalGroup = new ToggleGroup();
		_remoteRadio.setToggleGroup(_remoteLocalGroup);
		_remoteRadio.setUserData(SourceLocation.REMOTE);
		_localAllRadio.setToggleGroup(_remoteLocalGroup);
		_localAllRadio.setUserData(SourceLocation.LOCAL_ALL);
		_localSubsetRadio.setToggleGroup(_remoteLocalGroup);
		_localSubsetRadio.setUserData(SourceLocation.LOCAL_SUBSET);

		switch(table.getSourceLocation()){
		case REMOTE:
			_remoteRadio.setSelected(true);
			break;
		case LOCAL_ALL:
			_localAllRadio.setSelected(true);
			break;
		case LOCAL_SUBSET:
			_localSubsetRadio.setSelected(true);
			break;
		}	
		
		// --- Layout
		this.getChildren().addAll(
				aliasBox, 
				new Text("Data Location:"),
				remoteBox, 
				localBox, 
				subsetBox);	
	}

	// Get the alias text
	public String getAlias() {
		return _aliasField.getText();
	}
	
	// Get the source location
	public SourceLocation getSourceLocation(){
		return (SourceLocation) _remoteLocalGroup.getSelectedToggle().getUserData();
	}
	
	// Get the data subset
	public int getDataSubset(){
		return Integer.parseInt(_localField.getText());
	}

}
