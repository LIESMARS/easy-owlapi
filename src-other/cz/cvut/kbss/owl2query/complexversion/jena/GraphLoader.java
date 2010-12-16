package cz.cvut.kbss.owl2query.complexversion.jena;


import com.hp.hpl.jena.graph.Node;

import cz.cvut.kbss.owl2query.complexversion.model.Term;

public interface GraphLoader {

	Term node2term(Node value);

}
