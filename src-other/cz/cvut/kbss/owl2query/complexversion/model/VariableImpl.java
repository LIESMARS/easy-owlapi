package cz.cvut.kbss.owl2query.complexversion.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VariableImpl<CE, OPE, DPE, AP, DT, DR, NI, L> implements
		Variable<CE, OPE, DPE, AP, DT, DR, NI, L> {

	final Set<TermType> set = new HashSet<TermType>();

	final String name;

	public VariableImpl(final String name) {
		this.name = name;
	}

	@Override
	public Set<TermType> getAllowedTermTypes() {
		return Collections.unmodifiableSet(set);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public TermType getTermType() {
		return TermType.Variable;
	}

	@Override
	public boolean isVariable() {
		return true;
	}

	@Override
	public Term getArgument(int i) {
		throw new UnsupportedOperationException();
	}
}
