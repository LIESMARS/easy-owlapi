package cz.cvut.kbss.owl2query.complexversion.model.owlapi;

import java.net.URI;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import cz.cvut.kbss.owl2query.complexversion.model.OWL2QueryFactory;

public class OWLAPIQueryFactory
		extends
		OWL2QueryFactory<OWLClassExpression, OWLObjectPropertyExpression, OWLDataPropertyExpression, OWLAnnotationProperty, OWLDatatype, OWLDataRange, OWLNamedIndividual, OWLLiteral> {

	final OWLDataFactory f;
	final OWLOntologyManager m;
	final OWLOntology o;

	public OWLAPIQueryFactory(final OWLOntologyManager m, final OWLOntology o) {
		this.m = m;
		this.o = o;
		this.f = m.getOWLDataFactory();
	}

	@Override
	protected OWLObjectPropertyExpression internalInverseObjectProperty(
			OWLObjectPropertyExpression ope) {
		return f.getOWLObjectInverseOf(ope);
	}

	@Override
	protected OWLLiteral internalLiteral(int l) {
		return f.getOWLTypedLiteral(l);
	}

	@Override
	protected OWLLiteral internalLiteral(double l) {
		return f.getOWLTypedLiteral(l);
	}

	@Override
	protected OWLLiteral internalLiteral(String l) {
		return f.getOWLTypedLiteral(l);
	}

	@Override
	protected OWLLiteral internalLiteral(boolean l) {
		return f.getOWLTypedLiteral(l);
	}

	@Override
	protected OWLClassExpression internalNamedClass(URI uri) {
		return f.getOWLClass(uri);
	}

	@Override
	protected OWLDataPropertyExpression internalNamedDataProperty(URI uri) {
		return f.getOWLDataProperty(uri);
	}

	@Override
	protected OWLDatatype internalNamedDataRange(URI uri) {
		return f.getOWLDatatype(uri);
	}

	@Override
	protected OWLNamedIndividual internalNamedIndividual(URI uri) {
		return f.getOWLNamedIndividual(uri);
	}

	@Override
	protected OWLObjectPropertyExpression internalNamedObjectProperty(URI uri) {
		return f.getOWLObjectProperty(uri);
	}

	@Override
	protected OWLClassExpression internalObjectAllValuesFrom(
			OWLObjectPropertyExpression ope, OWLClassExpression ce) {
		return f.getOWLObjectAllValuesFrom(ope, ce);
	}

	@Override
	protected OWLClassExpression internalObjectComplementOf(
			OWLClassExpression ce) {
		return f.getOWLObjectComplementOf(ce);
	}

	@Override
	protected OWLClassExpression internalObjectExactCardinality(int card,
			OWLObjectPropertyExpression ope, OWLClassExpression ce) {
		return f.getOWLObjectExactCardinality(card, ope, ce);
	}

	@Override
	protected OWLClassExpression internalObjectHasSelf(
			OWLObjectPropertyExpression ope) {
		return f.getOWLObjectHasSelf(ope);
	}

	@Override
	protected OWLClassExpression internalObjectHasValue(
			OWLObjectPropertyExpression ope, OWLNamedIndividual ni) {
		return f.getOWLObjectHasValue(ope, ni);
	}

	@Override
	protected OWLClassExpression internalObjectIntersectionOf(
			Set<OWLClassExpression> c) {
		return f.getOWLObjectIntersectionOf(c);
	}

	@Override
	protected OWLClassExpression internalObjectMaxCardinality(int card,
			OWLObjectPropertyExpression ope, OWLClassExpression ce) {
		return f.getOWLObjectMaxCardinality(card, ope, ce);
	}

	@Override
	protected OWLClassExpression internalObjectMinCardinality(int card,
			OWLObjectPropertyExpression ope, OWLClassExpression ce) {
		return f.getOWLObjectMinCardinality(card, ope, ce);
	}

	@Override
	protected OWLClassExpression internalObjectOneOf(Set<OWLNamedIndividual> nis) {
		return f.getOWLObjectOneOf(nis);
	}

	@Override
	protected OWLClassExpression internalObjectSomeValuesFrom(
			OWLObjectPropertyExpression ope, OWLClassExpression ce) {
		return f.getOWLObjectSomeValuesFrom(ope, ce);
	}

	@Override
	protected OWLClassExpression internalObjectUnionOf(
			Set<OWLClassExpression> ces) {
		return f.getOWLObjectUnionOf(ces);
	}

}
