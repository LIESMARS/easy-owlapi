package cz.cvut.kbss.owl2query.simpleversion.model;

import java.util.Map;

public interface ResultBinding<G> extends Map<Variable<G>, GroundTerm<G>>, Cloneable {

	public ResultBinding<G> clone();
}
