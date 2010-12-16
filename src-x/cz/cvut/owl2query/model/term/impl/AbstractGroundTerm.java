package cz.cvut.owl2query.model.term.impl;

import cz.cvut.owl2query.model.GroundTerm;
import cz.cvut.owl2query.model.TermVisitor;

abstract class AbstractGroundTerm<C> extends AbstractTerm<C> implements GroundTerm {

    public AbstractGroundTerm(C term) {
        super(term);
    }

    public void accept(TermVisitor v) {
        v.accept(this);
    }
}
