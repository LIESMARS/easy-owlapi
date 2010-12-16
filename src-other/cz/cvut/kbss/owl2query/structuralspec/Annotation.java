package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Set;

public interface Annotation {
	
	Set<Annotation> getAnnotationAnnotations();

	AnnotationValue getAnnotationValue();

	AnnotationProperty getAnnotationProperty();
}
