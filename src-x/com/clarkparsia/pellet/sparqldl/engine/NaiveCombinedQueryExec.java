// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package com.clarkparsia.pellet.sparqldl.engine;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.query.QueryResultBinding;
import org.mindswap.pellet.query.QueryResults;
import org.mindswap.pellet.query.impl.QueryImpl;
import org.mindswap.pellet.utils.ATermUtils;

import com.clarkparsia.pellet.sparqldl.model.Query;
import com.clarkparsia.pellet.sparqldl.model.QueryAtom;
import com.clarkparsia.pellet.sparqldl.model.QueryAtomFactory;
import com.clarkparsia.pellet.sparqldl.model.QueryResult;
import com.clarkparsia.pellet.sparqldl.model.QueryResultImpl;
import com.clarkparsia.pellet.sparqldl.model.ResultBinding;
import com.clarkparsia.pellet.sparqldl.model.ResultBindingImpl;
import com.clarkparsia.pellet.sparqldl.model.Query.VarType;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

/**
 * <p>
 * Title: Query Engine that partitions the query, first evaluating the TBox part
 * then running old conjunctive abox query engine.
 * </p>
 * <p>
 * Description: All variable name spaces are disjoint.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Petr Kremen
 */
public class NaiveCombinedQueryExec implements QueryExec {
	public static Logger					log						= Logger
																		.getLogger( NaiveCombinedQueryExec.class.getName() );

	public static final QueryExec		distCombinedQueryExec	= new CombinedQueryEngine();

	public static final QueryOptimizer	optimizer				= new QueryOptimizer();

	protected Query						schemaQuery;

	protected Query						aboxQuery;

	/**
	 * {@inheritDoc}
	 */
	public boolean supports(Query q) {
		// final Set<ATermAppl> undistVars = new HashSet<ATermAppl>();
		//
		// undistVars.addAll(q.getDistVarsForType(VarType.CLASS));
		// undistVars.addAll(q.getDistVarsForType(VarType.PROPERTY));
		// undistVars.removeAll(q.getDistVars());
		//
		// return undistVars.isEmpty();
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public QueryResult exec(Query query) {
		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Executing query " + query.getAtoms() );
		}

		partitionQuery( query );

		final QueryResult newResult = new QueryResultImpl( query );

		boolean shouldHaveBinding;
		final QueryResult result;

		if( schemaQuery.getAtoms().isEmpty() ) {
			shouldHaveBinding = false;
			result = new QueryResultImpl( query );
			result.add( new ResultBindingImpl() );
		}
		else {

			result = distCombinedQueryExec.exec( schemaQuery );

			shouldHaveBinding = org.mindswap.pellet.utils.SetUtils.intersects( query
					.getDistVarsForType( VarType.CLASS ), query.getResultVars() )
					|| org.mindswap.pellet.utils.SetUtils.intersects( query
							.getDistVarsForType( VarType.PROPERTY ), query.getResultVars() );
		}

		if( shouldHaveBinding && result.isEmpty() ) {
			return result;
		}

		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Partial binding after schema query : " + result );
		}

		for( ResultBinding binding : result ) {
			final Query query2 = aboxQuery.apply( binding );

			if( log.isLoggable( Level.FINER ) ) {
				log.finer( "Query = " + query2.getAtoms() );
			}
			final org.mindswap.pellet.query.Query transformedQuery = transformQuery( query2 );

			long time = System.currentTimeMillis();
			final QueryResults results = org.mindswap.pellet.query.QueryEngine
					.exec( transformedQuery );
			if( log.isLoggable( Level.FINE ) ) {
				log.fine( "Time=" + (System.currentTimeMillis() - time) );
			}
			final QueryResult result2 = transformResult( results, query );
			if( log.isLoggable( Level.FINER ) ) {
				log.finer( "Result = " + result2 );
			}

			for( ResultBinding newBinding : result2 ) {
				for( final ATermAppl var : binding.getAllVariables() ) {
					newBinding.setValue( var, binding.getValue( var ) );
				}

				newResult.add( newBinding );
			}
		}

		return newResult;
	}

	private final void partitionQuery(final Query query) {

		schemaQuery = new com.clarkparsia.pellet.sparqldl.model.QueryImpl( query );
		aboxQuery = new com.clarkparsia.pellet.sparqldl.model.QueryImpl( query );

		for( final QueryAtom atom : query.getAtoms() ) {
			switch ( atom.getPredicate() ) {
			case Type:
			case PropertyValue:
				aboxQuery.add( atom );
				break;
			default:
				;
			}
		}

		final List<QueryAtom> atoms = new ArrayList<QueryAtom>( query.getAtoms() );
		atoms.removeAll( aboxQuery.getAtoms() );

		for( final QueryAtom atom : atoms ) {
			schemaQuery.add( atom );
		}

		for( final VarType t : VarType.values() ) {
			for( final ATermAppl a : query.getDistVarsForType( t ) ) {
				if( aboxQuery.getVars().contains( a ) ) {
					aboxQuery.addDistVar( a, t );
				}
				if( schemaQuery.getVars().contains( a ) ) {
					schemaQuery.addDistVar( a, t );
				}
			}
		}

		for( final ATermAppl a : query.getResultVars() ) {
			if( aboxQuery.getVars().contains( a ) ) {
				aboxQuery.addResultVar( a );
			}
			if( schemaQuery.getVars().contains( a ) ) {
				schemaQuery.addResultVar( a );
			}
		}

		for( final ATermAppl v : aboxQuery.getDistVarsForType( VarType.CLASS ) ) {
			if( !schemaQuery.getVars().contains( v ) ) {
				schemaQuery.add( QueryAtomFactory.SubClassOfAtom( v, ATermUtils.TOP ) );
			}
		}

		for( final ATermAppl v : aboxQuery.getDistVarsForType( VarType.PROPERTY ) ) {
			if( !schemaQuery.getVars().contains( v ) ) {
				schemaQuery.add( QueryAtomFactory.SubPropertyOfAtom( v, v ) );
			}
		}

	}

	private final org.mindswap.pellet.query.Query transformQuery(final Query query) {
		final org.mindswap.pellet.query.Query result = new QueryImpl( query.getKB(), query
				.isDistinct() );

		for( final QueryAtom atom : query.getAtoms() ) {
			switch ( atom.getPredicate() ) {
			case Type:
				result.addTypePattern( atom.getArguments().get( 0 ), atom.getArguments().get( 1 ) );
				break;
			case PropertyValue:
				result.addEdgePattern( atom.getArguments().get( 0 ), atom.getArguments().get( 1 ),
						atom.getArguments().get( 2 ) );
				break;
			default:
				;
			}
		}

		for( final ATermAppl a : query.getDistVarsForType( VarType.INDIVIDUAL ) ) {
			result.addDistVar( a );
		}

		for( final ATermAppl a : query.getResultVars() ) {
			result.addResultVar( a );
		}

		return result;
	}

	private final QueryResult transformResult(final QueryResults results, final Query query) {
		final QueryResult result = new QueryResultImpl( query );

		for( int i = 0; i < results.size(); i++ ) {
			final QueryResultBinding binding = results.get( i );

			final ResultBinding newBinding = new ResultBindingImpl();

			for( final Object varx : binding.getVars() ) {
				final ATermAppl var = (ATermAppl) varx;
				newBinding.setValue( var, binding.getValue( var ) );
			}

			result.add( newBinding );
		}
		return result;
	}
}