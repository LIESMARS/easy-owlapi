package org.mindswap.pellet;

import java.util.List;

import org.mindswap.pellet.utils.Pair;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

public interface TBox {

	List<Pair<ATermAppl, DependencySet>> getUC();

}
