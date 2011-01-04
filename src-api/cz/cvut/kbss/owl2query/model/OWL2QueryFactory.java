package cz.cvut.kbss.owl2query.model;

import java.util.Set;

public interface OWL2QueryFactory<G> {

	OWL2Query<G> createQuery(OWL2Ontology<G> o);

	// TERMS
	// variable
	Variable<G> variable(String name);

	GroundTerm<G> wrap(final G gt);

	// class
	G namedClass(final String uri);

	G namedObjectProperty(final String uri);

	G namedDataProperty(final String uri);

	G namedDataRange(final String uri);

	G namedIndividual(final String uri);

	G literal(String s);

	G literal(String s, String lang);

	G typedLiteral(String s, String datatype);

	G getThing();

	G getNothing();

	G getBottomObjectProperty();

	G getBottomDataProperty();

	G getTopDatatype();

	G getTopDataProperty();

	G getTopObjectProperty();

	G objectAllValuesFrom(final G ope, final G ce);

	G objectComplementOf(final G c);

	G objectHasSelf(final G ope);

	G objectHasValue(final G ope, final G ni);

	G objectIntersectionOf(final Set<G> c);

	G objectMinCardinality(final int card, final G ope, final G ce);

	G objectMaxCardinality(final int card, final G ope, final G ce);

	G objectExactCardinality(final int card, final G ope, G ce);

	G objectOneOf(final Set<G> nis);

	G objectSomeValuesFrom(final G ope, final G ce);

	G objectUnionOf(final Set<G> set);

	G dataAllValuesFrom(final G ope, final G ce);

	G dataHasValue(final G ope, final G ni);

	G dataIntersectionOf(final Set<G> c);

	G dataUnionOf(final Set<G> c);

	G dataMinCardinality(final int card, final G ope, final G dr);

	G dataMaxCardinality(final int card, final G ope, final G dr);

	G dataExactCardinality(final int card, final G ope, final G dr);

	G dataOneOf(final Set<G> nis);

	G dataSomeValuesFrom(final G ope, final G ce);

	G inverseObjectProperty(final G op);
}
