package edu.utah.sci.cyclist.core.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

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
	
	public enum StructureType {
		SIMPLE, COMPLEX, HIERARCHICAL
	}
	
	private Role _role;
	private Type _type;
	private Interpretation _interp;
	private Classification _classification;
	private StructureType  _structureType;
	private FieldStructure _structure;
	private ObjectProperty<Boolean> _forceNumericFilterProperty = new SimpleObjectProperty<>();
	
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
		
		_forceNumericFilterProperty.set(false);
		
		update();
	}
	
	public DataType(Role role, Type type, Interpretation interp, Classification classification) {
		_role = role;
		_type = type;
		_interp = interp;
		_classification = classification;
		_forceNumericFilterProperty.set(false);
	}
	
	public DataType(DataType copy) {
		_role = copy._role;
		_type = copy._type;
		_interp = copy._interp;
		_classification = copy._classification;
		_forceNumericFilterProperty = copy._forceNumericFilterProperty;
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
	
	/**
	 * 
	 * @return
	 */
	public Role getRole() {
		return _role;
	}
	
	/**
	 * 
	 * @param role
	 */
	public void setRole(Role role) {
		_role = role;
		if (_type == Type.NUMERIC) {
			if (role == Role.DIMENSION)
				_interp = Interpretation.DISCRETE;
			else
				_interp = Interpretation.CONTINUOUS;
		}
		update();
	}
	
	/**
	 * 
	 * @return
	 */
	public Interpretation getInterpretation() {
		return _interp;
	}
	
	/**
	 * 
	 * @param i
	 */
	public void setInterpetation(Interpretation i) {
		_interp = i;
		update();
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isContinuous() {
		return _interp == Interpretation.CONTINUOUS;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isDate() {
		return _type == Type.DATE || _type == Type.DATETIME;
	}
	
	public StructureType getStructurTypee() {
		return _structureType;
	}
	
	public void setStructureType(StructureType value) {
		_structureType = value;
	}
	
	public void setStructure(FieldStructure value) {
		_structure = value;
	}
	
	public FieldStructure getStructure() {
		return _structure;
	}
	
	public ObjectProperty<Boolean> forceNumericFilterProperty() {
        return _forceNumericFilterProperty;
    }

    public Boolean getForceNumericFilter() {
        return _forceNumericFilterProperty.get();
    }

    public void setForceNumericFilter(Boolean value) {
    	_forceNumericFilterProperty.set(value);
    }
	
	private void update() {
		if (_interp == Interpretation.DISCRETE) {
			if (_type == Type.DATE || _type == Type.DATETIME) 
				_classification = Classification.Cdate;
			else if (_type == Type.TEXT)
				_classification = Classification.C;
			else
				_classification = Classification.Qi;
		} else { /* Interpretation.CONINUOUS */
			if (_type == Type.DATE || _type == Type.DATETIME || _role == Role.DIMENSION) 
				_classification = Classification.Qi;
			else
				_classification = Classification.Qd;
		}
		
		// determine default structure
		if (isDate()) {
			_structureType = StructureType.HIERARCHICAL;
			_structure = new DateTimeStructure();
		} else {
			_structureType = StructureType.SIMPLE;
			_structure = null;
		}
	}
}
