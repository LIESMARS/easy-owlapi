package cz.cvut.owl2query.model;

import cz.cvut.owl2query.model.Term;
import java.util.Collection;

public interface QuerySolution {

    public Collection<Term> getVariables();

    public Term getBinding(final Term variable);
}
