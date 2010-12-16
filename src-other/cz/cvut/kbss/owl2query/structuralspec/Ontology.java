package cz.cvut.kbss.owl2query.structuralspec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface Ontology {

	public Set<Ontology> getDirectlyImportedOntologies();

	public Set<Ontology> getImportedOntologies();

	public Set<IRI> getDirectlyImportedDocuments();
	
	public IRI getVersionIRI();

	public IRI getOntologyIRI();
	
	public Set<Annotation> getOntologyAnnotations();

	public Set<Axiom> getAxioms();
	
	class OntologyImpl implements Ontology {

		enum AxiomType {
			Declaration;
		}
		
		final Map<AxiomType,Set<? extends Axiom>> axioms = new HashMap<AxiomType, Set<? extends Axiom>>();
				
		public OntologyImpl() {
			final Set<Declaration> a = new HashSet<Declaration>();
			
			a.add(new Declaration.DeclarationImpl(Class.OWL_THING));
			a.add(new Declaration.DeclarationImpl(Class.OWL_NOTHING));
			
			a.add(new Declaration.DeclarationImpl(ObjectProperty.OWL_TOP_OBJECT_PROPERTY));
			a.add(new Declaration.DeclarationImpl(ObjectProperty.OWL_BOTTOM_OBJECT_PROPERTY));
			
			a.add(new Declaration.DeclarationImpl(DataProperty.OWL_TOP_DATA_PROPERTY));
			a.add(new Declaration.DeclarationImpl(DataProperty.OWL_BOTTOM_DATA_PROPERTY));
			
			a.add(new Declaration.DeclarationImpl(Datatype.RDFS_LITERAL));
			
			a.add(new Declaration.DeclarationImpl(Datatype.OWL_RATIONAL));
			a.add(new Declaration.DeclarationImpl(Datatype.OWL_REAL));
			a.add(new Declaration.DeclarationImpl(Datatype.RDF_PLAINLITERAL));
			a.add(new Declaration.DeclarationImpl(Datatype.RDF_XML_LITERAL));
			a.add(new Declaration.DeclarationImpl(Datatype.RDFS_LITERAL));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_BASE_64_BINARY));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_BYTE));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_DATE_TIME));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_DATE_TIME_STAMP));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_DECIMAL));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_DOUBLE));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_FLOAT));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_HEX_BINARY));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_INT));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_INTEGER));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_LANGUAGE));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_LONG));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_NAME));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_NCNAME));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_NEGATIVE_INTEGER));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_NON_NEGATIVE_INTEGER));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_NON_POSITIVE_INTEGER));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_NORMALIZED_STRING));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_POSITIVE_INTEGER));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_SHORT));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_STRING));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_TOKEN));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_UNSIGNED_BYTE));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_UNSIGNED_INT));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_UNSIGNED_LONG));
			a.add(new Declaration.DeclarationImpl(Datatype.XSD_UNSIGNED_SHORT));
			a.add(new Declaration.DeclarationImpl(Datatype.xSD_NMTOKEN));

			a.add(new Declaration.DeclarationImpl(AnnotationProperty.OWL_BACKWARD_COMPATIBLE_WITH));
			a.add(new Declaration.DeclarationImpl(AnnotationProperty.OWL_INCOMPATIBLE_WITH));
			a.add(new Declaration.DeclarationImpl(AnnotationProperty.OWL_PRIOR_VERSION));
			a.add(new Declaration.DeclarationImpl(AnnotationProperty.OWL_VERSION_INFO));
			a.add(new Declaration.DeclarationImpl(AnnotationProperty.RDFS_COMMENT));
			a.add(new Declaration.DeclarationImpl(AnnotationProperty.RDFS_DEPRECATED));
			a.add(new Declaration.DeclarationImpl(AnnotationProperty.RDFS_IS_DEFINED_BY));
			a.add(new Declaration.DeclarationImpl(AnnotationProperty.RDFS_LABEL));
			a.add(new Declaration.DeclarationImpl(AnnotationProperty.RDFS_SEE_ALSO));
			
			axioms.put(AxiomType.Declaration, a);
		}
		
		@Override
		public Set<Axiom> getAxioms() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<IRI> getDirectlyImportedDocuments() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<Ontology> getDirectlyImportedOntologies() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<Ontology> getImportedOntologies() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<Annotation> getOntologyAnnotations() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IRI getOntologyIRI() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IRI getVersionIRI() {
			// TODO Auto-generated method stub
			return null;
		}
		
		
	}
}
