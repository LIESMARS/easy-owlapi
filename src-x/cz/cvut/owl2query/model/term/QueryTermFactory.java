package cz.cvut.owl2query.model.term;

import cz.cvut.owl2query.model.Variable;

public interface QueryTermFactory {

    public OWLClass getOWLThing();

    public OWLClass getOWLNothing();

    public OWLIndividual asIndividual(final String s);

    public Variable asIndividualVariable(final String s);

    public OWLLiteral asLiteral(final String s);

    public Variable asLiteralVariable(final String s);

    public Variable asIndividualOrLiteralVariable(final String s);

    public OWLClass asClass(final String s);

    public Variable asClassVariable(final String s);

    public Variable asPropertyVariable(final String s);

    public OWLObjectProperty asObjectProperty(final String s);

    public OWLDataProperty asDataProperty(final String s);
}
