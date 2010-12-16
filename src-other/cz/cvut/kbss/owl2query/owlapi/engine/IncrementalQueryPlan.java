// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.owlapi.engine;

import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cvut.kbss.owl2query.owlapi.model.Query;
import cz.cvut.kbss.owl2query.owlapi.model.QueryAtom;
import cz.cvut.kbss.owl2query.owlapi.model.QueryPredicate;
import cz.cvut.kbss.owl2query.owlapi.model.ResultBinding;

/**
 * <p>
 * Title: Query Plan that recomputes the cost of the query in a greedy way.
 * </p>
 * <p>
 * Description: TODO cache costs - not to recompute them
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
public class IncrementalQueryPlan extends QueryPlan {

	private static final Logger		log	= Logger.getLogger( IncrementalQueryPlan.class.getName() );

	public final Stack<Integer>		explored;

	private final List<QueryAtom>	atoms;

	private int						size;

	private QueryCost				cost;

	public IncrementalQueryPlan(Query query) {
		super( query );

		QuerySizeEstimator.computeSizeEstimate( query );

		explored = new Stack<Integer>();

		atoms = query.getAtoms();

		size = atoms.size();

		cost = new QueryCost( query.getKB() );

		reset();
	}

	@Override
	public QueryAtom next(final ResultBinding binding) {
		int best = -1;
		QueryAtom bestAtom = null;
		double bestCost = Double.POSITIVE_INFINITY;

		for( int i = 0; i < size; i++ ) {
			if( !explored.contains( i ) ) {
				QueryAtom atom = atoms.get( i );
				QueryAtom atom2;

				if( atom.isGround() ) {
					atom2 = atom;
				}
				else {
					atom2 = atom.apply( binding );

					if( atom2.getPredicate().equals( QueryPredicate.Not ) && !atom2.isGround() ) {
						continue;
					}
				}

				final double atomCost = cost.estimate( atom2 );

				if( log.isLoggable( Level.FINER ) ) {
					log.finer( "Atom=" + atom + ", cost=" + cost + ", best cost=" + bestCost );
				}
				if( atomCost <= bestCost ) {
					bestCost = atomCost;
					bestAtom = atom2;
					best = i;
				}
			}
		}

		explored.add( best );

		if( log.isLoggable( Level.FINER ) ) {
			String treePrint = "";
			for( int j = 0; j < explored.size(); j++ ) {
				treePrint += " ";
			}
			treePrint += bestAtom + " : " + bestCost;

			log.finer( treePrint );
		}

		return bestAtom;
	}

	@Override
	public boolean hasNext() {
		return explored.size() < size;
	}

	@Override
	public void back() {
		explored.pop();
	}

	@Override
	public void reset() {
		explored.clear();
	}
}
