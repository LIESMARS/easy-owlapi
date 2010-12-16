// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.complexversion.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cvut.kbss.owl2query.complexversion.modelold.KnowledgeBase;
import cz.cvut.kbss.owl2query.complexversion.modelold.Query;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryResult;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryResultImpl;
import cz.cvut.kbss.owl2query.complexversion.modelold.ResultBinding;
import cz.cvut.kbss.owl2query.complexversion.modelold.ResultBindingImpl;
import cz.cvut.kbss.owl2query.complexversion.modelold.Query.VarType;
import cz.cvut.kbss.owl2query.complexversion.model.Term;


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
			final Map<Term, Set<Term>> varBindings = new HashMap<Term, Set<Term>>();

			for (final Term currVar : q
					.getDistVarsForType(VarType.INDIVIDUAL)) {
				Term rolledUpClass = q.rollUpTo(currVar,
						Collections.EMPTY_SET, false);

				if (log.isLoggable( Level.FINER ))
					log.finer("Rolled up class " + rolledUpClass);
				Set<Term> inst = kb.getInstances(rolledUpClass);
				varBindings.put(currVar, inst);
			}

			if (log.isLoggable( Level.FINER ))
				log.finer("Var bindings: " + varBindings);

			final Iterator<ResultBinding> i = new BindingIterator(varBindings);

			final Set<Term> literalVars = q
					.getDistVarsForType(VarType.LITERAL);
			final Set<Term> individualVars = q
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
