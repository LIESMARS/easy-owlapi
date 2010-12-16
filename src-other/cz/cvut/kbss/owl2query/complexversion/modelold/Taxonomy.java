package cz.cvut.kbss.owl2query.complexversion.modelold;

import java.util.Collection;
import java.util.Set;

public interface Taxonomy<T> {

	Set<T> getFlattenedSubs(T c, boolean b);

	TaxonomyNode<T> getTop();

	Set<Set<T>> getSubs(T candidate, boolean direct);

	Set<Set<T>> getSupers(T candidate, boolean direct);

	Collection<T> getEquivalents(T candidate);

}
