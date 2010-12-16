package cz.cvut.kbss.owl2query.structuralspec;

public interface AnnotationProperty extends Entity {

	public static AnnotationProperty RDFS_LABEL = new AnnotationPropertyImpl(
			new IRI.IRIImpl(Namespaces.RDFS + "label"));

	public static AnnotationProperty RDFS_COMMENT = new AnnotationPropertyImpl(
			new IRI.IRIImpl(Namespaces.RDFS + "comment"));

	public static AnnotationProperty RDFS_SEE_ALSO = new AnnotationPropertyImpl(
			new IRI.IRIImpl(Namespaces.RDFS + "seeAlso"));
	
	public static AnnotationProperty RDFS_IS_DEFINED_BY = new AnnotationPropertyImpl(
			new IRI.IRIImpl(Namespaces.RDFS + "isDefinedBy"));

	public static AnnotationProperty RDFS_DEPRECATED = new AnnotationPropertyImpl(
			new IRI.IRIImpl(Namespaces.RDFS + "deprecated"));

	public static AnnotationProperty OWL_VERSION_INFO = new AnnotationPropertyImpl(
			new IRI.IRIImpl(Namespaces.OWL + "versionInfo"));
	
	public static AnnotationProperty OWL_PRIOR_VERSION = new AnnotationPropertyImpl(
			new IRI.IRIImpl(Namespaces.OWL + "priorVersion"));
	
	public static AnnotationProperty OWL_BACKWARD_COMPATIBLE_WITH = new AnnotationPropertyImpl(
			new IRI.IRIImpl(Namespaces.OWL + "backwardCompatibleWith"));
	
	public static AnnotationProperty OWL_INCOMPATIBLE_WITH = new AnnotationPropertyImpl(
			new IRI.IRIImpl(Namespaces.OWL + "incompatibleWith"));	

	class AnnotationPropertyImpl implements AnnotationProperty {

		IRI iri;

		AnnotationPropertyImpl(IRI iri) {
			this.iri = iri;
		}

		@Override
		public IRI getEntityIRI() {
			return iri;
		}
	}
	
}
