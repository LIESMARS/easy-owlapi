package cz.cvut.kbss.owl2query.complexversion.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class ObjectPropertyAssertionAtomImpl<OPE, NI> extends AbstractQueryAtom
		implements ObjectPropertyAssertionAtom<OPE, NI> {

	final ObjectPropertyTerm<OPE> property;
	final NamedIndividualTerm<NI> subject;
	final NamedIndividualTerm<NI> object;

	ObjectPropertyAssertionAtomImpl(
			final ObjectPropertyTerm<OPE> property,
			final NamedIndividualTerm<NI> subject,
			final NamedIndividualTerm<NI> object) {
		super(QueryPredicate.ObjectPropertyAssertionAtom);
		this.property = property;
		this.subject = subject;
		this.object = object;
	}

	@Override
	public ObjectPropertyTerm<OPE> getPropertyTerm() {
		return property;
	}

	@Override
	public NamedIndividualTerm<NI> getSubjectTerm() {
		return subject;
	}

	@Override
	public NamedIndividualTerm<NI> getObjectTerm() {
		return object;
	}

	@Override
	public List<Term> getTerms() {
		return Collections.unmodifiableList(Arrays.asList(property, subject,
				object));
	}
}
