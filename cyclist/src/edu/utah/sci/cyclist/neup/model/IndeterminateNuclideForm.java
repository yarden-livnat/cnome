package edu.utah.sci.cyclist.neup.model;

public class IndeterminateNuclideForm extends Exception {
	private static final long serialVersionUID = 1L;

	private int _nuc;

	public IndeterminateNuclideForm(int nuc) {
		this(nuc, "");
	}

	public IndeterminateNuclideForm(int nuc, String msg) {
		super(msg);
		_nuc = nuc;
	}

	public int getNuc() {
		return _nuc;
	}
}