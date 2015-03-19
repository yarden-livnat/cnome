package edu.utexas.cycic;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import edu.utah.sci.cyclist.core.event.dnd.DnD;
import edu.utah.sci.cyclist.core.ui.components.ViewBase;
/**
 * A view used to build and develop institutions for the simulation 
 * currently being built. 
 * @author Robert
 *
 */
public class InstitutionCorralView extends ViewBase{
	/**
	 * 
	 */
	static instituteNode workingInstitution = null;
	
	/**
	 * 
	 */
	MemoryScroll facilityScroll = new MemoryScroll(35, 35, 65);
	
	/**
	 * 
	 */
	static Pane institutionPane = new Pane();
	{
		setPrefHeight(375);
		setPrefWidth(630);
		setOnDragDropped(new EventHandler<DragEvent>(){
			public void handle(DragEvent event){
				if(event.getDragboard().hasContent(DnD.VALUE_FORMAT)){
					instituteNode institute = new instituteNode();
					institute.type = event.getDragboard().getContent(DnD.VALUE_FORMAT).toString();
					institute.type.trim();
					for (int i = 0; i < DataArrays.simInstitutions.size(); i++){
						if(DataArrays.simInstitutions.get(i).institName.equalsIgnoreCase(institute.type)){
							institute.institStruct = DataArrays.simInstitutions.get(i).institStruct;
							institute.archetype = DataArrays.simInstitutions.get(i).institArch;
						}
					}
					event.consume();
					institute.name = "";
					workingInstitution = institute;
					FormBuilderFunctions.formArrayBuilder(institute.institStruct, institute.institData);
					institute.institutionShape = InstitutionShape.addInst((String)institute.name, institute);
					institute.institutionShape.setLayoutX(event.getX());
					institute.institutionShape.setLayoutY(event.getY());
					institute.institutionShape.text.setLayoutX(event.getX()+institute.institutionShape.getRadiusX()*0.2);
					institute.institutionShape.text.setLayoutY(event.getY()+institute.institutionShape.getRadiusY()*0.2);
					DataArrays.institNodes.add(institute);
					institutionPane.getChildren().addAll(institute.institutionShape, institute.institutionShape.text, institute.institutionShape.menuBar);
				} else {
					event.consume();
				}
			}
		});
		setOnDragOver(new EventHandler <DragEvent>(){
			public void handle(DragEvent event){
				event.acceptTransferModes(TransferMode.ANY);
			}
		});
	}
	/**
	 * Initiates a new window for building and modifying institutions. 
	 */
	public InstitutionCorralView(){
		super();
	
		if (DataArrays.simInstitutions.size() < 1) {
			String string;
			for(int i = 0; i < XMLReader.institutionList.size(); i++){
				StringBuilder sb = new StringBuilder();
				StringBuilder sb1 = new StringBuilder();
				Process proc;
				try {
					proc = Runtime.getRuntime().exec("cyclus --agent-schema "+XMLReader.institutionList.get(i)); 
					BufferedReader read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
					while((string = read.readLine()) != null){
						sb.append(string);
					}
					Process proc1 = Runtime.getRuntime().exec("cyclus --agent-annotations "+XMLReader.institutionList.get(i));
					BufferedReader read1 = new BufferedReader(new InputStreamReader(proc1.getInputStream()));
					while((string = read1.readLine()) != null){
						sb1.append(string);
					}
					institutionStructure tempInst = new institutionStructure();
					tempInst.institName = XMLReader.institutionList.get(i).replace(":", " ").trim();
					tempInst.institStruct = XMLReader.annotationReader(sb1.toString(), XMLReader.readSchema(sb.toString()));
					DataArrays.simInstitutions.add(tempInst);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
			
		TextField institName = new TextField();
		
		final ComboBox<String> typeOptions = new ComboBox<String>();
		typeOptions.getItems().clear();
		for(int i = 0; i < DataArrays.simInstitutions.size(); i++){
			typeOptions.getItems().add(DataArrays.simInstitutions.get(i).institName);
		}
		
		Button institButton = new Button("Add Institution");
		/** TODO This literally does nothing. */
		institButton.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				instituteNode tempInstit = new instituteNode();
				tempInstit.name = institName.getText();
				workingInstit = tempInstit;
				for(int i = 0; i < DataArrays.simInstitutions.size(); i++){
					if(DataArrays.simInstitutions.get(i).institName.equalsIgnoreCase((String) typeOptions.getValue())){
						tempInstit.institStruct = DataArrays.simInstitutions.get(i).institStruct;
						tempInstit.archetype = DataArrays.simInstitutions.get(i).institArch;
					}
				}
				tempInstit.type = (String) typeOptions.getValue();
				FormBuilderFunctions.formArrayBuilder(workingInstit.institStruct, workingInstit.institData);
				DataArrays.institNodes.add(tempInstit);
				new Label("Name");
				FormBuilderFunctions.institNameBuilder(workingInstit);				
				typeLabel.setText(workingInstit.type);
			}
		});
		
		Button updateScroll = new Button("UPDATES!");
		
		updateScroll.setOnAction(new EventHandler<ActionEvent>(){
			public void handle(ActionEvent e){
				facilityScroll.updateFac();
			}
		});
		
		topGrid.add(new Label("New Institution"), 0, 0);
		topGrid.add(institName, 1, 0);
		topGrid.add(typeOptions, 2, 0);
		topGrid.add(institButton, 3, 0);
		topGrid.add(updateScroll, 4, 0);
		
				
		
		//ComboBox for adding a new facility to the initial facility array of the institution.
		
		// Building the grids for the views.
		topGrid.setHgap(10);
		topGrid.setVgap(5);
		
		ScrollPane scroll = new ScrollPane();
		scroll.autosize();
		Pane nodesPane = new Pane();
		nodesPane.autosize();
		for(int i = 0; i < DataArrays.simInstitutions.size(); i++){
			InstitutionEllipse instit = new InstitutionEllipse();
			instit.setFill(Color.web("#CF5300"));
			instit.setLayoutX(60 + (i*110));
			instit.setLayoutY(40);
			instit.setRadiusX(50);
			instit.setRadiusY(30);
			instit.setStroke(Color.BLACK);
			instit.text.setText(DataArrays.simInstitutions.get(i).institName);
			instit.text.setWrapText(true);
			instit.text.setMaxWidth(instit.getRadiusX()*1.6);
			instit.text.setLayoutX(instit.getLayoutX()-instit.getRadiusX()*0.8);
			instit.text.setLayoutY(instit.getLayoutY()-instit.getRadiusY()*0.7);	
			instit.text.setTextAlignment(TextAlignment.CENTER);
			instit.text.setMouseTransparent(true);
			nodesPane.getChildren().addAll(instit, instit.text);
		}
		scroll.setContent(nodesPane);
		
		VBox institBox = new VBox();
		institBox.autosize();
		institBox.getChildren().addAll(topGrid, scroll, institutionPane);	
		
		
		HBox corralBox = new HBox();
		corralBox.getChildren().addAll(institBox, facilityScroll);
		setContent(corralBox);

	}
	
	private GridPane topGrid = new GridPane(){
		{
			autosize();
		}
	};
	private Label typeLabel = new Label(){
		{
			autosize();
		}
	};
	static instituteNode workingInstit;
	public static String TITLE;

	/**
	 * This function takes a constructed data array and it's corresponding 
	 * institution structure array and creates a form for the structure 
	 * and data arrays.
	 * @param facArray This is the structure of the data array. Included 
	 * in this array should be all of the information needed to fully 
	 * describe the data structure of a institution.
	 * @param dataArray The empty data array that is associated with 
	 * this institution. It should be built to match the structure
	 * of the institution structure passed to the form. 
	 */

}
