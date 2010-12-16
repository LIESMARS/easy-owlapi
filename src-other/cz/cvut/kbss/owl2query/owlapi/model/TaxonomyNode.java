package cz.cvut.kbss.owl2query.owlapi.model;

import java.util.Collection;



public interface TaxonomyNode<T> {

	ATermAppl getName();

	Collection<TaxonomyNode<T>> getSubs();

}
