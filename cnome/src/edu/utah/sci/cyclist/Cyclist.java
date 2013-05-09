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

import org.apache.log4j.Logger;

import edu.utah.sci.cyclist.controller.CyclistController;
import edu.utah.sci.cyclist.event.dnd.DnDIcon;
import edu.utah.sci.cyclist.event.notification.EventBus;
import edu.utah.sci.cyclist.event.notification.SimpleEventBus;
import edu.utah.sci.cyclist.view.MainScreen;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.PaneBuilder;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
		StackPane stack = new StackPane();
		Pane glass = PaneBuilder.create()
				.style("-fx-background-color: rgba(0, 100, 100, 0)")
				.mouseTransparent(true).
				build();
		
	 	MainScreen root = new MainScreen(primaryStage);
        
	 	stack.getChildren().addAll(root, glass);
	 	
	 	DnDIcon.getInstance().setGlass(glass);
	 	
		EventBus eventBus = new SimpleEventBus();
		CyclistController controller = new CyclistController(eventBus);
		controller.setScreen(root);
		
        Scene scene = new Scene(stack, 800, 600);
        
        scene.getStylesheets().add(Cyclist.class.getResource("assets/Cyclist.css").toExternalForm());
        
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
	}

}
