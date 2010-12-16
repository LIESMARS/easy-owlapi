package cz.cvut.owl2query.model.term.impl;

import cz.cvut.owl2query.model.GroundTermVisitor;
import cz.cvut.owl2query.model.term.*;

class OWLIndividualImpl<I> extends AbstractGroundTerm<I> implements OWLIndividual {

    public OWLIndividualImpl(I t) {
        super(t);
    }

    public void accept(GroundTermVisitor v) {
        v.visit(this);
    }

    @Override
    public String toString() {
        return "I:" + getTerm().toString();
    }
}
