package edu.utah.sci.cyclist;

import org.apache.log4j.Logger;

import edu.utah.sci.cyclist.controller.CyclistController;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.event.notification.SimpleEventBus;
import edu.utah.sci.cyclist.view.MainScreen;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Cyclist extends Application {
	public static final String TITLE = "Cyclist";
    static Logger log = Logger.getLogger(Cyclist.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
	@Override
	public void start(Stage primaryStage) throws Exception {
		 	MainScreen root = new MainScreen(primaryStage);
	        
			EventBus eventBus = new SimpleEventBus();
			CyclistController controller = new CyclistController(eventBus);
			controller.setScreen(root);
			
	        Scene scene = new Scene(root, 800, 600);
	        
	        scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
	        
	        primaryStage.setTitle(TITLE);
	        primaryStage.setScene(scene);
	        primaryStage.show();
	}

}
