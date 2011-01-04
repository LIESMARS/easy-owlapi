package cz.cvut.kbss.owl2query.engine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import cz.cvut.kbss.owl2query.model.ResultBinding;

class CarthesianProductIterator<G> implements Iterator<ResultBinding<G>> {

	private final List<Iterator<ResultBinding<G>>> iterators = new ArrayList<Iterator<ResultBinding<G>>>();
	private final List<ResultBinding<G>> cursor = new ArrayList<ResultBinding<G>>();
	private boolean hasNext;

	private final List<? extends Iterable<ResultBinding<G>>> queryResults;

	CarthesianProductIterator(
			final List<? extends Iterable<ResultBinding<G>>> lstBindings) {
		this.queryResults = lstBindings;
		hasNext = true;
		for (final Iterable<ResultBinding<G>> result : lstBindings) {
			final Iterator<ResultBinding<G>> iterator = result.iterator();

			if (!iterator.hasNext()) {
				hasNext = false;
				break;
			}

			iterators.add(iterator);
			cursor.add(iterator.next());
		}
	}

	public boolean hasNext() {
		return hasNext;
	}

	public ResultBinding<G> next() {
		if (!hasNext())
			throw new NoSuchElementException();

		ResultBinding<G> result = new ResultBindingImpl<G>();
		for (ResultBinding<G> binding : cursor) {
			result.putAll(binding);
		}

		ListIterator<Iterator<ResultBinding<G>>> i = iterators.listIterator();

		for (int index = 0; index < iterators.size(); index++) {
			Iterator<ResultBinding<G>> iterator = i.next();
			if (iterator.hasNext()) {
				cursor.set(index, iterator.next());
				break;
			} else if (index == iterators.size() - 1) {
				hasNext = false;
				break;
			} else {
				iterator = queryResults.get(index).iterator();
				i.set(iterator);
				cursor.set(index, iterator.next());
			}
		}

		return result;
	}

	public void remove() {
		throw new UnsupportedOperationException(
				"Removal from this iterator is not supported.");
	}
}
