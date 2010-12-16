package cz.cvut.kbss.owl2query.complexversion.model;

// analogous to blank nodes in RDF
public interface AnonymousIndividual extends Individual, AnnotationValue, AnnotationSubject {
	
	String getNodeID();
	
}
