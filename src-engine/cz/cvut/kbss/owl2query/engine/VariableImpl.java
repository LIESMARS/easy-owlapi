package cz.cvut.kbss.owl2query.engine;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import cz.cvut.kbss.owl2query.model.GroundTerm;
import cz.cvut.kbss.owl2query.model.Term;
import cz.cvut.kbss.owl2query.model.VarType;
import cz.cvut.kbss.owl2query.model.Variable;

class VariableImpl<T> implements Variable<T> {

	private String id = null;

	public VariableImpl(final String string) {
		this.id = string;
	}

	@Override
	public boolean isVariable() {
		return true;
	}

	@Override
	public String getName() {
		return id;
	}

	@Override
	public GroundTerm<T> asGroundTerm() {
		throw new IllegalArgumentException();
	}

	@Override
	public Variable<T> asVariable() {
		return this;
	}

	@Override
	public boolean isGround() {
		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariableImpl other = (VariableImpl) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "?" + id;
	}

	@Override
	public String shortForm() {
		if (id.startsWith("?")) {
			return "_:" + id.substring(1);
		} else {
			return "?" + id;
		}
	}
	
	
	@Override
	public Term<T> apply(Map<Variable<T>, GroundTerm<T>> binding) {
		if (binding.containsKey(this)) {
			return binding.get(this);
		} else {
			return this;
		}
	}

	@Override
	public Set<Variable<T>> getVariables() {
		return Collections.<Variable<T>> singleton(this);
	}

	@Override
	public VarType getVariableType(Variable<T> var) {
		return null;
	}
	
}
