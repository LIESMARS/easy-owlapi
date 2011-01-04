package cz.cvut.kbss.owl2query.parser;

import java.io.Writer;

import cz.cvut.kbss.owl2query.model.OWL2Ontology;
import cz.cvut.kbss.owl2query.model.OWL2Query;

public interface QueryWriter<G> {
	
	public void write(final OWL2Query<G> queryString, final Writer os, final OWL2Ontology<G> kb);

}
