package cz.cvut.kbss.owl2query.complexversion.modelold;

import java.util.List;

import cz.cvut.kbss.owl2query.complexversion.model.Term;
import cz.cvut.kbss.owl2query.complexversion.util.Pair;


public interface TBox {

	List<Pair<Term, DependencySet>> getUC();

}
