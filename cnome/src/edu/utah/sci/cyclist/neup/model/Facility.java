package edu.utah.sci.cyclist.neup.model;

public class Facility {
	public int id;
	public String model;
	public String prototype;
	
	public Facility() {
		
	}
	
	public Facility(int id, String model, String prototype) {
		this.id = id;
		this.model = model;
		this.prototype = prototype;
	}
	
	public Integer getId() { return id; }
	public String getModel() { return model;}
	public String getPrototype() { return prototype; }
}
