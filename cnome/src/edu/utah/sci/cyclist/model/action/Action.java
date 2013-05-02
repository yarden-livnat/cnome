package edu.utah.sci.cyclist.model.action;

public interface Action<R,A> {
	R exec(A arg);
}
