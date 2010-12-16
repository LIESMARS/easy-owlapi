package org.mindswap.pellet.jena;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.jena.graph.loader.GraphLoader;

public interface PelletInfGraph {

	void prepare();

	KnowledgeBase getKB();

	GraphLoader getLoader();

}
