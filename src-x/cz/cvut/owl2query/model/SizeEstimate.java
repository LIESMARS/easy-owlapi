package cz.cvut.owl2query.model;

public interface SizeEstimate {

	public int getCost( final KBOperation operation );
	
	public int getReferencedOWLPropertyCount();
	
	public int getClassCount();

}
