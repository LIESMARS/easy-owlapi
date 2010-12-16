// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.query.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.query.Query;
import org.mindswap.pellet.query.QueryEngine;
import org.mindswap.pellet.query.QueryExec;
import org.mindswap.pellet.query.QueryResultBinding;
import org.mindswap.pellet.query.QueryResults;

import com.clarkparsia.pellet.sparqldl.model.QueryParameters;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

/**
 * @author Daniel
 *
 * @deprecated Use SPARQL-DL engine instead
 */
public class OptimizedQueryExec implements QueryExec {
    public static Logger log = Logger.getLogger( OptimizedQueryExec.class.getName() );
    
    public boolean supports( Query q ) {
        return q.getDistObjVars().size() > 2;
    }
   
	public QueryResults exec( Query q ) {
		// Enable query parameterization
		QueryParameters parameters = q.getQueryParameters();
		
	    QueryResults results = new QueryResultsImpl( q );
	    KnowledgeBase kb = q.getKB();
	    
	    // get dit obj vars
		Set distObjVars = q.getDistObjVars();
		int size = distObjVars.size();	

		// set of possible candidates for each var
		Map varBindings = new HashMap( size );
		// ordered vars (sorted according to the number of candidates)
		ATermAppl[] vars = new ATermAppl[ size ];
		int index = 0;
		for(Iterator i = distObjVars.iterator(); i.hasNext();) {
            ATermAppl currVar = (ATermAppl) i.next();
			ATermAppl rolledUpClass = q.rollUpTo( currVar );
			
            if( log.isLoggable( Level.FINER ) )
                log.finer( currVar + " -> " + rolledUpClass );

			// add the var
			vars[ index++ ] = currVar;

			// add the candidates
			varBindings.put( currVar, kb.getInstances( rolledUpClass ) );
		}
				
		log.finer( varBindings.toString() );
		
		// sort the vars according to the number of candidates
		Arrays.sort( vars, new ListSizeComparator( varBindings ) );

		// get the var bindings in the same order as the vars
		Collection[] varB = new Collection[ size ];
		for ( int i = 0; i < size; i++ )
		    varB[ i ] = (Collection) varBindings.get( vars[i] );		

		ArrayList[] goodList = new ArrayList[ size ];
		
		for ( int i = 0; i < goodList.length; i++ )
			goodList[i] = new ArrayList();
		
		for ( Iterator i = varB[0].iterator(); i.hasNext(); ) {
			ATermAppl curr = (ATermAppl) i.next();
			for ( Iterator j = varB[1].iterator(); j.hasNext(); ) {
			    QueryResultBinding b = new QueryResultBindingImpl();
				
				b.setValue( vars[ 0 ], curr );
				b.setValue( vars[ 1 ], (ATermAppl) j.next() );			

				boolean queryTrue = QueryEngine.execBoolean( q.apply( b ) ); 
				if ( queryTrue ) {
					goodList[ 1 ].add( b );
					if( log.isLoggable( Level.FINER ) )
                        log.finer( "Accepted Pair: " + b );
				} else {
				    if( log.isLoggable( Level.FINER )) 
                        log.finer( "Rejected Pair: " + b );
				}
			}
		}
		
		for ( int i = 2; i < size; i++ ) {
			for ( Iterator j = goodList[i - 1].iterator(); j.hasNext(); ) {
			    QueryResultBinding goodMap = (QueryResultBinding) j.next();
				
				for ( Iterator k = varB[i].iterator(); k.hasNext(); ) {
					ATermAppl curr = (ATermAppl) k.next();
					
					QueryResultBinding b = (QueryResultBinding) goodMap.clone();
					b.setValue( vars[ i ], curr );
									
					boolean queryTrue = QueryEngine.execBoolean( q.apply( b ) ); 
					if ( queryTrue ) {
					    goodList[i].add( b );
                        if( log.isLoggable( Level.FINER ) )
                            log.finer( "Accepted binding: " + b );
					} else {
                        if( log.isLoggable( Level.FINER ) )
                            log.finer( "Rejected binding: " + b );
					}
				}
			}
		}
				
		boolean hasLiterals = !q.getDistLitVars().isEmpty();
		if ( hasLiterals ) {
			for ( Iterator h = goodList[ goodList.length - 1 ].iterator(); h.hasNext(); ) {
			    QueryResultBinding b = (QueryResultBinding) h.next();
				Iterator l = new LiteralIterator( q, b );
				while ( l.hasNext() ) {
				    QueryResultBinding mappy = (QueryResultBinding) l.next();
					
				    // since literal iterator generates literals using getDataPropertyValues
				    // we do not need to check the correctness again (we know rest of the triples
				    // in the query is satisfied)
				    results.add( mappy );
				}
			}
		} else {
			// goodList check has already taken care of checking these bindings, just need to return them
			for ( Iterator h = goodList[ goodList.length - 1 ].iterator(); h.hasNext(); ) {
			    QueryResultBinding b = (QueryResultBinding) h.next();			
				
				results.add( b );
			}
		}
		
		return results;
	}

}
