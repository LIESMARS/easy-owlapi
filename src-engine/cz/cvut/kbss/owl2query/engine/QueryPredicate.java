package cz.cvut.kbss.owl2query.engine;

/**
 * @author Petr Kremen
 */

public enum QueryPredicate {
	Type("T"), PropertyValue("PV"), SameAs("SA"), DifferentFrom("DF"), ObjectProperty("OP"), DatatypeProperty("DP"), SubClassOf("SCO"), EquivalentClass("EC"), DisjointWith("DW"), ComplementOf("CO"), EquivalentProperty("EP"), SubPropertyOf("SPO"), InverseOf("IO"), Annotation("A"),

	Functional("Fun"), InverseFunctional("IFun"), Transitive("Tr"), Symmetric("Sym"), Asymmetric("Asym"), Reflexive("Ref"), Irreflexive("Irr"),

	// SPARQL-DL non-monotonic extensions
	DirectType("DT"), StrictSubClassOf("SSCO"), DirectSubClassOf("DSCO"), DirectSubPropertyOf("DSPO"), StrictSubPropertyOf("SSPO"),

	// Negation as failure
	Not("N"),

	// Nested query support
	Core("C");

	private QueryPredicate() {
	}

	private String shortForm;
	
	private QueryPredicate(String shortForm) {
		this.shortForm = shortForm;
	}
	
	
	public String shortForm() {
		return shortForm;
	}
	
	@Override
	public String toString() {
		return name();
	}
}