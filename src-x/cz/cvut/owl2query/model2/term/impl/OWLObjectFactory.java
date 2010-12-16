package cz.cvut.owl2query.model2.term.impl;

public interface OWLObjectFactory<C,OP,DP,I,L> {

    public C getOWLThing();

    public C getOWLNothing();

    public I asIndividual(String s);

    public L asLiteral(String s);

    public C asClass(String s);

    public OP asObjectProperty(String s);

    public DP asDataProperty(String s);

}
