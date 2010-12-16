// Portions Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// Clark & Parsia, LLC parts of this source code are available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package org.mindswap.pellet.jena;

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
import org.mindswap.pellet.exceptions.UnsupportedQueryException;
import org.mindswap.pellet.query.QueryEngine;
import org.mindswap.pellet.query.impl.ARQParser;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryExecException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.core.DatasetImpl;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingMap;
import com.hp.hpl.jena.sparql.syntax.Template;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.sparql.util.ModelUtils;
import com.hp.hpl.jena.util.FileManager;

/**
 * @author Evren Sirin
 * @deprecated Use the functions from {@link SparqlDLExecutionFactory} instead.
 */
public class PelletQueryExecution implements QueryExecution {
	public static Logger	log					= Logger.getLogger( PelletQueryExecution.class.getName() );

	private Query		query;

	private Dataset		source;

	private boolean		purePelletQueryExec	= false;

	public PelletQueryExecution(String query, Model source) {
		this( QueryFactory.create( query ), source );
	}

	public PelletQueryExecution(Query query, Model source) {
		this( query, new DatasetImpl( source ) );
	}

	public PelletQueryExecution(Query query, Dataset source) {
		this.query = query;
		this.source = source;

		Graph graph = source.getDefaultModel().getGraph();
		if( !(graph instanceof PelletInfGraph) )
			throw new QueryException(
					"PelletQueryExecution can only be used with Pellet-backed models" );

		if( PelletOptions.FULL_SIZE_ESTIMATE )
			((PelletInfGraph) graph).getKB().getSizeEstimate().computeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	public Model execDescribe() {
		throw new UnsupportedOperationException( "Not supported yet!" );
	}

	/**
	 * {@inheritDoc}
	 */
	public Model execDescribe(Model model) {
		throw new UnsupportedOperationException( "Not supported yet!" );
	}

	/**
	 * {@inheritDoc}
	 */
	public Model execConstruct() {
		Model model = ModelFactory.createDefaultModel();

		execConstruct( model );

		return model;
	}

	/**
	 * {@inheritDoc}
	 */
	public Model execConstruct(Model model) {
		if( !query.isConstructType() )
			throw new QueryExecException( "Attempt to get a CONSTRUCT model from a "
					+ labelForQuery( query ) + " query" );

		try {
			// first try Pellet engine for ABox queries
			ResultSet results = exec();

			model.setNsPrefixes( source.getDefaultModel() );
			model.setNsPrefixes( query.getPrefixMapping() );

			Set set = new HashSet();

			Template template = query.getConstructTemplate();

			while( results.hasNext() ) {
				Map bNodeMap = new HashMap();
				QuerySolution qs = results.nextSolution();
				Binding binding = toBinding( qs );
				template.subst( set, bNodeMap, binding );
			}

			for( Iterator iter = set.iterator(); iter.hasNext(); ) {
				Triple t = (Triple) iter.next();
				Statement stmt = ModelUtils.tripleToStatement( model, t );
				if( stmt != null )
					model.add( stmt );
			}

			close();

			return model;
		} catch( UnsupportedQueryException e ) {
			log.log( purePelletQueryExec
				? Level.INFO
				: Level.FINE, "This is not an ABox query: " + e.getMessage() );

			if( purePelletQueryExec ) {
				throw e;
			}
			else {
				// fall back to Jena engine for mixed queries
				log.fine( "Using Jena to answer the query" );
				return QueryExecutionFactory.create( query, source ).execConstruct();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean execAsk() {
		if( !query.isAskType() )
			throw new QueryExecException( "Attempt to have boolean from a " + labelForQuery( query )
					+ " query" );

		try {
			// first try Pellet engine for ABox queries
			ResultSet results = exec();

			return results.hasNext();
		} catch( UnsupportedQueryException e ) {
			log.log( purePelletQueryExec
				? Level.INFO
				: Level.FINE, "This is not an ABox query: " + e.getMessage() );

			if( purePelletQueryExec ) {
				throw e;
			}
			else {
				// fall back to Jena engine for mixed queries
				log.fine( "Using Jena to answer the query" );
				return QueryExecutionFactory.create( query, source ).execAsk();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ResultSet execSelect() {
		if( !query.isSelectType() )
			throw new QueryExecException( "Attempt to have ResultSet from a "
					+ labelForQuery( query ) + " query" );

		try {
			// first try Pellet engine for ABox queries
			return exec();
		} catch( UnsupportedQueryException e ) {
			log.log( purePelletQueryExec
				? Level.INFO
				: Level.FINE, "This is not an ABox query: " + e.getMessage() );

			if( purePelletQueryExec ) {
				throw e;
			}
			else {
				// fall back to Jena engine for mixed queries
				log.fine( "Using Jena to answer the query" );
				return QueryExecutionFactory.create( query, source ).execSelect();
			}
		}
	}

	private ResultSet exec() {
		PelletInfGraph pelletInfGraph = (PelletInfGraph) source.getDefaultModel().getGraph();
		
		pelletInfGraph.prepare();

		KnowledgeBase kb = pelletInfGraph.getKB();
		Model model = source.getDefaultModel();

		ARQParser parser = (ARQParser) QueryEngine.createParser();

		org.mindswap.pellet.query.Query q = parser.parse( query, kb );
	
		ResultSet results = new PelletResultSet( QueryEngine.exec( q ), model );

		List sortConditions = query.getOrderBy();
		
		if( sortConditions != null && !sortConditions.isEmpty() )
			results = ResultSetFactory.makeSorted( results, sortConditions );
		
		return results;
	}

	public void abort() {
		throw new UnsupportedOperationException( "Not supported yet!" );
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		log.fine( "Closing PelletQueryExecution '" + hashCode() + "'." );
	}

	public void setFileManager(FileManager manager) {
		throw new UnsupportedOperationException( "Not supported yet!" );
	}

	public void setInitialBinding(QuerySolution arg0) {
		throw new UnsupportedOperationException( "Not supported yet!" );
	}

	public Context getContext() {
		throw new UnsupportedOperationException( "Not supported yet!" );
	}

	/**
	 * {@inheritDoc}
	 */
	public Dataset getDataset() {
		throw new UnsupportedOperationException( "Not supported yet!" );
		// return source;
	}

	static private String labelForQuery(Query q) {
		if( q.isSelectType() )
			return "SELECT";
		if( q.isConstructType() )
			return "CONSTRUCT";
		if( q.isDescribeType() )
			return "DESCRIBE";
		if( q.isAskType() )
			return "ASK";
		return "<<unknown>>";
	}

	private Binding toBinding(QuerySolution solution) {
		BindingMap result = new BindingMap();

		for( Iterator i = solution.varNames(); i.hasNext(); ) {
			String varName = (String) i.next();

			result.add( Var.alloc( varName ), solution.get( varName ).asNode() );
		}

		return result;
	}

	public boolean isPurePelletQueryExec() {
		return purePelletQueryExec;
	}

	public void setPurePelletQueryExec(boolean purePelletQueryExec) {
		this.purePelletQueryExec = purePelletQueryExec;
	}
}
