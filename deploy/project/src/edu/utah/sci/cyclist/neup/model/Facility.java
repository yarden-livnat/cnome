package edu.utah.sci.cyclist.neup.model;

public class Facility {
	public int id;
	public String spec;
	public String prototype;
	public int intitution;
	public int region;
	
	public Facility() {
		
	}
	
	public Facility(int id, String spec, String prototype, int institution, int market) {
		this.id = id;
		this.spec = spec;
		this.prototype = prototype;
		this.intitution = institution;
		this.region = market;
	}
	
	public Integer getId() { return id; }
	public String getModel() { return spec;}
	public String getPrototype() { return prototype; }
	public Integer getInstitution() { return intitution; }
	public Integer getRegion() { return region; }
}
