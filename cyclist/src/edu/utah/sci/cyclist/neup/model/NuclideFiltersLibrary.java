package edu.utah.sci.cyclist.neup.model;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.IntPredicate;

public class NuclideFiltersLibrary {
	public static int ELEM_FACTOR = 1000000;
	public static int ISO_FACTOR = 10000;
	
	private static NuclideFiltersLibrary _instance = new NuclideFiltersLibrary();
	
	private Map<String, IntPredicate> _filters = new TreeMap<>();
	
	public static NuclideFiltersLibrary getInstance() {
		return _instance;	
	}
	
	public Map<String, IntPredicate> getFilters() {
		return _filters;
	}
	
	private NuclideFiltersLibrary() {
		_filters.put("Actinides",n->{
				int elem = n/ELEM_FACTOR;
				return 89 <= elem && elem <= 103;
			});
		
		_filters.put("Transuranics", n->{
			int elem = n/ELEM_FACTOR;
			return 93 <= elem && elem <= 103;
		});
		
		_filters.put("Minor Actinides", n->{
			int elem = n/ELEM_FACTOR;
			return elem==93 || (95 <= elem && elem <= 103);
		});
		
		_filters.put("Fissile nuclides", n->{
			int iso = n/ISO_FACTOR;
			return iso==92233 || iso==92235 || iso==94239 || iso== 94231 || iso==95242 || iso==96243 || iso==96245;
		});
		
		_filters.put("Fissile Uranium ", n->{
			int iso = n/ISO_FACTOR;
			return iso==92233 || iso==92235;
		});
		
		_filters.put("Fissile Plutonium ", n->{
			int iso = n/ISO_FACTOR;
			return iso==94239 || iso==94241;
		});
		
		_filters.put("Long-lived fission", n->{
			int iso = n/ISO_FACTOR;
			return iso==55135 || iso==3129 || iso==43099 || iso==46107 || iso==4009 || iso==34079;
		});
		
		_filters.put("Lanthanides", n->{
			int elem = n/ELEM_FACTOR;
			return 57 <= elem && elem <= 70;
		});
		
		_filters.put("Noble gases", n->{
			int elem = n/ELEM_FACTOR;
			return elem==36 || elem==54;
		});
	}
	
}
