package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Set;

public interface DataOneOf extends DataRange {

	/**
	 * 1+
	 */
	Set<Literal> getLiterals();

}
