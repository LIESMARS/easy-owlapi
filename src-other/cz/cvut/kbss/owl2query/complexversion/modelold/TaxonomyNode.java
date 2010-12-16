package cz.cvut.kbss.owl2query.complexversion.modelold;

import java.util.Collection;

import cz.cvut.kbss.owl2query.complexversion.model.Term;



public interface TaxonomyNode<T> {

	Term getName();

	Collection<TaxonomyNode<T>> getSubs();

}
