package cz.cvut.kbss.owl2query.structuralspec;

public interface AnnotationPropertyDomain extends AnnotationAxiom {

	AnnotationProperty getAnnotationProperty();
	
	IRI getDomain();
	
}
