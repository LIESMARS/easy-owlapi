package cz.cvut.kbss.owl2query.complexversion.model;

public interface DataRange<DR> extends DataRangeTerm<DR> {

	public DR getWrappedDataRangeObject();

	// TODO unlimited natural
	int getArity();
}
