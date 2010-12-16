// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.exceptions.UnsupportedFeatureException;
import org.mindswap.pellet.exceptions.UnsupportedQueryException;
import org.mindswap.pellet.jena.JenaUtils;
import org.mindswap.pellet.utils.ATermUtils;

import aterm.ATerm;
import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;
import aterm.ATermList;

import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory;
import com.clarkparsia.pellet.sparqldl.model.QueryImpl;
import com.clarkparsia.pellet.sparqldl.model.Query.VarType;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.QuerySolutionMap;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * <p>
 * Title: Parser for the SPARQL-DL based on ARQ
 * </p>
 * <p>
 * Description: Meanwhile does not deal with types of variables.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Petr Kremen
 */
public class ARQParser implements QueryParser {
	public static Logger		log					= Logger.getLogger( ARQParser.class.getName() );

	private Set<Triple>			triples;

	private Map<Node, ATerm>	terms;

	private KnowledgeBase		kb;

	private QuerySolution		initialBinding;

	/*
	 * If this variable is true then queries with variable SPO statements are
	 * not handled by the SPARQL-DL engine but fall back to ARQ
	 */
	private boolean				handleVariableSPO	= true;

	public ARQParser() {
		this( true );
	}

	public ARQParser(boolean handleVariableSPO) {
		this.handleVariableSPO = handleVariableSPO;
	}

	/**
	 * {@inheritDoc}
	 */
	public Query parse(InputStream stream, KnowledgeBase kb) {
		try {
			return parse( new InputStreamReader( stream ), kb );
		} catch( IOException e ) {
			final String message = "Error creating a reader from the input stream.";
			log.severe( message );
			throw new RuntimeException( message );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Query parse(String queryStr, KnowledgeBase kb) {
		com.hp.hpl.jena.query.Query sparql = QueryFactory.create( queryStr, Syntax.syntaxSPARQL );

		return parse( sparql, kb );
	}

	private Query parse(Reader in, KnowledgeBase kb) throws IOException {
		StringBuffer queryString = new StringBuffer();
		BufferedReader r = new BufferedReader( in );

		String line = r.readLine();
		while( line != null ) {
			queryString.append( line ).append( "\n" );
			line = r.readLine();
		}

		return parse( queryString.toString(), kb );
	}

	public Query parse(com.hp.hpl.jena.query.Query sparql, KnowledgeBase kb) {
		this.kb = kb;

		if( sparql.isDescribeType() )
			throw new UnsupportedQueryException(
					"DESCRIBE queries cannot be answered with PelletQueryEngine" );

		// MEANWHILE ONLY ONE QUERY GRAPH - named graphs, optionals, filters,
		// etc. to be added later.
		final Element pattern = sparql.getQueryPattern();

		if( !(pattern instanceof ElementGroup) )
			throw new UnsupportedQueryException( "ElementGroup was expected, but found '"
					+ pattern.getClass() + "'." );

		final ElementGroup elementGroup = (ElementGroup) pattern;

		final List<?> elements = elementGroup.getElements();
		final Element first = (Element) elements.get( 0 );
		if( elements.size() != 1 || !(first instanceof ElementTriplesBlock) )
			throw new UnsupportedQueryException( "Complex query patterns are not supported yet." );

		// very important to call this function so that getResultVars() will
		// work fine for SELECT * queries
		sparql.setResultVars();

		return parse( ((ElementTriplesBlock) first).getTriples(), sparql.getResultVars(), kb,
				sparql.isDistinct() );
	}

	public Query parse(BasicPattern basicPattern, Collection<?> resultVars, KnowledgeBase kb,
			boolean isDistinct) throws UnsupportedQueryException {
		this.kb = kb;

		// This set contains predicates that are distinguished variables. The
		// elements are accumulated for PropertyValueAtom and removed if used in
		// subject position of other SPARQL-DL query atoms. If the set
		// is not empty, we throw an unsupported query exception and fall back
		// to ARQ to process the query. This solves the problem of {} ?p {}
		// queries where ?p is not used as subject in other patterns
		Set<ATermAppl> variablePredicates = new HashSet<ATermAppl>();
		// This set contains subjects that are distinguished variables and is
		// used to collect variables along the way while processing triple
		// patterns. The list is used to decide whether or not the variable
		// property of a pattern {} ?p {} has to be accumulated to the
		// variablePredicates set. This avoids to add them for the case where
		// the variable in predicate position is bound to a subject of another
		// triple pattern, e.g. ?p rdf:type owl:ObjectProperty . ?s ?p ?o
		Set<ATermAppl> variableSubjects = new HashSet<ATermAppl>();

		terms = new HashMap<Node, ATerm>();
		// Make sure to resolve the query parameterization first, i.e.
		// substitute the variables with initial bindings, if applicable
		triples = new LinkedHashSet<Triple>( resolveParameterization( basicPattern.getList() ) );

		final Query query = new QueryImpl( kb, isDistinct );

		for( Iterator<?> i = resultVars.iterator(); i.hasNext(); ) {
			String var = (String) i.next();

			query.addResultVar( ATermUtils.makeVar( var ) );
		}

		for( final Triple t : new ArrayList<Triple>( triples ) ) {
			if( !triples.contains( t ) ) {
				continue;
			}

			Node subj = t.getSubject();
			Node pred = t.getPredicate();
			Node obj = t.getObject();

			ATermAppl s1 = node2term( subj );
			ATermAppl p1 = node2term( pred );
			ATermAppl o1 = node2term( obj );

			terms.put( subj, s1 );
			terms.put( pred, p1 );
			terms.put( obj, o1 );
		}

		final Set<ATermAppl> possibleLiteralVars = new HashSet<ATermAppl>();

		for( final Triple t : triples ) {

			Node subj = t.getSubject();
			Node pred = t.getPredicate();
			Node obj = t.getObject();

			ATermAppl s = (ATermAppl) terms.get( subj );
			ATermAppl p = (ATermAppl) terms.get( pred );
			ATermAppl o = (ATermAppl) terms.get( obj );

			if( pred.equals( RDF.Nodes.type ) ) {
				// ObjectProperty(p)
				if( obj.equals( OWL.ObjectProperty.asNode() ) ) {
					query.add( QueryAtomFactory.ObjectPropertyAtom( s ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
				}

				// DatatypeProperty(p)
				else if( obj.equals( OWL.DatatypeProperty.asNode() ) ) {
					query.add( QueryAtomFactory.DatatypePropertyAtom( s ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
				}

				// Property(p)
				else if( obj.equals( RDF.Property.asNode() ) ) {
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
				}

				// Functional(p)
				else if( obj.equals( OWL.FunctionalProperty.asNode() ) ) {
					query.add( QueryAtomFactory.FunctionalAtom( s ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
				}

				// InverseFunctional(p)
				else if( obj.equals( OWL.InverseFunctionalProperty.asNode() ) ) {
					query.add( QueryAtomFactory.InverseFunctionalAtom( s ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}

				}

				// Transitive(p)
				else if( obj.equals( OWL.TransitiveProperty.asNode() ) ) {
					query.add( QueryAtomFactory.TransitiveAtom( s ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}
				}

				// Symmetric(p)
				else if( obj.equals( OWL.SymmetricProperty.asNode() ) ) {
					query.add( QueryAtomFactory.SymmetricAtom( s ) );
					if( ATermUtils.isVar( s ) ) {
						ensureDistinguished( subj );
						query.addDistVar( s, VarType.PROPERTY );
						if( handleVariableSPO ) {
							variablePredicates.remove( s );
							variableSubjects.add( s );
						}
					}

				}

				// Annotation(s,pa,o)
				else if( hasObject( pred, RDF.type.asNode(), OWL.AnnotationProperty.asNode() ) ) {
					query.add( QueryAtomFactory.AnnotationAtom( s, p, o ) );
					if( ATermUtils.isVar( s ) || ATermUtils.isVar( p ) || ATermUtils.isVar( o ) ) {
						throw new IllegalArgumentException(
								"Variables in annotation atom are not supported." );
					}
				}

				// Type(i,c)
				else {
					query.add( QueryAtomFactory.TypeAtom( s, o ) );

					if( ATermUtils.isVar( o ) ) {
						ensureDistinguished( obj );
						query.addDistVar( o, VarType.CLASS );
					}
					else if( !kb.isClass( o ) ) {
						if( log.isLoggable( Level.FINE ) )
							log
									.fine( "Class " + o
											+ " used in the query is not defined in the KB." );
					}

					if( isDistinguishedVariable( subj ) ) {
						query.addDistVar( s, VarType.INDIVIDUAL );
					}
				}
			}

			// SameAs(i1,i2)
			else if( pred.equals( OWL.sameAs.asNode() ) ) {
				query.add( QueryAtomFactory.SameAsAtom( s, o ) );
				if( isDistinguishedVariable( subj ) ) {
					query.addDistVar( s, VarType.INDIVIDUAL );
				}

				if( isDistinguishedVariable( obj ) ) {
					query.addDistVar( o, VarType.INDIVIDUAL );
				}

			}

			// DifferentFrom(i1,i2)
			else if( pred.equals( OWL.differentFrom.asNode() ) ) {
				query.add( QueryAtomFactory.DifferentFromAtom( s, o ) );
				if( isDistinguishedVariable( subj ) ) {
					query.addDistVar( s, VarType.INDIVIDUAL );
				}

				if( isDistinguishedVariable( obj ) ) {
					query.addDistVar( o, VarType.INDIVIDUAL );
				}

			}

			// SubClassOf(c1,c2)
			else if( pred.equals( RDFS.subClassOf.asNode() ) ) {
				query.add( QueryAtomFactory.SubClassOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.CLASS );
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.CLASS );
				}
			}

			// strict subclass - nonmonotonic
			else if( pred.equals( SparqldlExtensionsVocabulary.strictSubClassOf.asNode() ) ) {
				query.add( QueryAtomFactory.StrictSubClassOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.CLASS );
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.CLASS );
				}
			}

			// direct subclass - nonmonotonic
			else if( pred.equals( SparqldlExtensionsVocabulary.directSubClassOf.asNode() ) ) {
				query.add( QueryAtomFactory.DirectSubClassOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.CLASS );
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.CLASS );
				}
			}

			// EquivalentClass(c1,c2)
			else if( pred.equals( OWL.equivalentClass.asNode() ) ) {
				query.add( QueryAtomFactory.EquivalentClassAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.CLASS );
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.CLASS );
				}
			}

			// DisjointWith(c1,c2)
			else if( pred.equals( OWL.disjointWith.asNode() ) ) {
				query.add( QueryAtomFactory.DisjointWithAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.CLASS );
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.CLASS );
				}

			}

			// ComplementOf(c1,c2)
			else if( pred.equals( OWL.complementOf.asNode() ) ) {
				query.add( QueryAtomFactory.ComplementOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.CLASS );
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.CLASS );
				}
			}

			// SubPropertyOf(p1,p2)
			else if( pred.equals( RDFS.subPropertyOf.asNode() ) ) {
				query.add( QueryAtomFactory.SubPropertyOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( s );
						variableSubjects.add( s );
					}
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( o );
						variableSubjects.add( o );
					}
				}
			}

			// DirectSubPropertyOf(i,c) - nonmonotonic
			else if( pred.equals( SparqldlExtensionsVocabulary.directSubPropertyOf.asNode() ) ) {
				query.add( QueryAtomFactory.DirectSubPropertyOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( obj );
					query.addDistVar( s, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( s );
						variableSubjects.add( s );
					}
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( o );
						variableSubjects.add( o );
					}
				}
			}

			// DirectSubPropertyOf(i,c) - nonmonotonic
			else if( pred.equals( SparqldlExtensionsVocabulary.strictSubPropertyOf.asNode() ) ) {
				query.add( QueryAtomFactory.StrictSubPropertyOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( obj );
					query.addDistVar( s, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( s );
						variableSubjects.add( s );
					}
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( o );
						variableSubjects.add( o );
					}
				}
			}

			// EquivalentProperty(p1,p2)
			else if( pred.equals( OWL.equivalentProperty.asNode() ) ) {
				query.add( QueryAtomFactory.EquivalentPropertyAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( s );
						variableSubjects.add( s );
					}
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( o );
						variableSubjects.add( o );
					}
				}
			}

			// InverseOf(p1,p2)
			else if( pred.equals( OWL.inverseOf.asNode() ) ) {
				query.add( QueryAtomFactory.InverseOfAtom( s, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( s );
						variableSubjects.add( s );
					}
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( o );
						variableSubjects.add( o );
					}
				}
			}

			// DirectType(i,c) - nonmonotonic
			else if( pred.equals( SparqldlExtensionsVocabulary.directType.asNode() ) ) {
				query.add( QueryAtomFactory.DirectTypeAtom( s, o ) );
				if( isDistinguishedVariable( subj ) ) {
					query.addDistVar( s, VarType.INDIVIDUAL );
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.CLASS );
				}
			}

			else if( kb.isAnnotationProperty( p ) ) {
				query.add( QueryAtomFactory.AnnotationAtom( s, p, o ) );
				if( ATermUtils.isVar( s ) ) {
					ensureDistinguished( subj );
					query.addDistVar( s, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( s );
						variableSubjects.add( s );
					}
				}
				if( ATermUtils.isVar( o ) ) {
					ensureDistinguished( obj );
					query.addDistVar( o, VarType.PROPERTY );
					if( handleVariableSPO ) {
						variablePredicates.remove( o );
						variableSubjects.add( o );
					}
				}
				// throw new UnsupportedQueryException(
				// "Annotation properties are not supported in queries." );
			}

			// PropertyValue(i,p,j)
			else {
				query.add( QueryAtomFactory.PropertyValueAtom( s, p, o ) );

				if( ATermUtils.isVar( p ) ) {
					ensureDistinguished( pred );
					query.addDistVar( p, VarType.PROPERTY );

					// If the predicate is a variable used in a subject position
					// we don't have to consider it as it is bound to another
					// triple pattern
					if( !variableSubjects.contains( p ) )
						variablePredicates.add( p );
				}
				else if( kb.isOntologyProperty( p ) ) {
					throw new UnsupportedQueryException(
							"Ontology properties are not supported in queries." );
				}
				else if( !kb.isProperty( p ) ) {
					if( log.isLoggable( Level.FINE ) )
						log
								.fine( "Predicate " + p
										+ " used in the query is not defined in the KB." );
				}

				if( isDistinguishedVariable( subj ) ) {
					query.addDistVar( s, VarType.INDIVIDUAL );
				}

				if( isDistinguishedVariable( obj ) ) {
					if( ATermUtils.isVar( p ) ) {
						possibleLiteralVars.add( o );
					}
					else {
						if( kb.isObjectProperty( p ) ) {
							query.addDistVar( o, VarType.INDIVIDUAL );
						}
						else if( kb.isDatatypeProperty( p ) ) {
							query.addDistVar( o, VarType.LITERAL );
						}
					}
				}
			}
		}

		for( final ATermAppl v : possibleLiteralVars ) {
			if( !query.getDistVars().contains( v ) ) {
				query.addDistVar( v, VarType.LITERAL );
			}
			query.addDistVar( v, VarType.INDIVIDUAL );
		}

		if( !handleVariableSPO )
			return query;

		if( variablePredicates.isEmpty() )
			return query;

		throw new UnsupportedQueryException( "Queries with variable predicates are not supported "
				+ "(add the pattern {?p rdf:type owl:ObjectProperty} or"
				+ " {?p rdf:type owl:DatatypeProperty} to the query)" );

	}

	public void setInitialBinding(QuerySolution initialBinding) {
		this.initialBinding = initialBinding;
	}

	private void ensureDistinguished(Node pred) {
		ensureDistinguished( pred,
				"Non-distinguished variables in class and predicate positions are not supported : " );
	}

	private void ensureDistinguished(Node pred, String errorNonDist) {
		if( !isDistinguishedVariable( pred ) ) {
			throw new UnsupportedQueryException( errorNonDist + pred );
		}
	}

	public static boolean isDistinguishedVariable(final Node node) {
		return Var.isVar( node )
				&& (Var.isNamedVar( node ) || PelletOptions.TREAT_ALL_VARS_DISTINGUISHED);
	}

	private Node getObject(Node subj, Node pred) {
		for( final Iterator<Triple> i = triples.iterator(); i.hasNext(); ) {
			Triple t = i.next();
			if( subj.equals( t.getSubject() ) && pred.equals( t.getPredicate() ) ) {
				i.remove();
				return t.getObject();
			}
		}

		return null;
	}

	private boolean hasObject(Node subj, Node pred) {
		for( final Iterator<Triple> i = triples.iterator(); i.hasNext(); ) {
			Triple t = i.next();
			if( subj.equals( t.getSubject() ) && pred.equals( t.getPredicate() ) )
				return true;
		}

		return false;
	}

	private boolean hasObject(Node subj, Node pred, Node obj) {
		for( final Iterator<Triple> i = triples.iterator(); i.hasNext(); ) {
			Triple t = i.next();
			if( subj.equals( t.getSubject() ) && pred.equals( t.getPredicate() ) ) {
				i.remove();
				if( obj.equals( t.getObject() ) ) {
					return true;
				}
				throw new UnsupportedQueryException( "Expecting rdf:type " + obj
						+ " but found rdf:type " + t.getObject() );
			}
		}

		return false;
	}

	private ATermList createList(Node node) {
		if( node.equals( RDF.nil.asNode() ) )
			return ATermUtils.EMPTY_LIST;
		else if( terms.containsKey( node ) )
			return (ATermList) terms.get( node );

		hasObject( node, RDF.type.asNode(), RDF.List.asNode() );

		Node first = getObject( node, RDF.first.asNode() );
		Node rest = getObject( node, RDF.rest.asNode() );

		if( first == null || rest == null ) {
			throw new UnsupportedQueryException( "Invalid list structure: List " + node
					+ " does not have a " + (first == null
						? "rdf:first"
						: "rdf:rest") + " property." );
		}

		ATermList list = ATermUtils.makeList( node2term( first ), createList( rest ) );

		terms.put( node, list );

		return list;
	}

	private ATermAppl createRestriction(Node node) throws UnsupportedFeatureException {
		ATermAppl aTerm = ATermUtils.TOP;

		hasObject( node, RDF.type.asNode(), OWL.Restriction.asNode() );

		Node p = getObject( node, OWL.onProperty.asNode() );

		// TODO warning message: no owl:onProperty
		if( p == null )
			return aTerm;

		ATermAppl pt = node2term( p );
		if( !kb.isProperty( pt ) )
			throw new UnsupportedQueryException( "Property " + pt + " is not present in KB." );

		// TODO warning message: multiple owl:onProperty
		Node o = null;
		if( (o = getObject( node, OWL.hasValue.asNode() )) != null ) {
			if( PelletOptions.USE_PSEUDO_NOMINALS ) {
				if( o.isLiteral() ) {
					aTerm = ATermUtils.makeMin( pt, 1, ATermUtils.TOP_LIT );
				}
				else {
					ATermAppl ind = ATermUtils.makeTermAppl( o.getURI() );
					if( !kb.isIndividual( ind ) )
						throw new UnsupportedQueryException( "Individual " + ind
								+ " is not present in KB." );

					ATermAppl nom = ATermUtils.makeTermAppl( o.getURI() + "_nom" );

					aTerm = ATermUtils.makeSomeValues( pt, nom );
				}
			}
			else {
				ATermAppl ot = node2term( o );

				aTerm = ATermUtils.makeHasValue( pt, ot );
			}
		}
		else if( (o = getObject( node, OWL.allValuesFrom.asNode() )) != null ) {
			ATermAppl ot = node2term( o );

			aTerm = ATermUtils.makeAllValues( pt, ot );
		}
		else if( (o = getObject( node, OWL.someValuesFrom.asNode() )) != null ) {
			ATermAppl ot = node2term( o );

			aTerm = ATermUtils.makeSomeValues( pt, ot );
		}
		else if( (o = getObject( node, OWL.minCardinality.asNode() )) != null ) {
			try {
				ATermAppl top = null;
				if( kb.isDatatypeProperty( pt ) )
					top = ATermUtils.TOP_LIT;
				else
					top = ATermUtils.TOP;

				int cardinality = Integer.parseInt( o.getLiteral().getLexicalForm() );
				aTerm = ATermUtils.makeMin( pt, cardinality, top );
			} catch( Exception ex ) {
				// TODO print warning message (invalid number)
			}
		}
		else if( (o = getObject( node, OWL.maxCardinality.asNode() )) != null ) {
			try {
				ATermAppl top = null;
				if( kb.isDatatypeProperty( pt ) )
					top = ATermUtils.TOP_LIT;
				else
					top = ATermUtils.TOP;

				int cardinality = Integer.parseInt( o.getLiteral().getLexicalForm() );
				aTerm = ATermUtils.makeMax( pt, cardinality, top );
			} catch( Exception ex ) {
				// TODO print warning message (invalid number)
			}
		}
		else if( (o = getObject( node, OWL.cardinality.asNode() )) != null ) {
			try {
				ATermAppl top = null;
				if( kb.isDatatypeProperty( pt ) )
					top = ATermUtils.TOP_LIT;
				else
					top = ATermUtils.TOP;

				int cardinality = Integer.parseInt( o.getLiteral().getLexicalForm() );
				aTerm = ATermUtils.makeCard( pt, cardinality, top );
			} catch( Exception ex ) {
				// TODO print warning message (invalid number)
			}
		}
		else {
			// TODO print warning message (invalid restriction type)
		}

		return aTerm;
	}

	private ATermAppl node2term(Node node) {
		ATermAppl aTerm = (ATermAppl) terms.get( node );

		if( aTerm == null ) {
			if( node.equals( OWL.Thing.asNode() ) )
				return ATermUtils.TOP;
			else if( node.equals( OWL.Nothing.asNode() ) )
				return ATermUtils.BOTTOM;
			else if( node.equals( RDF.type.asNode() ) )
				return null;
			else if( node.isLiteral() )
				return JenaUtils.makeLiteral( node.getLiteral() );
			else if( hasObject( node, OWL.onProperty.asNode() ) ) {
				aTerm = createRestriction( node );
				terms.put( node, aTerm );
			}
			else if( node.isBlank() || node.isVariable() ) {
				Node o = null;
				if( (o = getObject( node, OWL.intersectionOf.asNode() )) != null ) {
					ATermList list = createList( o );
					hasObject( node, RDF.type.asNode(), OWL.Class.asNode() );

					aTerm = ATermUtils.makeAnd( list );
				}
				else if( (o = getObject( node, OWL.unionOf.asNode() )) != null ) {
					ATermList list = createList( o );
					hasObject( node, RDF.type.asNode(), OWL.Class.asNode() );

					aTerm = ATermUtils.makeOr( list );
				}
				else if( (o = getObject( node, OWL.oneOf.asNode() )) != null ) {
					ATermList list = createList( o );
					hasObject( node, RDF.type.asNode(), OWL.Class.asNode() );

					ATermList result = ATermUtils.EMPTY_LIST;
					for( ATermList l = list; !l.isEmpty(); l = l.getNext() ) {
						ATermAppl c = (ATermAppl) l.getFirst();
						if( PelletOptions.USE_PSEUDO_NOMINALS ) {
							ATermAppl nominal = ATermUtils.makeTermAppl( c.getName() + "_nominal" );
							result = result.insert( nominal );
						}
						else {
							ATermAppl nominal = ATermUtils.makeValue( c );
							result = result.insert( nominal );
						}
					}

					aTerm = ATermUtils.makeOr( result );
				}
				else if( Var.isBlankNodeVar( node )
						&& (o = getObject( node, OWL.complementOf.asNode() )) != null ) {
					ATermAppl complement = node2term( o );
					hasObject( node, RDF.type.asNode(), OWL.Class.asNode() );

					aTerm = ATermUtils.makeNot( complement );
				}
				else if( node.isVariable() ) {
					return ATermUtils.makeVar( node.getName() );
				}
				else {
					if( ((o = getObject( node, OWL.complementOf.asNode() )) != null) ) {
						log.info( "Blank nodes in class variable positions are not supported" );

						// TODO
					}

					aTerm = ATermUtils.makeBnode( node.getBlankNodeId().toString() );
				}
			}
			else {
				String uri = node.getURI();

				aTerm = ATermUtils.makeTermAppl( uri );
			}

			terms.put( node, aTerm );
		}

		return aTerm;
	}

	/*
	 * Given a parameterized query, resolve the node (SPO of a triple pattern)
	 * i.e. if it is a variable and the variable name is contained in the
	 * initial binding (as a parameter) resolve it, i.e. substitute the variable
	 * with the constant.
	 */
	private List<Triple> resolveParameterization(List<?> triples) {
		if( triples == null )
			throw new NullPointerException( "The set of triples cannot be null" );

		// Ensure that the initial binding is not a null pointer
		if( initialBinding == null )
			initialBinding = new QuerySolutionMap();

		List<Triple> ret = new ArrayList<Triple>();

		for( final Triple t : triples.toArray( new Triple[triples.size()] ) ) {
			if( !triples.contains( t ) ) {
				continue;
			}

			Node s = resolveParameterization( t.getSubject() );
			Node p = resolveParameterization( t.getPredicate() );
			Node o = resolveParameterization( t.getObject() );

			ret.add( Triple.create( s, p, o ) );
		}

		return ret;
	}

	private Node resolveParameterization(Node node) {
		if( node == null )
			throw new NullPointerException( "Node is null" );
		if( initialBinding == null )
			throw new NullPointerException( "Initial binding is null" );

		if( node.isConcrete() )
			return node;

		RDFNode binding = initialBinding.get( node.getName() );

		if( binding == null )
			return node;

		return binding.asNode();
	}
}
