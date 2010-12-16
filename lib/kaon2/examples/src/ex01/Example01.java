package ex01;

import java.util.*;

import org.semanticweb.kaon2.api.*;                 // This package contains the basic classes of the API
import org.semanticweb.kaon2.api.owl.elements.*;    // This package contains classes used to represent elements of OWL ontologies


/**
 * This example shows how to load an ontology and print subclasses of some classes.
 * To understand this example, it is necessary to understand the syntax of OWL language.
 * A good source to learn OWL is the OWL Guide, available at http://www.w3.org/TR/owl-guide/.
 */
public class Example01 {
    /**
     * The entry point to this example. In order not to make this example too complicated,
     * we neglect error handling, and allow this method to end with an exception.
     */
    public static void main(String[] args) throws Exception {
        // The first thing we have to do is to obtain a OntologyManager.
        // A OntologyManager is then used to open and manipulate ontologies.
        // OntologyManager is important for ontology inclusion: in a graph of ontologies,
        // each of which imports other ontologies, an ontologies can be imported "multiple times".
        // However, within a single OntologyManager, each ontology exists only once.
        // Hence, a OntologyManager provides a context for ontologies.
        OntologyManager ontologyManager=KAON2Manager.newOntologyManager();

        // In OWL, each ontology is identified by its URI, such as 'http://kaon2.semanticweb.org/example01'.
        // However, such an ontology does not need to be physically located at the host 'kaon2.semanticweb.org'.
        // In our example, the ontology is located in the src/ex1 directory. KAON2 can open this ontology,
        // but the logical URI 'http://kaon2.semanticweb.org/example01' has to be translated into
        // a physical URI 'file:src/ex01/example01.xml' (notice we use a relative URI which is valid if you start
        // the example from 'examples' directory in KAON2 distribution). URI translation can be performed
        // through an 'OntologyResolver'. This is an interface which provides callbacks for URI
        // translation. A simple default implementation allows registering several (logical-URI,physical-URI) pairs.
        // We now create and initialize an ontology resolver.
        DefaultOntologyResolver resolver=new DefaultOntologyResolver();
        resolver.registerReplacement("http://kaon2.semanticweb.org/example01","file:src/ex01/example01.xml");

        // Our ontology includes another ontology, so we need to register the physical URI of the included ontology as well.
        resolver.registerReplacement("http://kaon2.semanticweb.org/example01-inc","file:src/ex01/example01-inc.xml");

        // We now register the resolver to the connection.
        ontologyManager.setOntologyResolver(resolver);

        // We are now ready to open an ontology. In doing so, we just specify the logical URI of the ontology;
        // the resolver will take care of finding ontologies in appropriate locations on the local drive.
        // Depending on the type of ontology storage, an ontology might require various parameters, such
        // as a password or communication information. Such parameters may be passed in the map.
        // Ontologies stored on a local drive do not require special parameters, so we may pass an empty map.
        Ontology ontology=ontologyManager.openOntology("http://kaon2.semanticweb.org/example01",new HashMap<String,Object>());

        // Our ontology contains a class called "http://kaon2.semanticweb.org/example01#document".
        // (In the rest the prefix "http://kaon2.semanticweb.org/example01" shall be omitted.)
        // Our goal now is to read its subclasses. The first thing is to obtain an OWLClass object
        // representing "#document" class from the ontology. We do this by going through a factory,
        // which can be obtained by the KAON2Manager.
        OWLClass document=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example01#document");

        // We can now ask the document object to return all subclasses. In fact, we get a set of
        // Description objects. Remember that in OWL, apart from classes, you can build complex description classes.
        Set<Description> subDescriptions=document.getSubDescriptions(ontology);

        // We can now iterate over this set. We filter out atomic classes by checking for
        // each description if it is an instance of OWLClass.
        // Note that this will include subclasses defined in 'example01', as well as in 'example01-inc' ontology.
        System.out.println("The subclasses of '"+document.getURI()+"' are:");
        for (Description subDescription : subDescriptions)
            if (subDescription instanceof OWLClass) {
                OWLClass subClass=(OWLClass)subDescription;
                System.out.println("    "+subClass.getURI());
            }
        System.out.println();

        // Other objects from the API also contain various methods for browsing the ontology.
        // The structure of these methods follows closely the syntactic structure of OWL ontologies.
        // Please take a look at the Javadoc to familiarize yourself with these methods.
        ObjectProperty isAuthor=KAON2Manager.factory().objectProperty("http://kaon2.semanticweb.org/example01#is-author");
        System.out.println("The domain classes of the '"+isAuthor.getURI()+"' object property are:");
        for (Description description : isAuthor.getDomainDescriptions(ontology))
            if (description instanceof OWLClass) {
                OWLClass domain=(OWLClass)description;
                System.out.println("    "+domain.getURI());
            }
        System.out.println();

        OWLClass person=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example01#person");
        AnnotationProperty labelProperty=KAON2Manager.factory().annotationProperty("http://www.w3.org/2000/01/rdf-schema#label");
        System.out.println("The label of '"+person.getURI()+"' is '"+person.getEntityAnnotationValue(ontology,labelProperty)+"'.");
        System.out.println();

        // As you might have noticed, all retrieval operations require an ontology as the first parameter.
        // This parameter determines the ontology in which the retrieval is done. To demonstrate the difference,
        // we shall now try to read all subclasses of '#document' in 'http://kaon2.semanticweb.org/example01-inc'
        // ontology. The first thing we need to do is obtain the ontology object from OntologyManager.
        // We can do this using openOntology() method. Since 'http://kaon2.semanticweb.org/example01' includes
        // 'http://kaon2.semanticweb.org/example01-inc', this ontology is already in-memory, and will not be parsed again.
        // Still, we need to pass in the empty parameter map.
        Ontology includedOntology=ontologyManager.openOntology("http://kaon2.semanticweb.org/example01-inc",new HashMap<String,Object>());

        // We retrieve subclasses of '#document' in 'http://kaon2.semanticweb.org/example01-inc' in the same way as before.
        // The output is different, since it is restricted to axioms contained exclusively in
        // 'http://kaon2.semanticweb.org/example01-inc' ontology.
        System.out.println("The subclasses of '"+document.getURI()+"' in the inculded ontology are:");
        subDescriptions=document.getSubDescriptions(includedOntology);
        for (Description subDescription : subDescriptions)
            if (subDescription instanceof OWLClass) {
                OWLClass subClass=(OWLClass)subDescription;
                System.out.println("    "+subClass.getURI());
            }
        System.out.println();

        // After usage, we need to close the connection. Otherwise, we risk a resource leak.
        ontologyManager.close();
    }
}
