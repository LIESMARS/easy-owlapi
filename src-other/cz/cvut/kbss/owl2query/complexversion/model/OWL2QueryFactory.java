package cz.cvut.kbss.owl2query.complexversion.model;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public abstract class OWL2QueryFactory<CE, OPE, DPE, AP, DT, DR, NI, L> {

	// ATOMS

	// SubClassOfQueryAtom subClassOf(final ClassTerm sub, final ClassTerm sup);
	//
	// SubObjectPropertyOfQueryAtom subObjectPropertyOf(final ObjectPropertyTerm
	// sub, final ObjectPropertyTerm sup);
	//
	// SubDataPropertyOfQueryAtom subDataPropertyOf(final DataPropertyTerm sub,
	// final DataPropertyTerm sup);
	//	
	// SymmetricObjectPropertyAtom symmetricObjectProperty(final
	// ObjectPropertyTerm op);
	//	
	// AsymmetricObjectPropertyAtom asymmetricObjectProperty(final
	// ObjectPropertyTerm op);

	// generalized conjunctive queries

	public ClassAssertionAtom<CE, NI> classAssertion(final ClassTerm<CE> sub,
			final NamedIndividualTerm<NI> sup) {
		return new ClassAssertionAtomImpl<CE, NI>(sub, sup);
	}

	public ObjectPropertyAssertionAtom<OPE, NI> objectPropertyAssertion(
			final ObjectPropertyTerm<OPE> op, final NamedIndividualTerm<NI> i1,
			final NamedIndividualTerm<NI> i2) {
		return new ObjectPropertyAssertionAtomImpl<OPE, NI>(op, i1, i2);
	}

	// TODO with NOT
	// NegativeObjectPropertyAssertionAtom negativeObjectPropertyAssertion(
	// final ObjectPropertyTerm op, final NamedIndividualTerm i1,
	// final NamedIndividualTerm i2);

	public DataPropertyAssertionAtom<DPE, NI, L, DT, DR> dataPropertyAssertion(
			final DataPropertyTerm<DPE> op, final NamedIndividualTerm<NI> i1,
			final LiteralTerm<L, DT, DR> i2) {
		return new DataPropertyAssertionAtomImpl<DPE, NI, L, DT, DR>(op, i1, i2);
	}

	// TODO with NOT
	// NegativeDataPropertyAssertionAtom negativeDataPropertyAssertion(
	// final DataPropertyTerm op, final NamedIndividualTerm i1,
	// final LiteralTerm i2);

	public SameIndividualAtom<NI> sameIndividual(
			final NamedIndividualTerm<NI> i1, final NamedIndividualTerm<NI> i2) {
		return new SameIndividualAtomImpl<NI>(i1, i2);
	}

	public DifferentIndividualsAtom<NI> differentIndividuals(
			final NamedIndividualTerm<NI> i1, final NamedIndividualTerm<NI> i2) {
		return new DifferentIndividualsAtomImpl<NI>(i1, i2);
	}

	// TERMS

	// variable
	public Variable<CE, OPE, DPE, AP, DT, DR, NI, L> variable(String name) {
		return new VariableImpl<CE, OPE, DPE, AP, DT, DR, NI, L>(name);
	}

	// class
	public ClassExpression<CE> namedClass(final URI uri) {
		return new ClassExpressionImpl<CE>(internalNamedClass(uri));
	}

	protected abstract CE internalNamedClass(final URI uri);

	public ClassExpression<CE> objectAllValuesFrom(
			final ObjectPropertyExpression<OPE> op, final ClassExpression<CE> c) {
		return new ClassExpressionImpl<CE>(internalObjectAllValuesFrom(op
				.getWrappedObject(), c.getWrappedObject()));
	}

	protected abstract CE internalObjectAllValuesFrom(final OPE ope, final CE ce);

	public ClassExpression<CE> objectComplementOf(final ClassExpression<CE> c) {
		return new ClassExpressionImpl<CE>(internalObjectComplementOf(c
				.getWrappedObject()));
	}

	protected abstract CE internalObjectComplementOf(final CE ce);

	public ClassExpression<CE> objectExactCardinality(final int card,
			final ObjectPropertyExpression<OPE> op, final ClassExpression<CE> c) {
		return new ClassExpressionImpl<CE>(internalObjectExactCardinality(card,
				op.getWrappedObject(), c.getWrappedObject()));
	}

	protected abstract CE internalObjectExactCardinality(final int card,
			final OPE op, final CE c);

	public ClassExpression<CE> objectHasSelf(
			final ObjectPropertyExpression<OPE> op) {
		return new ClassExpressionImpl<CE>(internalObjectHasSelf(op
				.getWrappedObject()));
	}

	protected abstract CE internalObjectHasSelf(final OPE op);

	public ClassExpression<CE> objectHasValue(
			final ObjectPropertyExpression<OPE> op, final NamedIndividual<NI> ni) {
		return new ClassExpressionImpl<CE>(internalObjectHasValue(op
				.getWrappedObject(), ni.getWrappedObject()));
	}

	protected abstract CE internalObjectHasValue(final OPE op, final NI ni);

	public ClassExpression<CE> objectIntersectionOf(
			final ClassExpression<CE>... c) {
		final Set<CE> e = new HashSet<CE>();

		for (final ClassExpression<CE> ci : c) {
			e.add(ci.getWrappedObject());
		}

		return new ClassExpressionImpl<CE>(internalObjectIntersectionOf(e));
	}

	protected abstract CE internalObjectIntersectionOf(final Set<CE> c);

	public ClassExpression<CE> objectMinCardinality(final int card,
			final ObjectPropertyExpression<OPE> op, final ClassExpression<CE> c) {
		return new ClassExpressionImpl<CE>(internalObjectExactCardinality(card,
				op.getWrappedObject(), c.getWrappedObject()));
	}

	protected abstract CE internalObjectMinCardinality(final int card,
			final OPE op, final CE c);

	public ClassExpression<CE> objectMaxCardinality(final int card,
			final ObjectPropertyExpression<OPE> op, final ClassExpression<CE> c) {
		return new ClassExpressionImpl<CE>(internalObjectExactCardinality(card,
				op.getWrappedObject(), c.getWrappedObject()));
	}

	protected abstract CE internalObjectMaxCardinality(final int card,
			final OPE op, final CE c);

	public ClassExpression<CE> objectOneOf(final NamedIndividual<NI>... ni) {
		final Set<NI> set = new HashSet<NI>();

		for (final NamedIndividual<NI> c : ni) {
			set.add(c.getWrappedObject());
		}

		return new ClassExpressionImpl<CE>(internalObjectOneOf(set));
	}

	protected abstract CE internalObjectOneOf(final Set<NI> nis);

	public ClassExpression<CE> objectSomeValuesFrom(
			final ObjectPropertyExpression<OPE> op, final ClassExpression<CE> c) {
		return new ClassExpressionImpl<CE>(internalObjectSomeValuesFrom(op
				.getWrappedObject(), c.getWrappedObject()));
	}

	protected abstract CE internalObjectSomeValuesFrom(final OPE op, final CE c);

	public ClassExpression<CE> objectUnionOf(final ClassExpression<CE>... c) {
		final Set<CE> e = new HashSet<CE>();

		for (final ClassExpression<CE> ci : c) {
			e.add(ci.getWrappedObject());
		}

		return new ClassExpressionImpl<CE>(internalObjectUnionOf(e));
	}

	protected abstract CE internalObjectUnionOf(final Set<CE> c);

	// object property
	public ObjectPropertyExpression<OPE> namedObjectProperty(final URI uri) {
		return new ObjectPropertyExpressionImpl<OPE>(
				internalNamedObjectProperty(uri));
	}

	protected abstract OPE internalNamedObjectProperty(final URI uri);

	public ObjectPropertyExpression<OPE> inverseObjectProperty(
			final ObjectPropertyExpression<OPE> op) {
		return new ObjectPropertyExpressionImpl<OPE>(
				internalInverseObjectProperty(op.getWrappedObject()));
	}

	protected abstract OPE internalInverseObjectProperty(final OPE op);

	// data property
	public DataPropertyExpression<DPE> namedDataProperty(final URI uri) {
		return new DataPropertyExpressionImpl<DPE>(
				internalNamedDataProperty(uri));
	}

	protected abstract DPE internalNamedDataProperty(final URI uri);

	// // data property
	// AnnotationProperty<AP> namedAnnotationProperty(final URI uri );

	// data range (datatype)

	/**
	 * NamedDataRange = Datatype
	 */
	public Datatype<DT, DR> namedDataRange(final URI uri) {
		return new DatatypeImpl<DT, DR>(internalNamedDataRange(uri), uri);
	}

	protected abstract DT internalNamedDataRange(final URI uri);

	// individual
	public NamedIndividual<NI> namedIndividual(final URI uri) {
		return new NamedIndividualImpl<NI>(internalNamedIndividual(uri));
	}

	protected abstract NI internalNamedIndividual(final URI uri);

	public Literal<L, DT, DR> literal(int i) {
		return new LiteralImpl<L, DT, DR>(internalLiteral(i));
	}

	protected abstract L internalLiteral(int i);

	public Literal<L, DT, DR> literal(double d) {
		return new LiteralImpl<L, DT, DR>(internalLiteral(d));
	}

	protected abstract L internalLiteral(double i);

	public Literal<L, DT, DR> literal(String s) {
		return new LiteralImpl<L, DT, DR>(internalLiteral(s));
	}

	protected abstract L internalLiteral(String s);

	public Literal<L, DT, DR> literal(boolean s) {
		return new LiteralImpl<L, DT, DR>(internalLiteral(s));
	}

	protected abstract L internalLiteral(boolean s);

	// TODO

}
