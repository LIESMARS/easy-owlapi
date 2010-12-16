// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.query.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.mindswap.pellet.output.ATermManchesterSyntaxRenderer;
import org.mindswap.pellet.output.ATermRenderer;
import org.mindswap.pellet.output.TableData;
import org.mindswap.pellet.query.Query;
import org.mindswap.pellet.query.QueryResultBinding;
import org.mindswap.pellet.query.QueryResults;
import org.mindswap.pellet.query.QueryUtils;

import com.clarkparsia.pellet.sparqldl.model.QueryParameters;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

/**
 * @author Evren Sirin
 */
public class QueryResultsImpl implements QueryResults {
	private Query			query;
	private QueryParameters	parameters;
	private List			results;

	public QueryResultsImpl(Query query) {
		this.query = query;
		this.parameters = query.getQueryParameters();
		results = new ArrayList();
	}

	public void add(QueryResultBinding binding) {
		results.add( process(binding) );
	}

	public boolean contains(QueryResultBinding binding) {
		return results.contains( process(binding) );
	}

	public QueryResultBinding get(int index) {
		return (QueryResultBinding) results.get( index );
	}

	public int size() {
		return results.size();
	}

	public boolean isEmpty() {
		return results.isEmpty();
	}

	public Query getQuery() {
		return query;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mindswap.pellet.newquery.QueryResults#getResultVars()
	 */
	public List getResultVars() {
		return query.getResultVars();
	}

	public TableData toTable() {
		return toTable( false );
	}

	public TableData toTable(boolean formatHTML) {
		List resultVars = query.getResultVars();
		List colNames = new ArrayList( resultVars.size() );
		for( int i = 0; i < resultVars.size(); i++ ) {
			ATermAppl var = (ATermAppl) resultVars.get( i );

			colNames.add( QueryUtils.getVarName( var ) );
		}

		StringWriter sw = new StringWriter();
		PrintWriter formatter = new PrintWriter( sw );

		ATermRenderer renderer = new ATermManchesterSyntaxRenderer();
		renderer.setWriter( formatter );

		TableData table = new TableData( colNames );
		for( int i = 0; i < size(); i++ ) {
			QueryResultBinding binding = get( i );

			List list = new ArrayList();
			for( int j = 0; j < resultVars.size(); j++ ) {
				sw.getBuffer().setLength( 0 );
				ATermAppl var = (ATermAppl) resultVars.get( j );
				ATermAppl val = binding.getValue( var );

				if( val != null ) {
					renderer.visit( val );
					list.add( sw.toString() );
				}
				else
					list.add( "<<unbound>>" );
			}

			table.add( list );
		}

		return table;
	}

	public String toString() {
		return toTable().toString();
	}
	
	private QueryResultBinding process(QueryResultBinding binding) {
		if( parameters == null )
			return binding;
		
		int numOfVars = query.getResultVars().size();
		
		// Add the query parameters to the binding if the variable is in the
		// query projection
		for( Iterator<Entry<ATermAppl, ATermAppl>> iter = parameters.entrySet().iterator(); iter.hasNext(); ) {
			Entry<ATermAppl, ATermAppl> entry = iter.next();
			ATermAppl var = entry.getKey();
			ATermAppl value = entry.getValue();
			
			if( numOfVars == 0 || query.getResultVars().contains( var ) )
				binding.setValue( var, value );
		}

		return binding;
	}
}
