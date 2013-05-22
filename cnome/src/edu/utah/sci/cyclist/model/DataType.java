package edu.utah.sci.cyclist.model;

public class DataType {

	public static enum Role {
		MEASURE, DIMENSION;
	}

	public enum Type {
		NUMERIC,
		TEXT,
		DATE,
		DATETIME,
		BOOLEAN,
		NA
	}
	
	public enum Interpretation {
		DISCRETE, CONTINUOUS
	}

	public enum Classification {
		C, Cdate, Qd, Qi
	}
	
	private Role _role;
	private Type _type;
	private Interpretation _interp;
	private Classification _classification;
	
	public DataType(Type type) {
		_type = type;
		switch (type) {
		case NUMERIC:
			_role = Role.MEASURE;
			_interp = Interpretation.CONTINUOUS;
			break;
		case TEXT:
			_role = Role.DIMENSION;
			_interp = Interpretation.DISCRETE;
		case DATE:
		case DATETIME:
			_role = Role.DIMENSION;
			_interp = Interpretation.DISCRETE;
		case BOOLEAN:
		case NA:
			break;
		}
		
		update();
	}
	
	public DataType(Role role, Type type, Interpretation interp, Classification classification) {
		_role = role;
		_type = type;
		_interp = interp;
		_classification = classification;
	}
	
	/**
	 * read-only
	 * @return
	 */
	
	public Type getType() {
		return _type;
	}
	
	/**
	 * read-only
	 * @return
	 */
	public Classification getClassification() {
		return _classification;
	}
	
	public Role getRole() {
		return _role;
	}
	
	public void setRole(Role role) {
		_role = role;
		update();
	}
	
	public Interpretation getInterpretation() {
		return _interp;
	}
	
	public void setInterpetation(Interpretation i) {
		_interp = i;
		update();
	}
	
	public boolean isContinuous() {
		return _interp == Interpretation.CONTINUOUS;
	}
	
	public boolean isDate() {
		return _type == Type.DATE || _type == Type.DATETIME;
	}
	
	
	private void update() {
		if (_interp == Interpretation.DISCRETE) {
			if (_type == Type.DATE || _type == Type.DATETIME) 
				_classification = Classification.Cdate;
			else
				_classification = Classification.C;
		} else { /* Interpretation.CONINUOUS */
			if (_type == Type.DATE || _type == Type.DATETIME || _role == Role.DIMENSION) 
				_classification = Classification.Qi;
			else
				_classification = Classification.Qd;
		}
	}
}
