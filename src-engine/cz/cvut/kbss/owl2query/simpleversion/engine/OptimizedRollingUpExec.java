package cz.cvut.kbss.owl2query.simpleversion.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cvut.kbss.owl2query.simpleversion.model.OWL2Ontology;
import cz.cvut.kbss.owl2query.simpleversion.model.QueryResult;
import cz.cvut.kbss.owl2query.simpleversion.model.ResultBinding;
import cz.cvut.kbss.owl2query.simpleversion.model.Term;
import cz.cvut.kbss.owl2query.simpleversion.model.VarType;
import cz.cvut.kbss.owl2query.simpleversion.model.Variable;

class OptimizedRollingUpExec<G> extends AbstractABoxExec<G> {
	private static final Logger log = OWL2QueryEngine.log;

	@Override
	public QueryResult<G> execABoxQuery(final InternalQuery<G> q) {
		final QueryResult<G> results = new QueryResultImpl<G>(q);
		final OWL2Ontology<G> kb = q.getOntology();

		if (q.getDistVars().isEmpty()) {
			if (OWL2QueryEngine.execBooleanABoxQuery(q)) {
				results.add(new ResultBindingImpl<G>());
			}
		} else {
			final Map<Variable<G>, Set<? extends G>> varBindings = new HashMap<Variable<G>, Set<? extends G>>();

			for (final Variable<G> currVar : q
					.getDistVarsOfTypes(VarType.INDIVIDUAL)) {
				varBindings.put(currVar, kb.getInstances(q.rollUpTo(currVar, Collections.<Term<G>>emptySet()),
						false));
			}

			if (log.isLoggable(Level.FINER))
				log.finer("Var bindings: " + varBindings);

			final List<Variable<G>> varList = new ArrayList<Variable<G>>(
					varBindings.keySet());

			final Map<Variable<G>, Collection<ResultBinding<G>>> goodLists = new HashMap<Variable<G>, Collection<ResultBinding<G>>>();

			final Variable<G> first = varList.get(0);
			final Collection<ResultBinding<G>> c = new HashSet<ResultBinding<G>>();

			for (final G a : varBindings.get(first)) {
				final ResultBinding<G> bind = new ResultBindingImpl<G>();
				bind.put(first, kb.getFactory().wrap(a));
				c.add(bind);
			}

			goodLists.put(first, c);

			Collection<ResultBinding<G>> previous = goodLists.get(first);
			for (int i = 1; i < varList.size(); i++) {
				final Variable<G> next = varList.get(i);

				final Collection<ResultBinding<G>> newBindings = new HashSet<ResultBinding<G>>();

				for (final ResultBinding<G> binding : previous) {
					for (final G testBind : varBindings.get(next)) {
						final ResultBinding<G> bindingCandidate = binding
								.clone();

						bindingCandidate.put(next, kb.getFactory().wrap(
								testBind));

						boolean queryTrue = OWL2QueryEngine
								.execBooleanABoxQuery(q.apply(bindingCandidate));
						if (queryTrue) {
							newBindings.add(bindingCandidate);
							if (log.isLoggable(Level.FINER)) {
								log.finer("Accepted binding: "
										+ bindingCandidate);
							}
						} else {
							if (log.isLoggable(Level.FINER)) {
								log.finer("Rejected binding: "
										+ bindingCandidate);
							}
						}
					}
				}

				previous = newBindings;
			}

			// no var. should be marked as both INDIVIDUAL and LITERAL in an
			// ABox query.
			boolean hasLiterals = !q.getDistVarsOfTypes(VarType.LITERAL,
					VarType.INDIVIDUAL_OR_LITERAL).isEmpty();

			if (hasLiterals) {
				for (final ResultBinding<G> b : previous) {
					for (final Iterator<ResultBinding<G>> i = new LiteralIterator<G>(
							q, b, kb.getFactory()); i.hasNext();) {
						results.add(i.next());
					}
				}
			} else {
				for (final ResultBinding<G> b : previous) {
					results.add(b);
				}
			}
		}
		return results;
	}
}
