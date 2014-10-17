package edu.utah.sci.cyclist.neup.model;

public class NotNuclide extends Exception {
	private static final long serialVersionUID = 1L;

	private String _was;
	private String _now;

	public NotNuclide(String was, String now) {
		_was = was;
		_now = now;
	}

	public NotNuclide(int was, String now) {
		this(String.valueOf(was), now);
	}

	public NotNuclide(String was, int now) {
		this(was, String.valueOf(now));
	}

	public NotNuclide(int was, int now) {
		this(String.valueOf(was), String.valueOf(now));
		;
	}

	public String toString() {
		String str = "Not a Nuclide: ";
		if (_was != null)
			str = str + _was;
		if (_now != null)
			str = str + " --> " + _now;
		return str;
	}
}