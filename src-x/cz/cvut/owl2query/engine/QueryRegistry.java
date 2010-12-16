// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.owl2query.engine;

 import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.cvut.owl2query.model.Query;
import cz.cvut.owl2query.model.QueryAtom;
import cz.cvut.owl2query.model.QueryPredicate;
import cz.cvut.owl2query.model.ResultBinding;
import cz.cvut.owl2query.model.ResultBindingImpl;
import cz.cvut.owl2query.model.Term;
import cz.cvut.owl2query.model.Variable;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */
public class QueryRegistry {

	private static final Map<String, Query>	registry	= new HashMap<String, Query>();

	public static boolean containsQuery(Term name) {
		return registry.containsKey( name );
	}

	public static int getArity(Term name) {
		Query query = getQuery( name );

		return query == null
			? -1
			: query.getResultVars().size();
	}

	public static Query getQuery(Term name) {
		return registry.get( name );
	}

	public static Query getQuery(QueryAtom executeAtom) {
		if( executeAtom.getPredicate() != QueryPredicate.Execute )
			throw new IllegalArgumentException( "Not an Execute atom: " + executeAtom );

		List<Term> args = executeAtom.getArguments();
		Term queryName = args.get( 0 );
		Query query = getQuery( queryName );

		if( query == null ) {
			throw new UnsupportedQueryException( "Query not found in the registry: " + queryName );
		}

		List<Variable> resultVars = query.getResultVars();

		if( resultVars.size() != args.size() - 1 ) {
			throw new UnsupportedQueryException(
					"Mismatch between the query aotm and registered query: " + executeAtom + " "
							+ query );
		}

		int generatedVarCount = 0;
		ResultBinding inputBinding = new ResultBindingImpl();
		for( int i = 0; i < resultVars.size(); i++ ) {
			Term var = resultVars.get( i );
			Term newValue = args.get( i + 1 );
			if( newValue.isVariable() )
				inputBinding.setValue( newValue, ATermUtils.makeVar( "_v" + generatedVarCount++ ) );
			inputBinding.setValue( var, newValue );
		}

		return query.apply( inputBinding );
	}

	public static Query registerQuery(String name, Query query) {

		query.setName( name );
		
		return registry.put( name, query );
	}

	public static Collection<Query> getQueries() {
		return Collections.unmodifiableCollection( registry.values() );
	}
}
