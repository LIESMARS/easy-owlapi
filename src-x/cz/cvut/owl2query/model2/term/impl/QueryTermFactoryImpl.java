package cz.cvut.owl2query.model2.term.impl;

import cz.cvut.owl2query.model.*;

import java.util.EnumSet;

class QueryTermFactoryImpl<C,P,OP,DP,I,L> implements QueryTermFactory {

    private final OWLObjectFactory<C,OP,DP,I,L> f;

    public QueryTermFactoryImpl(OWLObjectFactory<C,OP,DP,I,L> f) {
        this.f = f;
    }

    public GroundTermImpl<I> asIndividual(String s) {
        return new GroundTermImpl<I>(f.asIndividual(s), GroundTermType.Individual);
    }

    public VariableImpl asIndividualVariable(String s) {
        return new VariableImpl(s, EnumSet.of(VarType.INDIVIDUAL) );
    }

    public GroundTermImpl<L> asLiteral(String s) {
        return new GroundTermImpl<L>(f.asLiteral(s), GroundTermType.Literal);
    }

    public VariableImpl asLiteralVariable(String s) {
        return new VariableImpl(s, EnumSet.of(VarType.LITERAL));
    }

    public Variable asIndividualOrLiteralVariable(String s) {
        return new VariableImpl(s, EnumSet.of(VarType.INDIVIDUAL, VarType.INDIVIDUAL));
    }

    public GroundTermImpl<C> asClass(String s) {
        return new GroundTermImpl<C>(f.asClass(s), GroundTermType.Class);
    }

    public Variable asClassVariable(String s) {
        return new VariableImpl(s, EnumSet.of(VarType.CLASS));
    }

    public Variable asPropertyVariable(String s) {
        return new VariableImpl(s, EnumSet.of(VarType.PROPERTY));
    }

    public GroundTermImpl<OP> asObjectProperty(String s) {
        return new GroundTermImpl<OP>(f.asObjectProperty(s), GroundTermType.ObjectProperty);
    }

    public GroundTermImpl<DP> asDataProperty(String s) {
        return new GroundTermImpl<DP>(f.asDataProperty(s), GroundTermType.DataProperty);
    }

    public GroundTermImpl<C> getOWLThing() {
        return new GroundTermImpl<C>(f.getOWLThing(), GroundTermType.Class);
    }

    public GroundTermImpl<C> getOWLNothing() {
        return new GroundTermImpl<C>(f.getOWLNothing(), GroundTermType.Class);
    }

}
