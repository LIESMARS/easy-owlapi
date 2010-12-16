package cz.cvut.owl2query.model.term.impl;

import cz.cvut.owl2query.model.Term;

abstract class AbstractTerm<C> implements Term {

    private final C term;

    public AbstractTerm(C term) {
        this.term = term;
    }

    public C getTerm() {
        return term;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractTerm<C> other = (AbstractTerm<C>) obj;
        if (this.term != other.term && (this.term == null || !this.term.equals(other.term))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.term != null ? this.term.hashCode() : 0);
        return hash;
    }
}
