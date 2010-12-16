package cz.cvut.owl2query.model2.term.impl;

import cz.cvut.owl2query.model.GroundTerm;
import cz.cvut.owl2query.model.VarType;
import cz.cvut.owl2query.model.TermVisitor;
import cz.cvut.owl2query.model.QueryEvaluationException;
import cz.cvut.owl2query.model.Variable;

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

class VariableImpl extends AbstractTerm<String> implements Variable {

    private static final Logger log = Logger.getLogger(VariableImpl.class.getName());

    private final EnumSet<VarType> varTypes;

    public VariableImpl(String term, final EnumSet<VarType> initialSet) {
        super(term);

        varTypes = initialSet;
    }

    public void accept(TermVisitor v) {
        v.accept(this);
    }

    public void addVarType(VarType t) throws QueryEvaluationException {
        if (varTypes.contains(t)) {
            if ( log.isLoggable(Level.CONFIG)) {
                log.config("Adding variable type '"+t+"' that is already present in '" + varTypes+ "'.");
            }
            return;
        }

        varTypes.add(t);
    }

    public boolean allowsType(VarType t) {
        return varTypes.contains(t);
    }

    public Set<VarType> getAllowedTypes() {
        return varTypes;
    }
    
    public String toString() {
    	return "?" + varTypes + ":" + getTerm();
    }

	@Override
	public GroundTerm asGroundTerm() {
		throw new QueryEvaluationException("This term is not a ground term.");
	}

	@Override
	public Variable asVariable() {
		return this;
	}

	@Override
	public boolean isVariable() {
		return true;
	}
}
