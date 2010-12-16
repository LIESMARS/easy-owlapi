package cz.cvut.owl2query.model.term.impl;

import cz.cvut.owl2query.model.GroundTermVisitor;
import cz.cvut.owl2query.model.term.OWLClass;
import cz.cvut.owl2query.model.term.OWLDataProperty;
import cz.cvut.owl2query.model.term.OWLIndividual;
import cz.cvut.owl2query.model.term.OWLLiteral;
import cz.cvut.owl2query.model.term.OWLObjectProperty;

public class GroundTermVisitorAdapter implements GroundTermVisitor {

    public void visit(final OWLClass t) {}

    public void visit(final OWLObjectProperty t) {}

    public void visit(final OWLDataProperty t) {}

    public void visit(final OWLIndividual t) {}

    public void visit(final OWLLiteral t) {}
}
