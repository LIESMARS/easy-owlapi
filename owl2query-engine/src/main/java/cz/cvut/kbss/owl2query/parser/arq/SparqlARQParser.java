/*******************************************************************************
 * Copyright (C) 2011 Czech Technical University in Prague                                                                                                                                                        
 *                                                                                                                                                                                                                
 * This program is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your option) any 
 * later version. 
 *                                                                                                                                                                                                                
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
 * details. You should have received a copy of the GNU General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package cz.cvut.kbss.owl2query.parser.arq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.datatypes.BaseDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.lang.MyParserSPARQL10;
import com.hp.hpl.jena.sparql.lang.Parser;
import com.hp.hpl.jena.sparql.lang.ParserFactory;
import com.hp.hpl.jena.sparql.lang.ParserRegistry;
import com.hp.hpl.jena.sparql.serializer.MySerializer;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.util.IndentedLineBuffer;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import cz.cvut.kbss.owl2query.UnsupportedQueryException;
import cz.cvut.kbss.owl2query.engine.InternalQuery;
import cz.cvut.kbss.owl2query.engine.QueryAtom;
import cz.cvut.kbss.owl2query.model.AllValuesFrom;
import cz.cvut.kbss.owl2query.model.Configuration;
import cz.cvut.kbss.owl2query.model.ExactCardinality;
import cz.cvut.kbss.owl2query.model.GroundTerm;
import cz.cvut.kbss.owl2query.model.HasValue;
import cz.cvut.kbss.owl2query.model.IntersectionOf;
import cz.cvut.kbss.owl2query.model.MaxCardinality;
import cz.cvut.kbss.owl2query.model.MinCardinality;
import cz.cvut.kbss.owl2query.model.OWL2Ontology;
import cz.cvut.kbss.owl2query.model.OWL2Query;
import cz.cvut.kbss.owl2query.model.OWL2QueryFactory;
import cz.cvut.kbss.owl2query.model.OWLObjectType;
import cz.cvut.kbss.owl2query.model.ObjectComplementOf;
import cz.cvut.kbss.owl2query.model.ObjectHasSelf;
import cz.cvut.kbss.owl2query.model.SomeValuesFrom;
import cz.cvut.kbss.owl2query.model.Term;
import cz.cvut.kbss.owl2query.model.UnionOf;
import cz.cvut.kbss.owl2query.model.VarType;
import cz.cvut.kbss.owl2query.model.Variable;
import cz.cvut.kbss.owl2query.parser.QueryParseException;
import cz.cvut.kbss.owl2query.parser.QueryParser;
import cz.cvut.kbss.owl2query.parser.QueryWriter;

public class SparqlARQParser<G> implements QueryParser<G>, QueryWriter<G> {
	private Set<Triple> triples;

	private Map<Node, Term<G>> terms;

	private Map<Node, List<Term<G>>> lists;

	private OWL2Ontology<G> ont;

	private OWL2QueryFactory<G> f;

	/**
	 * {@inheritDoc}
	 */
	public OWL2Query<G> parse(final InputStream stream, final OWL2Ontology<G> kb)
			throws QueryParseException {
		try {
			final BufferedReader r = new BufferedReader(new InputStreamReader(
					stream));

			final StringBuffer queryString = new StringBuffer();
			String line;
			while ((line = r.readLine()) != null) {
				queryString.append(line).append("\n");
			}

			return parse(queryString.toString(), kb);
		} catch (final IOException e) {
			throw new QueryParseException(
					"Error creating a reader from the input stream.", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public OWL2Query<G> parse(final String queryStr, final OWL2Ontology<G> kb)
			throws QueryParseException {

		// Register parser factory that preserves bnode labels
		ParserRegistry.addFactory(Syntax.syntaxSPARQL, new ParserFactory() {
			public boolean accept(Syntax syntax) {
				return Syntax.syntaxSPARQL.equals(syntax);
			}

			public Parser create(Syntax syntax) {
				return new MyParserSPARQL10();
			}
		});

		return parse(QueryFactory.create(queryStr, Syntax.syntaxSPARQL), kb);
	}

	private OWL2Query<G> parse(final com.hp.hpl.jena.query.Query sparqlQuery,
			final OWL2Ontology<G> ontology) throws QueryParseException {
		this.ont = ontology;
		this.f = ontology.getFactory();

		if (sparqlQuery.isDescribeType())
			throw new UnsupportedQueryException(
					"DESCRIBE queries are not supported.");

		// MEANWHILE ONLY ONE QUERY GRAPH - named graphs, optionals, filters,
		// etc. to be added later.
		final Element pattern = sparqlQuery.getQueryPattern();

		if (!(pattern instanceof ElementGroup))
			throw new UnsupportedQueryException(
					"ElementGroup was expected, but found '"
							+ pattern.getClass() + "'.");

		final ElementGroup elementGroup = (ElementGroup) pattern;
		final List<Element> elements = elementGroup.getElements();

		if (elements.size() != 1
				|| !(elements.get(0) instanceof ElementTriplesBlock)) {
			System.out.println("elements = " + elements);
			throw new UnsupportedQueryException(
					"Complex query patterns are not supported yet.");
		}
		final Element first = elements.get(0);

		// very important to call this function so that getResultVars() will
		// work fine for SELECT * queries
		sparqlQuery.setResultVars();

		final OWL2Query<G> query = this.f.createQuery(ont).distinct(
				sparqlQuery.isDistinct());

		for (final Iterator<String> i = sparqlQuery.getResultVars().iterator(); i
				.hasNext();) {
			query.addResultVar(f.variable(i.next()));
		}

		parse(((ElementTriplesBlock) first).getPattern(), query);

		return query;
	}

	private void parse(final BasicPattern basicPattern, final OWL2Query<G> query)
			throws UnsupportedQueryException {
		terms = new HashMap<Node, Term<G>>();
		lists = new HashMap<Node, List<Term<G>>>();
		triples = new LinkedHashSet<Triple>(basicPattern.getList());

		for (final Triple t : new ArrayList<Triple>(triples)) {
			final Node subj = t.getSubject();
			final Node pred = t.getPredicate();
			final Node obj = t.getObject();

			terms.put(subj, node2term(subj, null));
			terms.put(pred, node2term(pred, null));
			terms.put(obj, node2term(obj, null));
		}

		for (final Triple t : triples) {

			final Node subj = t.getSubject();
			final Node pred = t.getPredicate();
			final Node obj = t.getObject();

			final Term<G> s = terms.get(subj);
			final Term<G> p = terms.get(pred);
			final Term<G> o = terms.get(obj);

			if (pred.equals(RDF.Nodes.type)) {
				// ObjectProperty(p)
				if (obj.equals(OWL2.ObjectProperty.asNode())) {
					query.ObjectProperty(s);
					setupPropertyTerm(subj, s, null, query);
				}

				// DatatypeProperty(p)
				else if (obj.equals(OWL2.DatatypeProperty.asNode())) {
					query.DatatypeProperty(s);
					setupPropertyTerm(subj, s, null, query);
				}

				// Property(p)
				else if (obj.equals(RDF.Property.asNode())) {
					setupPropertyTerm(subj, s, null, query);
				}

				// Functional(p)
				else if (obj.equals(OWL2.FunctionalProperty.asNode())) {
					query.Functional(s);
					setupPropertyTerm(subj, s, null, query);
				}

				// InverseFunctional(p)
				else if (obj.equals(OWL2.InverseFunctionalProperty.asNode())) {
					query.InverseFunctional(s);
					setupPropertyTerm(subj, s, null, query);
				}

				// Reflexive(p)
				else if (obj.equals(OWL2.ReflexiveProperty.asNode())) {
					query.Reflexive(s);
					setupPropertyTerm(subj, s, null, query);
				}

				// Reflexive(p)
				else if (obj.equals(OWL2.IrreflexiveProperty.asNode())) {
					query.Irreflexive(s);
					setupPropertyTerm(subj, s, null, query);
				}

				// Transitive(p)
				else if (obj.equals(OWL2.TransitiveProperty.asNode())) {
					query.Transitive(s);
					setupPropertyTerm(subj, s, null, query);
				}

				// Symmetric(p)
				else if (obj.equals(OWL2.SymmetricProperty.asNode())) {
					query.Symmetric(s);
					setupPropertyTerm(subj, s, null, query);
				}

				// Asymmetric(p)
				else if (obj.equals(OWL2.AsymmetricProperty.asNode())) {
					query.Asymmetric(s);
					setupPropertyTerm(subj, s, null, query);
				}

				// TODO AnnotationProperty(p)
				// else if (hasObject(subj, RDF.type.asNode(),
				// OWL2.AnnotationProperty.asNode())) {
				// query.add(factory.AnnotationAtom(s, p, o));
				// if (s.isVariable() || p.isVariable() || o.isVariable()) {
				// throw new IllegalArgumentException(
				// "Variables in annotation atom are not supported.");
				// }
				// }

				// Type(c,i)
				else {
					query.Type(o, s);
					setupIndividualTerm(subj, s, query);
					setupClassTerm(obj, o, query);
				}
			}

			// SameAs(i1,i2)
			else if (pred.equals(OWL2.sameAs.asNode())) {
				query.SameAs(s, o);
				setupIndividualTerm(subj, s, query);
				setupIndividualTerm(obj, o, query);

			}

			// DifferentFrom(i1,i2)
			else if (pred.equals(OWL2.differentFrom.asNode())) {
				query.DifferentFrom(s, o);
				setupIndividualTerm(subj, s, query);
				setupIndividualTerm(obj, o, query);
			}

			// SubClassOf(c1,c2)
			else if (pred.equals(RDFS.subClassOf.asNode())) {
				query.SubClassOf(s, o);
				setupClassTerm(subj, s, query);
				setupClassTerm(obj, o, query);
			}

			// StrictSubClassOf(cSub, cSup) - nonmonotonic
			else if (pred.equals(SparqlDL.strictSubClassOf.asNode())) {
				query.StrictSubClassOf(s, o);
				setupClassTerm(subj, s, query);
				setupClassTerm(obj, o, query);
			}

			// DirectSubClassOf(cSub, cSup) - nonmonotonic
			else if (pred.equals(SparqlDL.directSubClassOf.asNode())) {
				query.DirectSubClassOf(s, o);
				setupClassTerm(subj, s, query);
				setupClassTerm(obj, o, query);
			}

			// EquivalentClass(c1,c2)
			else if (pred.equals(OWL2.equivalentClass.asNode())) {
				query.EquivalentClass(s, o);
				setupClassTerm(subj, s, query);
				setupClassTerm(obj, o, query);
			}

			// DisjointWith(c1,c2)
			else if (pred.equals(OWL2.disjointWith.asNode())) {
				query.DisjointWith(s, o);
				setupClassTerm(subj, s, query);
				setupClassTerm(obj, o, query);
			}

			// ComplementOf(c1,c2)
			else if (pred.equals(OWL2.complementOf.asNode())) {
				query.ComplementOf(s, o);
				setupClassTerm(subj, s, query);
				setupClassTerm(obj, o, query);
			}

			// SubPropertyOf(pSub,pSup)
			else if (pred.equals(RDFS.subPropertyOf.asNode())) {
				query.SubPropertyOf(s, o);
				setupPropertyTerm(subj, s, null, query);
				setupPropertyTerm(obj, o, null, query);
			}

			// StrictSubPropertyOf(pSub,pSup) - nonmonotonic
			else if (pred.equals(SparqlDL.strictSubPropertyOf.asNode())) {
				query.StrictSubPropertyOf(s, o);
				setupPropertyTerm(subj, s, null, query);
				setupPropertyTerm(obj, o, null, query);
			}

			// DirectSubPropertyOf(pSub,pSup) - nonmonotonic
			else if (pred.equals(SparqlDL.directSubPropertyOf.asNode())) {
				query.DirectSubPropertyOf(s, o);
				setupPropertyTerm(subj, s, null, query);
				setupPropertyTerm(obj, o, null, query);
			}

			// EquivalentProperty(p1,p2)
			else if (pred.equals(OWL2.equivalentProperty.asNode())) {
				query.EquivalentProperty(s, o);
				setupPropertyTerm(subj, s, null, query);
				setupPropertyTerm(obj, o, null, query);
			}

			// InverseOf(p1,p2)
			else if (pred.equals(OWL2.inverseOf.asNode())) {
				query.InverseOf(s, o);
				setupPropertyTerm(subj, s, null, query);
				setupPropertyTerm(obj, o, null, query);
			}

			// DirectType(i,c) - nonmonotonic
			else if (pred.equals(SparqlDL.directType.asNode())) {
				query.DirectType(o, s);
				setupIndividualTerm(subj, s, query);
				setupClassTerm(obj, o, query);
			}

			// TODO annotations
			// else if (!p.isVariable()
			// && ontology.isAnnotationProperty(p.asGroundTerm()
			// .getWrappedObject())) {
			// query.add(factory.AnnotationAtom(s, p, o));
			// if (s.isVariable()) {
			// ensureDistinguished(subj);
			// query.addDistVar(s.asVariable(), VarType.PROPERTY);
			// }
			// if (o.getTermType().equals(TermType.Variable)) {
			// ensureDistinguished(obj);
			// query.addDistVar(o.asVariable(), VarType.PROPERTY);
			// }
			// // throw new UnsupportedQueryException(
			// // "Annotation properties are not supported in queries." );
			// }

			// PropertyValue(p,i,j)
			else {
				query.PropertyValue(p, s, o);
				setupIndividualTerm(subj, s, query);
				setupPropertyTerm(pred, p, o, query);
				setupIndividualOrLiteralTerm(obj, o, p, query);
			}
		}

		// throw new UnsupportedQueryException(
		// "Queries with variable predicates are not supported "
		// + "(add the pattern {?p rdf:type owl:ObjectProperty} or"
		// + " {?p rdf:type owl:DatatypeProperty} to the query)");

	}

	private void setupIndividualOrLiteralTerm(final Node oN, final Term<G> oT,
			final Term<G> pT, final OWL2Query<G> query) {
		if (isDistinguishedVariable(oN)) {
			// VarType vt = VarType.INDIVIDUAL_OR_LITERAL;
			//
			// if ((pT != null) && (pT.isGround())) {
			// final G g = pT.asGroundTerm().getWrappedObject();
			//
			// if (ont.isDataProperty(g)) {
			// vt = VarType.LITERAL;
			// } else if (ont.isObjectProperty(g)) {
			// vt = VarType.INDIVIDUAL;
			// } else if (log.isLoggable(Level.WARNING)) {
			// log
			// .warning("The term '"
			// + g
			// +
			// "' is neither a data property nor an object property, yet it is used as a property in a property assertion.");
			// }
			// }

			query.addDistVar(oT.asVariable());
		}
	}

	private void setupIndividualTerm(final Node pred, final Term<G> term,
			final OWL2Query<G> query) {
		if (isDistinguishedVariable(pred)) {
			query.addDistVar(term.asVariable());
		}
	}

	private void setupClassTerm(final Node pred, final Term<G> term,
			final OWL2Query<G> query) throws UnsupportedQueryException {
		if (term.isVariable()) {
			ensureDistinguished(pred);
			query.addDistVar(term.asVariable());
		} else if (!term.isGround()) {
			for (final Variable<G> var : term.getVariables()) {
				query.addDistVar(var);
			}
		}
	}

	private void setupPropertyTerm(final Node pN, final Term<G> pT,
			final Term<G> oT, final OWL2Query<G> query)
			throws UnsupportedQueryException {
		if (pT.isVariable()) {
			ensureDistinguished(pN);

			// VarType vt;
			// if (type == null) {
			// vt = VarType.OBJECT_OR_DATA_PROPERTY;
			// if ((oT != null) && (oT.isGround())) {
			// final G g = oT.asGroundTerm().getWrappedObject();
			//
			// if (ont.isLiteral(g)) {
			// vt = VarType.DATA_PROPERTY;
			// } else if (ont.isIndividual(g)) {
			// vt = VarType.OBJECT_PROPERTY;
			// } else if (log.isLoggable(Level.WARNING)) {
			// log
			// .warning("The term '"
			// + g
			// +
			// "' is neither a literal nor an individual, yet it is used as an object in a property assertion.");
			// }
			// }
			// } else {
			// vt = type;
			// }

			query.addDistVar(pT.asVariable());
		} else if (!pT.isGround()) {
			for (final Variable<G> var : pT.getVariables()) {
				query.addDistVar(var);
			}
		}
	}

	private static void ensureDistinguished(final Node pred)
			throws UnsupportedQueryException {
		if (!isDistinguishedVariable(pred)) {
			throw new UnsupportedQueryException(
					"Non-distinguished variables in class and predicate positions are not supported : "
							+ pred);
		}
	}

	private static boolean isDistinguishedVariable(final Node node) {
		return Var.isVar(node)
				&& (Var.isNamedVar(node) || Configuration.TREAT_ALL_VARS_DISTINGUISHED);
	}

	private Node getObject(final Node subj, final Node pred) {
		for (final Iterator<Triple> i = triples.iterator(); i.hasNext();) {
			final Triple t = i.next();
			if (subj.equals(t.getSubject()) && pred.equals(t.getPredicate())) {
				i.remove();
				return t.getObject();
			}
		}

		return null;
	}

	private boolean hasObject(Node subj, Node pred) {
		for (final Iterator<Triple> i = triples.iterator(); i.hasNext();) {
			final Triple t = i.next();
			if (subj.equals(t.getSubject()) && pred.equals(t.getPredicate()))
				return true;
		}

		return false;
	}

	private boolean hasObject(Node subj, Node pred, Node obj,
			boolean strictParsing) throws UnsupportedQueryException {
		for (final Iterator<Triple> i = triples.iterator(); i.hasNext();) {
			final Triple t = i.next();
			if (subj.equals(t.getSubject()) && pred.equals(t.getPredicate())) {
				if (obj.equals(t.getObject())) {
					i.remove();
					return true;
				}
				if (strictParsing) {
					throw new UnsupportedQueryException("Expecting rdf:type "
							+ obj + " but found rdf:type " + t.getObject());
				}
			}
		}

		return false;
	}

	// private G getLiteralDatatype(Node subj, Node pred, Node obj)
	// throws UnsupportedQueryException {
	// if (!obj.isLiteral()) {
	// throw new UnsupportedQueryException("Expected literal : " + obj);
	// }
	// if (obj.getLiteralDatatypeURI() == null) {
	// return null;
	// } else {
	// return ont.getFactory().namedDataRange(obj.getLiteralDatatypeURI());
	// }
	// }

	private List<Term<G>> createList(final Node node, VarType type)
			throws UnsupportedQueryException {
		if (node.equals(RDF.nil.asNode()))
			return Collections.emptyList();
		else if (terms.containsKey(node))
			return lists.get(node);

		hasObject(node, RDF.type.asNode(), RDF.List.asNode(), true);

		final Node first = getObject(node, RDF.first.asNode());
		final Node rest = getObject(node, RDF.rest.asNode());

		if (first == null || rest == null) {
			throw new UnsupportedQueryException("Invalid list structure: List "
					+ node + " does not have a "
					+ (first == null ? "rdf:first" : "rdf:rest") + " property.");
		}

		final List<Term<G>> list = new ArrayList<Term<G>>();
		list.add(node2term(first, type));
		list.addAll(createList(rest, type));

		lists.put(node, list);

		return list;
	}

	private Term<G> createRestriction(final Node node)
			throws UnsupportedQueryException {
		Term<G> t = f.wrap(f.getThing());

		hasObject(node, RDF.type.asNode(), OWL2.Restriction.asNode(), true);

		final Node p = getObject(node, OWL2.onProperty.asNode());

		if (p == null) {
			log.warning("No triple with owl:onProperty as predicate was found for "
					+ node);
			return t;
		}

		if (hasObject(node, OWL2.onProperty.asNode())) {
			log.warning("Multiple triples with owl:onProperty as predicate were found for "
					+ node + ", taking " + node);
		}

		final Term<G> pt = node2term(p, VarType.OBJECT_OR_DATA_PROPERTY);

		// final G ptGT = pt.asGroundTerm().getWrappedObject();

		if (pt.isGround()
				&& !ont.is(pt.asGroundTerm().getWrappedObject(),
						OWLObjectType.OWLDataProperty,
						OWLObjectType.OWLAnnotationProperty,
						OWLObjectType.OWLObjectProperty))
			throw new UnsupportedQueryException("Property " + pt
					+ " is not referenced in the ontology.");

		// for qualified cardinalities
		final Node clazz = getObject(node, OWL2.onClass.asNode());

		Node o = null;
		if ((o = getObject(node, OWL2.hasValue.asNode())) != null) {
			t = hasValue(pt, node2term(o, VarType.INDIVIDUAL_OR_LITERAL));
		} else if (hasObject(node, OWL2.hasSelf.asNode(), ResourceFactory
				.createTypedLiteral(Boolean.TRUE).asNode(), true)) {
			t = objectHasSelf(pt);
		} else if ((o = getObject(node, OWL2.allValuesFrom.asNode())) != null) {
			t = allValuesFrom(pt, node2term(o, VarType.CLASS));
		} else if ((o = getObject(node, OWL2.someValuesFrom.asNode())) != null) {
			t = someValuesFrom(pt, node2term(o, VarType.CLASS));
		} else if ((o = getObject(node, OWL2.minCardinality.asNode())) != null) {
			int cardinality = Integer.parseInt(o.getLiteral().getLexicalForm());
			t = minCardinality(cardinality, pt);
		} else if ((o = getObject(node, OWL2.minQualifiedCardinality.asNode())) != null) {
			if (clazz == null) {
				log.warning("No triple with owl:onClass as predicate was found for "
						+ node);
			}

			int cardinality = Integer.parseInt(o.getLiteral().getLexicalForm());
			t = minCardinality(cardinality, pt, node2term(clazz, VarType.CLASS));
		} else if ((o = getObject(node, OWL2.maxCardinality.asNode())) != null) {
			int cardinality = Integer.parseInt(o.getLiteral().getLexicalForm());
			t = maxCardinality(cardinality, pt);
		} else if ((o = getObject(node, OWL2.maxQualifiedCardinality.asNode())) != null) {
			if (clazz == null) {
				log.warning("No triple with owl:onClass as predicate was found for "
						+ node);
			}
			int cardinality = Integer.parseInt(o.getLiteral().getLexicalForm());
			t = maxCardinality(cardinality, pt, node2term(clazz, VarType.CLASS));
		} else if ((o = getObject(node, OWL2.cardinality.asNode())) != null) {
			int cardinality = Integer.parseInt(o.getLiteral().getLexicalForm());
			t = exactCardinality(cardinality, pt);
		} else if ((o = getObject(node, OWL2.qualifiedCardinality.asNode())) != null) {
			if (clazz == null) {
				log.warning("No triple with owl:onClass as predicate was found for "
						+ node);
			}

			int cardinality = Integer.parseInt(o.getLiteral().getLexicalForm());
			t = exactCardinality(cardinality, pt, terms.get(clazz));
		} else {
			throw new UnsupportedQueryException("Unknown restriction type: "
					+ o);
		}

		return t;
	}

	public Term<G> someValuesFrom(final Term<G> ope, final Term<G> ce) {
		if (ce.isGround() && ope.isGround()) {
			final G ceG = ce.asGroundTerm().getWrappedObject();
			final G opeG = ope.asGroundTerm().getWrappedObject();

			if (ont.is(opeG, OWLObjectType.OWLDataProperty)) {
				return f.wrap(f.dataSomeValuesFrom(opeG, ceG));
			} else {
				return f.wrap(f.objectSomeValuesFrom(opeG, ceG));
			}
		} else {
			return new SomeValuesFrom<G>(ope, ce) {

				public Term<G> apply(Map<Variable<G>, GroundTerm<G>> binding) {
					final Term<G> ce2 = ce.apply(binding);
					final Term<G> ope2 = ope.apply(binding);

					return someValuesFrom(ope2, ce2);
				}

				public VarType getVariableType(Variable<G> var) {
					if (var.equals(terms.get(0))) {
						return VarType.OBJECT_OR_DATA_PROPERTY;
					} else if (var.equals(terms.get(1))) {
						return VarType.CLASS;
					} else {
						throw new IllegalArgumentException();
					}
				}
			};
		}
	}

	public Term<G> allValuesFrom(final Term<G> ope, final Term<G> ce) {
		if (ce.isGround() && ope.isGround()) {
			final G ceG = ce.asGroundTerm().getWrappedObject();
			final G opeG = ope.asGroundTerm().getWrappedObject();

			if (ont.is(opeG, OWLObjectType.OWLDataProperty)) {
				return f.wrap(f.dataAllValuesFrom(opeG, ceG));
			} else {
				return f.wrap(f.objectAllValuesFrom(opeG, ceG));
			}
		} else {
			return new AllValuesFrom<G>(ope, ce) {

				public Term<G> apply(Map<Variable<G>, GroundTerm<G>> binding) {
					final Term<G> ce2 = ce.apply(binding);
					final Term<G> ope2 = ope.apply(binding);

					return allValuesFrom(ope2, ce2);
				}

				public VarType getVariableType(Variable<G> var) {
					if (var.equals(terms.get(0))) {
						return VarType.OBJECT_OR_DATA_PROPERTY;
					} else if (var.equals(terms.get(1))) {
						return VarType.CLASS;
					} else {
						throw new IllegalArgumentException();
					}
				}
			};
		}
	}

	public Term<G> objectComplementOf(final Term<G> c) {
		if (c.isGround()) {
			return f.wrap(f.objectComplementOf(c.asGroundTerm()
					.getWrappedObject()));
		} else {
			return new ObjectComplementOf<G>(c) {

				public Term<G> apply(Map<Variable<G>, GroundTerm<G>> binding) {
					final Term<G> c2 = c.apply(binding);
					return objectComplementOf(c2);
				}

				public VarType getVariableType(Variable<G> var) {
					if (var.equals(terms.get(0))) {
						return VarType.CLASS;
					} else {
						throw new IllegalArgumentException();
					}
				}
			};
		}
	}

	public Term<G> exactCardinality(final int card, final Term<G> ope,
			final Term<G> ce) {
		if (ce.isGround() && ope.isGround()) {
			final G ceG = ce.asGroundTerm().getWrappedObject();
			final G opeG = ope.asGroundTerm().getWrappedObject();

			if (ont.is(opeG, OWLObjectType.OWLDataProperty)) {
				return f.wrap(f.dataExactCardinality(card, opeG, ceG));
			} else {
				return f.wrap(f.objectExactCardinality(card, opeG, ceG));
			}
		} else {
			return new ExactCardinality<G>(card, ope, ce) {

				public Term<G> apply(Map<Variable<G>, GroundTerm<G>> binding) {
					final Term<G> ce2 = ce.apply(binding);
					final Term<G> ope2 = ope.apply(binding);
					return exactCardinality(card, ope2, ce2);
				}

				public VarType getVariableType(Variable<G> var) {
					if (var.equals(terms.get(0))) {
						return VarType.OBJECT_OR_DATA_PROPERTY;
					} else if (var.equals(terms.get(1))) {
						return VarType.CLASS;
					} else {
						throw new IllegalArgumentException();
					}
				}
			};
		}
	}

	public Term<G> exactCardinality(final int card, final Term<G> ope) {
		if (ope.isGround()) {
			final G opeG = ope.asGroundTerm().getWrappedObject();

			if (ont.is(opeG, OWLObjectType.OWLDataProperty)) {
				return f.wrap(f.dataExactCardinality(card, opeG,
						f.getTopDatatype()));
			} else {
				return f.wrap(f.objectExactCardinality(card, opeG, f.getThing()));
			}
		} else {
			return new ExactCardinality<G>(card, ope) {

				public Term<G> apply(Map<Variable<G>, GroundTerm<G>> binding) {
					return exactCardinality(card, ope.apply(binding));
				}

				public VarType getVariableType(Variable<G> var) {
					if (var.equals(terms.get(0))) {
						return VarType.OBJECT_OR_DATA_PROPERTY;
					} else if (var.equals(terms.get(1))) {
						return VarType.CLASS;
					} else {
						throw new IllegalArgumentException();
					}
				}
			};
		}
	}

	public Term<G> objectHasSelf(final Term<G> ope) {
		if (ope.isGround()) {
			final G opeG = ope.asGroundTerm().getWrappedObject();
			return f.wrap(f.objectHasSelf(opeG));
		} else {
			return new ObjectHasSelf<G>(ope) {

				public Term<G> apply(Map<Variable<G>, GroundTerm<G>> binding) {
					return objectHasSelf(ope.apply(binding));
				}

				public VarType getVariableType(Variable<G> var) {
					if (var.equals(terms.get(0))) {
						return VarType.OBJECT_OR_DATA_PROPERTY;
					} else {
						throw new IllegalArgumentException();
					}
				}
			};
		}
	}

	public Term<G> hasValue(final Term<G> ope, final Term<G> ni) {
		if (ni.isGround() && ope.isGround()) {
			final G niG = ni.asGroundTerm().getWrappedObject();
			final G opeG = ope.asGroundTerm().getWrappedObject();

			if (ont.is(opeG, OWLObjectType.OWLDataProperty)) {
				return f.wrap(f.dataHasValue(opeG, niG));
			} else {
				return f.wrap(f.objectHasValue(opeG, niG));
			}
		} else {
			return new HasValue<G>(ope, ni) {

				public Term<G> apply(Map<Variable<G>, GroundTerm<G>> binding) {
					final Term<G> ce2 = ni.apply(binding);
					final Term<G> ope2 = ope.apply(binding);

					return hasValue(ope2, ce2);
				}

				public VarType getVariableType(Variable<G> var) {
					if (var.equals(terms.get(0))) {
						return VarType.OBJECT_OR_DATA_PROPERTY;
					} else if (var.equals(terms.get(1))) {
						return VarType.INDIVIDUAL;
					} else {
						throw new IllegalArgumentException();
					}
				}
			};
		}
	}

	public Term<G> maxCardinality(final int card, final Term<G> ope,
			final Term<G> ce) {
		if (ce.isGround() && ope.isGround()) {
			final G ceG = ce.asGroundTerm().getWrappedObject();
			final G opeG = ope.asGroundTerm().getWrappedObject();

			if (ont.is(opeG, OWLObjectType.OWLDataProperty)) {
				return f.wrap(f.dataMaxCardinality(card, opeG, ceG));
			} else {
				return f.wrap(f.objectMaxCardinality(card, opeG, ceG));
			}
		} else {
			return new MaxCardinality<G>(card, ope, ce) {

				public Term<G> apply(Map<Variable<G>, GroundTerm<G>> binding) {
					final Term<G> ce2 = ce.apply(binding);
					final Term<G> ope2 = ope.apply(binding);
					return maxCardinality(card, ope2, ce2);
				}

				public VarType getVariableType(Variable<G> var) {
					if (var.equals(terms.get(0))) {
						return VarType.OBJECT_OR_DATA_PROPERTY;
					} else if (var.equals(terms.get(1))) {
						return VarType.CLASS;
					} else {
						throw new IllegalArgumentException();
					}
				}
			};
		}
	}

	public Term<G> maxCardinality(final int card, final Term<G> ope) {
		if (ope.isGround()) {
			final G opeG = ope.asGroundTerm().getWrappedObject();

			if (ont.is(opeG, OWLObjectType.OWLDataProperty)) {
				return f.wrap(f.dataMaxCardinality(card, opeG,
						f.getTopDatatype()));
			} else {
				return f.wrap(f.objectMaxCardinality(card, opeG, f.getThing()));
			}
		} else {
			return new MaxCardinality<G>(card, ope) {

				public Term<G> apply(Map<Variable<G>, GroundTerm<G>> binding) {
					return maxCardinality(card, ope.apply(binding));
				}

				public VarType getVariableType(Variable<G> var) {
					if (var.equals(terms.get(0))) {
						return VarType.OBJECT_OR_DATA_PROPERTY;
					} else if (var.equals(terms.get(1))) {
						return VarType.CLASS;
					} else {
						throw new IllegalArgumentException();
					}
				}
			};
		}
	}

	public Term<G> minCardinality(final int card, final Term<G> ope) {
		if (ope.isGround()) {
			final G opeG = ope.asGroundTerm().getWrappedObject();

			if (ont.is(opeG, OWLObjectType.OWLDataProperty)) {
				return f.wrap(f.dataMinCardinality(card, opeG,
						f.getTopDatatype()));
			} else {
				return f.wrap(f.objectMinCardinality(card, opeG, f.getThing()));
			}
		} else {
			return new MinCardinality<G>(card, ope) {

				public Term<G> apply(Map<Variable<G>, GroundTerm<G>> binding) {
					return minCardinality(card, ope.apply(binding));
				}

				public VarType getVariableType(Variable<G> var) {
					if (var.equals(terms.get(0))) {
						return VarType.OBJECT_OR_DATA_PROPERTY;
					} else if (var.equals(terms.get(1))) {
						return VarType.CLASS;
					} else {
						throw new IllegalArgumentException();
					}
				}
			};
		}
	}

	public Term<G> minCardinality(final int card, final Term<G> ope,
			final Term<G> ce) {
		if (ce.isGround() && ope.isGround()) {
			final G ceG = ce.asGroundTerm().getWrappedObject();
			final G opeG = ope.asGroundTerm().getWrappedObject();

			if (ont.is(opeG, OWLObjectType.OWLDataProperty)) {
				return f.wrap(f.dataMinCardinality(card, opeG, ceG));
			} else {
				return f.wrap(f.objectMinCardinality(card, opeG, ceG));
			}
		} else {
			return new MinCardinality<G>(card, ope, ce) {

				public Term<G> apply(Map<Variable<G>, GroundTerm<G>> binding) {
					final Term<G> ce2 = ce.apply(binding);
					final Term<G> ope2 = ope.apply(binding);
					return minCardinality(card, ope2, ce2);
				}

				public VarType getVariableType(Variable<G> var) {
					if (var.equals(terms.get(0))) {
						return VarType.OBJECT_OR_DATA_PROPERTY;
					} else if (var.equals(terms.get(1))) {
						return VarType.CLASS;
					} else {
						throw new IllegalArgumentException();
					}
				}
			};
		}
	}

	private Term<G> unionOf(Set<Term<G>> c) {
		boolean ground = true;

		Set<Term<G>> terms = new HashSet<Term<G>>();
		Set<G> ces = new HashSet<G>();

		boolean data = false;

		for (final Term<G> o : c) {
			if (!o.isGround()) {
				ground = false;
				break;
			} else {
				G g = o.asGroundTerm().getWrappedObject();
				data |= !ont.is(g, OWLObjectType.OWLClass);
				ces.add(g);
			}
		}

		if (ground) {
			if (data) {
				return f.wrap(f.dataUnionOf(ces));
			} else {
				return f.wrap(f.objectUnionOf(ces));
			}

		} else {
			return new UnionOf<G>(c) {

				public Term<G> apply(Map<Variable<G>, GroundTerm<G>> binding) {
					Set<Term<G>> nts = new HashSet<Term<G>>();

					for (final Term<G> x : this.terms) {
						nts.add(x.apply(binding));
					}
					return unionOf(nts);
				}

				public VarType getVariableType(Variable<G> var) {
					if (terms.contains(var)) {
						return VarType.CLASS;
					} else {
						throw new IllegalArgumentException();
					}
				}
			};
		}
	}

	public Term<G> intersectionOf(Set<Term<G>> c) {
		boolean ground = true;

		Set<Term<G>> terms = new HashSet<Term<G>>();
		Set<G> ces = new HashSet<G>();

		boolean data = false;

		for (final Term<G> o : c) {
			if (!o.isGround()) {
				ground = false;
				break;
			} else {
				G g = o.asGroundTerm().getWrappedObject();
				data |= !ont.is(g, OWLObjectType.OWLClass);
				ces.add(g);
			}
		}

		if (ground) {
			if (data) {
				return f.wrap(f.dataIntersectionOf(ces));
			} else {
				return f.wrap(f.objectIntersectionOf(ces));
			}
		} else {
			return new IntersectionOf<G>(c) {

				public Term<G> apply(Map<Variable<G>, GroundTerm<G>> binding) {
					Set<Term<G>> nts = new HashSet<Term<G>>();

					for (final Term<G> x : this.terms) {
						nts.add(x.apply(binding));
					}
					return intersectionOf(nts);
				}

				public VarType getVariableType(Variable<G> var) {
					if (terms.contains(var)) {
						return VarType.CLASS;
					} else {
						throw new IllegalArgumentException();
					}
				}
			};
		}
	}

	private Set<G> asGroundTerms(Collection<Term<G>> t) {
		final Set<G> s = new HashSet<G>();

		for (final Term<G> x : t) {
			s.add(x.asGroundTerm().getWrappedObject());
		}

		return s;
	}

	private Term<G> node2term(Node node, VarType type)
			throws UnsupportedQueryException {
		Term<G> t = (Term<G>) terms.get(node);

		if (t == null) {
			if (node.equals(OWL2.Thing.asNode()))
				return f.wrap(f.getThing());
			else if (node.equals(OWL2.Nothing.asNode()))
				return f.wrap(f.getNothing());
			else if (node.equals(OWL2.topObjectProperty.asNode()))
				return f.wrap(f.getTopObjectProperty());
			else if (node.equals(OWL2.bottomObjectProperty.asNode())) {
				return f.wrap(f.getBottomObjectProperty());
			} else if (node.equals(OWL2.topDataProperty.asNode()))
				return f.wrap(f.getTopDataProperty());
			else if (node.equals(OWL2.bottomDataProperty.asNode()))
				return f.wrap(f.getBottomDataProperty());
			else if (node.equals(RDF.type.asNode()))
				return null;
			else if (node.isLiteral()) {
				final LiteralLabel jenaLiteral = node.getLiteral();

				final String lexicalValue = jenaLiteral.getLexicalForm();
				final String datatypeURI = jenaLiteral.getDatatypeURI();
				G literalValue = null;

				if (datatypeURI != null) {
					literalValue = f.typedLiteral(lexicalValue, datatypeURI);
				} else if (jenaLiteral.language() != null) {
					literalValue = f.literal(lexicalValue,
							jenaLiteral.language());
				} else {
					literalValue = f.literal(lexicalValue);
				}

				return f.wrap(literalValue);
			} else if (hasObject(node, OWL2.onProperty.asNode())) {
				t = createRestriction(node);
				terms.put(node, t);
			} else if (node.isBlank() || node.isVariable()) {
				Node o = null;
				if ((o = getObject(node, OWL2.intersectionOf.asNode())) != null) {
					final Collection<Term<G>> list = createList(o,
							VarType.CLASS);

					// if (hasObject(node, RDF.type.asNode(),
					// OWL2.Class.asNode())) {
					// // t = f.wrap(qf
					// // .objectIntersectionOf(asGroundTerms(list)));
					t = intersectionOf(new HashSet<Term<G>>(list));
					// } else {
					// throw new UnsupportedQueryException(
					// "Expected unionOf to be of type owl:Class.");
					// // aTerm = f.wrap(f.dataIntersectionOf(list)); // TODO
					// }
				} else if ((o = getObject(node, OWL2.unionOf.asNode())) != null) {
					final Collection<Term<G>> list = createList(o,
							VarType.CLASS);
					hasObject(node, RDF.type.asNode(), OWL2.Class.asNode(),
							true);

					// if (hasObject(node, RDF.type.asNode(),
					// OWL2.Class.asNode())) {
					t = unionOf(new HashSet<Term<G>>(list));
					// // t = f.wrap(f.objectUnionOf(asGroundTerms(list)));
					// } else {
					// throw new UnsupportedQueryException(
					// "Expected unionOf to be of type owl:Class.");
					// // aTerm = f.wrap(f.dataUnionOf(list)); TODO
					// }
				} else if ((o = getObject(node, OWL2.oneOf.asNode())) != null) {
					hasObject(node, RDF.type.asNode(), OWL2.Class.asNode(),
							true);

					final Collection<Term<G>> list = createList(o,
							VarType.CLASS);

					final Set<Term<G>> result = Collections.emptySet();
					// if (hasObject(node, RDF.type.asNode(),
					// OWL2.Class.asNode())) {
					// for (final Term<G> c : list) {
					// result.add(f.oneOf(Collections.singleton(c)));
					// }
					t = unionOf(result); // TODO
					// } else {
					// t = f.unionOf(result);
					// // aTerm = f.wrap(f.dataUnionOf(list)); TODO
					// }
				} else if (node.isVariable()
						&& node.isBlank()
						&& (o = getObject(node, OWL2.complementOf.asNode())) != null) {
					final Term<G> complement = node2term(o, VarType.CLASS);
					// if (hasObject(node, RDF.type.asNode(),
					// OWL2.Class.asNode())) {
					t = objectComplementOf(complement); // TODO also Data:
					// } else {
					// // aTerm = f.wrap(f.dataComplementOf(complement
					// // .getWrappedObject())); TODO
					// }
				} else if (node.isVariable()) {
					return f.variable(node.getName());
				}
			} else {
				final String uri = node.getURI();

				if (ont.is(f.namedClass(uri), OWLObjectType.OWLClass)
						|| hasObject(node, RDF.type.asNode(),
								OWL2.Class.asNode(), false)) {
					t = f.wrap(f.namedClass(uri));
				} else if (ont.is(f.namedObjectProperty(uri),
						OWLObjectType.OWLObjectProperty)
						|| hasObject(node, RDF.type.asNode(),
								OWL2.ObjectProperty.asNode(), false)) {
					t = f.wrap(f.namedObjectProperty(uri));
				} else if (ont.is(f.namedDataProperty(uri),
						OWLObjectType.OWLDataProperty)
						|| hasObject(node, RDF.type.asNode(),
								OWL2.DatatypeProperty.asNode(), false)) {
					t = f.wrap(f.namedDataProperty(uri));
				} else if (ont.is(f.namedIndividual(uri),
						OWLObjectType.OWLNamedIndividual)) {
					t = f.wrap(f.namedIndividual(uri));
				} else {
					if (type != null) {
						switch (type) {
						case CLASS:
							t = f.wrap(f.namedClass(uri));
							break;
						case OBJECT_PROPERTY:
							t = f.wrap(f.namedObjectProperty(uri));
							break;
						case DATA_PROPERTY:
							t = f.wrap(f.namedDataProperty(uri));
							break;
						case INDIVIDUAL_OR_LITERAL:
						case INDIVIDUAL:
							t = f.wrap(f.namedIndividual(uri));
							break;
						case OBJECT_OR_DATA_PROPERTY:
							t = f.wrap(f.namedObjectProperty(uri));
							break;
						}
					} else {

						t = f.wrap(f.namedIndividual(uri));
					}
				}
			}

			terms.put(node, t);
		}

		return t;
	}

	public void write(OWL2Query<G> query, Writer os, final OWL2Ontology<G> kb) {
		undistVars.clear();
		InternalQuery<G> internalQuery = (InternalQuery<G>) query;

		// Query q = QueryFactory.make();
		Query q = new Query() {
			public void serialize(IndentedLineBuffer buff, Syntax outSyntax) {
				MySerializer.serialize(this, buff, outSyntax);
			}
		};

		if (internalQuery.getResultVars().isEmpty()) {
			q.setQueryAskType();
		} else {
			q.setQuerySelectType();
			for (final Variable<G> v : internalQuery.getResultVars()) {
				q.addResultVar(v.getName());
			}
		}
		ElementGroup eg = new ElementGroup();
		q.setQueryPattern(eg);
		ElementTriplesBlock bp = new ElementTriplesBlock();
		eg.addElement(bp);

		for (final QueryAtom<G> a : internalQuery.getAtoms()) {
			// Node s = null;
			Node p = null;
			// Node o = null;

			switch (a.getPredicate()) {
			case DirectType:
				p = Node.createURI(BASE_NM + "directType");
			case Type:
				if (p == null) {
					p = RDF.type.asNode();
				}

				bp.addTriple(new Triple(individual2Node(
						a.getArguments().get(1), internalQuery), p,
						concept2Node(a.getArguments().get(0), internalQuery)));

				break;

			case PropertyValue:
				bp.addTriple(new Triple(individual2Node(
						a.getArguments().get(1), internalQuery), property2Node(
						a.getArguments().get(0), internalQuery),
						individualOrLiteral2Node(a.getArguments().get(2),
								internalQuery)));

				break;

			case SameAs:
				bp.addTriple(new Triple(individual2Node(
						a.getArguments().get(0), internalQuery), OWL.sameAs
						.asNode(), individual2Node(a.getArguments().get(1),
						internalQuery)));
				break;
			case DifferentFrom:
				bp.addTriple(new Triple(individual2Node(
						a.getArguments().get(0), internalQuery),
						OWL.differentFrom.asNode(), individual2Node(a
								.getArguments().get(1), internalQuery)));
				break;
			case DirectSubClassOf:
				p = Node.createURI(BASE_NM + "directSubClassOf");
			case StrictSubClassOf:
				if (p == null) {
					p = Node.createURI(BASE_NM + "strictSubClassOf");
				}
			case SubClassOf:
				if (p == null) {
					p = RDFS.subClassOf.asNode();
				}

				bp.addTriple(new Triple(concept2Node(a.getArguments().get(0),
						internalQuery), p, concept2Node(
						a.getArguments().get(1), internalQuery)));

				break;
			case EquivalentClass:
				bp.addTriple(new Triple(concept2Node(a.getArguments().get(0),
						internalQuery), OWL.equivalentClass.asNode(),
						concept2Node(a.getArguments().get(1), internalQuery)));

				break;
			case ComplementOf:
				bp.addTriple(new Triple(concept2Node(a.getArguments().get(0),
						internalQuery), OWL.complementOf.asNode(),
						concept2Node(a.getArguments().get(1), internalQuery)));

				break;
			case DisjointWith:
				bp.addTriple(new Triple(concept2Node(a.getArguments().get(0),
						internalQuery), OWL.disjointWith.asNode(),
						concept2Node(a.getArguments().get(1), internalQuery)));

				break;
			case DirectSubPropertyOf:
				p = Node.createURI(BASE_NM + "directSubPropertyOf");
			case StrictSubPropertyOf:
				if (p == null) {
					p = Node.createURI(BASE_NM + "strictSubClassOf");
				}
			case SubPropertyOf:
				if (p == null) {
					p = RDFS.subPropertyOf.asNode();
				}

				bp.addTriple(new Triple(property2Node(a.getArguments().get(0),
						internalQuery), p, property2Node(a.getArguments()
						.get(1), internalQuery)));

				break;
			case EquivalentProperty:
				if (p == null) {
					p = OWL.equivalentProperty.asNode();
				}

				bp.addTriple(new Triple(property2Node(a.getArguments().get(0),
						internalQuery), p, property2Node(a.getArguments()
						.get(1), internalQuery)));

				break;
			case Functional:
				bp.addTriple(new Triple(property2Node(a.getArguments().get(0),
						internalQuery), RDF.type.asNode(),
						OWL.FunctionalProperty.asNode()));
				break;
			case Symmetric:
				bp.addTriple(new Triple(property2Node(a.getArguments().get(0),
						internalQuery), RDF.type.asNode(),
						OWL.SymmetricProperty.asNode()));
				break;
			case Asymmetric:
				bp.addTriple(new Triple(property2Node(a.getArguments().get(0),
						internalQuery), RDF.type.asNode(),
						OWL2.AsymmetricProperty.asNode()));
				break;
			case Reflexive:
				bp.addTriple(new Triple(property2Node(a.getArguments().get(0),
						internalQuery), RDF.type.asNode(),
						OWL2.ReflexiveProperty.asNode()));
				break;
			case Irreflexive:
				bp.addTriple(new Triple(property2Node(a.getArguments().get(0),
						internalQuery), RDF.type.asNode(),
						OWL2.IrreflexiveProperty.asNode()));
				break;
			case Transitive:
				bp.addTriple(new Triple(property2Node(a.getArguments().get(0),
						internalQuery), RDF.type.asNode(),
						OWL.TransitiveProperty.asNode()));
				break;
			case InverseFunctional:
				bp.addTriple(new Triple(property2Node(a.getArguments().get(0),
						internalQuery), RDF.type.asNode(),
						OWL.InverseFunctionalProperty.asNode()));
				break;
			case InverseOf:
				bp.addTriple(new Triple(property2Node(a.getArguments().get(0),
						internalQuery), OWL.inverseOf.asNode(), property2Node(a
						.getArguments().get(1), internalQuery)));
				break;
			case ObjectProperty:
				bp.addTriple(new Triple(property2Node(a.getArguments().get(0),
						internalQuery), RDF.type.asNode(), OWL.ObjectProperty
						.asNode()));
				break;
			case DatatypeProperty:
				bp.addTriple(new Triple(property2Node(a.getArguments().get(0),
						internalQuery), RDF.type.asNode(), OWL.DatatypeProperty
						.asNode()));
				break;
			default:
				throw new UnsupportedOperationException("Not Supported Yet");
			}

		}

		try {
			os.write(q.toString(Syntax.syntaxSPARQL));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String BASE_NM = "http://pellet.owldl.com/ns/sdle#";

	private Map<String, Variable<G>> undistVars = new HashMap<String, Variable<G>>();

	private Node getVariable(final Variable<G> var, final InternalQuery<G> query) {
		Variable<G> v = undistVars.get(var.getName());
		if (v == null) {
			v = var;
			undistVars.put(var.getName(), var);
		}

		if (query.getUndistVars().contains(v)) {
			return Var.createAnon(new AnonId(v.getName()));
		} else {
			return Var.alloc(var.getName());
		}
	}

	public Node individual2Node(final Term<G> i, final InternalQuery<G> query) {
		if (i.isVariable()) {
			return getVariable(i.asVariable(), query);
		} else {
			try {
				String s = i.asGroundTerm().getWrappedObject().toString();

				URI uri = URI.create(s.substring(1, s.length() - 1));
				return Node.createURI(uri.toString());
			} catch (IllegalArgumentException e) {
				return Node.createLiteral(i.asGroundTerm().getWrappedObject()
						.toString());
			}
		}
	}

	public Node individualOrLiteral2Node(final Term<G> i,
			final InternalQuery<G> query) {
		if (i.isVariable()) {
			return getVariable(i.asVariable(), query);
		} else {
			try {
				String s = i.asGroundTerm().toString();
				String val = s.substring(1, s.length() - 1);

				// System.out.println(s);
				// if (s != null && s.length() > 0 && s.charAt(0) == '"') {//
				// literal

				if (ont.is(i.asGroundTerm().getWrappedObject(),
						OWLObjectType.OWLLiteral)) {// literal
					int ti = s.lastIndexOf("^^");
					String type = ont.getDatatypeOfLiteral(i.asGroundTerm().getWrappedObject());
					String lit = s;
						// if(sx != null){
						// type = sx;
						// }else {
						// type = s.substring(ti + 2);
						// }
						// lit = s.substring(0, ti);
					if (lit.charAt(0) == '"')
						lit = lit.substring(1, lit.length() - 1);
					if (type != null)
						return Node.createLiteral(lit, null, new BaseDatatype(
								type));
					else
						return Node.createLiteral(lit);
				} else {// uri
					URI uri = URI.create(val);
					return Node.createURI(uri.toString());
				}

			} catch (IllegalArgumentException e) {
				return Node.createLiteral(i.asGroundTerm().getWrappedObject()
						.toString());
			}
		}
	}

	public Node concept2Node(final Term<G> c, final InternalQuery<G> query) {
		if (c.isVariable()) {
			return getVariable(c.asVariable(), query);
		} else {
			try {
				String s = c.asGroundTerm().getWrappedObject().toString();
				if (s.charAt(0) == '<') {
					s = s.substring(1, s.length() - 1);
				}
				return Node.createURI(URI.create(s).toString());
			} catch (IllegalArgumentException e) {
				throw new UnsupportedQueryException(
						"Concept/role constructs are not supported: "
								+ c.asGroundTerm().getWrappedObject());
			}
		}
	}

	public Node property2Node(final Term<G> p, final InternalQuery<G> query) {
		if (p.isVariable()) {
			return getVariable(p.asVariable(), query);
		} else {
			String uri = p.asGroundTerm().getWrappedObject().toString();
			if (uri.charAt(0) == '<')
				uri = uri.substring(1, uri.length() - 1);
			return Node.createURI(uri);
			// return Node.createURI(p
			// .asGroundTerm()
			// .getWrappedObject()
			// .toString()
			// .substring(
			// 1,
			// p.asGroundTerm().getWrappedObject().toString()
			// .length() - 1));
		}
	}

	// public static void main(String[] args) {
	// SparqlARQParser<OWLObject> o = new SparqlARQParser<OWLObject>();
	// OWLOntologyManager m = OWLManager.createOWLOntologyManager();
	// OWLOntology ont;
	// try {
	// ont = m.createOntology();
	// OWLReasoner r = new StructuralReasonerFactory()
	// .createNonBufferingReasoner(ont);
	//
	// o.parse("SELECT * WHERE {?x ?y ?z.}", new OWLAPIv3OWL2Ontology(m,
	// ont, r));
	//
	// } catch (OWLOntologyCreationException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}