// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.query.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.exceptions.UnsupportedQueryException;
import org.mindswap.pellet.query.Query;
import org.mindswap.pellet.query.QueryEngine;
import org.mindswap.pellet.query.QueryExec;
import org.mindswap.pellet.query.QueryPattern;
import org.mindswap.pellet.query.QueryResults;
import org.mindswap.pellet.utils.Bool;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

/**
 * @author Evren Sirin
 */
public class NoDistVarsQueryExec implements QueryExec {
	public static Logger		log	= Logger.getLogger( NoDistVarsQueryExec.class.getName() );

	private KnowledgeBase	kb;

	public NoDistVarsQueryExec() {
	}

	public boolean supports(Query q) {
		return q.getDistVars().isEmpty();
	}

	public QueryResults exec(Query query) {
		QueryResultsImpl results = new QueryResultsImpl( query );

		boolean success = execBoolean( query );

		if( success )
			results.add( new QueryResultBindingImpl() );

		return results;
	}

	public boolean execBoolean(Query query) {
		boolean querySatisfied;

		kb = query.getKB();

		if( query.getConstants().isEmpty() ) {
			throw new UnsupportedQueryException(
					"Executing queries with no constants is not implemented yet!" );
		}

		// unless proven otherwise all (ground) triples are satisfied
		Bool allTriplesSatisfied = Bool.TRUE;

		List patterns = query.getQueryPatterns();
		for( int i = 0; i < patterns.size(); i++ ) {
			QueryPattern triple = (QueryPattern) patterns.get( i );

			// by default we don't know if triple is satisfied
			Bool tripleSatisfied = Bool.UNKNOWN;
			// we can only check ground triples
			if( triple.isGround() ) {
				tripleSatisfied = triple.isTypePattern()
					? kb.isKnownType( triple.getSubject(), triple.getObject() )
					: kb.hasKnownPropertyValue( triple.getSubject(), triple.getPredicate(), triple
							.getObject() );
			}

			// if we cannot decide the truth value of this triple (without a
			// consistency
			// check) then over all truth value cannot be true. However, we will
			// continue
			// to see if there is a triple that is obviously false
			if( tripleSatisfied.isUnknown() )
				allTriplesSatisfied = Bool.UNKNOWN;
			else if( tripleSatisfied.isFalse() ) {
				// if one triple is false then the whole query, which is the
				// conjunction of
				// all triples, is false. We can stop now.
				allTriplesSatisfied = Bool.FALSE;

				if( log.isLoggable( Level.FINER ) )
					log.finer( "Failed triple: " + triple );

				break;
			}
		}

		// if we reached a verdict, return it
		if( allTriplesSatisfied.isKnown() )
			querySatisfied = allTriplesSatisfied.isTrue();
		else {
			// do the unavoidable consistency check
			// We have to care about multiple connected components in the query
			// and evaluate all of them. Given the set of constants, we perform
			// a satisfiability check for the corresponding concepts that
			// describe the patterns for the connected component of the
			// constant. We keep a cache of the processed constants to make sure
			// we do not compute consistency multiple times for the same
			// connected component. As soon as there is a connected component
			// returning false to the consistency check, we return false and
			// exit the loop. If this doesn't happen, we return true.
			Set<ATermAppl> cache = new HashSet<ATermAppl>();

			for( Iterator iter = query.getConstants().iterator(); iter.hasNext(); ) {
				ATermAppl testInd = (ATermAppl) iter.next();

				if( !cache.contains( testInd ) ) {
					cache.add( testInd );
					// rollUpTo creates the class description for the connected
					// component corresponding to the individual
					if( !kb.isType( testInd, query.rollUpTo( testInd ) ) )
						return false;
				}

			}

			querySatisfied = true;
		}

		return querySatisfied;
	}
}
