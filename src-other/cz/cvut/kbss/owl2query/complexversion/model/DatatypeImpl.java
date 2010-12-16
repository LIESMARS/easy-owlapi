package cz.cvut.kbss.owl2query.complexversion.model;

import java.net.URI;

class DatatypeImpl<DT, DR> extends AbstractGroundTerm<DT> implements
		Datatype<DT, DR> {

	private URI uri;

	DatatypeImpl(DT o, URI uri) {
		super(TermType.DataRange, o);

		this.uri = uri;
	}

	@Override
	public URI getEntityURI() {
		return uri;
	}

	@Override
	public int getArity() {
		return 1;
	}

	@Override
	public DT getWrappedDatatypeObject() {
		return getWrappedObject();
	}

	@Override
	public DR getWrappedDataRangeObject() {
		return (DR) getWrappedObject();
	}
}
