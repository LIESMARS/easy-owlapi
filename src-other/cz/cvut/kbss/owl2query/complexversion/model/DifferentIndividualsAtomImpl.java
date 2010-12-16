package cz.cvut.kbss.owl2query.complexversion.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class DifferentIndividualsAtomImpl<NI> extends AbstractQueryAtom implements
		DifferentIndividualsAtom<NI> {

	final NamedIndividualTerm<NI> i1;
	final NamedIndividualTerm<NI> i2;

	DifferentIndividualsAtomImpl(final NamedIndividualTerm<NI> i1,
			final NamedIndividualTerm<NI> i2) {
		super(QueryPredicate.DifferentIndividualsAtom);

		this.i1 = i1;
		this.i2 = i2;
	}

	@Override
	public NamedIndividualTerm<NI> getNamedIndividualTerm1() {
		return i1;
	}

	@Override
	public NamedIndividualTerm<NI> getNamedIndividualTerm2() {
		return i2;
	}

	@Override
	public List<Term> getTerms() {
		return Collections.unmodifiableList(Arrays.asList((Term) i1, i2));
	}
}
