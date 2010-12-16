package cz.cvut.kbss.owl2query.complexversion.model;

public abstract class AbstractGroundTerm<T> implements Term {

	final TermType type;

	final T o;

	public AbstractGroundTerm(TermType t, T o) {
		this.type = t;
		this.o = o;
	}

	@Override
	public TermType getTermType() {
		return type;
	}

	public boolean isVariable() {
		return TermType.Variable.equals(type);
	}

	public T getWrappedObject() {
		return o;
	}

	@Override
	public Term getArgument(int i) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}
}
