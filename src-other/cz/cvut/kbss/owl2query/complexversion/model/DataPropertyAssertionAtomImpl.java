package cz.cvut.kbss.owl2query.complexversion.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class DataPropertyAssertionAtomImpl<DPE, NI, L, DT, DR> extends
		AbstractQueryAtom implements
		DataPropertyAssertionAtom<DPE, NI, L, DT, DR> {

	final DataPropertyTerm<DPE> property;
	final NamedIndividualTerm<NI> subject;
	final LiteralTerm<L, DT, DR> object;

	DataPropertyAssertionAtomImpl(final DataPropertyTerm<DPE> property,
			final NamedIndividualTerm<NI> subject,
			final LiteralTerm<L, DT, DR> object) {
		super(QueryPredicate.DataPropertyAssertionAtom);

		this.property = property;
		this.subject = subject;
		this.object = object;
	}

	@Override
	public DataPropertyTerm<DPE> getPropertyTerm() {
		return property;
	}

	@Override
	public NamedIndividualTerm<NI> getSubjectTerm() {
		return subject;
	}

	@Override
	public LiteralTerm<L, DT, DR> getObjectTerm() {
		return object;
	}

	@Override
	public List<Term> getTerms() {
		return Collections.unmodifiableList(Arrays.asList(property, subject,
				object));
	}
}
