package cz.cvut.kbss.owl2query.owlapi.jena;


import cz.cvut.kbss.owl2query.owlapi.model.KnowledgeBase;

public interface PelletInfGraph {

	void prepare();

	KnowledgeBase getKB();

	GraphLoader getLoader();
}
