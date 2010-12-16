package org.mindswap.pellet;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

public interface Role {

	Role getInverse();

	ATermAppl getName();

}
