package edu.utah.sci.cyclist.core.controller;

public interface Persistent {
	void save(IMemento memento);
	void restore(IMemento memento);
}
