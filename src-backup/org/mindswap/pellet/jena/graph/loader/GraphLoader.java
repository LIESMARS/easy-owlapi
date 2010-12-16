package org.mindswap.pellet.jena.graph.loader;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

import com.hp.hpl.jena.graph.Node;

public interface GraphLoader {

	ATermAppl node2term(Node value);

}
