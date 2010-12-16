package cz.cvut.kbss.owl2query.structuralspec;

public interface Class extends Entity, ClassExpression {

	public static Class OWL_THING = new ClassImpl(new IRI.IRIImpl(
			Namespaces.OWL + "Thing"));

	public static Class OWL_NOTHING = new ClassImpl(new IRI.IRIImpl(
			Namespaces.OWL + "Nothing"));

	class ClassImpl implements Class {

		IRI iri;

		ClassImpl(IRI iri) {
			this.iri = iri;
		}

		@Override
		public IRI getEntityIRI() {
			return iri;
		}

	}
}
