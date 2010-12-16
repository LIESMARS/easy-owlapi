// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.datatypes.Datatype;
import org.mindswap.pellet.query.impl.QueryImpl;
import org.mindswap.pellet.taxonomy.Taxonomy;
import org.mindswap.pellet.utils.ATermUtils;
import org.mindswap.pellet.utils.SetUtils;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;
import aterm.ATermList;

/**
 * Reorganizes the order of variables to improve the performance of the query answering.
 * Designed for queries with one connected component and no undistinguished variables.
 * 
 * @author Evren Sirin
 * @deprecated Use {@link QueryEngine#split(Query)} function for this functionality
 */
public class QuerySplitter {
    public static Logger log = Logger.getLogger( QueryEngine.class.getName() );
    
    private KnowledgeBase kb;
    private Query oldQuery;
    private Query newQuery;
    private Set addedVars; 
    private Set addedPatterns; 
    
    public List split( Query query ) {
        try {
            List queries = new ArrayList();
            
            this.kb = query.getKB();
            this.oldQuery = query;
            this.addedVars = new HashSet();
            this.addedPatterns = new HashSet();
            
            Set constants = oldQuery.getConstants();
            Set vars = oldQuery.getObjVars();
            Set distVars = oldQuery.getDistVars();
            Set resultVars = new HashSet( oldQuery.getResultVars() );
            
            if( log.isLoggable( Level.FINE ) )
                log.fine( oldQuery.toString() );

            
            for(Iterator i = constants.iterator(); i.hasNext();) {
                ATermAppl val = (ATermAppl) i.next();
                
                if( !addedVars.contains( val ) ) {
                    newQuery = new QueryImpl( kb );
                    addVar( val );
                    
                    queries.add( newQuery );
                    
                    for( Iterator j = newQuery.getVars().iterator(); j.hasNext(); ) {
                        ATermAppl var = (ATermAppl) j.next();
                        if( distVars.contains( var ) )
                            newQuery.addDistVar( var );
                        if( resultVars.contains( var ) )
                            newQuery.addResultVar( var );
                    }
                    
                    if( log.isLoggable( Level.FINE ) )
                        log.fine( newQuery.toString() );
                }                
            }
            
            for(Iterator i = vars.iterator(); i.hasNext();) {
                ATermAppl var = (ATermAppl) i.next();
                
                if( !addedVars.contains( var ) ) {
                    newQuery = new QueryImpl( kb );
                    addVar( var );
                    
                    queries.add( newQuery );                    
                    
                    for( Iterator j = newQuery.getVars().iterator(); j.hasNext(); ) {
                        var = (ATermAppl) j.next();
                        if( distVars.contains( var ) )
                            newQuery.addDistVar( var );
                        if( resultVars.contains( var ) )
                            newQuery.addResultVar( var );
                    }
                    
                    if( log.isLoggable( Level.FINE ) )
                        log.fine( newQuery.toString() );
                }                
            }
            
//            ATermUtils.assertTrue( 
//                oldQuery.getVars().containsAll(newQuery.getVars()) &&
//                newQuery.getVars().containsAll(oldQuery.getVars()) &&
//                oldQuery.getQueryPatterns().containsAll(newQuery.getQueryPatterns()) &&
//                newQuery.getQueryPatterns().containsAll(oldQuery.getQueryPatterns())); 

            return queries;
        } catch(RuntimeException e) {
            log.log( Level.WARNING,  "Query split failed, continuing with query execution.");
            if( log.isLoggable( Level.FINE ) ) {
                log.fine( oldQuery.toString() );
                log.fine( newQuery.toString() );
            }
            return Collections.singletonList( query );
        }
    }
    
    private void addVar( ATermAppl var ) {
//        if( !ATermUtils.isVar( var ) )
//            return;
        if( addedVars.contains( var ) )
            return;
        else
            addedVars.add( var );
        
//        if( oldQuery.getResultVars().contains( var ) )
//            newQuery.addResultVar( var );
        
        List inferredTypes = new ArrayList();
        List patterns = new ArrayList();
        Set neighbors = new LinkedHashSet();
        List outList = oldQuery.findPatterns( var, null, null );
        for(Iterator i = outList.iterator(); i.hasNext(); ) {
            QueryPattern pattern = (QueryPattern) i.next();
            ATermAppl pred = pattern.getPredicate();
            ATermAppl obj = pattern.getObject();
            
            inferredTypes.addAll( kb.getDomains( pred ) );

            if( addedPatterns.contains( pattern ) )
                continue;
            else
                addedPatterns.add( pattern );
                        
            patterns.add( new ATermAppl[] { var, pred, obj } );
            neighbors.add( obj );
        }
        
        List inList = oldQuery.findPatterns( null, null, var );
        for(Iterator i = inList.iterator(); i.hasNext(); ) {
            QueryPattern pattern = (QueryPattern) i.next();
            ATermAppl subj = pattern.getSubject();
            ATermAppl pred = pattern.getPredicate();

            inferredTypes.addAll( kb.getRanges( pred ) );
            
            if( addedPatterns.contains( pattern ) )
                continue;
            else
                addedPatterns.add( pattern );
            
            patterns.add( new ATermAppl[] { subj, pred, var } );
            neighbors.add( subj );
        }                
        
        boolean isConstant = !ATermUtils.isVar( var );
        boolean isLiteral = oldQuery.getLitVars().contains( var ) || ATermUtils.isLiteral( var );
        
        if( isLiteral ) {            
            Datatype datatype = oldQuery.getDatatype( var );
            newQuery.addConstraint( var, datatype );    
        }
        else {
            ATermAppl nominal = isConstant ? ATermUtils.makeValue( var ) : null;
            ATermList classes = oldQuery.getClasses( var );
            for( ; !classes.isEmpty(); classes = classes.getNext() ) {
                ATermAppl c = (ATermAppl) classes.getFirst();
                if( c == nominal )
                    continue;
                
                if( PelletOptions.SIMPLIFY_QUERY ) {
                    // TODO even if kb is not classified we should use told subsumers
                    if( kb.isClassified() ) {
                        Set<ATermAppl> subs = kb.getTaxonomy().getFlattenedSubs( c, false );
						Set<ATermAppl> eqs = kb.getAllEquivalentClasses( c );
                        if( SetUtils.intersects( inferredTypes, subs ) || 
                            SetUtils.intersects( inferredTypes, eqs ) )
                            continue;
                    }
                    else {
                        if( inferredTypes.contains( c ) )
                            continue;
                    }
                }
                    
                newQuery.addTypePattern( var, c );
            }
        }
        
        for(Iterator i = patterns.iterator(); i.hasNext();) {
            ATermAppl[] pattern = (ATermAppl[]) i.next();
            newQuery.addEdgePattern( pattern[0], pattern[1], pattern[2] );
        }                    
        
        for(Iterator i = neighbors.iterator(); i.hasNext();) {
            ATermAppl neighbor = (ATermAppl) i.next();
            addVar( neighbor );
        }
    }

}
