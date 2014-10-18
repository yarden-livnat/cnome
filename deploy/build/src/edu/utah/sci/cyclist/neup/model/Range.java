package edu.utah.sci.cyclist.neup.model;

public class Range<T> {
	public T from;
	public T to;
	
	public Range() {
	}
	
	public Range(T from, T to) {
		this.from = from;
		this.to = to;
	}
}
