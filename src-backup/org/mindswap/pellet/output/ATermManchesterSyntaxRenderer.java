package org.mindswap.pellet.output;

import java.io.PrintWriter;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

public class ATermManchesterSyntaxRenderer implements ATermRenderer {

	@Override
	public void setWriter(PrintWriter formatter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visit(ATermAppl val) {
		throw new UnsupportedOperationException();
	}

}
