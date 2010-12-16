package cz.cvut.kbss.owl2query.complexversion.model;

public interface Term {

	TermType getTermType();
		
	boolean isVariable();
	
	@Deprecated
	Term getArgument(int i);

	@Deprecated
	String getName();
}
