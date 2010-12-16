// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.owlapi.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




import cz.cvut.kbss.owl2query.owlapi.exceptions.UnsupportedQueryException;
import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;
import cz.cvut.kbss.owl2query.owlapi.model.Query;
import cz.cvut.kbss.owl2query.owlapi.model.QueryAtom;
import cz.cvut.kbss.owl2query.owlapi.model.QueryPredicate;
import cz.cvut.kbss.owl2query.owlapi.model.ResultBinding;
import cz.cvut.kbss.owl2query.owlapi.model.ResultBindingImpl;
import cz.cvut.kbss.owl2query.owlapi.util.ATermUtils;

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

	private static final Map<ATermAppl, Query>	registry	= new HashMap<ATermAppl, Query>();

	public static boolean containsQuery(ATermAppl name) {
		return registry.containsKey( name );
	}

	public static int getArity(ATermAppl name) {
		Query query = getQuery( name );

		return query == null
			? -1
			: query.getResultVars().size();
	}

	public static Query getQuery(ATermAppl name) {
		return registry.get( name );
	}

	public static Query getQuery(QueryAtom executeAtom) {
		if( executeAtom.getPredicate() != QueryPredicate.Execute )
			throw new IllegalArgumentException( "Not an Execute atom: " + executeAtom );

		List<ATermAppl> args = executeAtom.getArguments();
		ATermAppl queryName = args.get( 0 );
		Query query = getQuery( queryName );

		if( query == null ) {
			throw new UnsupportedQueryException( "Query not found in the registry: " + queryName );
		}

		List<ATermAppl> resultVars = query.getResultVars();

		if( resultVars.size() != args.size() - 1 ) {
			throw new UnsupportedQueryException(
					"Mismatch between the query aotm and registered query: " + executeAtom + " "
							+ query );
		}

		int generatedVarCount = 0;
		ResultBinding inputBinding = new ResultBindingImpl();
		for( int i = 0; i < resultVars.size(); i++ ) {
			ATermAppl var = resultVars.get( i );
			ATermAppl newValue = args.get( i + 1 );
			if( ATermUtils.isVar( newValue ) )
				inputBinding.setValue( newValue, ATermUtils.makeVar( "_v" + generatedVarCount++ ) );
			inputBinding.setValue( var, newValue );
		}

		return query.apply( inputBinding );
	}

	public static Query registerQuery(ATermAppl name, Query query) {
		if( ATermUtils.isVar( name ) )
			throw new IllegalArgumentException( "Query name cannot be a variable: " + name );

		query.setName( name );
		
		return registry.put( name, query );
	}

	public static Collection<Query> getQueries() {
		return Collections.unmodifiableCollection( registry.values() );
	}
}
