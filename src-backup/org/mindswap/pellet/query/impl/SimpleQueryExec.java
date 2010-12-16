// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.query.impl;

import java.util.HashMap;
import java.util.Iterator;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.query.Query;
import org.mindswap.pellet.query.QueryEngine;
import org.mindswap.pellet.query.QueryExec;
import org.mindswap.pellet.query.QueryResultBinding;
import org.mindswap.pellet.query.QueryResults;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;


/**
 * @author Daniel
 * @deprecated Use SPARQL-DL engine instead
 */
public class SimpleQueryExec implements QueryExec {
    public static Logger log = Logger.getLogger( SimpleQueryExec.class.getName() );
    
    public SimpleQueryExec() {        
    }
    
    public boolean supports( Query q ) {
        return !q.getDistObjVars().isEmpty();
    }
    
	public QueryResults exec( Query q ) {
	    QueryResults results = new QueryResultsImpl( q );
	    KnowledgeBase kb = q.getKB();
	    
	    long satCount = kb.getABox().satisfiabilityCount;
	    
		HashMap varBindings = new HashMap();
		
		for ( Iterator v = q.getDistObjVars().iterator(); v.hasNext(); ) {
			ATermAppl currVar = (ATermAppl) v.next();
			ATermAppl rolledUpClass = q.rollUpTo( currVar );
			
            if( log.isLoggable( Level.FINER ) )
                log.finer( "Rolled up class " + rolledUpClass );
			varBindings.put( currVar, kb.getInstances( rolledUpClass ) );
		}
		
        if( log.isLoggable( Level.FINER ) )
            log.finer( "Var bindings: " + varBindings );
		
		Iterator i = new BindingIterator( q, varBindings );
		
		boolean hasLiterals = !q.getDistLitVars().isEmpty();

		if ( hasLiterals ) {
			while ( i.hasNext() ) {
			    QueryResultBinding b = (QueryResultBinding) i.next();

				Iterator l = new LiteralIterator( q, b );
				while ( l.hasNext() ) {
				    QueryResultBinding mappy = (QueryResultBinding) l.next();
					boolean queryTrue = QueryEngine.execBoolean( q.apply( mappy ) ); 
					if ( queryTrue ) 
						results.add( mappy );				
				}
			}		
		} else {
			while ( i.hasNext() ) {
			    QueryResultBinding b = (QueryResultBinding) i.next();
	
				boolean queryTrue = 
				    (q.getDistObjVars().size() == 1) || // if there is only a single var no need to verify
					QueryEngine.execBoolean( q.apply( b ) ); 
				if ( queryTrue ) 
					results.add( b );					
			}
		}
		
        if( log.isLoggable( Level.FINER ) )
            log.finer( "Total satisfiability operations: " + (kb.getABox().satisfiabilityCount-satCount) );
		
		return results;
	}
	
}
