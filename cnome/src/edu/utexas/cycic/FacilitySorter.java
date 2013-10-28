package edu.utexas.cycic;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import edu.utah.sci.cyclist.ui.components.ViewBase;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;

public class FacilitySorter extends ViewBase {

	public FacilitySorter(){
		super();
		setWidth(780);
		setMaxWidth(900);
		setMinWidth(780);
		setHeight(300);
		setMaxHeight(300);
		init ();
	}

	static BorderPane pane= new BorderPane();
	static Pane display=new Pane(){
		{
			setMinSize(250,150);
		}
	};
	static ArrayList<Object> structure=new ArrayList<>();
	static Integer facilityIndex; 

	private void init (){
		//initialize most variables
		final Text list=new Text ();
		HBox topBox= new HBox();
		final HBox searchBox=new HBox(){
			{
				setSpacing(10);
				setMaxWidth(600);
			}
		};
		searchBox.setMaxHeight(35);
		searchBox.setStyle("-fx-border-color: black;");

		final ComboBox<String> facilityList=new ComboBox<String>();
		facilityList.setVisibleRowCount(5);
		searchBox.getChildren().add(facilityList);

		final Label number=new Label();
		searchBox.getChildren().add(number);

		final Slider numberShowen=new Slider(0,0,0);
		numberShowen.setShowTickMarks(true);
		numberShowen.setShowTickLabels(true);
		numberShowen.setMinorTickCount(0);
		numberShowen.setValue(numberShowen.getMax());
		numberShowen.setMajorTickUnit(1);
		numberShowen.setSnapToTicks(true);
		numberShowen.valueProperty().addListener(new ChangeListener<Number>(){
			public void changed(ObservableValue<? extends Number> ov,Number old_val, Number new_val) {
				number.setText("number of nodes shown:" +Integer.toString(new_val.intValue()));
				display.getChildren().clear();
				for (int iii=0;iii<new_val.intValue();iii++){
					display.getChildren().add(CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityClones.get(iii).sorterCircle);
					display.getChildren().add(CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityClones.get(iii).sorterCircle.text);
				}
			}
		});
		searchBox.getChildren().add(numberShowen);

		final Label type=new Label();
		searchBox.getChildren().add(type);

		final VBox criteria = new VBox(){
			{
				setSpacing(10);
			}
		};
		final Button add= new Button();
		add.setText("add Criteria");

		final VBox addMore= new VBox();

		final ArrayList<Object>data=new ArrayList<>();
		final ArrayList<Object> filt=new ArrayList<>();
		//variables default info


		final Button start=new Button();
		start.setText("Start");
		criteria.setStyle("-fx-border-color: black;");
		criteria.setPrefHeight(getHeight());
		addMore.setSpacing(10);

		topBox.getChildren().add(searchBox);
		topBox.getChildren().add(criteria);
		//top searchbox information

		topBox.setSpacing(50);

		//right criteria column 
		//drop down for all parent nodes
		for(int i =0; i < CycicScenarios.workingCycicScenario.FacilityNodes.size(); i++){
			facilityList.getItems().add((String) CycicScenarios.workingCycicScenario.FacilityNodes.get(i).name);
		}

		//read chosen facility parent node, update slider bar, type, and searchlist 
		facilityList.valueProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				//find the location of chosen node in the data list
				criteria.getChildren().clear();
				structure.clear();
				display.getChildren().clear();
				data.clear();
				for(int i = 0; i < CycicScenarios.workingCycicScenario.FacilityNodes.size(); i++){
					if(newValue == CycicScenarios.workingCycicScenario.FacilityNodes.get(i).name){
						facilityIndex = i;
						break;
					}
				}
				numberShowen.setMax(CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityClones.size());
				//update data array
				for (int i=0;i<CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityStructure.size();i++){
					ArrayList<Object>element=new ArrayList<>();
					for (int ii=0;ii<CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityClones.size();ii++){
						element.add(CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityClones.get(ii).facilityData.get(i));
					}
					data.add(element);
				}
				for (int i=0;i<numberShowen.getMax();i++){
					display.getChildren().add(CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityClones.get(i).sorterCircle);
					display.getChildren().add(CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityClones.get(i).sorterCircle.text);
				}
				number.setVisible(true);
				number.setText("number of nodes shown:" +Integer.toString(CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityClones.size()));

				//update information in searchbox
				type.setText("Type: "+CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityType);

				formSort(CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityStructure,data,structure);
				//System.out.println(data);
				//System.out.println(CycicScenarios.workingCycicScenario.FacilityNodes.get(i).facilityStructure);
				//System.out.println(structure);
				criteria.getChildren().add(add);
				criteria.getChildren().add(addMore);
				criteria.getChildren().add(start);


			}
		});


		//add button function. add addmore and filter button
		add.setOnMouseClicked(new EventHandler <MouseEvent>(){
			@Override
			public void handle(MouseEvent e) {
				final HBox detailBox=new HBox();
				detailBox.setSpacing(5);
				ComboBox<String> detail=new ComboBox<String>();
				//data
				final ArrayList<ArrayList> listed=new ArrayList<>();
				//stucture for the term
				final ArrayList<ArrayList> struc=new ArrayList<>();
				//
				final ArrayList<Object> order=new ArrayList<>();
				for (int k=0;k<structure.size();k++){
					ArrayList<ArrayList>detailInfo=(ArrayList<ArrayList>)structure.get(k);
					detail.getItems().add((String) detailInfo.get(0).get(0));
					order.add((String) detailInfo.get(0).get(0));
					listed.add(detailInfo.get(1));
					struc.add(detailInfo.get(0));
				}
				detail.setVisibleRowCount(7);
				detailBox.getChildren().add(detail);
				addMore.getChildren().add(detailBox);

				//		System.out.println(CycicScenarios.workingCycicScenario.FacilityNodes.get(0).facilityData);
				//		System.out.println(data);
				detail.valueProperty().addListener(new ChangeListener <String>(){
					@Override
					public void changed(ObservableValue<? extends String> arg0,	String oldValue, final String newValue) {
						for (int i = 0; i < order.size(); i++){
							if(order.get(i) == newValue){
								if (struc.get(i).get(2).equals("String")){
									if (detailBox.getChildren().size()>=1){
										detailBox.getChildren().remove(1);
									}
									final ComboBox <String> information=new ComboBox<>();
									for (int ii=0;ii<((ArrayList<Object>) listed.get(i)).size();ii++){
										ArrayList<ArrayList>newList=(ArrayList<ArrayList>)listed.get(i);
										if(!(information.getItems().contains(newList.get(ii).get(0)))){
											information.getItems().add(newList.get(ii).get(0).toString());
										}

									}
									detailBox.getChildren().add(information);
									information.valueProperty().addListener(new ChangeListener <String>(){

										@Override
										public void changed(ObservableValue<? extends String> arg,
												String oldProperty, String newProperty) {
											ArrayList <Object>subfilt=new ArrayList<>();
											subfilt.add(newValue);
											subfilt.add(newProperty);
											filt.add(subfilt);	
										}
									});
								}
								else {
									VBox range=new VBox();
									HBox lB=new HBox();
									HBox UB =new HBox();
									Text lbNum=new Text();
									Text uBNum=new Text();
									Button confirm=new Button();
									confirm.setText("confirm");
									lbNum.setText("LB");
									uBNum.setText("UB");
									lB.getChildren().add(lbNum);
									final TextField lBValue=new TextField();
									lBValue.setPromptText("Number Only");
									final TextField uBValue=new TextField();
									uBValue.setPromptText("Number Only");

									lB.getChildren().add(lBValue);
									UB.getChildren().add(uBNum);
									UB.getChildren().add(uBValue);
									range.getChildren().add(lB);
									range.getChildren().add(UB);
									range.getChildren().add(confirm);
									confirm.setOnAction(new EventHandler<ActionEvent>() {
										@Override
										public void handle(ActionEvent e) {
											if (!(isInteger(lBValue.getText()))){
												lBValue.setText("PLEASE!!!NUMBERS!!!");
											} else if (!(isInteger(uBValue.getText()))){
												uBValue.setText("PLEASE!!!NUMBERS!!!");
											} else if (Integer.parseInt(lBValue.getText())>Integer.parseInt(uBValue.getText())){
												lBValue.setText("PLEASE ENTER CORRECTLY");
												uBValue.setText("PLEASE ENTER CORRECTLY");
											}else {
												int lowvalue;
												int upvalue;
												lowvalue=Integer.parseInt(lBValue.getText());
												upvalue=Integer.parseInt(uBValue.getText());
												ArrayList <Object>Subfilt=new ArrayList<>();
												Subfilt.add(newValue);
												Subfilt.add(lowvalue);
												Subfilt.add(upvalue);
												filt.add(Subfilt);
											}
										}
									});
									if (detailBox.getChildren().size()>=1){
										detailBox.getChildren().remove(1);
									}
									detailBox.getChildren().add(range);

								}
							}
						}
					}

				});
				start.setOnMouseClicked(new EventHandler <MouseEvent>(){

					@Override
					public void handle(MouseEvent event) {
						display.getChildren().clear();
						System.out.println(filt);
						ArrayList<Object> displaylist=new ArrayList<>();
						ArrayList<ArrayList> struc=new ArrayList<>();
						for (int ii=0;ii<CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityClones.size();ii++){
							facilityNode dataarray=CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityClones.get(ii);
							for (int iv=0;iv<CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityStructure.size();iv++){
								if (!(((ArrayList) CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityStructure.get(iv)).get(1) instanceof ArrayList)){
									struc.add((ArrayList) CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityStructure.get(iv));
								} else {
									ArrayList<ArrayList> subtruc =(ArrayList) CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityStructure.get(iv);
									ArrayList<ArrayList> realsub=(ArrayList)subtruc.get(1);
									struc.add(realsub);
								}
							}
							boolean truth=false;
							//logic error occur
							for (int i=0;i<filt.size();i++){
								ArrayList <String>filter=(ArrayList)filt.get(i);
								System.out.println(filter);
								int lev=-1;
								int ord=-1;
								int ordernum=-1;
								boolean found=false;
								for (int l=0;l<struc.size();l++){
									if (!(found)){
										if ((struc.get(l).get(0)instanceof ArrayList)){
											lev++;
											ord=-1;
											for (int ll=0;ll<struc.get(l).size();ll++){
												ord++;
												if (((ArrayList) struc.get(l).get(ll)).get(0).equals(filter.get(0))){
													found=true;
													ordernum=ord;
												}
											}

										}else{
											lev++;
											ord=-1;
											if (struc.get(l).get(0).equals(filter.get(0))){
												found=true;
											}
										}
									} else {
										System.out.println(lev+"+"+ordernum);
									}
								}
								if (filter.size()<=2){
									if (ordernum==-1){ 	
										System.out.println(lev);
										System.out.println(ii);
										if ((dataarray.facilityData.get(lev).equals(filter.get(1)))){
											truth=true;
										}
									} else if(ordernum>-1){
										if ((((ArrayList) CycicScenarios.workingCycicScenario.FacilityNodes.get(ii).facilityData.get(lev)).get(ordernum).equals(filter.get(1))))
										{
											truth=true;
										}
									}
								} else if (filter.size()>2){

									if (ordernum==-1){
										// System.out.println((ArrayList) CycicScenarios.workingCycicScenario.FacilityNodes.get(ii).facilityData.get(lev));
										if (((ArrayList) CycicScenarios.workingCycicScenario.FacilityNodes.get(ii).facilityData.get(lev)).equals(null)){
											truth=false;

										}
										else if (!((ArrayList) CycicScenarios.workingCycicScenario.FacilityNodes.get(ii).facilityData.get(lev)).toString().equals("[]")) {
											System.out.println( (CycicScenarios.workingCycicScenario.FacilityNodes.get(ii).facilityData.get(lev)));
											//System.out.println(filter.get(2));
											if (((Integer.parseInt((CycicScenarios.workingCycicScenario.FacilityNodes.get(ii).facilityData.get(lev).toString())))

													<Integer.parseInt(filter.get(2)))&&
													Integer.parseInt(( CycicScenarios.workingCycicScenario.FacilityNodes.get(ii).facilityData.get(lev).toString()))
													>Integer.parseInt(filter.get(1))){
												truth=true;
											}
										}
									}


									else {
										if (((ArrayList) CycicScenarios.workingCycicScenario.FacilityNodes.get(ii).facilityData.get(lev)).get(ordernum).equals("")){
											truth=false;
										}
										else{
											if ((Integer.parseInt((String) ((ArrayList) CycicScenarios.workingCycicScenario.FacilityNodes.get(ii).facilityData.get(lev)).get(ordernum))

													<Integer.parseInt(filter.get(2))&&
													Integer.parseInt((String) ((ArrayList) CycicScenarios.workingCycicScenario.FacilityNodes.get(ii).facilityData.get(lev)).get(ordernum))
													>Integer.parseInt(filter.get(1)))){
												truth=true;
											}
										}
									}
								}


							}							
							if (truth==true){
								displaylist.add(CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityClones.get(ii));
								display.getChildren().add(CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityClones.get(ii).sorterCircle);
								display.getChildren().add(CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityClones.get(ii).sorterCircle.text);
							}

						}
						System.out.println(displaylist.toString());
						System.out.println(struc);
						System.out.println(CycicScenarios.workingCycicScenario.FacilityNodes.get(facilityIndex).facilityClones.get(0).facilityData);
					}
				});

				detailBox.getChildren().add(list);
			}
		});


		pane.setTop(topBox);
		pane.setLeft(display);

		setContent (pane);

	}
	public static boolean isInteger(String s) {
		try { 
			Integer.parseInt(s); 
		} catch(NumberFormatException e) { 
			return false; 
		}
		// only got here if we didn't return false
		return true;
	}

	//need fix the data for this
	protected void formSort(ArrayList<Object> struc, ArrayList<Object> data,ArrayList<Object> facilitySortArray){
		for (int i=0;i<struc.size();i++){
			if (struc.size()>2){
				if (struc.get(i) instanceof ArrayList && struc.get(0) instanceof ArrayList){
					formSort((ArrayList<Object>)struc.get(i),(ArrayList<Object>)data.get(i),facilitySortArray);
				}else if (i==0){
					if (struc.get(2)=="oneOrMore"||struc.get(2)=="zeroOrMore"){
						ArrayList <Object> newData=new ArrayList<>();
						ArrayList<Object> newStruc=(ArrayList<Object>)struc.get(1);
						for (int ii=0;ii<newStruc.size();ii++){
							ArrayList<Object> element=new ArrayList<>();
							for (int iii=0;iii<data.size();iii++){
								ArrayList<Object> subData=(ArrayList<Object>) data.get(iii);
								ArrayList<Object>dataCol=(ArrayList<Object>)subData.get(0);
								element.add(dataCol.get(ii));
							}
							newData.add(element);
						}
						formSort(newStruc,newData,facilitySortArray);
					}else if (struc.get(1) instanceof ArrayList){
						ArrayList <Object> newData=new ArrayList<>();
						ArrayList<Object> newStruc=(ArrayList<Object>)struc.get(1);
						for (int ii=0;ii<newStruc.size();ii++){
							ArrayList<Object> element=new ArrayList<>();
							for (int iii=0;iii<data.size();iii++){
								ArrayList<Object> subData=(ArrayList<Object>) data.get(iii);
								ArrayList<Object>dataCol=(ArrayList<Object>)subData.get(0);
								element.add(dataCol.get(ii));
							}
							newData.add(element);
						}
						formSort(newStruc,newData,facilitySortArray);

					}else {
						ArrayList<Object> element=new ArrayList<Object>();
						element.add(struc);
						element.add(data);
						if (!(facilitySortArray.contains(element))){
							facilitySortArray.add(element);
						}	
					}
				}
			} else formSort((ArrayList<Object>)struc.get(i),(ArrayList<Object>)data.get(i),facilitySortArray);
		}
	}	
}