package edu.utah.sci.cyclist.core.ui.wizards;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.BeanProperty;
import org.controlsfx.property.BeanPropertyUtils;

import edu.utah.sci.cyclist.Cyclist;
import edu.utah.sci.cyclist.core.model.Preferences;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class PreferencesWizard extends VBox {
	
	private Stage _dialog;
	private ObjectProperty<Boolean> _selection =  new SimpleObjectProperty<Boolean>();
	private Preferences _preferences = Preferences.getInstance();
    
	public ObjectProperty<Boolean> show(Window window) {
		_dialog.initOwner(window);
		_dialog.show();
		_dialog.setX(window.getX() + (window.getWidth() - _dialog.getWidth())*0.5);
		_dialog.setY(window.getY() + (window.getHeight() - _dialog.getHeight())*0.5);
		return _selection;
	}
	
	public PreferencesWizard() {	
		createDialog();
	}
	
	private void createDialog(){
		_dialog = new Stage();
		_dialog.setTitle("Preferences");
		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog) );
		_dialog.centerOnScreen();
		_selection.setValue(false);
	}
	
	private Scene createScene(final Stage dialog) {
		
		ObservableList<PropertySheet.Item> customList = FXCollections.observableArrayList();
		ObservableList<PropertySheet.Item> _beansProperties =  BeanPropertyUtils.getProperties(_preferences);
		 
		for(PropertySheet.Item item : _beansProperties){
			String displayName = convertNameToDisplayFormat(item.getName());
			customList.add(new CustomPropertyItem(displayName, item.getValue(),item.getName()));
		}
		PropertySheet propertySheet = new PropertySheet(customList);
		propertySheet.setSearchBoxVisible(false);
		propertySheet.setModeSwitcherVisible(false);
		propertySheet.setMinWidth(350);
		VBox.setVgrow(propertySheet, Priority.ALWAYS);
		
		
		HBox buttons = new HBox();
		buttons.setSpacing(10);
		buttons.setPadding(new Insets(5));
		buttons.setAlignment(Pos.CENTER_RIGHT);
		
		Button cancel = new Button("Cancel");
		cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dialog.close();
			}
		});
		
		Button ok = new Button("Ok");
		ok.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				
				ObservableList<PropertySheet.Item>items =  propertySheet.getItems();
				for(PropertySheet.Item item:items){
					for(PropertySheet.Item beanItem: _beansProperties){
						if(((CustomPropertyItem)item).getPropertyName().equals(beanItem.getName())){
							PropertyDescriptor property = ((BeanProperty)beanItem).getPropertyDescriptor();
							try {
								property.getWriteMethod().invoke(_preferences, item.getValue());
							} catch (IllegalAccessException |IllegalArgumentException |InvocationTargetException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
						}
					}
				}
				_selection.setValue(!_selection.getValue());
				dialog.close();
			}
		});
		
		buttons.getChildren().addAll(cancel,ok);
		
		VBox mainVbox = new VBox();
		mainVbox = new VBox();
		mainVbox.setSpacing(5);
		mainVbox.setPadding(new Insets(5));
		mainVbox.getChildren().addAll(propertySheet,buttons);
		
		Scene scene = new Scene(mainVbox);
		
        scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
        
		return scene;
	}
	
	/*
	 * Converts a property name retrieved by the BeanPropertyUtils (in the format of: "defaultServer" for example),
	 * to a format which would be displayed in the dialog. e.g: defaultServer -> Default Server:
	 * @param : String name - the property name to convert.
	 * @return String - the converted name in the display format.
	 */
	private String convertNameToDisplayFormat(String name){
		 String dispalyName = "";
		 int lastFound = 0;
		 Pattern p = Pattern.compile("[A-Z]");
		 Matcher m = p.matcher(name);
		 //First match
		 while(m.find()){
			 dispalyName += name.substring(lastFound,m.start())+" ";
			 dispalyName = dispalyName.substring(0, lastFound) + dispalyName.substring(lastFound, lastFound+1).toUpperCase() + dispalyName.substring(lastFound+1, dispalyName.length());
			 lastFound = m.start();
		 }
		 dispalyName += name.substring(lastFound,name.length())+":";
		 
		 return dispalyName;
	}
	
	/*
	 * A class which extends the PropertySheet.Item.
	 * Enables saving the name and the property name of the item.
	 * 
	 */
	public class CustomPropertyItem implements PropertySheet.Item {

        private String _name;
        private Object _value;
        private String _propertyName;
        
        public CustomPropertyItem(String key, Object value, String propertyName) {
            _name  = key;
            _value = value;
            _propertyName = propertyName;
        }
        
        public String getPropertyName(){
        	return _propertyName;
        }
        
        @Override public Class<?> getType() {
            return _value.getClass();
        }

        @Override public String getCategory() {
            return null;
        }

        @Override public String getName() {
            return _name;
        }

        @Override public String getDescription() {
            return null;
        }

        @Override public Object getValue() {
            return _value;
        }

        @Override public void setValue(Object value) {
            _value = value;
        }
        
    }
	
}
