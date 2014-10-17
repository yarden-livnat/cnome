package edu.utah.sci.cyclist.core.event;

public class Pair<T,S> {
	public T v1;
	public S v2;
	
	public Pair() {}
	public Pair(T t, S s) { v1 = t; v2 = s; }
}