package cz.cvut.owl2query.model.term.impl;

import cz.cvut.owl2query.model.GroundTermVisitor;
import cz.cvut.owl2query.model.term.*;

class OWLObjectPropertyImpl<OP> extends AbstractGroundTerm<OP> implements OWLObjectProperty {

    public OWLObjectPropertyImpl(OP t) {
        super(t);
    }

    public void accept(GroundTermVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return "OP:" + getTerm().toString();
    }
}
