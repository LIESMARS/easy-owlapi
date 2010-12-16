package cz.cvut.kbss.owl2query.owlapi.model;

import java.util.List;


import cz.cvut.kbss.owl2query.owlapi.util.Pair;


public interface TBox {

	List<Pair<ATermAppl, DependencySet>> getUC();

}
