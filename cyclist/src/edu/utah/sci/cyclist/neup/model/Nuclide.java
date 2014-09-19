package edu.utah.sci.cyclist.neup.model;

import java.util.HashMap;
import java.util.Map;

import edu.utah.sci.cyclist.core.model.CyclistData;
import edu.utah.sci.cyclist.core.util.NuclideUtils;

public class Nuclide implements CyclistData {
	static private Map<Integer, Nuclide> _cache = new HashMap<>();
	static private Nuclide UNKNOWN = new Nuclide(0, "<Illegal>");
	
	private Integer _id;
	private String _name = null;

	private Nuclide(Integer id, String name) {
		_id = id;
		_name = name;
	}

	public boolean equals(Nuclide other) {
		return this == other; // Nuclides are unique
	}
	
	public int hashCode() {
		return _id.hashCode();
	}
	
	public String toString() {
		return _name;
	}

	public String sqlValue() {
		return String.valueOf(_id);
	}

	
	static public Nuclide create(Integer nuc) {
		try {
			Nuclide nuclide = _cache.get(nuc) ;
			if (nuclide == null) {
				nuclide = new Nuclide(nuc, NuclideUtils.name(nuc));
				_cache.put(nuc, nuclide);
			}
			return nuclide;
		} catch (NotNuclide | IndeterminateNuclideForm e) {
			e.printStackTrace();
			return UNKNOWN;
		}
	}
	
	static public Nuclide create(String code) {
		try {
			Integer nuc = NuclideUtils.id(code);
			Nuclide nuclide = _cache.get(nuc) ;
			if (nuclide == null) {
				nuclide = new Nuclide(nuc, code);
				_cache.put(nuc, nuclide);
			}
			return nuclide;
		} catch (NotNuclide | IndeterminateNuclideForm e) {
			e.printStackTrace();
			return UNKNOWN;
		}
	}
	
	static public Nuclide create(Object key) {
		if (key instanceof Integer)
			return create((Integer)key);
		
		if (key instanceof String)
			return create((String) key);
		
		return UNKNOWN;
	}
}
