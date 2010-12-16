package cz.cvut.kbss.owl2query.complexversion.jena;


import cz.cvut.kbss.owl2query.complexversion.modelold.KnowledgeBase;

public interface PelletInfGraph {

	void prepare();

	KnowledgeBase getKB();

	GraphLoader getLoader();
}
