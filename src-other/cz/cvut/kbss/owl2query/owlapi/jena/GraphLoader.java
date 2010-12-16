package cz.cvut.kbss.owl2query.owlapi.jena;


import com.hp.hpl.jena.graph.Node;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

public interface GraphLoader {

	ATermAppl node2term(Node value);

}
