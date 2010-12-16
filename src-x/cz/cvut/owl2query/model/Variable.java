package cz.cvut.owl2query.model;

import java.util.Set;

public interface Variable extends Term {

    public void addVarType( final VarType t) throws QueryEvaluationException;

    public boolean allowsType( final VarType t);

    public Set<VarType> getAllowedTypes();
}
