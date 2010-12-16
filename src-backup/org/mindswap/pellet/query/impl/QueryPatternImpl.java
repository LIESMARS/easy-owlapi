// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.query.impl;

import org.mindswap.pellet.exceptions.UnsupportedFeatureException;
import org.mindswap.pellet.query.QueryPattern;
import org.mindswap.pellet.query.QueryResultBinding;
import org.mindswap.pellet.utils.ATermUtils;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

/**
 * @author Evren Sirin
 *
 */
public class QueryPatternImpl implements QueryPattern {
    private ATermAppl s;
    private ATermAppl p;
    private ATermAppl o;
    
    public QueryPatternImpl(ATermAppl ind, ATermAppl c) {
        if(ATermUtils.isVar( c ))
            throw new UnsupportedFeatureException();

        this.s = ind;
        this.o = c;
    }

    public QueryPatternImpl(ATermAppl s, ATermAppl p, ATermAppl o) {
        if(ATermUtils.isVar( p ))
            throw new UnsupportedFeatureException();

        this.s = s;
        this.p = p;
        this.o = o;
    }

    public boolean isTypePattern() {
        return (p == null);
    }

    public boolean isEdgePattern() {
        return (p != null);
    }

    public boolean isGround() {
        return !ATermUtils.isVar( s ) && !ATermUtils.isVar( o ) ;
    }

    public ATermAppl getSubject() {
        return s;
    }

    public ATermAppl getPredicate() {
        return p;
    }
    
    public ATermAppl getObject() {
        return o;
    }
    
    public QueryPattern apply( QueryResultBinding binding ) {
	    ATermAppl subj = binding.hasValue( s ) ? binding.getValue( s ) : s;
	    ATermAppl obj = binding.hasValue( o ) ? binding.getValue( o ) : o;
	    
	    if( p == null )
	        return new QueryPatternImpl( subj, obj );
	    else
	        return new QueryPatternImpl( subj, p, obj );
    }
    
	
    public boolean equals(Object other) {
        if(this == other) return true;
        if(!(other instanceof QueryPatternImpl)) return false;
        QueryPatternImpl that = (QueryPatternImpl) other;
        return s.equals(that.s) && 
               o.equals(that.o) &&
               ((p == null && that.p == null) ||
                (p != null && that.p != null && p.equals(that.p)));
    }
    
    public int hashCode() {
        int hashCode = 11;
        
        hashCode = 37 * hashCode + s.hashCode();
        hashCode = 37 * hashCode + p.hashCode();
        hashCode = 37 * hashCode + o.hashCode();     
        
        return hashCode;
    }
    
    public String toString() {
        if(p== null)
            return "(" + s + " rdf:type " + o + ")"; 
        else
            return "(" + s + " " + p +  " " + o + ")";
    }
}
