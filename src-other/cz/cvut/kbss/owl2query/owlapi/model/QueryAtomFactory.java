// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.owlapi.model;

import java.util.Collection;




/**
 * <p>
 * Title: Factory for creating query atoms.
 * </p>
 * <p>
 * Description:
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
public class QueryAtomFactory {

	// ABOX atoms
	public static QueryAtom TypeAtom(final ATermAppl iA, final ATermAppl cA) {
		return new AbstractQueryAtom(QueryPredicate.Type, iA, cA);
	}

	public static QueryAtom PropertyValueAtom(final ATermAppl iA,
			final ATermAppl pA, final ATermAppl ilA) {
		return new AbstractQueryAtom(QueryPredicate.PropertyValue, iA, pA, ilA);
	}

	public static QueryAtom SameAsAtom(final ATermAppl iA1, final ATermAppl iA2) {
		return new AbstractQueryAtom(QueryPredicate.SameAs, iA1, iA2);
	}

	public static QueryAtom DifferentFromAtom(final ATermAppl iA1,
			final ATermAppl iA2) {
		return new AbstractQueryAtom(QueryPredicate.DifferentFrom, iA1, iA2);
	}

	// TBOX atoms
	public static QueryAtom SubClassOfAtom(final ATermAppl cA1,
			final ATermAppl cA2) {
		return new AbstractQueryAtom(QueryPredicate.SubClassOf, cA1, cA2);
	}

	public static QueryAtom EquivalentClassAtom(final ATermAppl classArgument,
			final ATermAppl classArgument2) {
		return new AbstractQueryAtom(QueryPredicate.EquivalentClass,
				classArgument, classArgument2);
	}

	public static QueryAtom DisjointWithAtom(final ATermAppl cA1,
			final ATermAppl cA2) {
		return new AbstractQueryAtom(QueryPredicate.DisjointWith, cA1, cA2);
	}

	public static QueryAtom ComplementOfAtom(final ATermAppl cA1,
			final ATermAppl cA2) {
		return new AbstractQueryAtom(QueryPredicate.ComplementOf, cA1, cA2);
	}

	// RBOX atoms
	public static QueryAtom SubPropertyOfAtom(final ATermAppl pA1,
			final ATermAppl pA2) {
		return new AbstractQueryAtom(QueryPredicate.SubPropertyOf, pA1, pA2);
	}

	public static QueryAtom EquivalentPropertyAtom(final ATermAppl pA1,
			final ATermAppl pA2) {
		return new AbstractQueryAtom(QueryPredicate.EquivalentProperty, pA1,
				pA2);
	}

	public static QueryAtom InverseOfAtom(final ATermAppl pA1,
			final ATermAppl pA2) {
		return new AbstractQueryAtom(QueryPredicate.InverseOf, pA1, pA2);
	}

	public static QueryAtom ObjectPropertyAtom(final ATermAppl pA) {
		return new AbstractQueryAtom(QueryPredicate.ObjectProperty, pA);
	}

	public static QueryAtom DatatypePropertyAtom(final ATermAppl pA) {
		return new AbstractQueryAtom(QueryPredicate.DatatypeProperty, pA);
	}

	public static QueryAtom FunctionalAtom(final ATermAppl pA) {
		return new AbstractQueryAtom(QueryPredicate.Functional, pA);
	}

	public static QueryAtom InverseFunctionalAtom(final ATermAppl pA) {
		return new AbstractQueryAtom(QueryPredicate.InverseFunctional, pA);
	}

	public static QueryAtom TransitiveAtom(final ATermAppl pA) {
		return new AbstractQueryAtom(QueryPredicate.Transitive, pA);
	}

	public static QueryAtom SymmetricAtom(final ATermAppl pA) {
		return new AbstractQueryAtom(QueryPredicate.Symmetric, pA);
	}

	public static QueryAtom AnnotationAtom(final ATermAppl iA,
			final ATermAppl pA, final ATermAppl ilA) {
		return new AbstractQueryAtom(QueryPredicate.Annotation, iA, pA, ilA);
	}

	// SPARQL-DL nonmonotonic extension
	public static QueryAtom StrictSubClassOfAtom(final ATermAppl c1,
			final ATermAppl c2) {
		return new AbstractQueryAtom(QueryPredicate.StrictSubClassOf, c1, c2);
	}

	public static QueryAtom DirectSubClassOfAtom(final ATermAppl c1,
			final ATermAppl c2) {
		return new AbstractQueryAtom(QueryPredicate.DirectSubClassOf, c1, c2);
	}

	public static QueryAtom DirectSubPropertyOfAtom(final ATermAppl c1,
			final ATermAppl c2) {
		return new AbstractQueryAtom(QueryPredicate.DirectSubPropertyOf, c1, c2);
	}

	public static QueryAtom StrictSubPropertyOfAtom(final ATermAppl c1,
			final ATermAppl c2) {
		return new AbstractQueryAtom(QueryPredicate.StrictSubPropertyOf, c1, c2);
	}

	public static QueryAtom DirectTypeAtom(final ATermAppl i, final ATermAppl c) {
		return new AbstractQueryAtom(QueryPredicate.DirectType, i, c);
	}

	// core of undistinguished variables
	public static QueryAtom Core(final Collection<QueryAtom> atoms, final Collection<ATermAppl> uv, 
			final KnowledgeBase kb) {
		return new CoreNewImpl(atoms, uv, kb);
	}
	
	public static QueryAtom NotAtom(QueryAtom atom) {
		return new NotQueryAtom( atom );
	}
	
	public static QueryAtom ExecuteAtom(ATermAppl queryName, ATermAppl... args) {
		ATermAppl[] allArgs = new ATermAppl[args.length + 1];
		allArgs[ 0] = queryName ;
		System.arraycopy( args, 0, allArgs, 1, args.length );
		
		return new AbstractQueryAtom(QueryPredicate.Execute, allArgs);
	}
}
