// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindswap.pellet.exceptions.UnsupportedQueryException;
import org.mindswap.pellet.utils.ATermUtils;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryAtom;
import com.clarkparsia.pellet.sparqldl.model.QueryPredicate;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;

/**
 * <p>
 * Title: Query Plan the Uses Full Query Reordering.
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
public class CostBasedQueryPlanNew extends QueryPlan {
	private static final Logger	log	= Logger.getLogger( CostBasedQueryPlanNew.class.getName() );

	private List<QueryAtom>	sortedAtoms;

	private int				index;

	private int				size;

	private QueryCost		cost;

	public CostBasedQueryPlanNew(Query query) {
		super( query );

		QuerySizeEstimator.computeSizeEstimate( query );

		index = 0;
		size = query.getAtoms().size();
		cost = new QueryCost( query.getKB() );
		sortedAtoms = null;

		if( size == 0 ) {
			return;
		}
		else if( size == 1 ) {
			sortedAtoms = query.getAtoms();
		}
		else {
			double minCost = chooseOrdering( new ArrayList<QueryAtom>( query.getAtoms() ), new ArrayList<QueryAtom>( size ), new HashSet<ATermAppl>(), false, Double.POSITIVE_INFINITY );
			
			if( sortedAtoms == null ) {
				throw new UnsupportedQueryException( "No safe ordering for query: " + query );
			}

			if( log.isLoggable( Level.FINE ) ) {
				log.log( Level.FINE, "WINNER : Cost=" + minCost + " ,atoms=" + sortedAtoms );
			}
		}
	}

	private double chooseOrdering(List<QueryAtom> atoms, List<QueryAtom> orderedAtoms, Set<ATermAppl> boundVars, boolean notOptimal, double minCost) {
		if( atoms.isEmpty() ) {
			if( notOptimal ) {
				if( sortedAtoms == null ) {
					sortedAtoms = new ArrayList<QueryAtom>( orderedAtoms );
				}						
			}
			else {
				double queryCost = cost.estimate( orderedAtoms );		
				log.fine( "Cost " + queryCost + " for " + orderedAtoms  );
				if( queryCost < minCost ) {
					sortedAtoms = new ArrayList<QueryAtom>( orderedAtoms );
					minCost = queryCost;
				}
			}
			
			return minCost;
		}
		
		for( int i = 0; i < atoms.size(); i++ ) {
			QueryAtom atom = atoms.get( i );
			
			boolean newNonOptimal = notOptimal;
			Set<ATermAppl> newBoundVars = new HashSet<ATermAppl>( boundVars );
			// TODO reorder UV atoms after all class and property variables are
			// bound.
	
			if( !atom.isGround() ) {
				int boundCount = 0;
				int unboundCount = 0;
		
				for( ATermAppl a : atom.getArguments() ) {
					if( ATermUtils.isVar( a ) ) {
						if( newBoundVars.add( a ) ) {
							unboundCount++;
						}
						else {
							boundCount++;
						}
					}
				}
		
				if( unboundCount > 0 && atom.getPredicate().equals( QueryPredicate.Not ) ) {
					log.fine( "Unbound vars for not" );
					continue;
				}
		
				if( boundCount == 0 && newBoundVars.size() > unboundCount ) {
					if( sortedAtoms != null ) {
						log.fine( "Stop at not optimal ordering" );
						continue;
					}
					else {
						log.fine( "Continue not optimal ordering, no solution yet." );
						newNonOptimal = true;
					}
				}
			}

			atoms.remove( atom );			
			orderedAtoms.add( atom );
			
			log.fine( "Atom[" + i + "/" + atoms.size() + "] " + atom + " from " + atoms + " to " + orderedAtoms );
						
			minCost = chooseOrdering( atoms, orderedAtoms, newBoundVars, newNonOptimal, minCost );	
			
			atoms.add( i, atom );			
			orderedAtoms.remove( orderedAtoms.size() - 1 );
		}
		
		return minCost;
	}

	@Override
	public QueryAtom next(final ResultBinding binding) {
		final QueryAtom a = sortedAtoms.get( index++ );
		if( a.isGround() ) {
			return a;
		}
		else {
			return a.apply( binding );
		}
	}

	@Override
	public boolean hasNext() {
		return index < size;
	}

	@Override
	public void back() {
		index--;
	}

	@Override
	public void reset() {
		index = 0;
	}
}
