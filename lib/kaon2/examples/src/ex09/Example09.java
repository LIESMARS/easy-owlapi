package ex09;

import java.util.HashMap;
import java.io.PrintWriter;

import org.semanticweb.kaon2.api.*;
import org.semanticweb.kaon2.api.reasoner.*;
import org.semanticweb.kaon2.api.logic.*;

import org.semanticweb.kaon2.extensionapi.jdbc.*;

/**
 * This example shows how to invoke built-in database functions from KAON2. 
 */
public class Example09 {
    /** The URI of the ontology descriptor. */
    public static String ONTOLOGY_DESCRIPTOR_URI="file:src/ex09/ontology_descriptor.xml";

    public static void main(String[] args) throws Exception {
        // Many databases offer functionality that can be invoked using proprietary SQL fragments.
        // You might want to invoke this functionality from KAON2. This example will show you how
        // you can do this.
        //
        // In this example we shall assume that the database offers a special function called append100()
        // that increases the value of a integer field by 100. You might invoke this function as follows:
        //
        //   SELECT personID FROM persons WHERE append100(zip)=20100
        //
        // Furthermore, in order to make this example independent from the database, we shall not write 
        // append100(height), but will use the SQL fragment (height+100). However, you can follow the guidelines
        // from this example to create an extension using an argitrary SQL fragment.
        //
        // In the following we shall create a new built-in function append100() that, when evaluated in the
        // database, appends 100 to the numeric value. This function can be invoked as any other built-in function
        // (see example 5). Thus, our built-in function can be used in kaon2:IfTrue or kaon2:Evaluate predicate,
        // just like any other function. In our example, we shall evaluate the following SPARQL query:
        // 
        //   SELECT ?x ?y WHERE { ?x <http://test.com/ontology#personHasZIP> ?y . FILTER append100(?y) == 20100 }
        //
        // The FILTER condition will be translated internally into an invocation of kaon2:Evaluate predicate.
        // In this way, the users of built-in functions do not need to concern themselves with whether a function
        // is implemented in-memory or in a database.
        //
        // In this example, we shall only create a database version of a built-in function. Therefore, even for
        // data that comes from the memory, a database query will be invoked to compute the function. Alternatively,
        // you might implement the in-memory variant of the built-in as well (as explained in Example 5). In this case,
        // KAON2 will invoke the version of the function that promises best performance: for in-memory data, KAON2 will
        // invoke the in-memory version, and for data coming from the database, KAON2 will invoke the database version
        // (in order to reduce the data transfered from the database).
        //
        // OK, let's get started. You should have the example database ready (as axplained in Example 8).
        // In the following couple of lines, we shall open an ontology descriptor, which is identical with the one
        // from Example 8, with just one line of difference: in line 7 we specify that the function append100()
        // is implemented in the class ex9.Example9$Append100. (Note that the names of nested classes are
        // separated with $ in Java.) Please refer to the class below for more information.
        DefaultOntologyResolver resolver=new DefaultOntologyResolver();
        String ontologyURI=resolver.registerOntology(ONTOLOGY_DESCRIPTOR_URI);
        OntologyManager ontologyManager=KAON2Manager.newOntologyManager();
        ontologyManager.setOntologyResolver(resolver);
        Ontology ontology=ontologyManager.openOntology(ontologyURI,new HashMap<String,Object>());
        
        // We shall now ask the query specified above.
        System.out.println("  Query results:");
        System.out.println("-----------------------------------");
        Reasoner reasoner=ontology.createReasoner();
        
        // So that you don't think this is just smoke and mirrors, we shall turn on debugging of SQL queries.
        reasoner.setTrace("queryAnsweringSQL",true,new PrintWriter(System.out,true),Namespaces.INSTANCE);
        
        // Here is the query...
        Query query=reasoner.createQuery(Namespaces.INSTANCE,"SELECT ?x ?y WHERE { ?x <http://test.com/ontology#personHasZIP> ?y . FILTER append100(?y) = 20100 }");
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

        // Do not forget to clean-up, or your connections to the database will not be released!
        ontologyManager.close();
    }

    // This class implements the SQL expression for append100 built-in function.
    public static class Append100 implements SQLExpression {
        protected final SQLExpression m_argument;

         // A class that implements SQLExpression must have a constructor with a single argument of type SQLExpression[].
         // This constructor will be called, and the parameter will be the expressions that are passed to this
         // expression as arguments. 
        public Append100(SQLExpression[] arguments) throws KAON2Exception {
            if (arguments.length!=1)
                throw new KAON2Exception("Invalid number of arguments.");
            m_argument=arguments[0];
        }
        // This method should return the type of the expression. The list of possible types is specified in the SQLExpression interface.
        public int getType() {
            return TYPE_INTEGER;
        }
        // This method generates the SQL string representing the call of the given SQL expression.
        public void writeExpression(StringBuffer buffer,DatabaseSpecifics databaseSpecifics) throws KAON2Exception {
            buffer.append('(');
            m_argument.writeExpression(buffer,databaseSpecifics);
            buffer.append("+100)");
        }
        // This method should return true if the SQL expression is a boolean expression that is always true.
        // If you are unsure about how to implement this method, feel free to return false.
        public boolean isTautology() {
            return false;
        }
        // Each SQLExpression should implement equals().
        public boolean equals(Object that) {
            if (this==that)
                return true;
            if (!(that instanceof Append100))
                return false;
            return m_argument.equals(((Append100)that).m_argument);
        }
        // Each SQLExpression should implement hashCode().
        public int hashCode() {
            return m_argument.hashCode();
        }
    }
}
