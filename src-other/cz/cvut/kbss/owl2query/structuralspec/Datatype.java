package cz.cvut.kbss.owl2query.structuralspec;


/**
 * Arity of Datatype must be one.
 */
public interface Datatype extends Entity, DataRange {

	static Datatype RDFS_LITERAL = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.RDFS + "Literal"));

	static Datatype OWL_REAL = new DatatypeImpl(new IRI.IRIImpl(Namespaces.OWL
			+ "real"));
	static Datatype OWL_RATIONAL = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.OWL + "rational"));

	static Datatype XSD_DECIMAL = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "decimal"));
	static Datatype XSD_INTEGER = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "integer"));
	static Datatype XSD_NON_NEGATIVE_INTEGER = new DatatypeImpl(
			new IRI.IRIImpl(Namespaces.XSD + "nonNegativeInteger"));
	static Datatype XSD_NON_POSITIVE_INTEGER = new DatatypeImpl(
			new IRI.IRIImpl(Namespaces.XSD + "nonPositiveInteger"));
	static Datatype XSD_NEGATIVE_INTEGER = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "negativeInteger"));
	static Datatype XSD_POSITIVE_INTEGER = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "positiveInteger"));
	static Datatype XSD_LONG = new DatatypeImpl(new IRI.IRIImpl(Namespaces.XSD
			+ "long"));
	static Datatype XSD_INT = new DatatypeImpl(new IRI.IRIImpl(Namespaces.XSD
			+ "int"));
	static Datatype XSD_SHORT = new DatatypeImpl(new IRI.IRIImpl(Namespaces.XSD
			+ "short"));
	static Datatype XSD_BYTE = new DatatypeImpl(new IRI.IRIImpl(Namespaces.XSD
			+ "byte"));
	static Datatype XSD_UNSIGNED_LONG = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "unsignedLong"));
	static Datatype XSD_UNSIGNED_INT = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "unsignedInt"));
	static Datatype XSD_UNSIGNED_SHORT = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "unsignedShort"));
	static Datatype XSD_UNSIGNED_BYTE = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "unsignedByte"));

	static Datatype XSD_DOUBLE = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "double"));
	static Datatype XSD_FLOAT = new DatatypeImpl(new IRI.IRIImpl(Namespaces.XSD
			+ "float"));

	static Datatype RDF_PLAINLITERAL = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.RDF + "PlainLiteral"));
	static Datatype XSD_STRING = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "string"));
	static Datatype XSD_NORMALIZED_STRING = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "normalizedString"));
	static Datatype XSD_TOKEN = new DatatypeImpl(new IRI.IRIImpl(Namespaces.XSD
			+ "token"));
	static Datatype XSD_LANGUAGE = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "language"));
	static Datatype XSD_NAME = new DatatypeImpl(new IRI.IRIImpl(Namespaces.XSD
			+ "Name"));
	static Datatype XSD_NCNAME = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "NCName"));
	static Datatype xSD_NMTOKEN = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "NMTOKEN"));

	static Datatype XSD_HEX_BINARY = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "hexBinary"));
	static Datatype XSD_BASE_64_BINARY = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "base64Binary"));

	static Datatype XSD_DATE_TIME = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "dateTime"));
	static Datatype XSD_DATE_TIME_STAMP = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.XSD + "dateTimeStamp"));

	static Datatype RDF_XML_LITERAL = new DatatypeImpl(new IRI.IRIImpl(
			Namespaces.RDF + "XMLLiteral"));

	class DatatypeImpl implements Datatype {

		IRI iri;

		DatatypeImpl(IRI iri) {
			this.iri = iri;
		}

		@Override
		public IRI getEntityIRI() {
			return iri;
		}

		@Override
		public int getArity() {
			return 1;
		}

	}

}
