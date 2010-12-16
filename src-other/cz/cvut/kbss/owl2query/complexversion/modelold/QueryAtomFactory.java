// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.complexversion.modelold;

import java.util.Collection;

import cz.cvut.kbss.owl2query.complexversion.model.Term;




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
	public static QueryAtom TypeAtom(final Term iA, final Term cA) {
		return new AbstractQueryAtom(QueryPredicate.Type, iA, cA);
	}

	public static QueryAtom PropertyValueAtom(final Term iA,
			final Term pA, final Term ilA) {
		return new AbstractQueryAtom(QueryPredicate.PropertyValue, iA, pA, ilA);
	}

	public static QueryAtom SameAsAtom(final Term iA1, final Term iA2) {
		return new AbstractQueryAtom(QueryPredicate.SameAs, iA1, iA2);
	}

	public static QueryAtom DifferentFromAtom(final Term iA1,
			final Term iA2) {
		return new AbstractQueryAtom(QueryPredicate.DifferentFrom, iA1, iA2);
	}

	// TBOX atoms
	public static QueryAtom SubClassOfAtom(final Term cA1,
			final Term cA2) {
		return new AbstractQueryAtom(QueryPredicate.SubClassOf, cA1, cA2);
	}

	public static QueryAtom EquivalentClassAtom(final Term classArgument,
			final Term classArgument2) {
		return new AbstractQueryAtom(QueryPredicate.EquivalentClass,
				classArgument, classArgument2);
	}

	public static QueryAtom DisjointWithAtom(final Term cA1,
			final Term cA2) {
		return new AbstractQueryAtom(QueryPredicate.DisjointWith, cA1, cA2);
	}

	public static QueryAtom ComplementOfAtom(final Term cA1,
			final Term cA2) {
		return new AbstractQueryAtom(QueryPredicate.ComplementOf, cA1, cA2);
	}

	// RBOX atoms
	public static QueryAtom SubPropertyOfAtom(final Term pA1,
			final Term pA2) {
		return new AbstractQueryAtom(QueryPredicate.SubPropertyOf, pA1, pA2);
	}

	public static QueryAtom EquivalentPropertyAtom(final Term pA1,
			final Term pA2) {
		return new AbstractQueryAtom(QueryPredicate.EquivalentProperty, pA1,
				pA2);
	}

	public static QueryAtom InverseOfAtom(final Term pA1,
			final Term pA2) {
		return new AbstractQueryAtom(QueryPredicate.InverseOf, pA1, pA2);
	}

	public static QueryAtom ObjectPropertyAtom(final Term pA) {
		return new AbstractQueryAtom(QueryPredicate.ObjectProperty, pA);
	}

	public static QueryAtom DatatypePropertyAtom(final Term pA) {
		return new AbstractQueryAtom(QueryPredicate.DatatypeProperty, pA);
	}

	public static QueryAtom FunctionalAtom(final Term pA) {
		return new AbstractQueryAtom(QueryPredicate.Functional, pA);
	}

	public static QueryAtom InverseFunctionalAtom(final Term pA) {
		return new AbstractQueryAtom(QueryPredicate.InverseFunctional, pA);
	}

	public static QueryAtom TransitiveAtom(final Term pA) {
		return new AbstractQueryAtom(QueryPredicate.Transitive, pA);
	}

	public static QueryAtom SymmetricAtom(final Term pA) {
		return new AbstractQueryAtom(QueryPredicate.Symmetric, pA);
	}

	public static QueryAtom AnnotationAtom(final Term iA,
			final Term pA, final Term ilA) {
		return new AbstractQueryAtom(QueryPredicate.Annotation, iA, pA, ilA);
	}

	// SPARQL-DL nonmonotonic extension
	public static QueryAtom StrictSubClassOfAtom(final Term c1,
			final Term c2) {
		return new AbstractQueryAtom(QueryPredicate.StrictSubClassOf, c1, c2);
	}

	public static QueryAtom DirectSubClassOfAtom(final Term c1,
			final Term c2) {
		return new AbstractQueryAtom(QueryPredicate.DirectSubClassOf, c1, c2);
	}

	public static QueryAtom DirectSubPropertyOfAtom(final Term c1,
			final Term c2) {
		return new AbstractQueryAtom(QueryPredicate.DirectSubPropertyOf, c1, c2);
	}

	public static QueryAtom StrictSubPropertyOfAtom(final Term c1,
			final Term c2) {
		return new AbstractQueryAtom(QueryPredicate.StrictSubPropertyOf, c1, c2);
	}

	public static QueryAtom DirectTypeAtom(final Term i, final Term c) {
		return new AbstractQueryAtom(QueryPredicate.DirectType, i, c);
	}

	// core of undistinguished variables
	public static QueryAtom Core(final Collection<QueryAtom> atoms, final Collection<Term> uv, 
			final KnowledgeBase kb) {
		return new CoreNewImpl(atoms, uv, kb);
	}
	
	public static QueryAtom NotAtom(QueryAtom atom) {
		return new NotQueryAtom( atom );
	}
	
	public static QueryAtom ExecuteAtom(Term queryName, Term... args) {
		Term[] allArgs = new Term[args.length + 1];
		allArgs[ 0] = queryName ;
		System.arraycopy( args, 0, allArgs, 1, args.length );
		
		return new AbstractQueryAtom(QueryPredicate.Execute, allArgs);
	}
}
