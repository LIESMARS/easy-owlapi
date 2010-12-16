// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.owl2query.engine;

import cz.cvut.owl2query.model.KBOperation;
import cz.cvut.owl2query.model.Term;
import cz.cvut.owl2query.model.KnowledgeBase;
import cz.cvut.owl2query.model.QueryAtom;
import cz.cvut.owl2query.model.SizeEstimate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Title: AtomCostImpl
 * </p>
 * <p>
 * Description: Computes the cost estimate for given atom.
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
public class QueryCost {
	private double			staticCost;

	private double			branchCount;
	
	private KnowledgeBase	kb;

	private SizeEstimate	estimate;

	public QueryCost(KnowledgeBase kb) {
		this.kb = kb;
		this.estimate = kb.getSizeEstimate();
	}


	public double estimate(List<QueryAtom> atoms) {
		return estimate( atoms, new HashSet<Term>() );
	}

	public double estimate(List<QueryAtom> atoms, Collection<Term> bound) {
		double totalStaticCount = 1.0;
		double totalBranchCount = 1.0;

		branchCount = 1;
		staticCost = 1.0;

		int n = atoms.size();
		
		Set<Term> lastBound = new HashSet<Term>( bound );
		List<Set<Term>> boundList = new ArrayList<Set<Term>>(n);
		for( int i = 0; i < n; i++ ) {
			QueryAtom atom = atoms.get( i );
			
			boundList.add( lastBound );
			
			lastBound = new HashSet<Term>( lastBound );
			lastBound.addAll( atom.getArguments() );	
		}
		
		
		for( int i = n - 1; i >= 0; i-- ) {
			QueryAtom atom = atoms.get( i );

			estimate( atom, boundList.get( i ) );

			totalBranchCount *= branchCount;
			totalStaticCount = staticCost + branchCount * totalStaticCount;
		}

		staticCost = totalStaticCount;
		branchCount = totalBranchCount;
		
		return staticCost;
	}
	
	public double estimate(final QueryAtom atom) {
		return estimate( atom, new HashSet<Term>() );
	}
	
	public double estimate(final QueryAtom atom, final Collection<Term> bound) {
		boolean direct = false;
		boolean strict = false;

		List<Term> arguments = atom.getArguments();
		for( Term a : arguments ) {
			if( isConstant( a ) ) {
				bound.add( a );
			}
		}

		switch ( atom.getPredicate() ) {
		case DirectType:
			direct = true;
		case Type:
			Term instance = arguments.get( 0 );
			Term clazz = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = direct
					? estimate.getCost( KBOperation.IS_DIRECT_TYPE )
					: estimate.getCost( KBOperation.IS_TYPE );
				branchCount = 1;
			}
			else if( bound.contains( clazz ) ) {
				staticCost = direct
					? estimate.getCost( KBOperation.GET_DIRECT_INSTANCES )
					: estimate.getCost( KBOperation.GET_INSTANCES );
				branchCount = isConstant( clazz )
					? estimate.size( clazz )
					: estimate.avgInstancesPerClass( direct );
			}
			else if( bound.contains( instance ) ) {
				staticCost = estimate.getCost( KBOperation.GET_TYPES );
				branchCount = isConstant( instance )
					? estimate.classesPerInstance( instance, direct )
					: estimate.avgClassesPerInstance( direct );
			}
			else {
				staticCost = estimate.getClassCount() * (direct
					? estimate.getCost( KBOperation.GET_DIRECT_INSTANCES )
					: estimate.getCost( KBOperation.GET_INSTANCES ));
				branchCount = estimate.getClassCount() * estimate.avgInstancesPerClass( direct );
			}
			break;

		case Annotation: // TODO
		case PropertyValue:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.HAS_PROPERTY_VALUE );
				branchCount = 1;
			}
			else {
				Term subject = arguments.get( 0 );
				Term predicate = arguments.get( 1 );
				Term object = arguments.get( 2 );

				if( bound.contains( predicate ) ) {
					if( bound.contains( subject ) ) {
						staticCost = estimate.getCost( KBOperation.GET_PROPERTY_VALUE );
						branchCount = isConstant( predicate )
							? estimate.avg( predicate )
							: estimate.avgSubjectsPerProperty();
					}
					else if( bound.contains( object ) ) {
						staticCost = estimate.getCost( KBOperation.GET_PROPERTY_VALUE );
						if( isConstant( predicate ) ) {
							if( kb.isObjectProperty( predicate ) ) {
								branchCount = estimate.avg( inv( predicate ) );
							}
							else {
								branchCount = estimate.avgSubjectsPerProperty();
							}
						}
						else {
							branchCount = estimate.avgSubjectsPerProperty();
						}
					}
					else {
						staticCost = estimate.getCost( KBOperation.GET_PROPERTY_VALUE )
						/*
						 * TODO should be st. like
						 * GET_INSTANCES_OF_ROLLED_CONCEPT that reflects the
						 * complexity of the concept.
						 */
						+ (isConstant( predicate )
							? estimate.avg( predicate )
							: estimate.avgSubjectsPerProperty())
								* estimate.getCost( KBOperation.GET_PROPERTY_VALUE );
						branchCount = (isConstant( predicate )
							? estimate.size( predicate )
							: estimate.avgPairsPerProperty());
					}
				}
				else if( bound.contains( subject ) || bound.contains( object ) ) {
					staticCost = estimate.getReferencedOWLPropertyCount()
							* estimate.getCost( KBOperation.GET_PROPERTY_VALUE );
					branchCount = estimate.getReferencedOWLPropertyCount() * estimate.avgSubjectsPerProperty();
				}
				else {
					staticCost = estimate.getReferencedOWLPropertyCount()
							* (estimate.getCost( KBOperation.GET_PROPERTY_VALUE )
							/*
							 * TODO should be st. like
							 * GET_INSTANCES_OF_ROLLED_CONCEPT that reflects the
							 * complexity of the concept.
							 */+ estimate.avgSubjectsPerProperty()
									* estimate.getCost( KBOperation.GET_PROPERTY_VALUE ));
					branchCount = estimate.avgPairsPerProperty() * estimate.getReferencedOWLPropertyCount();
				}
			}
			break;

		case SameAs:
			Term saLHS = arguments.get( 0 );
			Term saRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_SAME_AS );
				branchCount = 1;
			}
			else if( bound.contains( saLHS ) || bound.contains( saRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_SAMES );

				if( bound.contains( saLHS ) ) {
					branchCount = isConstant( saLHS )
						? estimate.sames( saLHS )
						: estimate.avgSamesPerInstance();
				}
				else {
					branchCount = isConstant( saRHS )
						? estimate.sames( saRHS )
						: estimate.avgSamesPerInstance();
				}
			}
			else {
				staticCost = estimate.getInstanceCount() * estimate.getCost( KBOperation.GET_SAMES );
				branchCount = estimate.getInstanceCount() * estimate.avgSamesPerInstance();
			}
			break;
		case DifferentFrom:
			Term dfLHS = arguments.get( 0 );
			Term dfRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_DIFFERENT_FROM );
				branchCount = 1;
			}
			else if( bound.contains( dfLHS ) || bound.contains( dfRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_DIFFERENTS );

				if( bound.contains( dfLHS ) ) {
					branchCount = isConstant( dfLHS )
						? estimate.differents( dfLHS )
						: estimate.avgDifferentsPerInstance();
				}
				else {
					branchCount = isConstant( dfRHS )
						? estimate.differents( dfRHS )
						: estimate.avgDifferentsPerInstance();
				}
			}
			else {
				staticCost = estimate.getInstanceCount()
						* estimate.getCost( KBOperation.GET_DIFFERENTS );
				branchCount = estimate.getInstanceCount() * estimate.avgDifferentsPerInstance();
			}
			break;

		case DirectSubClassOf:
			direct = true;
		case StrictSubClassOf:
			strict = true;
		case SubClassOf:
			Term clazzLHS = arguments.get( 0 );
			Term clazzRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				if( strict ) {
					if( direct ) {
						staticCost = estimate.getCost( KBOperation.GET_DIRECT_SUB_OR_SUPERCLASSES );
					}
					else {
						staticCost = estimate.getCost( KBOperation.IS_SUBCLASS_OF )
								+ estimate.getCost( KBOperation.GET_EQUIVALENT_CLASSES );
					}
				}
				else {
					staticCost = estimate.getCost( KBOperation.IS_SUBCLASS_OF );
				}

				branchCount = 1;
			}
			else if( bound.contains( clazzLHS ) || bound.contains( clazzRHS ) ) {
				if( strict && !direct ) {
					staticCost = estimate.getCost( KBOperation.GET_SUB_OR_SUPERCLASSES )
							+ estimate.getCost( KBOperation.GET_EQUIVALENT_CLASSES );
				}
				else {
					staticCost = direct
						? estimate.getCost( KBOperation.GET_DIRECT_SUB_OR_SUPERCLASSES )
						: estimate.getCost( KBOperation.GET_SUB_OR_SUPERCLASSES );
				}
				if( bound.contains( clazzLHS ) ) {
					branchCount = isConstant( clazzLHS )
						? estimate.superClasses( clazzLHS, direct )
						: estimate.avgSuperClasses( direct );

					if( strict ) {
						branchCount -= isConstant( clazzLHS )
							? estimate.equivClasses( clazzLHS )
							: estimate.avgEquivClasses();
						branchCount = Math.max( branchCount, 0 );
					}
				}
				else {
					branchCount = isConstant( clazzRHS )
						? estimate.superClasses( clazzRHS, direct )
						: estimate.avgSuperClasses( direct );

					if( strict ) {
						branchCount -= isConstant( clazzRHS )
							? estimate.equivClasses( clazzRHS )
							: estimate.avgEquivClasses();
						branchCount = Math.max( branchCount, 0 );
					}
				}
			}
			else {
				if( strict && !direct ) {
					staticCost = estimate.getCost( KBOperation.GET_SUB_OR_SUPERCLASSES )
							+ estimate.getCost( KBOperation.GET_EQUIVALENT_CLASSES );
				}
				else {
					staticCost = direct
						? estimate.getCost( KBOperation.GET_DIRECT_SUB_OR_SUPERCLASSES )
						: estimate.getCost( KBOperation.GET_SUB_OR_SUPERCLASSES );
				}

				staticCost *= estimate.getClassCount();

				branchCount = estimate.getClassCount() * estimate.avgSubClasses( direct );

				if( strict ) {
					branchCount -= estimate.avgEquivClasses();
					branchCount = Math.max( branchCount, 0 );
				}
			}
			break;
		case EquivalentClass:
			Term eqcLHS = arguments.get( 0 );
			Term eqcRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_EQUIVALENT_CLASS );
				branchCount = 1;
			}
			else if( bound.contains( eqcLHS ) || bound.contains( eqcRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_EQUIVALENT_CLASSES );

				if( bound.contains( eqcLHS ) ) {
					branchCount = isConstant( eqcLHS )
						? estimate.equivClasses( eqcLHS )
						: estimate.avgEquivClasses();
				}
				else {
					branchCount = isConstant( eqcRHS )
						? estimate.equivClasses( eqcRHS )
						: estimate.avgEquivClasses();
				}
			}
			else {
				staticCost = estimate.getClassCount()
						* estimate.getCost( KBOperation.GET_EQUIVALENT_CLASSES );
				branchCount = estimate.getClassCount() * estimate.avgEquivClasses();
			}
			break;
		case DisjointWith:
			Term dwLHS = arguments.get( 0 );
			Term dwRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_DISJOINT_WITH );
				branchCount = 1;
			}
			else if( bound.contains( dwLHS ) || bound.contains( dwRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_DISJOINT_CLASSES );

				if( bound.contains( dwLHS ) ) {
					branchCount = isConstant( dwLHS )
						? estimate.disjoints( dwLHS )
						: estimate.avgDisjointClasses();
				}
				else {
					branchCount = isConstant( dwRHS )
						? estimate.disjoints( dwRHS )
						: estimate.avgDisjointClasses();
				}
			}
			else {
				staticCost = estimate.getClassCount()
						* estimate.getCost( KBOperation.GET_DISJOINT_CLASSES );
				branchCount = estimate.getClassCount() * estimate.avgDisjointClasses();
			}
			break;
		case ComplementOf:
			Term coLHS = arguments.get( 0 );
			Term coRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_COMPLEMENT_OF );
				branchCount = 1;
			}
			else if( bound.contains( coLHS ) || bound.contains( coRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_COMPLEMENT_CLASSES );

				if( bound.contains( coLHS ) ) {
					branchCount = isConstant( coLHS )
						? estimate.complements( coLHS )
						: estimate.avgComplementClasses();
				}
				else {
					branchCount = isConstant( coRHS )
						? estimate.complements( coRHS )
						: estimate.avgComplementClasses();
				}
			}
			else {
				staticCost = estimate.getClassCount()
						* estimate.getCost( KBOperation.GET_COMPLEMENT_CLASSES );
				branchCount = estimate.getClassCount() * estimate.avgComplementClasses();
			}
			break;

		case DirectSubPropertyOf:
			direct = true;
		case StrictSubPropertyOf:
			strict = true;
		case SubPropertyOf:
			Term spLHS = arguments.get( 0 );
			Term spRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				if( strict ) {
					if( direct ) {
						staticCost = estimate
								.getCost( KBOperation.GET_DIRECT_SUB_OR_SUPERPROPERTIES );
					}
					else {
						staticCost = estimate.getCost( KBOperation.IS_SUBPROPERTY_OF )
								+ estimate.getCost( KBOperation.GET_EQUIVALENT_PROPERTIES );
					}
				}
				else {
					staticCost = estimate.getCost( KBOperation.IS_SUBPROPERTY_OF );
				}

				branchCount = 1;
			}
			else if( bound.contains( spLHS ) || bound.contains( spRHS ) ) {
				if( strict && !direct ) {
					staticCost = estimate.getCost( KBOperation.GET_SUB_OR_SUPERPROPERTIES )
							+ estimate.getCost( KBOperation.GET_EQUIVALENT_PROPERTIES );
				}
				else {
					staticCost = direct
						? estimate.getCost( KBOperation.GET_DIRECT_SUB_OR_SUPERPROPERTIES )
						: estimate.getCost( KBOperation.GET_SUB_OR_SUPERPROPERTIES );
				}
				if( bound.contains( spLHS ) ) {
					branchCount = isConstant( spLHS )
						? estimate.superProperties( spLHS, direct )
						: estimate.avgSuperProperties( direct );

					if( strict ) {
						branchCount -= isConstant( spLHS )
							? estimate.equivProperties( spLHS )
							: estimate.avgEquivProperties();
						branchCount = Math.max( branchCount, 0 );

					}
				}
				else {
					branchCount = isConstant( spRHS )
						? estimate.superProperties( spRHS, direct )
						: estimate.avgSuperProperties( direct );

					if( strict ) {
						branchCount -= isConstant( spRHS )
							? estimate.equivProperties( spRHS )
							: estimate.avgEquivProperties();
						branchCount = Math.max( branchCount, 0 );

					}
				}
			}
			else {
				if( strict && !direct ) {
					staticCost = estimate.getCost( KBOperation.GET_SUB_OR_SUPERPROPERTIES )
							+ estimate.getCost( KBOperation.GET_EQUIVALENT_PROPERTIES );
				}
				else {
					staticCost = direct
						? estimate.getCost( KBOperation.GET_DIRECT_SUB_OR_SUPERPROPERTIES )
						: estimate.getCost( KBOperation.GET_SUB_OR_SUPERPROPERTIES );
				}

				staticCost *= estimate.getReferencedOWLPropertyCount();

				branchCount = estimate.getReferencedOWLPropertyCount() * estimate.avgSubProperties( direct );

				if( strict ) {
					branchCount -= estimate.avgEquivProperties();
					branchCount = Math.max( branchCount, 0 );

				}
			}
			break;

		case EquivalentProperty:
			Term eqpLHS = arguments.get( 0 );
			Term eqpRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_EQUIVALENT_PROPERTY );
				branchCount = 1;
			}
			else if( bound.contains( eqpLHS ) || bound.contains( eqpRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_EQUIVALENT_PROPERTIES );

				if( bound.contains( eqpLHS ) ) {
					branchCount = isConstant( eqpLHS )
						? estimate.equivProperties( eqpLHS )
						: estimate.avgEquivProperties();
				}
				else {
					branchCount = isConstant( eqpRHS )
						? estimate.equivProperties( eqpRHS )
						: estimate.avgEquivProperties();
				}
			}
			else {
				staticCost = estimate.getReferencedOWLPropertyCount()
						* estimate.getCost( KBOperation.GET_EQUIVALENT_PROPERTIES );
				branchCount = estimate.getReferencedOWLPropertyCount() * estimate.avgEquivProperties();
			}
			break;
		case InverseOf:
			Term ioLHS = arguments.get( 0 );
			Term ioRHS = arguments.get( 1 );

			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_INVERSE_OF );
				branchCount = 1;
			}
			else if( bound.contains( ioLHS ) || bound.contains( ioRHS ) ) {
				staticCost = estimate.getCost( KBOperation.GET_INVERSES );

				if( bound.contains( ioLHS ) ) {
					branchCount = isConstant( ioLHS )
						? estimate.inverses( ioLHS )
						: estimate.avgInverseProperties();
				}
				else {
					branchCount = isConstant( ioRHS )
						? estimate.inverses( ioRHS )
						: estimate.avgInverseProperties();
				}
			}
			else {
				staticCost = estimate.getReferencedOWLPropertyCount()
						* estimate.getCost( KBOperation.GET_INVERSES );
				branchCount = estimate.getReferencedOWLPropertyCount() * estimate.avgInverseProperties();
			}
			break;
		case ObjectProperty:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_OBJECT_PROPERTY );
				branchCount = 1;
			}
			else {
				staticCost = estimate.getCost( KBOperation.GET_OBJECT_PROPERTIES );
				branchCount = estimate.getObjectPropertyCount();
			}
			break;
		case DatatypeProperty:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_DATATYPE_PROPERTY );
				branchCount = 1;
			}
			else {
				staticCost = estimate.getCost( KBOperation.GET_DATATYPE_PROPERTIES );
				branchCount = estimate.getDataPropertyCount();
			}
			break;
		case Functional:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_FUNCTIONAL_PROPERTY );
				branchCount = 1;
			}
			else {
				staticCost = estimate.getCost( KBOperation.GET_FUNCTIONAL_PROPERTIES );
				branchCount = estimate.getFunctionalPropertyCount();
			}
			break;
		case InverseFunctional:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_INVERSE_FUNCTIONAL_PROPERTY );
				branchCount = 1;
			}
			else {
				staticCost = estimate.getCost( KBOperation.GET_INVERSE_FUNCTIONAL_PROPERTIES );
				branchCount = estimate.getInverseFunctionalPropertyCount();
			}
			break;
		case Transitive:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_TRANSITIVE_PROPERTY );
				branchCount = 1;
			}
			else {
				staticCost = estimate.getCost( KBOperation.GET_TRANSITIVE_PROPERTIES );
				branchCount = estimate.getTransitivePropertyCount();
			}
			break;
		case Symmetric:
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_SYMMETRIC_PROPERTY );
				branchCount = 1;
			}
			else {
				staticCost = estimate.getCost( KBOperation.GET_SYMMETRIC_PROPERTIES );
				branchCount = estimate.getSymmetricPropertyCount();
			}
			break;
		case Not:
			estimate( ((NotQueryAtom) atom).getAtom(), bound );
			if( !bound.containsAll( arguments ) ) {
				staticCost = Double.POSITIVE_INFINITY;
			}
			break;

		case Execute:
			Query query = QueryRegistry.getQuery( atom );
			estimate( query.getAtoms(), bound );

			break;

		case UndistVarCore:
			// if (!bound.containsAll(query.getDistVarsForType(VarType.CLASS))
			// || !bound.containsAll(query
			// .getDistVarsForType(VarType.PROPERTY))) {
			// // meanwhile not supporting query orderings that allow evaluate
			// // schema atoms after a core.
			// return Double.MAX_VALUE;
			// }

			// neglecting rolling-ups
			if( bound.containsAll( arguments ) ) {
				staticCost = estimate.getCost( KBOperation.IS_TYPE );
				branchCount = 1;
			}
			else {
				CoreAtom core = (CoreAtom) atom;
				int n = core.getDistVars().size();

				double b = Math.pow( estimate.avgInstancesPerClass( false ), n );
				branchCount = b;

				switch ( QueryEngine.getStrategy( atom ) ) {
				case ALLFAST: // TODO
				case SIMPLE:
					staticCost = n * estimate.getCost( KBOperation.GET_INSTANCES ) + b
							* estimate.getCost( KBOperation.IS_TYPE );
					break;
				default:
					throw new IllegalArgumentException( "Not yet implemented." );
				}
			}
			break;
		default:
			throw new UnsupportedFeatureException( "Unknown atom type " + atom.getPredicate() + "." );
		}
		
		return staticCost;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getBranchCount() {
		return branchCount;
	}

	/**
	 * {@inheritDoc}
	 */
	public double getStaticCost() {
		return staticCost;
	}

	private Term inv(Term pred) {
		return kb.getRBox().getRole( pred ).getInverse().getName();
	}

	private boolean isConstant(Term a) {
		return !ATermUtils.isVar( a );
	}

}
