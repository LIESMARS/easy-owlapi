// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.complexversion.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cvut.kbss.owl2query.complexversion.modelold.KnowledgeBase;
import cz.cvut.kbss.owl2query.complexversion.modelold.Query;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryAtom;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryPredicate;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryResult;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryResultImpl;
import cz.cvut.kbss.owl2query.complexversion.modelold.ResultBinding;
import cz.cvut.kbss.owl2query.complexversion.modelold.ResultBindingImpl;
import cz.cvut.kbss.owl2query.complexversion.model.Term;
import cz.cvut.kbss.owl2query.complexversion.util.ATermUtils;


/**
 * <p>
 * Title: SimpleQueryEngine
 * </p>
 * <p>
 * Description:
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
public class OptimizedQueryEngine2 extends AbstractABoxEngineWrapper {
	public static final Logger log = Logger.getLogger(QueryEngine.class.getName());

	private QueryResult results;

	private KnowledgeBase kb;

	/**
	 * {@inheritDoc}
	 */
	public boolean supports(final Query q) {
		return !q.getDistVars().isEmpty();
	}

	private void exec(final Query q, final ResultBinding binding,
			final boolean first) {
		if (q.getDistVars().isEmpty()) {
			results.add(binding);
			return;
		}

		final Iterator<Term> i = q.getDistVars().iterator();

		final Term var = i.next();

		final Term clazz = q.rollUpTo(var, Collections.EMPTY_SET, false);

		if (log.isLoggable( Level.FINE )) {
			log.fine("Rolling up " + var + " to " + clazz);
		}

		final Collection<Term> instances;

		if (first) {
			instances = new HashSet<Term>(kb.getIndividuals());
			for (final QueryAtom atom : q.findAtoms(
					QueryPredicate.PropertyValue, var, null, null)) {
				instances.retainAll(kb.retrieveIndividualsWithProperty(atom
						.getArguments().get(1)));
			}

			for (final QueryAtom atom : q.findAtoms(
					QueryPredicate.PropertyValue, null, null, var)) {
				instances.retainAll(kb
						.retrieveIndividualsWithProperty(ATermUtils
								.makeInv(atom.getArguments().get(1))));
			}
		} else {
			instances = kb.getInstances(clazz);
		}

		for (final Term b : instances) {
			if (log.isLoggable( Level.FINE )) {
				log.fine("trying " + var + " --> " + b);
			}
			final ResultBinding newBinding = binding.clone();

			newBinding.setValue(var, b);
			final Query q2 = q.apply(newBinding);
			exec(q2, newBinding, false);
		}
	}

	@Override
	public QueryResult execABoxQuery(final Query q) {
		results = new QueryResultImpl(q);

		this.kb = q.getKB();

		long satCount = kb.getABox().satisfiabilityCount;
		long consCount = kb.getABox().consistencyCount;

		exec(q, new ResultBindingImpl(), true);

		if (log.isLoggable( Level.FINE )) {
			log.fine("Total satisfiability operations: "
					+ (kb.getABox().satisfiabilityCount - satCount));
			log.fine("Total consistency operations: "
					+ (kb.getABox().consistencyCount - consCount));
			log.fine("Results of ABox query : " + results);
		}

		return results;
	}
}