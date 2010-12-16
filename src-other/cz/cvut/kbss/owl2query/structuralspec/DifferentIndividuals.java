package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Set;


public interface DifferentIndividuals extends Assertion {
	
	/**
	 * 2+ 
	 */
	Set<Individual> getIndividuals();
	
}
