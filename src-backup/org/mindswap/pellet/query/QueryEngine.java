// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com
//
// ---
// Portions Copyright (c) 2003 Ron Alford, Mike Grove, Bijan Parsia, Evren Sirin
// Alford, Grove, Parsia, Sirin parts of this source code are available under the terms of the MIT License.
//
// The MIT License
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to
// deal in the Software without restriction, including without limitation the
// rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
// sell copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
// IN THE SOFTWARE.

package org.mindswap.pellet.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.exceptions.InternalReasonerException;
import org.mindswap.pellet.query.impl.ARQParser;
import org.mindswap.pellet.query.impl.DistVarsQueryExec;
import org.mindswap.pellet.query.impl.MultiQueryResults;
import org.mindswap.pellet.query.impl.NoDistVarsQueryExec;
import org.mindswap.pellet.query.impl.OptimizedQueryExec;
import org.mindswap.pellet.query.impl.QueryImpl;
import org.mindswap.pellet.query.impl.QueryResultBindingImpl;
import org.mindswap.pellet.query.impl.QueryResultsImpl;
import org.mindswap.pellet.query.impl.SimpleQueryExec;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.DisjointSet;
import org.mindswap.pellet.utils.PermutationGenerator;
import org.mindswap.pellet.utils.SetUtils;
import org.mindswap.pellet.utils.SizeEstimate;
import org.mindswap.pellet.utils.Timer;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

import com.hp.hpl.jena.query.Syntax;


/**
 * @author Evren Sirin
 * @deprecated Use {@link com.clarkparsia.pellet.sparqldl.engine.QueryEngine} instead
 */
public class QueryEngine {
    public final static Logger log = Logger.getLogger( QueryEngine.class.getName() );
    
    public final static Syntax DEFAULT_SYNTAX = Syntax.syntaxSPARQL;
    
    private static DistVarsQueryExec distVars = new DistVarsQueryExec();
    private static OptimizedQueryExec optimized = new OptimizedQueryExec();
    private static SimpleQueryExec simple = new SimpleQueryExec();
    private static NoDistVarsQueryExec noVars = new NoDistVarsQueryExec();
    
    private static QueryExec[] queryExecs = 
    	{noVars, distVars, optimized, simple};
    
//    private static QuerySplitter splitter = new QuerySplitter();

    public static QueryParser createParser() {
        return createParser( DEFAULT_SYNTAX );
    }
    
    public static QueryParser createParser( Syntax syntax ) {
        ARQParser parser = new ARQParser();   
        parser.setSyntax( syntax );

        return parser;
    }

    public static QueryResults exec( String queryStr, KnowledgeBase kb ) {    
	    return exec( queryStr, kb, DEFAULT_SYNTAX );	    
	}
    
    public static QueryResults execRDQL( String queryStr, KnowledgeBase kb ) {
	    return exec( queryStr, kb, Syntax.syntaxRDQL );	    
	}
    
    public static QueryResults execSPARQL( String queryStr, KnowledgeBase kb ) {
	    return exec( queryStr, kb, Syntax.syntaxSPARQL );	    
	}

    public static Query parse( String queryStr, KnowledgeBase kb ) {
        return parse( queryStr, kb, DEFAULT_SYNTAX );       
    }
    
    public static Query parse( String queryStr, KnowledgeBase kb, Syntax syntax ) {    
        QueryParser parser = createParser( syntax );
        Query query = parser.parse( queryStr, kb );
        
        return query;
    }
    
    public static QueryResults exec( String queryStr, KnowledgeBase kb, Syntax syntax ) {    
	    Query query = parse( queryStr, kb, syntax );
	    
	    return exec( query );
	}

    public static QueryResults exec( Query query, KnowledgeBase kb ) {
        KnowledgeBase origKB = query.getKB();
        query.setKB( kb );
        QueryResults results = exec( query );
        query.setKB( origKB );
        
        return results;
    }
    
    public static QueryResults exec( Query query ) {
        if( query.getQueryPatterns().isEmpty() ) {
    		QueryResultsImpl results = new QueryResultsImpl( query );	
   		    results.add(new QueryResultBindingImpl());
                
            return results;             
        }        
            
        if( PelletOptions.SIMPLIFY_QUERY ) {
        	if( log.isLoggable( Level.FINE ) )
                log.fine( "Simplifying:\n" + query );
            
            simplify( query );
        }

        if( log.isLoggable( Level.FINE ) )
            log.fine( "Split:\n" + query );
        
        List queries = split( query );

        if( queries.isEmpty() ) {
    	    throw new InternalReasonerException( "Splitting query returned no results!" );
        }
        else if( queries.size() == 1 ) {
            return execSingleQuery( (Query) queries.get( 0 ) );
        }
        else {    
            QueryResults[] results = new QueryResults[ queries.size() ];
            for( int i = 0; i < queries.size(); i++ ) {
                Query qry = (Query) queries.get( i );
                results[i] = execSingleQuery( qry );
            }
            
            return new MultiQueryResults( query, results );
        }
	}
    
    private static QueryResults execSingleQuery( Query query ) {
        query.prepare();
        
        boolean hasUndefinedPredicate = false;
        KnowledgeBase kb = query.getKB();
        List patterns = query.getQueryPatterns();
        for(Iterator i = patterns.iterator(); i.hasNext(); ) {
        	QueryPattern pattern = (QueryPattern) i.next();
        	ATermAppl s = pattern.getSubject();
        	ATermAppl o = pattern.getObject();
        	
        	if( !ATermUtils.isVar( s ) && !kb.isIndividual( s ) ) {                
        		hasUndefinedPredicate = true;
                log.warning( "Query refers to an undefined individual: " + s ); 
                break;
        	}
        	
        	if( pattern.isTypePattern() ) {
        		if( !kb.isClass( o ) ) {                
            		hasUndefinedPredicate = true;
                    log.warning( "Query refers to an undefined class: " + o ); 
                    break;
            	}
        	}
        	else {
        		if( !ATermUtils.isVar( o ) && !ATermUtils.isLiteral( o ) && !kb.isIndividual( o ) ) {                
            		hasUndefinedPredicate = true;
                    log.warning( "Query refers to an undefined individual: " + o ); 
                    break;
            	}
        		
        		ATermAppl p = pattern.getPredicate();
        		if( !kb.isProperty( p ) ) {                
            		hasUndefinedPredicate = true;
                    log.warning( "Query refers to an undefined property: " + p ); 
                    break;
            	}
        	}
        }
        
        if( hasUndefinedPredicate )
            return new QueryResultsImpl( query ); 
        
        if( query.isGround() ) {
            return noVars.exec( query );
        }
        
        if( PelletOptions.SAMPLING_RATIO > 0 ) {
        	if( log.isLoggable( Level.FINE ) )
                log.fine( "Reorder\n" + query );
            
            query = reorder( query );
        }
        
        if( log.isLoggable( Level.FINE ) )
            log.fine( "Execute\n" + query );
        
        for( int i = 0; i < queryExecs.length; i++ ) {
            if( queryExecs[ i ].supports( query ) ) {
                Timer timer = query.getKB().timers.startTimer( "Query" );
                QueryExec queryExec = queryExecs[ i ];
                QueryResults results = queryExec.exec( query );
                timer.stop();
                
                return results;
            }
        }
        
        // this should never happen
	    throw new InternalReasonerException( "Cannot determine which query engine to use" );
    }
    
    public static Query reorder( Query query ) {        
        double minCost = Double.POSITIVE_INFINITY;
        
        KnowledgeBase kb = query.getKB();
        List patterns = query.getQueryPatterns();
        
        if( kb.getIndividuals().size() <= 100 || patterns.size() > 7 )
            return query;
        
        computeSizeEstimates( query );
        
        QueryCost queryCost = new QueryCost( query.getKB() );
        
        List bestOrder = null;
        int n = patterns.size();
        
        PermutationGenerator gen = new PermutationGenerator( n );
        for(int i = 0; gen.hasMore(); i++) {
            int[] perm = gen.getNext();
            List newPatterns = new ArrayList();
            for(int j = 0; j < n; j++) 
                newPatterns.add( patterns.get( perm[j] ) );

            double cost = queryCost.estimateCost( newPatterns );
            if( cost < minCost ) {
                minCost = cost;
                bestOrder = newPatterns;
            }
        }
        
        if( bestOrder == patterns )
            return query;
        
        Query newQuery = new QueryImpl( kb, query.isDistinct() );
        for(int j = 0; j < n; j++) {
            newQuery.addPattern( (QueryPattern) bestOrder.get( j ) );
        }
        for( Iterator j = query.getResultVars().iterator(); j.hasNext(); ) {
            ATermAppl var = (ATermAppl) j.next();
            newQuery.addResultVar( var );
        }
        for( Iterator j = query.getDistVars().iterator(); j.hasNext(); ) {
            ATermAppl var = (ATermAppl) j.next();
            newQuery.addDistVar( var );
        }        
        return newQuery;
    }
    
    /**
     * If a query has disconnected components such as C(x), D(y) then it should be
     * answered as two separate queries. The answers to each query should be 
     * combined at the end by taking Cartesian product.(we combine results on a tuple 
     * basis as results are iterated. This way we avoid generating the full Cartesian 
     * product. Splitting the query ensures the correctness of the answer, e.g. 
     * rolling-up technique becomes applicable.
     * 
     * @param query Query to be split
     * @return List of queries (contains the initial query if the initial query is connected)
     */
    public static List split( Query query ) {        
        try {
            Set distVars = query.getDistVars();
            Set resultVars = new HashSet( query.getResultVars() );
            
            DisjointSet disjointSet = new DisjointSet();
            
            List patterns = query.getQueryPatterns();
            for( Iterator i = patterns.iterator(); i.hasNext(); ) {
                QueryPattern pattern = (QueryPattern) i.next();
                
                ATermAppl subj = pattern.getSubject();
                ATermAppl obj = pattern.getObject();
                
                if( pattern.isTypePattern() )
                    disjointSet.add( subj );
                else {
                    disjointSet.add( subj );
                    disjointSet.add( obj );
                    
                    disjointSet.union( subj, obj );
                }
            }
            
            Collection equivalenceSets = disjointSet.getEquivalanceSets();
            if( equivalenceSets.size() == 1 )
                return  Collections.singletonList( query );
            
            Map queries = new HashMap();
            for( Iterator i = patterns.iterator(); i.hasNext(); ) {
                QueryPattern pattern = (QueryPattern) i.next();
                
                ATermAppl subj = pattern.getSubject();
                ATermAppl pred = pattern.getPredicate();
                ATermAppl obj = pattern.getObject();
                
                Object representative = disjointSet.find( subj );
                Query newQuery = (Query) queries.get( representative );
                if( newQuery == null ) {
                    newQuery = new QueryImpl( query.getKB(), query.isDistinct() );
                    queries.put( representative, newQuery );
                }
                    
                if( resultVars.contains( subj ) )
                    newQuery.addResultVar( subj );
                else if( distVars.contains( subj ) )
                    newQuery.addDistVar( subj );
                
                if( pattern.isTypePattern() )
                    newQuery.addTypePattern( subj, obj );
                else {
                    newQuery.addEdgePattern( subj, pred, obj );
                    
                    if( resultVars.contains( obj ) )
                        newQuery.addResultVar( obj );
                    else if( distVars.contains( obj ) )
                        newQuery.addDistVar( obj );
                }
            }             

            return new ArrayList( queries.values() );
        } catch(RuntimeException e) {
            log.log( Level.WARNING,  "Query split failed, continuing with query execution.");
            e.printStackTrace();
            return Collections.singletonList( query );
        }
    }
    
    public static void simplify( Query query ) {
        Map allInferredTypes = new HashMap();
        
        KnowledgeBase kb = query.getKB();
        Set vars = query.getObjVars();
        
        for( Iterator i = vars.iterator(); i.hasNext(); ) {
            ATermAppl var = (ATermAppl) i.next();

            Set inferredTypes = new HashSet();            

            List outList = query.findPatterns( var, null, null );
            for( Iterator j = outList.iterator(); j.hasNext(); ) {
                QueryPattern pattern = (QueryPattern) j.next();
                ATermAppl pred = pattern.getPredicate();

                inferredTypes.addAll( kb.getDomains( pred ) );
            }

            List inList = query.findPatterns( null, null, var );
            for( Iterator j = inList.iterator(); j.hasNext(); ) {
                QueryPattern pattern = (QueryPattern) j.next();
                ATermAppl pred = pattern.getPredicate();

                inferredTypes.addAll( kb.getRanges( pred ) );
            }
            
            if( !inferredTypes.isEmpty() )
                allInferredTypes.put( var, inferredTypes );
        }

        List patterns = new ArrayList( query.getQueryPatterns() );
        for( Iterator i = patterns.iterator(); i.hasNext(); ) {
            QueryPattern pattern = (QueryPattern) i.next();
            
            if( pattern.isEdgePattern() ) continue;
            
            ATermAppl var = pattern.getSubject();
            Set inferredTypes = (Set) allInferredTypes.get( var );
            
            if( inferredTypes == null ) continue;
            
            ATermAppl c = pattern.getObject();
            
            if( inferredTypes.contains( c ) )
                query.removePattern( pattern );
            else if( kb.isClassified() ) {
                Set<ATermAppl> subs = kb.getTaxonomy().getFlattenedSubs( c, false );
				Set<ATermAppl> eqs = kb.getAllEquivalentClasses( c );
                if( SetUtils.intersects( inferredTypes, subs )
                    || SetUtils.intersects( inferredTypes, eqs ) )
                    query.removePattern( pattern );
            }
        }      
    }
    
    /**
     * @deprecated Renamed to {@link #computeSizeEstimates(KnowledgeBase)}
     */
    public static void prepare( KnowledgeBase kb ) {
    	computeSizeEstimates( kb );
    }
    
    public static void computeSizeEstimates( KnowledgeBase kb ) {
        kb.getSizeEstimate().computeAll();
    }
    
    /**
     * @deprecated Renamed to {@link #computeSizeEstimates(Query...)}
     */
    public static void prepare( Query... queries ) {
    	computeSizeEstimates( queries );
    }
    
    public static void computeSizeEstimates( Query... queries ) {
    	if( queries == null || queries.length == 0 )
    		throw new IllegalArgumentException( "No query specified!" );
    	
        SizeEstimate sizeEstimate = queries[0].getKB().getSizeEstimate();
        
        Set<ATermAppl> concepts = new HashSet<ATermAppl>();
        Set<ATermAppl> properties = new HashSet<ATermAppl>();        
        for(int j = 0; j < queries.length; j++) {
            Query query = queries[j];
            List patterns = query.getQueryPatterns();
            for( int i = 0; i < patterns.size(); i++ ) {
                QueryPattern pattern = (QueryPattern) patterns.get( i );

                if( pattern.isTypePattern() ) {
					if( !sizeEstimate.isComputed( pattern.getObject() ) )
						concepts.add( pattern.getObject() );
				}
				else {
					if( !sizeEstimate.isComputed( pattern.getPredicate() ) )
						properties.add( pattern.getPredicate() );
				}
            }
        }
        
        sizeEstimate.compute( concepts, properties );
    }
    
    public static boolean execBoolean( Query query ) {
	    return noVars.execBoolean( query );
	}

    public static boolean isEquivalent( Query q1, Query q2 ) {
        return isSubsumedBy( q1, q2 ) && isSubsumedBy( q2, q1 );
    }
    
    /**
     * @deprecated Use {@link #isSubsumed(Query, Query)}
     */
    public static boolean isSubsumed( Query sub, Query sup ) {
    	return isSubsumedBy( sub, sup );
    }
    
    public static boolean isSubsumedBy( Query sub, Query sup ) {
        return !getSubsumptionMappings( sub, sup ).isEmpty();
    }

    /**
     * @deprecated Use {@link #isSubsumed(Query, Query, KnowledgeBase)}
     */
    public static boolean isSubsumed( Query sub, Query sup, KnowledgeBase backgroundKB ) {
        return isSubsumedBy( sub, sup, backgroundKB );
    }

    public static boolean isSubsumedBy( Query sub, Query sup, KnowledgeBase backgroundKB ) {
        return !getSubsumptionMappings( sub, sup, backgroundKB ).isEmpty();
    }

    public static QueryResults getSubsumptionMappings( Query sub, Query sup ) {
        return getSubsumptionMappings( sub, sup, sub.getKB() );
    }
    
    public static QueryResults getSubsumptionMappings( Query sub, Query sup, KnowledgeBase backgroundKB ) {
        KnowledgeBase kb = backgroundKB.copy( true );
        
        List patterns = sub.getQueryPatterns();
        for( Iterator i = patterns.iterator(); i.hasNext(); ) {
            QueryPattern pattern = (QueryPattern) i.next();
            
            ATermAppl subj = pattern.getSubject();
            ATermAppl pred = pattern.getPredicate();
            ATermAppl obj = pattern.getObject();
            
            subj = (ATermAppl) (ATermUtils.isVar(subj) ? subj.getArgument(0) : subj);
            obj = (ATermAppl) (ATermUtils.isVar(obj) ? obj.getArgument(0) : obj);
            
            kb.addIndividual(subj);
            
            if( pattern.isTypePattern() )
                kb.addType( subj, obj );
            else {
                kb.addIndividual(obj);
                kb.addPropertyValue( pred, subj, obj );
            }
        }
        
        kb.isConsistent();
        
        sup.setKB( kb );
        QueryResults results = QueryEngine.exec( sup );
        sup.setKB( backgroundKB );
        
        return results;
    }

}
