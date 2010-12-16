package cz.cvut.owl2query.model.term.impl;

import cz.cvut.owl2query.model.GroundTermVisitor;
import cz.cvut.owl2query.model.term.*;

class OWLDataPropertyImpl<DP> extends AbstractGroundTerm<DP> implements OWLDataProperty {

    public OWLDataPropertyImpl(DP t) {
        super(t);
    }

    public void accept(GroundTermVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return "DP:" + getTerm().toString();
    }
}

