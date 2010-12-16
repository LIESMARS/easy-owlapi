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

package org.mindswap.pellet.jena;

import java.util.ArrayList;
import java.util.List;

import org.mindswap.pellet.query.QueryResultBinding;
import org.mindswap.pellet.query.QueryResults;
import org.mindswap.pellet.query.QueryUtils;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.core.ResultBinding;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingMap;

/**
 * 
 * @author Evren Sirin
 */
public class PelletResultSet implements ResultSet {
    private Model model;
    private List<String> resultVars;
    private List<ATermAppl> varTerms;
    
    private QueryResults answers;
    private int index;
    private int size;

    private Binding parent;
    
    public PelletResultSet( QueryResults answers, Model model ) {
    	this( answers, model, null );
    }
    
    public PelletResultSet( QueryResults answers, Model model, Binding parent ) {
        this.answers = answers;
        this.model = model;
        this.parent=  parent;
        this.index = 0;
        this.size = answers.size();
        
        varTerms = answers.getResultVars();
    }
    
    /**
     * Return the underlying QueryResults object
     * @return
     */
    public QueryResults getAnswers() {
        return answers;
    }

    public boolean hasNext() {
        return index < size;
    }

	public Binding nextBinding() {
        QueryResultBinding binding = answers.get(index++);
        BindingMap result = parent == null
         	? new BindingMap()
        	: new BindingMap( parent );
		
        for( ATermAppl var : varTerms ) {
			String varName = QueryUtils.getVarName( var );
			
			ATermAppl value = binding.getValue( var );
			
			Node node = JenaUtils.makeGraphNode( value );
			result.add( Var.alloc( varName ), node );
		}
		
        return result; 
	}
    
    public QuerySolution nextSolution() {        
        return new ResultBinding(model, nextBinding() );
    }

    public Object next() {
        return nextSolution();
    }

    public boolean isDistinct() {
        return false;
    }

    public boolean isOrdered() {
        return false;
    }

    public int getRowNumber() {
        return index;
    }

    public List getResultVars() {
        if( resultVars == null ) {
            resultVars = new ArrayList<String>( varTerms.size() );
            for( ATermAppl var : varTerms ) {
				String varName = QueryUtils.getVarName( var );
				resultVars.add(varName);
	        }
	    }
         
        return resultVars;
    }


    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Cannot remove from QueryResults");
    }
    
    public String toString() {
        return answers.toString();
    }
    
    public Model getResourceModel() {
    	return model;
    }
    
}