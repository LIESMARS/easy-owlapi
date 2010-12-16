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

package org.mindswap.pellet.query.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.datatypes.Datatype;
import org.mindswap.pellet.query.Query;
import org.mindswap.pellet.query.QueryEngine;
import org.mindswap.pellet.query.QueryExec;
import org.mindswap.pellet.query.QueryPattern;
import org.mindswap.pellet.query.QueryResultBinding;
import org.mindswap.pellet.query.QueryResults;
import org.mindswap.pellet.utils.ATermUtils;

import com.clarkparsia.pellet.sparqldl.model.QueryParameters;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;
import aterm.ATermList;

/**
 * @author Evren Sirin
 */
public class DistVarsQueryExec implements QueryExec {
	public static Logger		log	= Logger.getLogger( DistVarsQueryExec.class.getName() );

	protected Query			query;
	protected KnowledgeBase	kb;

	protected List			patterns;
	protected Set			vars;

	// protected Map varMap;

	public DistVarsQueryExec() {
	}

	public void prepare() {
		kb = query.getKB();
		patterns = query.getQueryPatterns();
		vars = query.getObjVars();
		// varMap = new HashMap();

		if( kb == null )
			throw new RuntimeException( "No input data set is given for query!" );

		// warm up the reasoner by computing the satisfiability of classes
		// used in the query so that cached models can be used for instance
		// checking
		if( PelletOptions.USE_CACHING && !kb.isClassified() ) {
			for( Iterator i = vars.iterator(); i.hasNext(); ) {
				ATermAppl var = (ATermAppl) i.next();

				ATermList list = query.getClasses( var );
				for( ; !list.isEmpty(); list = list.getNext() ) {
					ATermAppl c = (ATermAppl) list.getFirst();
					ATermAppl notC = ATermUtils.makeNot( c );

					kb.isSatisfiable( c );
					kb.isSatisfiable( notC );
				}
			}
		}
	}

	public boolean supports(Query q) {
		return q.getDistVars().containsAll( q.getVars() );
	}

	public boolean execBoolean(Query query) {
		this.query = query;

		if( !query.getVars().isEmpty() )
			throw new RuntimeException( "Boolean query cannot have variables!" );

		prepare();

		return isQuerySatisfied();
	}

	public QueryResults exec(Query query) {
		this.query = query;

		prepare();

		QueryResults results = new QueryResultsImpl( query );

		if( isQuerySatisfied() ) {
			if( log.isLoggable( Level.FINE ) )
				log.fine( "Bind nonground triples" );
			exec( 0, new QueryResultBindingImpl(), results );
		}

		return results;
	}

	protected void exec(int index, QueryResultBinding binding, QueryResults results) {
		// Defines query parameterization
		QueryParameters parameters = results.getQuery().getQueryParameters();
		
		if( patterns.size() <= index ) {
			// It is possible that dist vars are not same as result vars (some
			// vars may be forced to be distinguished because of the query
			// structure). Filter those forced vars out of the results
			if( !results.getResultVars().containsAll( binding.getVars() ) ) {
				QueryResultBinding newBinding = new QueryResultBindingImpl();
				List resultVars = results.getResultVars();
				for( int i = 0; i < resultVars.size(); i++ ) {
					ATermAppl var = (ATermAppl) resultVars.get( i );
					ATermAppl value = binding.getValue( var );
					
					newBinding.setValue( var, value );
				}
				binding = newBinding;

				if( query.isDistinct() ) {
					if( results.contains( binding ) )
						return;
				}
			}
			
			results.add( binding );

			return;
		}

		QueryPattern pattern0 = (QueryPattern) patterns.get( index );

		if( log.isLoggable( Level.FINER ) )
			log.finer( "Check pattern " + pattern0 + " " + binding );

		boolean alreadySatisfied = false;
		Collection sValues = null;
		Collection oValues = null;
		if( !pattern0.isGround() ) {
			QueryPattern pattern = pattern0.apply( binding );

			ATermAppl subj = pattern.getSubject();
			ATermAppl pred = pattern.getPredicate();
			ATermAppl obj = pattern.getObject();

			boolean objectPattern = kb.isObjectProperty( pred );

			if( ATermUtils.isVar( subj ) ) {
				if( pattern.isTypePattern() ) {
					sValues = kb.getInstances( obj );
					alreadySatisfied = true;
				}
				else if( !ATermUtils.isVar( obj ) ) {
					sValues = kb.getIndividualsWithProperty( pred, obj );
				}
				else {
					sValues = kb.getIndividuals();// kb.retrieveIndividualsWithProperty(
					// pred );
				}
			}
			else {
				sValues = Collections.singletonList( subj );
			}

			if( ATermUtils.isVar( obj ) ) {
				if( !ATermUtils.isVar( subj ) ) {
					if( objectPattern )
						oValues = kb.getObjectPropertyValues( pred, subj );
					else
						oValues = kb.getDataPropertyValues( pred, subj );
					alreadySatisfied = true;
				}
			}
			else
				oValues = Collections.singletonList( obj );

			Iterator i = sValues.iterator();
			while( i.hasNext() ) {
				ATermAppl sValue = (ATermAppl) i.next();

				Iterator j = null;
				if( oValues != null )
					j = oValues.iterator();
				else if( objectPattern ) {
					j = kb.getObjectPropertyValues( pred, sValue ).iterator();
					alreadySatisfied = true;
				}
				else {
					j = kb.getDataPropertyValues( pred, sValue ).iterator();
					alreadySatisfied = true;
				}

				while( j.hasNext() ) {
					ATermAppl oValue = (ATermAppl) j.next();

					boolean satisfied = alreadySatisfied
							|| isTripleSatisfied( sValue, pred, oValue );
					if( satisfied ) {
						QueryResultBinding newBinding = (QueryResultBinding) binding.clone();
						if( ATermUtils.isVar( subj ) )
							newBinding.setValue( subj, sValue );
						if( ATermUtils.isVar( obj ) )
							newBinding.setValue( obj, oValue );

						exec( index + 1, newBinding, results );
					}
				}
			}
		}
		else
			exec( index + 1, binding, results );
	}

	private boolean isQuerySatisfied() {
		log.fine( "Check ground triples" );

		boolean querySatisfied = true;
		for( int i = 0; querySatisfied && i < patterns.size(); i++ ) {
			QueryPattern triple = (QueryPattern) patterns.get( i );

			if( triple.isGround() )
				querySatisfied = isTripleSatisfied( triple.getSubject(), triple.getPredicate(),
						triple.getObject() );
		}

		return querySatisfied;
	}

	private boolean isTripleSatisfied(ATermAppl s, ATermAppl p, ATermAppl o) {
		if( log.isLoggable( Level.FINER ) )
			log.finer( "Check triple " + s + " " + (p == null
				? "rdf:type"
				: p.getName()) + " " + o );

		if( ATermUtils.isVar( s ) || ATermUtils.isVar( o ) )
			throw new RuntimeException(
					"No value assigned to variables when checking triple in query!" );

		boolean tripleSatisfied = (p == null)
			? kb.isType( s, o )
			: kb.hasPropertyValue( s, p, o );

		return tripleSatisfied;
	}
}
