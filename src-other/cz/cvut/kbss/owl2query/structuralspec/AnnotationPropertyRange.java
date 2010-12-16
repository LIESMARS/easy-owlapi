package cz.cvut.kbss.owl2query.structuralspec;

public interface AnnotationPropertyRange extends AnnotationAxiom {

	AnnotationProperty getAnnotationProperty();
	
	IRI getRange();
	
}
