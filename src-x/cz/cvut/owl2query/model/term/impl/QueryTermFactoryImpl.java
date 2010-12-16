package cz.cvut.owl2query.model.term.impl;

import cz.cvut.owl2query.model.term.QueryTermFactory;
import cz.cvut.owl2query.model.*;
import java.util.EnumSet;

class QueryTermFactoryImpl<C,P,OP,DP,I,L> implements QueryTermFactory {

    private final OWLObjectFactory<C,OP,DP,I,L> f;

    public QueryTermFactoryImpl(OWLObjectFactory<C,OP,DP,I,L> f) {
        this.f = f;
    }

    public OWLIndividualImpl<I> asIndividual(String s) {
        return new OWLIndividualImpl<I>(f.asIndividual(s));
    }

    public VariableImpl asIndividualVariable(String s) {
        return new VariableImpl(s, EnumSet.of(VarType.INDIVIDUAL) );
    }

    public OWLLiteralImpl<L> asLiteral(String s) {
        return new OWLLiteralImpl<L>(f.asLiteral(s));
    }

    public VariableImpl asLiteralVariable(String s) {
        return new VariableImpl(s, EnumSet.of(VarType.LITERAL));
    }

    public Variable asIndividualOrLiteralVariable(String s) {
        return new VariableImpl(s, EnumSet.of(VarType.INDIVIDUAL, VarType.INDIVIDUAL));
    }

    public OWLClassImpl<C> asClass(String s) {
        return new OWLClassImpl<C>(f.asClass(s));
    }

    public Variable asClassVariable(String s) {
        return new VariableImpl(s, EnumSet.of(VarType.CLASS));
    }

    public Variable asPropertyVariable(String s) {
        return new VariableImpl(s, EnumSet.of(VarType.PROPERTY));
    }

    public OWLObjectPropertyImpl<OP> asObjectProperty(String s) {
        return new OWLObjectPropertyImpl<OP>(f.asObjectProperty(s));
    }

    public OWLDataPropertyImpl<DP> asDataProperty(String s) {
        return new OWLDataPropertyImpl<DP>(f.asDataProperty(s));
    }

    public OWLClassImpl<C> getOWLThing() {
        return new OWLClassImpl<C>(f.getOWLThing());
    }

    public OWLClassImpl<C> getOWLNothing() {
        return new OWLClassImpl<C>(f.getOWLNothing());
    }
}
