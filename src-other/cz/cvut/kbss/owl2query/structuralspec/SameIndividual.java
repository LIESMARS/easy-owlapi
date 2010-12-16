package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Set;


public interface SameIndividual extends Assertion {
	
	/**
	 * 2+ 
	 */
	Set<Individual> getIndividuals();
	
}
