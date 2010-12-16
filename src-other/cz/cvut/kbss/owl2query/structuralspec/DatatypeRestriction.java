package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Set;

public interface DatatypeRestriction extends DataRange {

	Datatype getDatatype();
	
	Set<FacetRestriction> getRestrictions();
	
}
