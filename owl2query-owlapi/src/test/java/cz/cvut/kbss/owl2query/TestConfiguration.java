package cz.cvut.kbss.owl2query;

import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class TestConfiguration {
	
	public static OWLReasonerFactory FACTORY;
	
	static {
		try {
			FACTORY = (OWLReasonerFactory) Class.forName("com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory").newInstance();
		} catch ( Exception e) {			
			e.printStackTrace();
			FACTORY = null;
		}
	}
}
