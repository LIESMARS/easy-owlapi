package ex02;

import java.util.*;
import java.io.*;

import org.semanticweb.kaon2.api.*;
import org.semanticweb.kaon2.api.owl.elements.*;
import org.semanticweb.kaon2.api.owl.axioms.*;
import org.semanticweb.kaon2.api.formatting.*;

/**
 * This example demonstrates how to create and save an ontology. Please make sure
 * you understand Example 1 before you try to understand this example.
 * To understand this example, it is necessary to understand the syntax of OWL language.
 * A good source to learn OWL is the OWL Guide, available at http://www.w3.org/TR/owl-guide/.
 */
public class Example02 {
    public static void main(String[] args) throws Exception {
        // To create an ontology, we again start by creating a connection.
        // We again need to register a resolver that will provide a physical URI
        // for the ontology. In this example, the physical URI is relative to the current directory.
        OntologyManager ontologyManager=KAON2Manager.newOntologyManager();
        DefaultOntologyResolver resolver=new DefaultOntologyResolver();
        resolver.registerReplacement("http://kaon2.semanticweb.org/example02","file:example02.xml");
        ontologyManager.setOntologyResolver(resolver);

        // We create an ontology by specifying its logical URI. The resolver provides the physical URI.
        Ontology ontology=ontologyManager.createOntology("http://kaon2.semanticweb.org/example02",new HashMap<String,Object>());

        // An ontology can be viewed as a set of axioms. In fact, axioms are the only
        // types of objects that can be added or removed to an ontology. The types
        // of axioms supported by the API is determined by the OWL language.
        // We now create an axiom that states that '#cat' is a type of '#animal'.
        // Axioms are created by the factory, which can be obtained from KAON2Manager.
        OWLClass cat=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example02#cat");
        OWLClass animal=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example02#animal");
        SubClassOf catIsAnimal=KAON2Manager.factory().subClassOf(cat,animal);

        // Often one needs to perform a series of operations on an ontology. Hence, KAON2 supports
        // bulk updates: instead of adding axioms one-by-one, it is possible to construct a list of
        // changes, and send all of them to the ontology for processing. This may seem as an overkill
        // in small examples, such as this one, but has proven indispensable in larger applications.
        // Hence, we now construct a list of OntologyChangeEvent objects. Each OntologyChangeEvent
        // contains an axioms, and specifies whether the axiom should be added or removed from an ontology.
        List<OntologyChangeEvent> changes=new ArrayList<OntologyChangeEvent>();
        changes.add(new OntologyChangeEvent(catIsAnimal,OntologyChangeEvent.ChangeType.ADD));

        // We not apply the changes to the ontology. Notice that the following method call
        // will work, even though classes '#cat' and '#animal' are not a part of the ontology.
        // In fact, in KAON2 API, you don't need to 'declare' entities in the ontology before you use
        // them. On the contrary, an entity becomes a part of the ontology as soon as it is referenced
        // by some axiom. When the last axiom referencing an entity is removed from an ontology,
        // the entity becomes no longer a part of the ontology. This may seem counterintuitive at first:
        // it is a common practice to declare stuff before you use it. However, in the context of Semantic
        // Web, I found this difficult to enforce, especially due to ontology inclusion facility.
        // Hence, KAON2 does not require you to declare stuff before you use it, but leaves this decision
        // to the application.
        ontology.applyChanges(changes);

        // Typing in namespaces is tedious; for this, you can use the Namespaces object. This object
        // maintains a list of (prefix, namespace URI) pairs, and is used below to save me from typing too much.
        Namespaces namespaces=new Namespaces();
        namespaces.registerPrefix("example02","http://kaon2.semanticweb.org/example02#");

        // Creating axioms though object structures might be difficult. Hence, KAON2 has its internal
        // LISP-like syntax for axioms. This syntax is not yet documented, but you may take a look at
        // org.semanticweb.kaon2.memoryapi.test.OWL2XMLExporterTest for some hints.
        // The following axiom says that '#dog' is also a type of animal.
        SubClassOf dogIsAnimal=(SubClassOf)KAON2Manager.factory().axiom("[subClassOf example02:dog example02:animal]",namespaces);

        // We now add this axiom in the same way as before.
        changes.clear();
        changes.add(new OntologyChangeEvent(dogIsAnimal,OntologyChangeEvent.ChangeType.ADD));
        ontology.applyChanges(changes);

        // We now save the ontology by calling the serializer. Observe that the
        // location where the ontology is stored does not need to be the same
        // as the physical URI. This is deliberate, as this allows you to implement
        // 'Save As' operation. The second parameter defines the character encoding used
        // in the XML file. we save the ontology into 'c:\temp\example02.xml'.
        System.out.println("The ontology will be saved into 'c:\\temp\\example02.xml'.");
        System.out.println("Please ensure that 'c:\\temp' directory exists.");
        ontology.saveOntology(OntologyFileFormat.OWL_XML,new File("c:\\temp\\example02.xml"),"ISO-8859-1");

        // Don't forget to close the connection!
        ontologyManager.close();

        System.out.println("The ontology was saved successfully into 'c:\\temp\\example02.xml'.");
    }
}

