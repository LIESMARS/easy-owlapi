package cz.cvut.kbss.owl2query.complexversion.model;

public interface Literal<L, DT, DR> extends AnnotationValue,
		LiteralTerm<L, DT, DR> {

	L getWrappedObject();

}
