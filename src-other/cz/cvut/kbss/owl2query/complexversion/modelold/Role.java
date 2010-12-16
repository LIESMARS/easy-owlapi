package cz.cvut.kbss.owl2query.complexversion.modelold;

import cz.cvut.kbss.owl2query.complexversion.model.Term;


public interface Role {

	Role getInverse();

	Term getName();

}
