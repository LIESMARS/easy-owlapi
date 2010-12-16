package cz.cvut.kbss.owl2query.structuralspec;

public interface Literal extends AnnotationValue {
	
	String getLexicalForm();
	
	Datatype getDatatype();
	
}
