package edu.utexas.cycic;

import java.util.ArrayList;

public class CycicScenarios {
	ArrayList<DataArrays> cycicScenarios = new ArrayList<DataArrays>();
	DataArrays workingCycicScenario;
	
	ArrayList<DataArrays> getScenarios(){
		return this.cycicScenarios;
	}
	
	DataArrays getWorkingScen(){
		return this.workingCycicScenario;
	}
	
	void setWorkingScen(DataArrays data){
		this.workingCycicScenario = data;
	}
}
