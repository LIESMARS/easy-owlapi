package cz.cvut.kbss.owl2query.structuralspec;

public interface DataProperty extends Entity, DataPropertyExpression {
	public static DataProperty OWL_TOP_DATA_PROPERTY = new DataPropertyImpl(
			new IRI.IRIImpl(Namespaces.OWL + "topDataProperty"));

	public static DataProperty OWL_BOTTOM_DATA_PROPERTY = new DataPropertyImpl(
			new IRI.IRIImpl(Namespaces.OWL + "bottomDataProperty"));

	class DataPropertyImpl implements DataProperty {

		IRI iri;

		DataPropertyImpl(IRI iri) {
			this.iri = iri;
		}

		@Override
		public IRI getEntityIRI() {
			return iri;
		}
	}
}
