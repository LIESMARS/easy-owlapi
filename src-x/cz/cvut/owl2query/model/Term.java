package cz.cvut.owl2query.model;

public interface Term {
    
	public boolean isVariable();
	
	public Variable asVariable();
	
	public GroundTerm asGroundTerm();

}
