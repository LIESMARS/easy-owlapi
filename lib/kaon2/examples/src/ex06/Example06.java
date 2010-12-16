package ex06;

import java.io.*;
import java.util.*;

import org.semanticweb.kaon2.api.*;
import org.semanticweb.kaon2.api.logic.*;
import org.semanticweb.kaon2.api.owl.elements.*;
import org.semanticweb.kaon2.api.formatting.*;
import org.semanticweb.kaon2.api.reasoner.*;

import org.semanticweb.kaon2.extensionapi.datatype.*;
import org.semanticweb.kaon2.extensionapi.builtins.*;

/**
 * This example explains how to extend KAON2 with new datatypes and with new builtin functions.
 */
public class Example06 {
    public static void main(String[] args) throws Exception {
        // Imagine you want to introduce a new datatype, Currency, whose values consist of a
        // (amount, code) pair. KAON2 does not have function symbols, so you can't represent
        // this data structure as you might do it in Prolog. However, KAON2 can do better.
        // In particular, you can introduce a class Currency, which will represent new data values.
        // Also, you can extend the reasoning facilities of KAON2 with new functions that operate on these values.
        //
        // The first step in doing so is to acutally create a class that will represent currency objects.
        // The class is defined after this method. It can have an arbitrary form. However, in most
        // cases, the class should have "value" semantics, i.e., objects of the class should be immutable,
        // and one should implement equals() and hashCode(). The reason for this is that KAON2 compares all
        // objects using equals(), and stores objects in internal maps using hashCode(). If an object is stored
        // in an internal map, but the object is changed so that its hashcode is also changed, the behavior
        // of KAON2 inferences is not defined. Alternatively, the objects might have "reference" semantics,
        // i.e., each object is equal only to itself. It is up to you then to ensure such semantics.
        //
        // After providing the class for the actual data value, one must register the so-called handler
        // for the datatype. The handler is responsible for loading and storing datatype objects into files.
        // Refer to the definition of CurrencyHandler class for more information.
        Datatypes.registerDatatypeHandler(new CurrencyHandler());

        OntologyManager ontologyManager=KAON2Manager.newOntologyManager();
        DefaultOntologyResolver resolver=new DefaultOntologyResolver();
        resolver.registerReplacement("http://kaon2.semanticweb.org/example06","file:example06.xml");
        ontologyManager.setOntologyResolver(resolver);
        Ontology ontology=ontologyManager.createOntology("http://kaon2.semanticweb.org/example06",new HashMap<String,Object>());

        List<OntologyChangeEvent> changes=new ArrayList<OntologyChangeEvent>();

        // We can now use the new datatype. For example, we now specify some information about Smart cars
        // (the small cars by DaimlerChrysler).
        OWLClass car=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example06#car");
        Individual smart=KAON2Manager.factory().individual("http://kaon2.semanticweb.org/example06#smart");
        DataProperty price=KAON2Manager.factory().dataProperty("http://kaon2.semanticweb.org/example06#price");

        // Smart is a car.
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().classMember(car,smart),OntologyChangeEvent.ChangeType.ADD));

        // We now say that smart costs 9000 EUR. Notice that we use the newly created Currency object here.
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().dataPropertyMember(price,smart,KAON2Manager.factory().constant(new Currency(9000,"EUR"))),OntologyChangeEvent.ChangeType.ADD));

        // We add these facts to the ontolgoy.
        ontology.applyChanges(changes);

        // We now save the ontology to C:\Temp. You may open the file to assure yourself that the Currency datatype has been property saved.
        System.out.println("The ontology will be saved into 'c:\\temp\\example06.xml'.");
        System.out.println("Please ensure that 'c:\\temp' directory exists.");
        ontology.saveOntology(OntologyFileFormat.OWL_XML,new File("c:\\temp\\example06.xml"),"ISO-8859-1");

        System.out.println("The ontology was saved successfully into 'c:\\temp\\example06.xml'.");

        // Let us now retrieve the prices or all cars, but in USD. To do this, we implement a new builtin function,
        // which performs the conversion of currencies. Each function is implemented in a sperate class. Please refer
        // to the documentation of the CurrencyConversion class for more information. We register this new builin function
        // as specified below. The first parameter is the name of the function (which will be used in the kaon:evaluate and
        // kaon2:ifTrue expressions), and the second parameter is the actual class.
        ExpressionEvaluator.registerBuiltinFunction("convert",CurrencyConversion.class);

        // We can now create the literal that performs the conversion from any price to a price in USD.
        Variable PRICE_ANY=KAON2Manager.factory().variable("PRICE_ANY");
        Variable PRICE_USD=KAON2Manager.factory().variable("PRICE_USD");
        Literal conversion=KAON2Manager.factory().literal(true,KAON2Manager.factory().evaluate(3),new Term[] {
            KAON2Manager.factory().constant("convert($1,\"USD\")"),
            PRICE_ANY,
            PRICE_USD
        });

        // We now create the query.
        Reasoner reasoner=ontology.createReasoner();
        Variable CAR=KAON2Manager.factory().variable("CAR");
        Query inUSD=reasoner.createQuery(new Literal[] {
            KAON2Manager.factory().literal(true,car,new Term[] { CAR }),
            KAON2Manager.factory().literal(true,price,new Term[] { CAR,PRICE_ANY }),
            conversion
        },new Variable[] { CAR,PRICE_USD });

        System.out.println("Cars and their prices:");
        System.out.println("----------------------");

        inUSD.open();
        while (!inUSD.afterLast()) {
            Term[] tupleBuffer=inUSD.tupleBuffer();
            System.out.println("'"+tupleBuffer[0].toString()+"' costs "+tupleBuffer[1].toString());
            inUSD.next();
        }
        inUSD.close();
        inUSD.dispose();

        System.out.println("-----------------------");

        // You can call the new functions from SPARQL. The functions can be called either in the FILTER clause, or in a proprietary EVALUATE clause.
        
        inUSD=reasoner.createQuery(new Namespaces(Namespaces.INSTANCE),
            "PREFIX a: <http://kaon2.semanticweb.org/example06#> "+
            "SELECT ?CAR ?PRICE_USD WHERE { ?CAR rdf:type a:car . ?CAR a:price ?PRICE_ANY . EVALUATE ?PRICE_USD := convert(?PRICE_ANY,\"USD\") }");
        
        System.out.println("Cars and their prices from SPARQL:");
        System.out.println("----------------------------------");

        inUSD.open();
        while (!inUSD.afterLast()) {
            Term[] tupleBuffer=inUSD.tupleBuffer();
            System.out.println("'"+tupleBuffer[0].toString()+"' costs "+tupleBuffer[1].toString());
            inUSD.next();
        }
        inUSD.close();
        inUSD.dispose();

        System.out.println("----------------------------------");

        // Do not forget to celan up!
        reasoner.dispose();
        ontologyManager.close();
    }

    /**
     * The class representing a currency value.
     */
    protected static class Currency {
        protected final double m_amount;
        protected final String m_code;

        public Currency(double amount,String code) {
            m_amount=amount;
            m_code=code;
        }
        public double getAmount() {
            return m_amount;
        }
        public String getCode() {
            return m_code;
        }
        public boolean equals(Object that) {
            if (this==that)
                return true;
            if (!(that instanceof Currency))
                return false;
            Currency thatCurrency=(Currency)that;
            return m_amount==thatCurrency.m_amount && m_code.equals(thatCurrency.m_code);
        }
        public int hashCode() {
            return m_code.hashCode()+(int)m_amount;
        }
        public String toString() {
            return m_amount+" "+m_code;
        }
    }

    /**
     * Implements a handler for Currency objects. The handler is mainly reponsible for loading and storing
     * object instances in files. Also, the handler probvides a URI for the datatype.
     */
    protected static class CurrencyHandler implements DatatypeHandler {
        public boolean isDatatypeInstance(Object object) {
            // This method should determine whether the supplied object is an instance of the given datatype.
            return object instanceof Currency;
        }
        public String getDatatypeURI() {
            // Each datatype has an URI, which is used in OWL files to denote the type of the value.
            // Here, we simply invent a new URI.
            return "http://kaon2.semanticweb.org/example06#Currency";
        }
        public int getArity() {
            // This method determines the arity of this datatype.
            return 1;
        }
        public String toString(Object object) {
            // This method produces a string representation of the object.
            return object.toString();
        }
        public Object parseObject(String objectValue) throws KAON2Exception {
            // This method converts the string representation of the object into an actual object.
            int spaceIndex=objectValue.indexOf(' ');
            if (spaceIndex!=-1) {
                String amountString=objectValue.substring(0,spaceIndex).trim();
                String code=objectValue.substring(spaceIndex+1).trim();
                try {
                    double amount=Double.parseDouble(amountString);
                    return new Currency(amount,code);
                }
                catch (NumberFormatException e) {
                }
            }
            throw new KAON2Exception("Invalid currency '"+objectValue+"'.");
        }

    }

    /**
     * Implements a builtin function for conversion of currencies. In expressions, this function
     * is called as convert(cur,to_code), where cur is the expression having Currency as result,
     * and to_code is the code of the currency to which conversion is performed.
     */
    protected static class CurrencyConversion extends ExpressionEvaluator {
        protected static final Map<String,Double> s_currencyToEUR;
        static {
            s_currencyToEUR=new HashMap<String,Double>();
            s_currencyToEUR.put("EUR",1.0);
            s_currencyToEUR.put("USD",0.8);
            s_currencyToEUR.put("GBP",1.3);
        }

        protected final ExpressionEvaluator[] m_arguments;

        /**
         * A builtin function should have a public constructor receiving
         * ExpressionEvaluator... as parameters. These evaluators will
         * produce the values of function arguments.
         *
         * @param arguments                     the evaluators producing arguments for this function
         */
        public CurrencyConversion(ExpressionEvaluator... arguments) {
            m_arguments=arguments;
        }
        /**
         * This method will be called to evaluate the function. The arguments of the kaon2:evaluate
         * or kaon2:ifTrue predicate are passed in boundValues.
         *
         * @param context                       the context for the evaluation
         * @param boundValues                   the arguments of the kaon2:evalute or kaon2:ifTrue predicate
         * @return                              the result value
         * @throws KAON2Exception               thrown if there is an error
         */
        public Term evaluate(ExpressionEvaluatorContext context,Term[] boundValues) throws KAON2Exception {
            if (m_arguments.length!=2)
                throw new KAON2Exception("The function 'convert' requires exactly two arguments.");
            // The first argument is the currency.
            Term result=m_arguments[0].evaluate(null, boundValues);
            // If it does not evaluate to the Currency object, throw an error. Don't worry: the exeption
            // below will be caught by the kaon2:evaluate or kaon2:ifTrue predicate.
            if (!(result instanceof Constant))
                throw new KAON2Exception("Type error.");
            Object resultValue=((Constant)result).getValue();
            if (!(resultValue instanceof Currency))
                throw new KAON2Exception("Type error.");
            Currency currency=(Currency)resultValue;
            // The second argument is the new currency code.
            result=m_arguments[1].evaluate(null, boundValues);
            if (!(result instanceof Constant))
                throw new KAON2Exception("Type error.");
            resultValue=((Constant)result).getValue();
            if (!(resultValue instanceof String))
                throw new KAON2Exception("Type error.");
            String newCode=(String)resultValue;

            // We now perform the actual conversion. For simplicity we avoid numerous checks.
            double newAmount=currency.getAmount()*s_currencyToEUR.get(currency.getCode())/s_currencyToEUR.get(newCode);

            // Finally, we create the new object.
            return KAON2Manager.factory().constant(new Currency(newAmount,newCode));
        }
        /**
         * Returns the number of arguments.
         * 
         * @return                              the number of arguments
         */
        public int getNumberOfArguments() {
            return m_arguments.length;
        }
        /**
         * Returns the argument with a given index.
         * 
         * @param index                         the index
         * @return                              the argument with a given index
         */
        public ExpressionEvaluator getArgument(int index) {
            return m_arguments[index];
        }

    }
}
