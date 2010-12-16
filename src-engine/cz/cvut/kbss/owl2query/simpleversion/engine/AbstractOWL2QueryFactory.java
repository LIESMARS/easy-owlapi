package cz.cvut.kbss.owl2query.simpleversion.engine;

import cz.cvut.kbss.owl2query.simpleversion.model.GroundTerm;
import cz.cvut.kbss.owl2query.simpleversion.model.OWL2Ontology;
import cz.cvut.kbss.owl2query.simpleversion.model.OWL2QueryFactory;
import cz.cvut.kbss.owl2query.simpleversion.model.OWL2Query;
import cz.cvut.kbss.owl2query.simpleversion.model.Variable;

public abstract class AbstractOWL2QueryFactory<G> implements OWL2QueryFactory<G> {

	public OWL2Query<G> createQuery(final OWL2Ontology<G> o) {
		return new QueryImpl<G>(o);
	}

	// TERMS
	// variable
	public Variable<G> variable(String name) {
		return new VariableImpl<G>(name);
	}

	public GroundTerm<G> wrap(final G gt) {
		return new GroundTermImpl<G>(gt);
	}
}