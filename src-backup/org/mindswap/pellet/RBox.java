package org.mindswap.pellet;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

public interface RBox {

	Role getRole(ATermAppl pred);

}
