package cz.cvut.kbss.owl2query.owlapi.util;

import java.util.List;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;


public class ATermUtils {

	public static final ATermAppl TOP = null;
	public static final ATermAppl TOP_LIT = null;
	public static final ATermAppl BOTTOM = null;

	public static boolean isVar(ATermAppl subj) {
		throw new UnsupportedOperationException();
	}

	public static boolean isLiteral(ATermAppl term) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl makeValue(ATermAppl var) {
		throw new UnsupportedOperationException();
	}

	public static Object toString(ATermAppl a) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl makeVar(String varName) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl normalize(Object makeNot) {
		throw new UnsupportedOperationException();
	}

	public static boolean isComplexClass(ATermAppl argument) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl makeTermAppl(String uri) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl makeBnode(String string) {
		throw new UnsupportedOperationException();
	}











	public static ATermAppl makeAnd(List<ATermAppl> classParts) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl makeSomeValues(ATermAppl pred, String topLit) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl makeSomeValues(ATermAppl pred, ATermAppl top2) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl makeMin(ATermAppl pred, int i, String topLit) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl makeMin(ATermAppl pred, int i, ATermAppl top2) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl makeInv(ATermAppl aTermAppl) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl makeNot(ATermAppl testClass) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl makeHasValue(ATermAppl pt, ATermAppl ot) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl makeAllValues(ATermAppl pt, ATermAppl ot) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl makeMax(ATermAppl pt, int cardinality,
			ATermAppl top2) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl makeCard(ATermAppl pt, int cardinality,
			ATermAppl top2) {
		throw new UnsupportedOperationException();
	}

	public static ATermAppl makeOr(List<ATermAppl> result) {
		throw new UnsupportedOperationException();
	}

}
