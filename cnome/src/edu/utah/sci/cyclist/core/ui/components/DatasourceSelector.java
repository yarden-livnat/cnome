package edu.utah.sci.cyclist.core.ui.components;

import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextBuilder;
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
		_localField = TextFieldBuilder.create()
               .prefWidth(150)
               	.minHeight(20)
               .text(new Integer(table.getDataSubset()).toString())
               .build();

		// Create the alias field
		_aliasField = TextFieldBuilder.create()
				.prefWidth(150)
				.minHeight(20)
				.text(table.getAlias()).build();

		// --- Alias Box
		HBox aliasBox = HBoxBuilder.create()
					.spacing(5)
					.children(TextBuilder.create().text("Alias:").build(),
							_aliasField)
					.build();
		
		// --- Remote box
		HBox remoteBox = HBoxBuilder.create()
				.spacing(5)
				.children(_remoteRadio = new RadioButton(),
						TextBuilder.create().text("Remote").build())
				.build();
		
		// --- Local box
		HBox localBox = HBoxBuilder.create()
				.spacing(5)
				.children(_localAllRadio = new RadioButton(),
						TextBuilder.create().text("Local (all)").build())
				.build();

		// --- Local subset box
		HBox subsetBox =  HBoxBuilder.create()
				.spacing(5)
				.children(_localSubsetRadio = new RadioButton(),
						TextBuilder.create().text("Local (subset):").build(),
						_localField)
				.build();
		
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
		this.getChildren().addAll(aliasBox, TextBuilder.create().text("Data Location:").build(), remoteBox, localBox, subsetBox);	
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
