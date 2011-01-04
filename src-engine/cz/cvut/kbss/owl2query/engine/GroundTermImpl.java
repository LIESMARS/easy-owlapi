package cz.cvut.kbss.owl2query.engine;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import cz.cvut.kbss.owl2query.model.GroundTerm;
import cz.cvut.kbss.owl2query.model.Term;
import cz.cvut.kbss.owl2query.model.VarType;
import cz.cvut.kbss.owl2query.model.Variable;

class GroundTermImpl<T> implements GroundTerm<T> {

	private T groundTerm;

	/**
	 * Constructs a URI
	 */
	public GroundTermImpl(final T groundTerm) {
		this.groundTerm = groundTerm;
	}

	public boolean isVariable() {
		return false;
	}

	@Override
	public T getWrappedObject() {
		return groundTerm;
	}

	@Override
	public GroundTerm<T> asGroundTerm() {
		return this;
	}

	@Override
	public Variable<T> asVariable() {
		throw new IllegalArgumentException();
	}

	@Override
	public boolean isGround() {
		return true;
	}

	@Override
	public int hashCode() {
		return groundTerm.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final GroundTermImpl<T> other = (GroundTermImpl<T>) obj;
		if (groundTerm == null) {
			if (other.groundTerm != null)
				return false;
		} else if (!groundTerm.equals(other.groundTerm))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return groundTerm + "";
	}

	@Override
	public Term<T> apply(Map<Variable<T>, GroundTerm<T>> binding) {
		return this;
	}

	@Override
	public Set<Variable<T>> getVariables() {
		return Collections.emptySet();
	}

	@Override
	public VarType getVariableType(Variable<T> var) {
		return null;
	}


	@Override
	public String shortForm() {
		if (groundTerm.toString().startsWith("http://")
				|| groundTerm.toString().startsWith("file://")) {

			if ( groundTerm.toString().contains("#")) {
				return groundTerm.toString().substring(
						groundTerm.toString().lastIndexOf("#")+1);
			} else {
				return groundTerm.toString().substring(
						groundTerm.toString().lastIndexOf("/")+1);
			}
		} else {
			return groundTerm.toString();
		}
	}
}
