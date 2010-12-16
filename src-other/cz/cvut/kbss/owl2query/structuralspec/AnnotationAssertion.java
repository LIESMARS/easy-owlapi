package cz.cvut.kbss.owl2query.structuralspec;


public interface AnnotationAssertion extends AnnotationAxiom {

	public AnnotationProperty getAnnotationProperty();

	public AnnotationSubject getAnnotationSubject();
	
	public AnnotationValue getAnnotationValue();
}
