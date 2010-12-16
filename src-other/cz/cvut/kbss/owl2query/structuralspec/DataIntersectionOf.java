package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Set;

public interface DataIntersectionOf extends DataRange {

	/**
	 * 2+
	 */
	Set<DataRange> getDataRanges();

}
