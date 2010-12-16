package cz.cvut.owl2query.model;

public interface ObjectDistinguisher {
	
	public boolean isVariable(final Object object);

	public boolean isVariableOfType(final Object object, VarType type);
	
	public boolean isGroundTerm(final Object object);

	public boolean isGroundTermOfType(final Object object, GroundTermType type);	
}
