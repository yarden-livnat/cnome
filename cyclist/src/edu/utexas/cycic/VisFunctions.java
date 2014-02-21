package edu.utexas.cycic;

import java.util.ArrayList;

import javafx.scene.effect.Bloom;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Light.Distant;
import javafx.scene.effect.Lighting;

/**
 * Contains all of the generic and misc visualization functions 
 * for the CYCIC fuel cycle simulator.
 * @author Robert
 *
 */
public class VisFunctions {
	/**
	 * Converts a string to a color.
	 * @param string String to be converted.
	 * @return rbg triple stored in an ArrayList<Integer>
	 */
	public static ArrayList<Integer> stringToColor(String string){
		String hashCode;
		int red=0;
		int blue=0;
		int green=0;
		int p=0;
		ArrayList<Integer> rgbArray = new ArrayList<Integer>();
		
		hashCode = Integer.toString(Math.abs(string.hashCode()));
		if(hashCode.length()>(p+3)){
			red=Integer.parseInt(hashCode.substring(p,p+3));
			p+=3;
			if(hashCode.length()>(p+3)){
				green=Integer.parseInt(hashCode.substring(p,p+3));
				p+=3;
				if(hashCode.length()>(p+3)){
					blue=Integer.parseInt(hashCode.substring(p,p+3));
					p+=3;
				}else{
					blue=Integer.parseInt(hashCode.substring(p,hashCode.length()));
				}	
			}else{
				green=Integer.parseInt(hashCode.substring(p,hashCode.length()));
			}
		}else{
			red=Integer.parseInt(hashCode);
		}
		
		while (red>255) {
			red-=256;
		}
		while (green>255) {
			green-=256;
		}
		while (blue>255) {
			blue-=256;
		}
		
		rgbArray.add(red);
		rgbArray.add(green);
		rgbArray.add(blue);
		return rgbArray;
	}
	
	/**
	 * Test to determine which font color to use, based on the color
	 * of the node the text belongs to.
	 * @param array rbg color ArrayList<Integer>
	 * @return
	 */
	public static boolean colorTest(ArrayList<Integer> array){
		int tally = 0;
		for(int i = 0; i < array.size(); i++){
			if(array.get(i) < 120){
				tally +=1;
			}
		}
		if(tally > 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Test determines if the a color should be made darker or lighter
	 * based on the initial color being used.
	 * @param color rbg Integer to test.
	 * @return
	 */
	public static double colorMultiplierTest(Integer color){
		if(color < 185){
			return 1.3;
		}else{
			return 0.7;
		}
	}
	
	/**
	 * Adjusts the color of a node when highlighted
	 */
	static ColorAdjust colorAdjust = new ColorAdjust(){
		{
			setBrightness(0.2);
			setHue(-0.05);
		}
	};
	
	/**
	 * Depricated
	 */
	static Bloom bloom = new Bloom(){
		{
			setThreshold(1.0);
		}
	};
	
	// Really cool effect, not ready for testing yet.
	static Distant light = new Distant(){
		{
			setAzimuth(-90);
			setElevation(55);
		}
	};
	static Lighting lighting = new Lighting(){
		{
			setLight(light);
			setSurfaceScale(2);
		}
	};
	
	/**
	 * Code used to link a facilityCircle and a marketCircle in the CYCIC pane.
	 * @param source facilityCircle that starts the line.
	 * @param target marketCircle that ends the line.
	 */
	static void linkNodesSimple(FacilityCircle source, MarketCircle target){
		MarketCircle markIndex = null;
		nodeLink link = new nodeLink();
		link.source = source;
		link.target = target;
		link.line.setStartX(source.getCenterX());
		link.line.setStartY(source.getCenterY());
		link.line.setEndX(target.getCenterX());
		link.line.setEndY(target.getCenterY());

		for(int i = 0; i < CycicScenarios.workingCycicScenario.marketNodes.size(); i++){
			if(CycicScenarios.workingCycicScenario.marketNodes.get(i) == target){
				markIndex = CycicScenarios.workingCycicScenario.marketNodes.get(i);
			}
		}
		CycicScenarios.workingCycicScenario.Links.add(link);
		Cycic.pane.getChildren().addAll(link.line);
		link.line.toBack();
	}
	
	/**
	 * Reloads the CYCIC pane when changes have been made to it's contents.
	 */
	static void reloadPane(){
		Cycic.pane.getChildren().remove(0, Cycic.pane.getChildren().size());
		for(int i = 0; i < CycicScenarios.workingCycicScenario.FacilityNodes.size(); i++){
			Cycic.pane.getChildren().add(CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle);
			Cycic.pane.getChildren().add(CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle.menu);
			Cycic.pane.getChildren().add(CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle.text);
			Cycic.pane.getChildren().add(CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle.image);
			for(int ii = 0; ii < CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle.childrenList.size(); ii++){
				Cycic.pane.getChildren().add(CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle.childrenList.get(ii));
				Cycic.pane.getChildren().add(CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle.childrenList.get(ii).menu);
				Cycic.pane.getChildren().add(CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle.childrenList.get(ii).text);
				Cycic.pane.getChildren().add(CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle.childrenList.get(ii).image);
			}
			for(int n = 0; n < CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle.childrenLinks.size(); n++){
				Cycic.pane.getChildren().add(CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle.childrenLinks.get(n).line);
				CycicScenarios.workingCycicScenario.FacilityNodes.get(i).cycicCircle.childrenLinks.get(n).line.toBack();
			}
		}
		for(int i = 0; i < CycicScenarios.workingCycicScenario.marketNodes.size(); i++){
			Cycic.pane.getChildren().add(CycicScenarios.workingCycicScenario.marketNodes.get(i));
			Cycic.pane.getChildren().add(CycicScenarios.workingCycicScenario.marketNodes.get(i).menu);
			Cycic.pane.getChildren().add(CycicScenarios.workingCycicScenario.marketNodes.get(i).text);			
		}
		for(int n = 0; n < CycicScenarios.workingCycicScenario.Links.size(); n++){
			Cycic.pane.getChildren().add(CycicScenarios.workingCycicScenario.Links.get(n).line);
			CycicScenarios.workingCycicScenario.Links.get(n).line.toBack();
		}
	}
}
