package cz.cvut.kbss.owl2query.simpleversion.model;

import java.util.Map;
import java.util.Set;

public interface Term<T> {

	/**
	 * Returns true, if the term is a variable.
	 */
	public boolean isVariable();

	/**
	 * Returns true, if the term is an expression that represents a ground term,
	 * i.e. no variable occurs in its body
	 */
	public boolean isGround();

	public Variable<T> asVariable();

	public GroundTerm<T> asGroundTerm();

	public Set<Variable<T>> getVariables();

	public VarType getVariableType(final Variable<T> var);

	public Term<T> apply(Map<Variable<T>, GroundTerm<T>> binding);
	
	public String shortForm();
}
