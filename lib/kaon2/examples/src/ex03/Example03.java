package ex03;

import java.util.*;

import org.semanticweb.kaon2.api.*;
import org.semanticweb.kaon2.api.owl.elements.*;
import org.semanticweb.kaon2.api.owl.axioms.*;

/**
 * This example demonstrates how to retrieve elements in an ontology. Please make sure
 * you understand Example 1 before you try to understand this example.
 * To understand this example, it is necessary to understand the syntax of OWL language.
 * A good source to learn OWL is the OWL Guide, available at http://www.w3.org/TR/owl-guide/.
 */
public class Example03 {
    /**
     * For details on error handling, please consult Javadoc to see the types
     * of exceptions that can be thrown by various methods.
     */
    public static void main(String[] args) throws Exception {
        // We now show some more advanced aspects of retreving elements from an ontology.
        // We first open the ontology from Example 1.
        OntologyManager ontologyManager=KAON2Manager.newOntologyManager();
        DefaultOntologyResolver resolver=new DefaultOntologyResolver();
        resolver.registerReplacement("http://kaon2.semanticweb.org/example01","file:src/ex01/example01.xml");
        resolver.registerReplacement("http://kaon2.semanticweb.org/example01-inc","file:src/ex01/example01-inc.xml");
        ontologyManager.setOntologyResolver(resolver);
        Ontology ontology=ontologyManager.openOntology("http://kaon2.semanticweb.org/example01",new HashMap<String,Object>());

        // In Example 1, the ontology was browsed by means of utility methods, located on
        // various API classes, such as OWLClass.getSubDescriptions(). This is a very
        // simple API, to be used only for very simple applications.
        // In reality, ontologies can be large. Hence, calling a method which e.g. returns all
        // individuals of a class might be a problem, if there are many individuals.
        // To remedy that, KAON2 provides a special optimized retrieval API, which we discuss next.
        // This API allows to read ontology elements successively, in batches. Hence, if an application
        // sees that there are too many potential results, it can always stop the retrieval.

        // All retrieval is done by means of the Request<T> class. You create a request object
        // in the following way:
        Request<Axiom> axiomRequest=ontology.createAxiomRequest();

        // You can see how many axioms the ontology contains in the following way.
        System.out.println("There are "+axiomRequest.sizeAll()+" axioms in 'example01' ontology , including all included ontologies.");

        // You might also want to know how many axioms are only in 'example01', without
        // taking into account axioms which the ontology 'inherits' through inclusion.
        // You do this using Request<T>.size() method. In fact, each retrieval operation has
        // two methods: one which takes information only into this ontology into account (and has no
        // name suffix), and one which takes information from this and all included ontologies into
        // account (and as 'All' suffix).
        System.out.println("Ontology 'example01' contains only "+axiomRequest.size()+" axioms (without taking the included ontology into account).");
        System.out.println();

        // We can now retrieve axioms with indices from 5 to 7 in all ontologies in the following way:
        Set<Axiom> axioms=axiomRequest.getAll(5,7);
        int index=5;
        for (Axiom axiom : axioms)
            System.out.println("Axiom with index "+(index++)+" is: "+axiom.toString());
        System.out.println();

        // Sometimes, you might want to retrieve all objects; this you can done by calling
        // Request<T>.get() or Request<T>.getAll(). This will return the set of all objects
        // matching the criteria.
        axioms=axiomRequest.getAll();
        System.out.println("Listing all axioms on all ontologies:");
        for (Axiom axiom : axioms)
            System.out.println("    "+axiom.toString());
        System.out.println();

        // You may set various parameters on a Request<T> to determine which information you actually want.
        // For example, you can retrieve all subclass axioms, where the superclass is '#document' as follows.
        // By passing SubClassOf.class, you say that you want SubClassOf axioms.
        Request<SubClassOf> subClassOfRequest=ontology.createAxiomRequest(SubClassOf.class);

        // Now you need to say that you don't want all subClassOf axioms, but only those there
        // the superclass is '#document'. The properties that you use as parameters correspond to
        // the JavaBeans specification. Hence, in class SubClassOf, you have the getSuperDescription() method.
        // According to the JavaBeans specification, this is equivalent to saying that SubClassOf class
        // has a superDescription property. For more details on JavaBeans, please refer to the Java documentation.
        OWLClass document=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example01#document");
        subClassOfRequest.setCondition("superDescription",document);

        // Now you can access all axioms.
        System.out.println("Listing subClassOf axioms where superDescription is '#document':");
        Set<SubClassOf> subClassOfAxioms=subClassOfRequest.get();
        for (SubClassOf axiom : subClassOfAxioms)
            System.out.println("    "+axiom.toString());
        System.out.println();

        // You might sometimes want to access all objects sequentially. In such a case, repeated calls
        // to Request<T> to retrieve the next window are a significant overhead: on each call,
        // a query will be executed to retrieve a particular window. To remedy that, you can open a cursor
        // over the results. A cursor allows you to access all objects sequentially, but without loading
        // then all at once into a set. This is useful for operations such as export.
        // The method Request.openCursor() exists only in one variant (i.e. there is no "All" variant); i.e.
        // a cursor allows access only to objects in one ontology.
        System.out.println("Listing all entities through a cursor:");
        Request<Entity> entityRequest=ontology.createEntityRequest();
        Cursor<Entity> cursor=entityRequest.openCursor();
        while (cursor.hasNext()) {
            Entity entity=cursor.next();
            System.out.println("    "+entity.toString());
        }
        System.out.println();

        // Cursors must be closed, otherwise you risk a resource leak.
        // Requests do not need to be closed, since they do not represent a 'live'
        // connection to the ontology.
        cursor.close();

        // We finally close the connection.
        ontologyManager.close();
    }
}
