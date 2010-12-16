package cz.cvut.owl2query.model;

public interface QueryTermFactory {

    public GroundTerm getOWLThing();

    public GroundTerm getOWLNothing();

    public GroundTerm asIndividual(final String s);

    public Variable asIndividualVariable(final String s);

    public GroundTerm asLiteral(final String s);

    public Variable asLiteralVariable(final String s);

    public Variable asIndividualOrLiteralVariable(final String s);

    public GroundTerm asClass(final String s);

    public Variable asClassVariable(final String s);

    public GroundTerm asObjectProperty(final String s);

    public GroundTerm asDataProperty(final String s);

    public Variable asPropertyVariable(final String s);
}
