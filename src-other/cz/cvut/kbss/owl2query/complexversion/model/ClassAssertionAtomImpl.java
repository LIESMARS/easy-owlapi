package cz.cvut.kbss.owl2query.complexversion.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class ClassAssertionAtomImpl<CE, NI> extends AbstractQueryAtom implements
		ClassAssertionAtom<CE, NI> {

	final ClassTerm<CE> ce;
	final NamedIndividualTerm<NI> ni;

	ClassAssertionAtomImpl(final ClassTerm<CE> ce,
			final NamedIndividualTerm<NI> ni) {
		super(QueryPredicate.ClassAssertionAtom);

		this.ce = ce;
		this.ni = ni;
	}

	@Override
	public ClassTerm<CE> getClassTerm() {
		return ce;
	}

	@Override
	public NamedIndividualTerm<NI> getIndividualTerm() {
		return ni;
	}

	public List<Term> getTerms() {
		return Collections.unmodifiableList(Arrays.asList(ce, ni));
	}

}
