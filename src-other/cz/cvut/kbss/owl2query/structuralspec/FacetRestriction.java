package cz.cvut.kbss.owl2query.structuralspec;

public interface FacetRestriction {

	Literal getRestrictionValue();
	
	IRI getConstrainingFacet();
	
}
