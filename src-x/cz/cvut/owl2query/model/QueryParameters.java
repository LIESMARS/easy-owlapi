// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.owl2query.model;

import cz.cvut.owl2query.model.Term;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Title: QueryParameter
 * </p>
 * <p>
 * Description: Class for query parameterization
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Markus Stocker
 */
public class QueryParameters {

	private Map<Term, Term> parameters ;
	
	public QueryParameters() {
		parameters = new HashMap<Term, Term>();
	}
	
	public QueryParameters(QuerySolution initialBinding) {
		this();
		
		if (initialBinding == null)
			initialBinding = new QuerySolutionImpl();
		
		for (final Term key : initialBinding.getVariables()) {
			parameters.put( key, initialBinding.getBinding( key ) );
		}
	}
	
	public void add(Term key, Term value) {
		parameters.put( key, value );
	}
	
	public Set<Map.Entry<Term, Term>> entrySet() {
		return parameters.entrySet();
	}
	
	public boolean cointains(Term key) {
		return parameters.containsKey( key );
	}
	
	public Term get(Term key) {
		return parameters.get( key );
	}
	
	public String toString() {
		return parameters.toString();
	}
}
