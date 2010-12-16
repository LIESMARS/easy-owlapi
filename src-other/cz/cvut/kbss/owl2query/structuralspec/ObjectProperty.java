package cz.cvut.kbss.owl2query.structuralspec;

public interface ObjectProperty extends Entity, ObjectPropertyExpression {
	
	public static ObjectProperty OWL_TOP_OBJECT_PROPERTY = new ObjectPropertyImpl(new IRI.IRIImpl(
			Namespaces.OWL + "topObjectProperty"));

	public static ObjectProperty OWL_BOTTOM_OBJECT_PROPERTY = new ObjectPropertyImpl(new IRI.IRIImpl(
			Namespaces.OWL + "bottomObjectProperty"));
	
	class ObjectPropertyImpl implements ObjectProperty {

		IRI iri;

		ObjectPropertyImpl(IRI iri) {
			this.iri = iri;
		}

		@Override
		public IRI getEntityIRI() {
			return iri;
		}
	}
}
