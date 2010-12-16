package cz.cvut.kbss.owl2query.complexversion.util;

import java.util.List;

import cz.cvut.kbss.owl2query.complexversion.model.Term;

public class ATermUtils {

	public static final Term TOP = null;
	public static final Term TOP_LIT = null;
	public static final Term BOTTOM = null;

	public static boolean isVar(Term subj) {
		throw new UnsupportedOperationException();
	}

	public static boolean isLiteral(Term term) {
		throw new UnsupportedOperationException();
	}

	public static Term makeValue(Term var) {
		throw new UnsupportedOperationException();
	}

	public static Object toString(Term a) {
		throw new UnsupportedOperationException();
	}

	public static Term makeVar(String varName) {
		throw new UnsupportedOperationException();
	}

	public static Term normalize(Object makeNot) {
		throw new UnsupportedOperationException();
	}

	public static boolean isComplexClass(Term argument) {
		throw new UnsupportedOperationException();
	}

	public static Term makeTermAppl(String uri) {
		throw new UnsupportedOperationException();
	}

	public static Term makeBnode(String string) {
		throw new UnsupportedOperationException();
	}

	public static Term makeAnd(List<Term> classParts) {
		throw new UnsupportedOperationException();
	}

	public static Term makeSomeValues(Term pred, String topLit) {
		throw new UnsupportedOperationException();
	}

	public static Term makeSomeValues(Term pred, Term top2) {
		throw new UnsupportedOperationException();
	}

	public static Term makeMin(Term pred, int i, String topLit) {
		throw new UnsupportedOperationException();
	}

	public static Term makeMin(Term pred, int i, Term top2) {
		throw new UnsupportedOperationException();
	}

	public static Term makeInv(Term Term) {
		throw new UnsupportedOperationException();
	}

	public static Term makeNot(Term testClass) {
		throw new UnsupportedOperationException();
	}

	public static Term makeHasValue(Term pt, Term ot) {
		throw new UnsupportedOperationException();
	}

	public static Term makeAllValues(Term pt, Term ot) {
		throw new UnsupportedOperationException();
	}

	public static Term makeMax(Term pt, int cardinality, Term top2) {
		throw new UnsupportedOperationException();
	}

	public static Term makeCard(Term pt, int cardinality, Term top2) {
		throw new UnsupportedOperationException();
	}

	public static Term makeOr(List<Term> result) {
		throw new UnsupportedOperationException();
	}

}
