// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.complexversion.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cvut.kbss.owl2query.complexversion.model.Term;
import cz.cvut.kbss.owl2query.complexversion.modelold.KnowledgeBase;
import cz.cvut.kbss.owl2query.complexversion.modelold.Query;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryAtom;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryAtomFactory;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryImpl;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryPredicate;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryResult;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryResultImpl;
import cz.cvut.kbss.owl2query.complexversion.modelold.ResultBinding;
import cz.cvut.kbss.owl2query.complexversion.modelold.ResultBindingImpl;
import cz.cvut.kbss.owl2query.complexversion.modelold.Query.VarType;
import cz.cvut.kbss.owl2query.complexversion.util.ATermUtils;

/**
 * <p>
 * Title: Abstract class for all purely ABox engines.
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
public abstract class AbstractABoxEngineWrapper implements QueryExec {
	public static final Logger log = Logger.getLogger(QueryEngine.class
			.getName());

	public static final QueryExec distCombinedQueryExec = new CombinedQueryEngine();

	protected Query schemaQuery;

	protected Query aboxQuery;

	/**
	 * {@inheritDoc}
	 */
	public QueryResult exec(Query query) {
		if (log.isLoggable(Level.FINE)) {
			log.fine("Executing query " + query.getAtoms());
		}

		partitionQuery(query);

		QueryResult newResult;

		boolean shouldHaveBinding;
		final QueryResult result;

		if (schemaQuery.getAtoms().isEmpty()) {
			shouldHaveBinding = false;
			result = new QueryResultImpl(query);
			result.add(new ResultBindingImpl());
		} else {
			if (log.isLoggable(Level.FINE)) {
				log.fine("Executing TBox query: " + schemaQuery);
			}
			result = distCombinedQueryExec.exec(schemaQuery);

			shouldHaveBinding = !Collections.disjoint(query
					.getDistVarsForType(VarType.CLASS), query.getResultVars())
					|| !Collections.disjoint(query
							.getDistVarsForType(VarType.PROPERTY), query
							.getResultVars());
		}
		if (shouldHaveBinding && result.isEmpty()) {
			return result;
		}

		if (log.isLoggable(Level.FINE)) {
			log.fine("Partial binding after schema query : " + result);
		}

		if (aboxQuery.getAtoms().size() > 0) {
			newResult = new QueryResultImpl(query);
			for (ResultBinding binding : result) {
				final Query query2 = aboxQuery.apply(binding);

				if (log.isLoggable(Level.FINE)) {
					log.fine("Executing ABox query: " + query2);
				}
				final QueryResult aboxResult = execABoxQuery(query2);

				for (ResultBinding newBinding : aboxResult) {
					for (final Term var : binding.getAllVariables()) {
						newBinding.setValue(var, binding.getValue(var));
					}

					newResult.add(newBinding);
				}
			}
		} else {
			newResult = result;
			if (log.isLoggable(Level.FINER)) {
				log.finer("ABox query empty ... returning.");
			}
		}
		return newResult;
	}

	private final void partitionQuery(final Query query) {

		schemaQuery = new QueryImpl(query);
		aboxQuery = new QueryImpl(query);

		for (final QueryAtom atom : query.getAtoms()) {
			switch (atom.getPredicate()) {
			case Type:
			case PropertyValue:
				// case SameAs:
				// case DifferentFrom:
				aboxQuery.add(atom);
				break;
			default:
				;
			}
		}

		final List<QueryAtom> atoms = new ArrayList<QueryAtom>(query.getAtoms());
		atoms.removeAll(aboxQuery.getAtoms());

		for (final QueryAtom atom : atoms) {
			schemaQuery.add(atom);
		}

		for (final VarType t : VarType.values()) {
			for (final Term a : query.getDistVarsForType(t)) {
				if (aboxQuery.getVars().contains(a)) {
					aboxQuery.addDistVar(a, t);
				}
				if (schemaQuery.getVars().contains(a)) {
					schemaQuery.addDistVar(a, t);
				}
			}
		}

		for (final Term a : query.getResultVars()) {
			if (aboxQuery.getVars().contains(a)) {
				aboxQuery.addResultVar(a);
			}
			if (schemaQuery.getVars().contains(a)) {
				schemaQuery.addResultVar(a);
			}
		}

		for (final Term v : aboxQuery.getDistVarsForType(VarType.CLASS)) {
			if (!schemaQuery.getVars().contains(v)) {
				schemaQuery.add(QueryAtomFactory.SubClassOfAtom(v,
						ATermUtils.TOP));
			}
		}

		for (final Term v : aboxQuery.getDistVarsForType(VarType.PROPERTY)) {
			if (!schemaQuery.getVars().contains(v)) {
				schemaQuery.add(QueryAtomFactory.SubPropertyOfAtom(v, v));
			}
		}

	}

	protected abstract QueryResult execABoxQuery(final Query q);
}

class BindingIterator implements Iterator<ResultBinding> {
	private final List<List<Term>> varB = new ArrayList<List<Term>>();

	private final List<Term> vars = new ArrayList<Term>();

	private int[] indices;

	private boolean more = true;

	public BindingIterator(final Map<Term, Set<Term>> bindings) {
		vars.addAll(bindings.keySet());

		for (final Term var : vars) {
			final Set<Term> values = bindings.get(var);
			if (values.isEmpty()) {
				more = false;
				break;
			} else {
				varB.add(new ArrayList<Term>(values));
			}
		}

		indices = new int[vars.size()];
	}

	private boolean incIndex(int index) {
		if (indices[index] + 1 < varB.get(index).size()) {
			indices[index]++;
		} else {
			if (index == indices.length - 1) {
				return false;
			} else {
				indices[index] = 0;
				return incIndex(index + 1);
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return more;
	}

	/**
	 * {@inheritDoc}
	 */
	public ResultBinding next() {
		if (!more)
			return null;

		final ResultBinding next = new ResultBindingImpl();

		for (int i = 0; i < indices.length; i++) {
			next.setValue(vars.get(i), varB.get(i).get(indices[i]));
		}

		more = incIndex(0);

		return next;
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove() {
		throw new UnsupportedOperationException(
				"Removal from this iterator is not supported.");
	}
}

class LiteralIterator implements Iterator<ResultBinding> {
	private int[] indices;

	private ResultBinding binding;

	private Set<Term> litVars;

	private List<List<Term>> litVarBindings = new ArrayList<List<Term>>();

	private boolean more = true;

	public LiteralIterator(final Query q, final ResultBinding binding) {
		final KnowledgeBase kb = q.getKB();
		this.binding = binding;
		this.litVars = q.getDistVarsForType(VarType.LITERAL);

		indices = new int[litVars.size()];
		int index = 0;
		for (final Term litVar : litVars) {
			// final Datatype dtype = ;// q.getDatatype(litVar); TODO after
			// recognizing Datatypes and adjusting Query model supply the
			// corresponding literal.

			final List<Term> foundLiterals = new ArrayList<Term>();
			boolean first = true;

			for (final QueryAtom atom : q.findAtoms(
					QueryPredicate.PropertyValue, null, null, litVar)) {

				Term subject = atom.getArguments().get(0);
				final Term predicate = atom.getArguments().get(1);

				if (ATermUtils.isVar(subject))
					subject = binding.getValue(subject);

				litVarBindings.add(index, new ArrayList<Term>());

				final List<Term> act = kb.getDataPropertyValues(predicate,
						subject); // dtype);

				if (first) {
					foundLiterals.addAll(act);
				} else {
					foundLiterals.retainAll(act);
					first = false;
				}
			}

			if (foundLiterals.size() > 0) {
				litVarBindings.get(index++).addAll(foundLiterals);
			} else {
				more = false;
			}
		}
	}

	private boolean incIndex(int index) {
		if (indices[index] + 1 < litVarBindings.get(index).size()) {
			indices[index]++;
		} else {
			if (index == indices.length - 1) {
				return false;
			} else {
				indices[index] = 0;
				return incIndex(index + 1);
			}
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove() {
		throw new UnsupportedOperationException(
				"Removal from this iterator is not supported.");
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasNext() {
		return more;
	}

	/**
	 * {@inheritDoc}
	 */
	public ResultBinding next() {
		if (!more)
			return null;

		final ResultBinding next = binding.clone();

		int index = 0;
		for (final Term o1 : litVars) {
			Term o2 = litVarBindings.get(index).get(indices[index++]);
			next.setValue(o1, o2);
		}

		more = incIndex(0);

		return next;
	}
}