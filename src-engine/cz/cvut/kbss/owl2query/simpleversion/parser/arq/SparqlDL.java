package cz.cvut.kbss.owl2query.simpleversion.parser.arq;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class SparqlDL {

	public static final String sdleBase = "http://pellet.owldl.com/ns/sdle#";

	public static final String sdleNS = "sdle";

	// SPARQL-DL extensions
	public static final Property strictSubClassOf = ResourceFactory
			.createProperty(sdleBase + "strictSubClassOf");

	public static final Property directSubClassOf = ResourceFactory
			.createProperty(sdleBase + "directSubClassOf");

	public static final Property directSubPropertyOf = ResourceFactory
			.createProperty(sdleBase + "directSubPropertyOf");

	public static final Property strictSubPropertyOf = ResourceFactory
			.createProperty(sdleBase + "strictSubPropertyOf");

	public static final Property directType = ResourceFactory
			.createProperty(sdleBase + "directType");
}
