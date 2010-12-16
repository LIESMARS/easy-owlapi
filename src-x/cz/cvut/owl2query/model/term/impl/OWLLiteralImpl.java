package cz.cvut.owl2query.model.term.impl;

import cz.cvut.owl2query.model.GroundTermVisitor;
import cz.cvut.owl2query.model.term.*;

class OWLLiteralImpl<I> extends AbstractGroundTerm<I> implements OWLLiteral {

    public OWLLiteralImpl(I t) {
        super(t);
    }

    public void accept(GroundTermVisitor v) {
        v.visit(this);
    }

    public String toString() {
        return "L:" + getTerm().toString();
    }
}
