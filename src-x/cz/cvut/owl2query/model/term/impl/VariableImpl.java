package cz.cvut.owl2query.model.term.impl;

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cvut.owl2query.model.QueryEvaluationException;
import cz.cvut.owl2query.model.TermVisitor;
import cz.cvut.owl2query.model.VarType;
import cz.cvut.owl2query.model.Variable;

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
}
