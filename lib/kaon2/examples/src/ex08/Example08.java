package ex08;

import java.util.HashMap;

import org.semanticweb.kaon2.api.*;
import org.semanticweb.kaon2.api.logic.*;
import org.semanticweb.kaon2.api.reasoner.*;

/**
 * This example shows how to provide an ontology-based view over an existing relational database.
 * You should go through Example 1 before proceeding with this example.
 * Also, you should be familiar with the Java Database Connectivity (JDBC) technology.
 */
public class Example08 {
    /** The URI of the ontology descriptor. */
    public static String ONTOLOGY_DESCRIPTOR_URI="file:src/ex08/ontology_descriptor.xml";
    
    public static void main(String[] args) throws Exception {
        // Assume that you have a database containing a large amount of data, which you'd like to
        // convert into an ontology and access it by KAON2. You might do it by writing a prorpietary
        // converter, which dumps the contents of the database into an OWL file. However, such a
        // solution has a significant drawback: if the data changes, you'll need to run the conversion
        // process from scratch. Furthermore, if you have REALLY a lot of data, KAON2 might not be able
        // to load all of it into main memory. It therefore makes sense to leave this data in the
        // relational database, and provide an ontology view over it. KAON2 provides such a feature.
        // In fact, by providing a mapping of tables and fields of your database to ontology classes
        // and properties, you can create a "virtual" ontology. The ontology is virtual in the sense that
        // it is not materialized; rather, each time KAON2 needs instances of a certain concept or a property,
        // it will use the mapping and will directly access the database. Hence, if your data changes, the
        // changes are immediately visible to KAON2. Furthermore, KAON2 does not need to load entire data
        // into main memory, which greatly increases the scalability of KAON2. In this example you'll learn
        // how to create the mapping, and how to open and with with the virtual ontology.
        
        // Before you proceed, you need to perform the following preparation steps. 
        // 1. Create a new test database.
        // 2. Execute the SQL script schema_and_data.sql supplied with this example. This script
        //    will create the database schema and fill it with some test data.
        // 3. Download the JDBC driver for your database, and put it in your class path. If you
        //    use the supplied ANT script to run the example, then modify the "JDBCpath" classpath
        //    to point to the correct location of your driver.
        // 4. Update the database connection string, the user name, the password, and the driver class name
        //    in the supplied "ontology_descriptor.xml" file.
        // 5. Recompile the examples, so that these changes come into effect.
        //
        // The file "ontology_descriptor.xml" contains the definition of the virtual ontology.
        // The attribute db:name of the db:DBOntology element defines the ontology URI.
        // The element db:Database contains information used to connect to a database. For connecting
        // to MSSQL Server, you should additionally append to this element the following attribute:
        //     db:databaseType="MSSQL"
        // The rest of the file is simply a set of mappings of various OWL elements to tables and fields.
        // Notice that for each field you need to specify a type, which must match the type of the field
        // in the database. Currently supported types are:
        //     db:String, db:Integer, db:Short, db:Byte, db:Float, db:Double, db:Boolean, and db:Date
        //
        // Certain fields in the database may be mapped to individuals. Usually, the fields in the databse
        // will not contain full individual URIs. Hence, you can specify using the db:uriPrefix attribute
        // a prefix for the URIs of individuals obtained from the table. For example, if you specify
        // db:uriPrefix="http://test.com/ontology#p", then if a field contains the value "14", the URI of the
        // individual obtained from this field will be "http://test.com/ontology#p14". Fields mapped to
        // individuals can be of either integer (mapped using db:IndividualInteger element) or string
        // (mapped using db:IndividualString element) type.
        //
        // If a field acts as a primary key for a table, you should specify db:primaryKey="true" attribute.
        // This is not strictly required, but may be used by KAON2 for optimizations. Compound primary
        // keys are currently not supported, so for such fields simply omit the db:primaryKey attribute.
        //
        // The following mapping types can be used:
        // 
        // - db:OWLClass maps a class to a field in a table
        // - db:ObjectProperty maps an object property to a pair of individuals in a table
        // - db:DataProperty and db:AnnotationProperty map a data property to an
        //   (individual,value) pair in a table
        // - db:SameIndividual and db:DifferentIndividuals specify the extensions of equality predicates
        //   (both must me mapped to pairs or individuals)
        // - db:PredicateSymbol maps a predicate symbols to the appropriate extension
        // - db:HerbrandUniverse provides a mapping to a table containing all individuals in a database.
        //   Such a mapping is needed in order to answer some advanced OWL queries. If you do not provide
        //   such a mapping, then some queries will throw an exception. This mapping is needed to answer
        //   some queries in ontologies that use either equality or negation.
        //
        // A virtual ontology can contain only assertions about individuals; it cannot contain any axioms
        // about classes or properties. Furthermore, it cannot include another ontology, but it can be
        // included in some other ontology. Hence, you should specify the classes and axioms in a file-based
        // ontology as usual; then, you can include the virtual ontology into it to provide the individuals.
        //
        // A virtual ontology is read-only. If you want to change the class memberships and relations stored
        // in it, you should do it outside of KAON2.
        //
        // An ontology descriptor file is used as any other file-based ontology. You can open it as usual,
        // by creating a resolver, registering the physical URI of the ontology, and then opening the ontology. 
        DefaultOntologyResolver resolver=new DefaultOntologyResolver();
        String ontologyURI=resolver.registerOntology(ONTOLOGY_DESCRIPTOR_URI);
        OntologyManager ontologyManager=KAON2Manager.newOntologyManager();
        ontologyManager.setOntologyResolver(resolver);
        Ontology ontology=ontologyManager.openOntology(ontologyURI,new HashMap<String,Object>());
        
        // Once you opened an ontology, you can access it just like you would access an ordinary ontology.
        // For example, you can ask some queries:
        System.out.println("  Query results:");
        System.out.println("-----------------------------------");
        Reasoner reasoner=ontology.createReasoner();
        Query query=reasoner.createQuery(Namespaces.INSTANCE,"SELECT ?x ?y ?z ?w WHERE { ?x rdf:type <http://test.com/ontology#Person> ; <http://test.com/ontology#personHasName> ?y ; <http://test.com/ontology#personHasParent> ?z . ?z <http://test.com/ontology#personHasName> ?w }");
        query.open();
        Term[] tupleBuffer=query.tupleBuffer();
        while (!query.afterLast()) {
            System.out.print("[ ");
            for (int i=0;i<tupleBuffer.length;i++) {
                if (i!=0)
                    System.out.print(", ");
                System.out.print(tupleBuffer[i]);
            }
            System.out.println(" ]");
            query.next();
        }
        System.out.println("-----------------------------------");
        query.dispose();
        reasoner.dispose();
        
        // You can also iterate through the axioms in the ontology:
        System.out.println();
        System.out.println("  Axioms of the ontology:");
        System.out.println("-----------------------------------");
        Cursor<Axiom> cursor1=ontology.createAxiomRequest().openCursor();
        while (cursor1.hasNext()) {
            Axiom axiom=cursor1.next();
            System.out.println(axiom.toString());
        }
        System.out.println("-----------------------------------");
        cursor1.close();
        
        // The retrieval functionality works as usual. For example, you can select the axioms just for some objects: 
        System.out.println();
        System.out.println("  Facts about 'http://test.com/ontology#p1':");
        System.out.println("-----------------------------------");
        Cursor<Literal> cursor2=ontology.createAxiomRequest(Literal.class).setCondition("argument",0,KAON2Manager.factory().individual("http://test.com/ontology#p1")).openCursor();
        while (cursor2.hasNext()) {
            Axiom axiom=cursor2.next();
            System.out.println(axiom.toString());
        }
        System.out.println("-----------------------------------");
        cursor2.close();
        
        // Do not forget to clean-up, or your connections to the database will not be released!
        ontologyManager.close();
    }
}
