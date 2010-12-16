package cz.cvut.kbss.owl2query.simpleversion.engine;

import java.util.LinkedHashMap;
import java.util.Map;

import cz.cvut.kbss.owl2query.simpleversion.model.GroundTerm;
import cz.cvut.kbss.owl2query.simpleversion.model.ResultBinding;
import cz.cvut.kbss.owl2query.simpleversion.model.Variable;

@SuppressWarnings("serial")
class ResultBindingImpl<G> extends LinkedHashMap<Variable<G>, GroundTerm<G>>
		implements ResultBinding<G> {

	ResultBindingImpl() {
	}

	ResultBindingImpl(final Map<Variable<G>, GroundTerm<G>> bindings) {
		super(bindings);
	}

	@Override
	public ResultBinding<G> clone() {
		return new ResultBindingImpl<G>(this);
	}
}
