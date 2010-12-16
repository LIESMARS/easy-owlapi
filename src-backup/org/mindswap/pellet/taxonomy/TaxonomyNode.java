package org.mindswap.pellet.taxonomy;

import java.util.Collection;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

public interface TaxonomyNode<T> {

	ATermAppl getName();

	Collection<TaxonomyNode<T>> getSubs();

}
