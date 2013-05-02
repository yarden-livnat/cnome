package edu.utah.sci.cyclist.view.components;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import edu.utah.sci.cyclist.Cyclist;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBuilder;
import javafx.scene.control.ProgressIndicatorBuilder;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.image.ImageViewBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.DirectoryChooser;
import javafx.stage.DirectoryChooserBuilder;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageBuilder;
import javafx.stage.Window;

public class DatasourceWizard extends VBox {

	
	// GUI elements
	private Stage            _dialog;
	private ComboBox<String> _sourceBox;
	private TextField        _nameField;
	private ImageView        _statusDisplay;

	private Map<String, DatasourceWizardPage> _panes;
	private DatasourceWizardPage _currentPage;	
	
	private String current = null;
	

	// This will have to be changed to a data source
	private ObjectProperty<String> selection = new SimpleObjectProperty<>();
	
	

	// * * * Default constructor creates new data source * * * //
	public DatasourceWizard() {
		createDialog(new String(""));
	}

	// * * * Constructor that edits existing source * * * *//
	public DatasourceWizard(String sourceProperty){
		createDialog(sourceProperty);		
	}
	
	// * * * Create the dialog * * * //
	private void createDialog(String sourceProperty){

		_dialog = StageBuilder.create()
				.title("Create or Edit Data Source")
				//.maxWidth(250).minWidth(250)
				//	.maxHeight(100).minHeight(95)
				.build();

		_dialog.initModality(Modality.WINDOW_MODAL);
		_dialog.setScene( createScene(_dialog, sourceProperty) );

	}
		
	// * * * Show the wizard * * * //
	public ObjectProperty<String> show(Window window) {
		_dialog.initOwner(window);
		_dialog.show();
		return selection;

	}
		
	// * * * Create scene creates the GUI * * * //
	private Scene createScene(final Stage dialog, String sourceProperty) {
		
		// Get the name of the source, if we have one
		String sourceName = sourceProperty;
		if (sourceName == null) sourceName = "";
		
		// The user-specified name of the table
		HBox nameBox = HBoxBuilder.create()
				.spacing(5)
				.alignment(Pos.CENTER)
				.children(
						TextBuilder.create().text("Name:").build(),
						_nameField = TextFieldBuilder.create()
						.prefWidth(150)
						.text(sourceName)
						.build())
						.build();
			
		// The selector for type of connection
		final Pane pane = new Pane();
		pane.prefHeight(200);
		_panes = createPanes(sourceProperty);
		
		ComboBox<String> cb = ComboBoxBuilder.<String>create()
				.prefWidth(200)
				.build();
		HBox.setHgrow(cb,  Priority.ALWAYS);
		cb.getSelectionModel().selectedItemProperty()
		.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				DatasourceWizardPage page = _panes.get(newValue);
				if (page != null) {
					pane.getChildren().clear();
					pane.getChildren().add(page.getNode());
					_currentPage = page;
				}
			}
		});
	
		cb.setItems(FXCollections.observableArrayList(_panes.keySet()));
	//	String type = ds.getProperties().getProperty("type");
		String type = null;
		if (type == null) type = "MySQL";
		cb.getSelectionModel().select(type);
		
		// The ok/cancel buttons
		Button ok;
		HBox buttonsBox = HBoxBuilder.create()
				.spacing(10)
				.alignment(Pos.CENTER_RIGHT)
				.padding(new Insets(5))
				.children(					
						// Test Connection
						ButtonBuilder.create()
						.text("Test Connection")
						.onAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								//	CyclistDataSource ds = _currentPage.getDataSource();
								String ds = "connection";
								testConnection(ds);
							};
						})
						.build(),
						_statusDisplay = ImageViewBuilder.create().build(),
						ProgressIndicatorBuilder.create().progress(-1).maxWidth(8).maxHeight(8).visible(false).build(),	
						new Spring(),						
						// Cancel
						ButtonBuilder.create()
						.text("Cancel")
						.onAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								dialog.hide();
							};
						})
						.build(),

						// OK
						ok = ButtonBuilder.create()
						.text("Ok")
						.onAction(new EventHandler<ActionEvent>() {	
							@Override
							public void handle(ActionEvent arg0) {
								System.out.println("Create & return a new data source");
								dialog.hide();
							};
						})
						.build()	
						)
						.build();
		HBox.setHgrow(buttonsBox,  Priority.ALWAYS);


		// The vertical layout of the whole wizard
		VBox header = VBoxBuilder.create()
				.spacing(10)
				.padding(new Insets(5))
				.children(nameBox, 
						cb,
						pane, 
						buttonsBox)
						.build();	


		Scene scene = new Scene(
				VBoxBuilder.create()
					.spacing(5)
					.padding(new Insets(5))
					.id("datasource-wizard")
					.children(header)
					.build()
				);
		
        scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
		return scene;
	}
	
	//private Map<String, DatasourceWizardPage> createPanes(CyclistDataSource ds) {
	private Map<String, DatasourceWizardPage> createPanes(String ds) {
		Map<String, DatasourceWizardPage> panes = new HashMap<>();

		panes.put("MySQL", new MySQLPage(ds));
		panes.put("SQLite", new SQLitePage(ds));
		return panes;
	}


	//private void testConnection(CyclistDataSource ds) {
	private void testConnection(String ds) {
		System.out.println("Test Connection");

		//	_indicator.setVisible(true);
		/*try (Connection conn = ds.getConnection()) {
			System.out.println("connection ok");
			_status.setImage(Resources.getIcon("ok"));
		} catch (Exception e) {
			System.out.println("connection failed");
			_status.setImage(Resources.getIcon("error"));
		}*/
		//		_indicator.setVisible(false);

	}
	
}
