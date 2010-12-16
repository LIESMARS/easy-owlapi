package cz.cvut.owl2query.model.term.impl;

import cz.cvut.owl2query.model.GroundTermVisitor;
import cz.cvut.owl2query.model.term.*;

class OWLClassImpl<C> extends AbstractGroundTerm<C> implements OWLClass {

    public OWLClassImpl(C t) {
        super(t);
    }

    public String toString() {
        return "C:" + getTerm().toString();
    }

	@Override
	public void accept(GroundTermVisitor v) {
		v.visit(this);
	}
}
