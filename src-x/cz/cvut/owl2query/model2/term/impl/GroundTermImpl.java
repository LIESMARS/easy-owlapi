package cz.cvut.owl2query.model2.term.impl;

import cz.cvut.owl2query.model.GroundTerm;
import cz.cvut.owl2query.model.GroundTermType;
import cz.cvut.owl2query.model.QueryEvaluationException;
import cz.cvut.owl2query.model.TermVisitor;
import cz.cvut.owl2query.model.Variable;

class GroundTermImpl<C> extends AbstractTerm<C> implements GroundTerm {

    private final GroundTermType type;

    public GroundTermImpl(C term, final GroundTermType type) {
        super(term);

        this.type = type;
    }

    public GroundTermType getType() {
        return type;
    }

    public void accept(TermVisitor v) {
        v.accept(this);
    }
    
    public String toString() {
    	return type+":"+"term";
    }

	@Override
	public GroundTerm asGroundTerm() {
		return this;
	}

	@Override
	public Variable asVariable() {
		throw new QueryEvaluationException("This term is not a variable !");
	}

	@Override
	public boolean isVariable() {
		return false;
	}
}
