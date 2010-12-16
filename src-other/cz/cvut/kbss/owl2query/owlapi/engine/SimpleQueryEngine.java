// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.owlapi.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.logging.Level;
import java.util.logging.Logger;


import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;
import cz.cvut.kbss.owl2query.owlapi.model.KnowledgeBase;
import cz.cvut.kbss.owl2query.owlapi.model.Query;
import cz.cvut.kbss.owl2query.owlapi.model.QueryResult;
import cz.cvut.kbss.owl2query.owlapi.model.QueryResultImpl;
import cz.cvut.kbss.owl2query.owlapi.model.ResultBinding;
import cz.cvut.kbss.owl2query.owlapi.model.ResultBindingImpl;
import cz.cvut.kbss.owl2query.owlapi.model.Query.VarType;


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
public class SimpleQueryEngine extends AbstractABoxEngineWrapper {
	public static final Logger log = Logger.getLogger(QueryEngine.class.getName());

	public boolean supports(final Query q) {
		return true; // TODO
	}

	@Override
	public QueryResult execABoxQuery(final Query q) {
		final QueryResult results = new QueryResultImpl(q);
		final KnowledgeBase kb = q.getKB();

		long satCount = kb.getABox().satisfiabilityCount;
		long consCount = kb.getABox().consistencyCount;

		if (q.getDistVars().isEmpty()) {
			if (QueryEngine.execBooleanABoxQuery(q)) {
				results.add(new ResultBindingImpl());
			}
		} else {
			final Map<ATermAppl, Set<ATermAppl>> varBindings = new HashMap<ATermAppl, Set<ATermAppl>>();

			for (final ATermAppl currVar : q
					.getDistVarsForType(VarType.INDIVIDUAL)) {
				ATermAppl rolledUpClass = q.rollUpTo(currVar,
						Collections.EMPTY_SET, false);

				if (log.isLoggable( Level.FINER ))
					log.finer("Rolled up class " + rolledUpClass);
				Set<ATermAppl> inst = kb.getInstances(rolledUpClass);
				varBindings.put(currVar, inst);
			}

			if (log.isLoggable( Level.FINER ))
				log.finer("Var bindings: " + varBindings);

			final Iterator<ResultBinding> i = new BindingIterator(varBindings);

			final Set<ATermAppl> literalVars = q
					.getDistVarsForType(VarType.LITERAL);
			final Set<ATermAppl> individualVars = q
					.getDistVarsForType(VarType.INDIVIDUAL);

			boolean hasLiterals = !individualVars.containsAll(literalVars);

			if (hasLiterals) {
				while (i.hasNext()) {
					final ResultBinding b = i.next();

					final Iterator<ResultBinding> l = new LiteralIterator(q, b);
					while (l.hasNext()) {
						ResultBinding mappy = l.next();
						boolean queryTrue = QueryEngine.execBooleanABoxQuery(q
								.apply(mappy));
						if (queryTrue)
							results.add(mappy);
					}
				}
			} else {
				while (i.hasNext()) {
					final ResultBinding b = i.next();
					boolean queryTrue = (q.getDistVarsForType(
							VarType.INDIVIDUAL).size() == 1)
							|| QueryEngine.execBooleanABoxQuery(q.apply(b));
					if (queryTrue)
						results.add(b);
				}
			}
		}

		if (log.isLoggable( Level.FINE )) {
			log.fine("Results: "
					+ results);
			log.fine("Total satisfiability operations: "
					+ (kb.getABox().satisfiabilityCount - satCount));
			log.fine("Total consistency operations: "
					+ (kb.getABox().consistencyCount - consCount));
		}

		return results;
	}
}
