package org.mindswap.pellet.output;

import java.io.PrintWriter;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

public interface ATermRenderer {

	void setWriter(PrintWriter formatter);

	void visit(ATermAppl val);

}
