package cz.cvut.kbss.owl2query.simpleversion.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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

class SimpleRollingUpExec<G> extends AbstractABoxExec<G> {
	private static final Logger LOG = OWL2QueryEngine.log;

	@Override
	public QueryResult<G> execABoxQuery(final InternalQuery<G> q) {
		final QueryResult<G> results = new QueryResultImpl<G>(q);
		final OWL2Ontology<G> onto = q.getOntology();

		if (q.getDistVars().isEmpty()) {
			if (OWL2QueryEngine.execBooleanABoxQuery(q)) {
				results.add(new ResultBindingImpl<G>());
			}
		} else {
			final Map<Variable<G>, Set<? extends G>> varBindings = new HashMap<Variable<G>, Set<? extends G>>();

			// roll-up the query into all variables
			for (final Variable<G> currVar : q
					.getDistVarsOfTypes(VarType.INDIVIDUAL)) {
				varBindings.put(currVar, onto.getInstances(q.rollUpTo(currVar,
						Collections.<Term<G>> emptySet()), false));
			}

			if (LOG.isLoggable(Level.FINER))
				LOG.finer("Variable bindings: " + varBindings);

			final Iterator<ResultBinding<G>> i = new BindingIterator<G>(
					varBindings, onto.getFactory());

			boolean hasLiterals = !q.getDistVarsOfTypes(
					VarType.INDIVIDUAL_OR_LITERAL, VarType.LITERAL).isEmpty();

			if (hasLiterals) {
				while (i.hasNext()) {
					final ResultBinding<G> b = i.next();

					final Iterator<ResultBinding<G>> l = new LiteralIterator<G>(
							q, b, onto.getFactory());
					while (l.hasNext()) {
						ResultBinding<G> mappy = l.next();
						boolean queryTrue = OWL2QueryEngine
								.execBooleanABoxQuery(q.apply(mappy));
						if (queryTrue)
							results.add(mappy);
					}
				}
			} else {
				while (i.hasNext()) {
					final ResultBinding<G> b = i.next();
					boolean queryTrue = (q.getDistVarsOfTypes(
							VarType.INDIVIDUAL, VarType.INDIVIDUAL_OR_LITERAL)
							.size() == 1)
							|| OWL2QueryEngine.execBooleanABoxQuery(q.apply(b));
					if (queryTrue)
						results.add(b);
				}
			}
		}

		return results;
	}
}
