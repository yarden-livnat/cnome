package edu.utah.sci.cyclist.neup.model;

public class Facility {
	public int id;
	public String model;
	public String prototype;
	public int intitution;
	public int region;
	
	public Facility() {
		
	}
	
	public Facility(int id, String model, String prototype, int institution, int market) {
		this.id = id;
		this.model = model;
		this.prototype = prototype;
		this.intitution = institution;
		this.region = market;
	}
	
	public Integer getId() { return id; }
	public String getModel() { return model;}
	public String getPrototype() { return prototype; }
	public Integer getInstitution() { return intitution; }
	public Integer getRegion() { return region; }
}
