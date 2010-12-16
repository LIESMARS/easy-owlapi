
package org.mindswap.pellet.query;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

/**
 * @author Evren Sirin
 *
 */
public interface QueryPattern {
    public boolean isTypePattern();
    public boolean isEdgePattern();
    public boolean isGround();
    
    public ATermAppl getSubject();
    public ATermAppl getPredicate();
    public ATermAppl getObject();    
    
    public QueryPattern apply( QueryResultBinding binding );
}
