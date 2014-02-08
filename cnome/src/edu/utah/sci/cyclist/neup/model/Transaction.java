package edu.utah.sci.cyclist.neup.model;

public class Transaction {
	public int timestep;
	public int target;
	public int iso;
	public double fraction;
	public double quantity;
	public String units;
	
	
	public Transaction(int timestep, int target, int iso, double fraction, double quantity, String units) {
		this.timestep = timestep;
		this.target = target;
		this.iso = iso;
		this.fraction = fraction;
		this.quantity = quantity;
		this.units = units;
	}
}

