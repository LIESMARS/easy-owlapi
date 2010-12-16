package ex10;

import java.util.Set;
import java.util.HashMap;

import org.semanticweb.kaon2.api.*;
import org.semanticweb.kaon2.api.owl.elements.*;
import org.semanticweb.kaon2.api.owl.axioms.*;
import org.semanticweb.kaon2.api.logic.*;
import org.semanticweb.kaon2.api.reasoner.*;

/**
 * This example shows how to use the metaviews framework. Before going through this example, you should read the
 * reference [9] from KAON2's Web site. 
 */
public class Example10 {
    public static void main(String[] args) throws Exception {
        // In OWL DL, annotations are metalogical information that is primarily intended for human users.
        // Sometimes, one might want to provide some tructure for annotations. One might, for example,
        // want to provide a hierarchy of annotation properties. In a way, one wants to give some logical
        // interpretation to metalogical information. Metaviews were devised for this purpose. They are
        // ontologies that reflect information about other ontologies. A metaview is generated from the
        // main ontology through a predefined transformation.
        //
        // Consider the ontology 'example10-ontology.xml', available in the source directory of this example.
        // It contains objects that are annotated using 'a:description' and 'a:name' annotation properties.
        // The values of annotations for both of these properties are strings. Hence, we might want to introduce a
        // new annotation property 'b:textAnnotation', and make 'a:description' and 'a:name' subproperties
        // of 'b:textAnnotation'. Our problem, however, is that we cannot do this in OWL DL: annotation properties
        // cannot occur in subproperty axioms.
        //
        // To capture this semantic relationship between annotations, we have to 'turn' annotation properties
        // into data properties. This is done in the entity annotation view: this is an ontology that contains
        // information about entities from another ontology. To see how this works, let us first register
        // the ontology that we want to examine.
        DefaultOntologyResolver resolver=new DefaultOntologyResolver();
        String ontologyURI=resolver.registerOntology("file:src/ex10/example10-ontology.xml");

        // The reason why we need to register the following ontology will be clear shortly -- please keep on reading. 
        String metaviewExtensionURI=resolver.registerOntology("file:src/ex10/example10-mw-ext.xml");

        // Let us now open the entity metaview of the ontology 'example10-ontology'. As described in
        // the paper [9], the entity metaviews have ontology URIs that are obtained from the URI
        // of the ontology by prefixing it with 'ent:'. Note that this 'ent:' is not a namespace
        // prefix; rather, it is just a dumb prefix that has been assigned to entity metaviews.
        // Hence, the following line computes the prefix of the entity metaview:
        String entityMetaviewOntologyURI="ent:"+ontologyURI;

        // This connection will contain the original ontology and all metaview-related information.
        OntologyManager ontologyManager=KAON2Manager.newOntologyManager();
        ontologyManager.setOntologyResolver(resolver);
        
        // We now open the entity metaview by simply asking for the mentioned URI.
        Ontology entityMetaview=ontologyManager.openOntology(entityMetaviewOntologyURI,new HashMap<String,Object>());

        // We'll use these namespaces to shorten the display of the results.
        Namespaces namespaces=new Namespaces();
        namespaces.registerPrefix("o",ontologyURI+"#");                                 // o: stands for the original ontology
        namespaces.registerPrefix("e",metaviewExtensionURI+"#");                        // e: stands for the the metaview extension
        namespaces.registerPrefix("ent","http://www.cs.man.ac.uk/EntityMetaview#");     // read on for the meaning of ent:
        
        // The contents of this ontology describes the original ontology.
        // the data properties in the ontology correspond to the annotation properties of the original ontology.
        System.out.println(" Data properties in the entity metaview:");
        System.out.println("-------------------------------------------");
        printEntityCursor(namespaces,entityMetaview.createEntityRequest(DataProperty.class).openCursor());

        // The facts in the entity metaview describe the annotations of the original ontology.
        System.out.println(" Facts in the entity metaview:");
        System.out.println("-------------------------------------------");
        printAxiomCursor(namespaces,entityMetaview.createAxiomRequest(Fact.class).openCursor());

        // Remember that our goal was to add structure to the annotations of 'example10-ontology.xml'.
        // Therefore, we created the ontology 'example10-mw-ext.xml' that describes the desired relationships.
        // Let us open this ontology. The ontology URI of this ontology can be chosen freely (i.e., the metaview
        // framework does not proscribe it); hence, we use the URI returned by the resolver.
        Ontology metaviewExtension=ontologyManager.openOntology(metaviewExtensionURI,new HashMap<String,Object>());

        // Note that, in the entity extension, annotation properties from the original ontology
        // are treated as data properties, just like in the entity metaview.
        System.out.println(" The axioms in the metaview extension:");
        System.out.println("-------------------------------------------");
        printAxiomCursor(namespaces,metaviewExtension.createAxiomRequest().openCursor());

        // We need to ensure that the entity metaview imports our metaview extension.
        // Therefore, the original ontology contains an ontology property, which says to
        // the metaview transformation that the entity metaview must import some other ontology.
        // All ontologies whose URIs are listed as values of the ontology property
        // 'http://www.cs.man.ac.uk/EntityMetaview#import' will be imported.
        Ontology ontology=ontologyManager.openOntology(ontologyURI,new HashMap<String,Object>());
        Set<Annotation> annotations=ontology.getAnnotations();
        System.out.println(" The original ontology says the entity metaview shoud import ontologies with these URIs:");
        System.out.println("-------------------------------------------");
        for (Annotation annotation : annotations)
            if ("http://www.cs.man.ac.uk/EntityMetaview#import".equals(annotation.getAnnotationProperty().getURI()))
            System.out.println(((AnnotationByConstant)annotation).getAnnotationValue().getValue());
        System.out.println("-------------------------------------------");
        System.out.println();

        // Let us examine what does the entity metaview really import.
        Set<Ontology> importedIntoEntityMetaview=entityMetaview.getImportedOntologies();
        System.out.println(" These ontologies are imported into entity metaview:");
        System.out.println("-------------------------------------------");
        for (Ontology importedOntology : importedIntoEntityMetaview)
            System.out.println(importedOntology.getOntologyURI());
        System.out.println("-------------------------------------------");
        System.out.println();
        // You should note that the entity metaview imports our extension ontology, but it also
        // imports an ontology with the URI 'http://www.cs.man.ac.uk/EntityMetaview'. This is
        // a predefined well-known ontology that describes the general structure of entity metaviews.
        // Please see [9] for a more detailed description of the latter ontology.
        
        // Phew, this was a lot of work. Let us now see what we can do using the metaviews.
        // For example, we can ask a query for all textual annotations. Because the entity
        // metaview imports 'example10-mw-ext.xml', our query will obey the semantics specified
        // in that ontology.
        System.out.println(" The list of textual annotations from the original ontology:");
        System.out.println("-------------------------------------------");
        executeQuery(namespaces,entityMetaview,"SELECT ?e ?a WHERE { ?e <http://kaon2.semanticweb.org/example10-mw-ext#textAnnotation> ?a }");

        // Note that metaviews are immutable ontologies. They will, however, change
        // automatically if you change the original ontology. Let us add an annotation to
        // the original ontology.
        OWLClass adam=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example10-ontology#Adam");
        AnnotationProperty description=KAON2Manager.factory().annotationProperty("http://kaon2.semanticweb.org/example10-ontology#description");
        EntityAnnotation newEntityAnnotation=KAON2Manager.factory().entityAnnotation(description,adam,KAON2Manager.factory().constant("He was sooo gullible!"));
        ontology.addAxiom(newEntityAnnotation);
        // Note the change in the result of the same query.
        System.out.println(" The list of textual annotations from the original ontology after the otnolgy was changed:");
        System.out.println("-------------------------------------------");
        executeQuery(namespaces,entityMetaview,"SELECT ?e ?a WHERE { ?e <http://kaon2.semanticweb.org/example10-mw-ext#textAnnotation> ?a }");

        // Finally, we clean-up before we exit.
        ontologyManager.close();
        
        // Apart from the entity metaview, KAON2 also supports the axiom metaview, which contains information about
        // axioms from an ontology. The basic principles for working with that metaview are the same as for the
        // entity metaview. Therefore, there is no example that explicitly explains how to use the axiom metaview.
        // Please refer to [9] for more information.
    }
    protected static <T extends OWLEntity> void printEntityCursor(Namespaces namespaces,Cursor<T> cursor) throws KAON2Exception {
        try {
            while (cursor.hasNext()) {
                T element=cursor.next();
                StringBuffer buffer=new StringBuffer();
                element.toString(buffer,namespaces);
                System.out.println(buffer.toString());
            }
            System.out.println("-------------------------------------------");
            System.out.println();
        }
        finally {
            cursor.close();
        }
    }
    protected static <T extends Axiom> void printAxiomCursor(Namespaces namespaces,Cursor<T> cursor) throws KAON2Exception {
        try {
            while (cursor.hasNext()) {
                T element=cursor.next();
                StringBuffer buffer=new StringBuffer();
                element.toString(buffer,namespaces);
                System.out.println(buffer.toString());
            }
            System.out.println("-------------------------------------------");
            System.out.println();
        }
        finally {
            cursor.close();
        }
    }
    protected static void executeQuery(Namespaces namespaces,Ontology ontology,String queryText) throws KAON2Exception,InterruptedException {
        Reasoner reasoner=ontology.createReasoner();
        try {
            Query query=reasoner.createQuery(namespaces,queryText);
            try {
                query.open();
                Term[] tupleBuffer=query.tupleBuffer();
                while (!query.afterLast()) {
                    System.out.print("[ ");
                    for (int i=0;i<tupleBuffer.length;i++) {
                        if (i>0)
                            System.out.print(", ");
                        StringBuffer buffer=new StringBuffer();
                        tupleBuffer[i].toString(buffer,namespaces);
                        System.out.print(buffer.toString());
                    }
                    System.out.println(" ]");
                    query.next();
                }
                query.close();
                System.out.println("-------------------------------------------");
                System.out.println();
            }
            finally {
                query.dispose();
            }
        }
        finally {
            reasoner.dispose();
        }
    }
}
