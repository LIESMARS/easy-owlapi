package ex05;

import java.util.*;

import org.semanticweb.kaon2.api.*;
import org.semanticweb.kaon2.api.logic.*;
import org.semanticweb.kaon2.api.owl.elements.*;
import org.semanticweb.kaon2.api.reasoner.*;

/**
 * This example shows how to use KAON2 built-in predicates. Using these predicates one can evaluate arithmetic expression
 * or perform value comparisons.
 */
public class Example05 {
    public static void main(String[] args) throws Exception {
        // To create an ontology, we again start by creating a ontology manager.
        // We again need to register a resolver that will provide a physical URI
        // for the ontology. In this example, the physical URI is relative to the current directory.
        OntologyManager ontologyManager=KAON2Manager.newOntologyManager();
        DefaultOntologyResolver resolver=new DefaultOntologyResolver();
        resolver.registerReplacement("http://kaon2.semanticweb.org/example05","file:example05.xml");
        ontologyManager.setOntologyResolver(resolver);

        // We create an ontology by specifying its logical URI. The resolver provides the physical URI.
        // Up until now this example is the same as Example 2.
        Ontology ontology=ontologyManager.createOntology("http://kaon2.semanticweb.org/example05",new HashMap<String,Object>());

        List<OntologyChangeEvent> changes=new ArrayList<OntologyChangeEvent>();

        // We now add some axioms to the ontology. In this example, we address the highly acute problem of people
        // beeing too fat, and provide means for classifying people according to their body-mass-index.
        // The information in this example is based on http://www.uni-hohenheim.de/~wwwin140/info/interaktives/bmi.htm
        // with some "artist's free interpretation".
        OWLClass person=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example05#person");

        // These are the classes into which people are classified according to their body-mass-index.
        OWLClass twig=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example05#twig");
        OWLClass normalWeight=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example05#normalWeight");
        OWLClass chubby=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example05#chubby");
        OWLClass walrus=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example05#walrus");
        OWLClass whale=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example05#whale");

        changes.add(new OntologyChangeEvent(KAON2Manager.factory().subClassOf(twig,person),OntologyChangeEvent.ChangeType.ADD));
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().subClassOf(normalWeight,person),OntologyChangeEvent.ChangeType.ADD));
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().subClassOf(chubby,person),OntologyChangeEvent.ChangeType.ADD));
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().subClassOf(walrus,person),OntologyChangeEvent.ChangeType.ADD));
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().subClassOf(whale,person),OntologyChangeEvent.ChangeType.ADD));

        // Data property 'height' contains the height of a person in centimeters.
        DataProperty height=KAON2Manager.factory().dataProperty("http://kaon2.semanticweb.org/example05#height");
        // Data property 'weight' contains the height of a person in kilograms.
        DataProperty weight=KAON2Manager.factory().dataProperty("http://kaon2.semanticweb.org/example05#weight");
        // Data property 'bmi' contains the body-mass-index of a person.
        DataProperty bmi=KAON2Manager.factory().dataProperty("http://kaon2.semanticweb.org/example05#bmi");

        // KAON2 currently does not support domain and range assertions for data properties. Hence, we shall NOT
        // state that the domain of 'height', 'weight' and 'bmi' is 'Person', and that the range is 'Integer';
        // doing so would currently prohibit answering any query. Similarly, we shall not state that these
        // properties are functional.  However, we still can use data properties in builtin predicates.
        Individual alice=KAON2Manager.factory().individual("http://kaon2.semanticweb.org/example05#alice");
        Individual bob=KAON2Manager.factory().individual("http://kaon2.semanticweb.org/example05#bob");
        Individual chuck=KAON2Manager.factory().individual("http://kaon2.semanticweb.org/example05#chuck");

        // The values of data properties can be arbitrary objects. For numeric values, KAON2 supports the usual conversions.

        // Alice is obsessed with her waistline, so she doesn't eat much...
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().dataPropertyMember(height,alice,KAON2Manager.factory().constant(new Integer(160))),OntologyChangeEvent.ChangeType.ADD));
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().dataPropertyMember(weight,alice,KAON2Manager.factory().constant(new Integer(40))),OntologyChangeEvent.ChangeType.ADD));

        // ...but therefore Bob cleans her fridge each time he vists her!
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().dataPropertyMember(height,bob,KAON2Manager.factory().constant(new Double(180))),OntologyChangeEvent.ChangeType.ADD));
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().dataPropertyMember(weight,bob,KAON2Manager.factory().constant(new Float(125.0))),OntologyChangeEvent.ChangeType.ADD));

        // Chuck likes to eat well on social occasions, but he exercises regularly and is very active in his running club.
        // Notice that we specify the weight of Chuck as a string. This is important for the discussion below about type conversions.
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().dataPropertyMember(height,chuck,KAON2Manager.factory().constant(new Integer(184))),OntologyChangeEvent.ChangeType.ADD));
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().dataPropertyMember(weight,chuck,KAON2Manager.factory().constant("74")),OntologyChangeEvent.ChangeType.ADD));

        // A formula can be evaluated using kaon2:evaluate(F,A1,...,An,R) predicate. The arguments have the following meaning:
        // - F is the string formula to evaluate
        // - Ai are the arguments
        // - R is the result of the evaluation
        //
        // You should think of the extension of this predicate as the set all all assertions of the above form where
        // evaluating F on the arguments Ai result in the result R.
        //
        // The formula F is a string which says what is to be evaluated. In the formuala one can use standard arithmetic
        // operators +, -, *, /, %, parenthesis, relational operators ==, !=, <, >, <=, >= and Boolean operators &&, || and !.
        // One can also use builtin functions, such as sum, sub, mul, div, min, max, concat, and, or, isuri, isliteral, regex and
        // conversion functions byte, short, int, long, float, double and str. The argument value Ai is denoted as $i (i should be
        // replaced with the actual number). Finally, one can use string constants delimited with "...", number constants
        // (interpreted as Double Java type), and arbitrary constants, whose syntax is "..."^^datatypeURI or "..."^^<datatypeURI>.
        //
        // We now create the rule that computes the body-mass-index, using the following formula:
        //
        //   BMI = WEIGHT / ((HEIGHT/100) * (HEIGHT/100))
        //
        // This can be accompished using the formula '$1/(($2/100.0)*($2/100.0))' (without apostrophes); this means that we
        // divide the value of the first argument with the square value of the second argument (converted into cm).
        // Hence, the literal in the rule has the form
        //
        //   kaon2:evaluate("$1/($2*$2)",WEIGHT,HEIGHT,BMI)
        //
        Variable HEIGHT=KAON2Manager.factory().variable("HEIGHT");
        Variable WEIGHT=KAON2Manager.factory().variable("WEIGHT");
        Variable BMI=KAON2Manager.factory().variable("BMI");
        Literal evaluateBMI=KAON2Manager.factory().literal(true,
            // The predicate is the kaon2:evaluate predicate, with the arity 4.
            KAON2Manager.factory().evaluate(4),
            new Term[] {
                KAON2Manager.factory().constant("$1/(($2/100.0)*($2/100.0))"),
                WEIGHT,
                HEIGHT,
                BMI
            });

        // To invoke the above literal, we need a rule which will match the values of HEIGHT and WEIGHT to some
        // concrete values. In general, only the last argument of kaon2:evaluate is allowed to be invoked free.
        Variable PERSON=KAON2Manager.factory().variable("PERSON");
        Rule computeBMI=KAON2Manager.factory().rule(
            KAON2Manager.factory().literal(true,bmi,new Term[] { PERSON,BMI }),
            new Literal[] {
                KAON2Manager.factory().literal(true,weight,new Term[] { PERSON,WEIGHT }),
                KAON2Manager.factory().literal(true,height,new Term[] { PERSON,HEIGHT }),
                evaluateBMI
            });

        changes.add(new OntologyChangeEvent(computeBMI,OntologyChangeEvent.ChangeType.ADD));

        // We now provide classification of people according to their BMI. For this purpose, we use
        // the kaon2:ifTrue(F,A1,...,An) predicate. In this case, F is the formula written in exactly the
        // same way as for kaon2:evaluate, and Ai are arguments. The extension of this predicate is equal
        // to the set of assertions of the above form where evaluating F on Ai results in Boolean.TRUE.
        Rule isTwig=KAON2Manager.factory().rule(
            KAON2Manager.factory().literal(true,twig,new Term[] { PERSON }),
            new Literal[] {
                KAON2Manager.factory().literal(true,bmi,new Term[] { PERSON,BMI }),
                KAON2Manager.factory().literal(true,KAON2Manager.factory().ifTrue(2),
                    new Term[] {
                        KAON2Manager.factory().constant("$1 < 20"),
                        BMI
                    })
            });
        changes.add(new OntologyChangeEvent(isTwig,OntologyChangeEvent.ChangeType.ADD));

        Rule isNormalWeight=KAON2Manager.factory().rule(
            KAON2Manager.factory().literal(true,normalWeight,new Term[] { PERSON }),
            new Literal[] {
                KAON2Manager.factory().literal(true,bmi,new Term[] { PERSON,BMI }),
                KAON2Manager.factory().literal(true,KAON2Manager.factory().ifTrue(2),
                    new Term[] {
                        KAON2Manager.factory().constant("20 <= $1 && $1 < 25"),
                        BMI
                    })
            });
        changes.add(new OntologyChangeEvent(isNormalWeight,OntologyChangeEvent.ChangeType.ADD));

        Rule isChubby=KAON2Manager.factory().rule(
            KAON2Manager.factory().literal(true,chubby,new Term[] { PERSON }),
            new Literal[] {
                KAON2Manager.factory().literal(true,bmi,new Term[] { PERSON,BMI }),
                KAON2Manager.factory().literal(true,KAON2Manager.factory().ifTrue(2),
                    new Term[] {
                        KAON2Manager.factory().constant("25 <= $1 && $1 < 30"),
                        BMI
                    })
            });
        changes.add(new OntologyChangeEvent(isChubby,OntologyChangeEvent.ChangeType.ADD));

        Rule isWalrus=KAON2Manager.factory().rule(
            KAON2Manager.factory().literal(true,walrus,new Term[] { PERSON }),
            new Literal[] {
                KAON2Manager.factory().literal(true,bmi,new Term[] { PERSON,BMI }),
                KAON2Manager.factory().literal(true,KAON2Manager.factory().ifTrue(2),
                    new Term[] {
                        KAON2Manager.factory().constant("30 <= $1 && $1 < 40"),
                        BMI
                    })
            });
        changes.add(new OntologyChangeEvent(isWalrus,OntologyChangeEvent.ChangeType.ADD));

        Rule isWhale=KAON2Manager.factory().rule(
            KAON2Manager.factory().literal(true,whale,new Term[] { PERSON }),
            new Literal[] {
                KAON2Manager.factory().literal(true,bmi,new Term[] { PERSON,BMI }),
                KAON2Manager.factory().literal(true,KAON2Manager.factory().ifTrue(2),
                    new Term[] {
                        KAON2Manager.factory().constant("$1 > 40"),
                        BMI
                    })
            });
        changes.add(new OntologyChangeEvent(isWhale,OntologyChangeEvent.ChangeType.ADD));

        // We add the axoms to the ontology.
        ontology.applyChanges(changes);

        // We are now ready to see how people are fairing. On general issues related to running queries, pl;ease consult
        // Exmaple 5. We now ask for the BMI of all people:
        Reasoner reasoner=ontology.createReasoner();

        System.out.println("The BMI of people is as follows:");
        System.out.println("--------------------------------");

        Query getBMI=reasoner.createQuery(new Literal[] {
            KAON2Manager.factory().literal(true,bmi,new Term[] { PERSON,BMI })
        },new Variable[] { PERSON,BMI });

        getBMI.open();
        while (!getBMI.afterLast()) {
            Term[] tupleBuffer=getBMI.tupleBuffer();
            System.out.println("The BMI of '"+tupleBuffer[0].toString()+"' is "+tupleBuffer[1].toString()+".");
            getBMI.next();
        }
        // If you don't plan to use a query any more, you don't have to bother closing it; you can
        // simply dispose of it.
        getBMI.dispose();

        System.out.println("--------------------------------");

        // What happened to Chuck? The he wasn't included in this list, since his weight has been specified as a string,
        // which effectively caused a type error. However, KAON2 does not throw an exception on type errors. The reason
        // for this is that exceptions are hard to caputre in logic. Instead, consider what actually happened.
        // The rule for evaluating BMI of chuck was invoked on this literal:
        //
        //   kaon2:evaluate("$1/($2*$2)",184,"74",BMI)
        //
        // This is equivalent to asking whether there is an atomic fact matching this literal. Well, the answer is
        // that there is no such fact, so the rule 'failed' for Chuck. This corresponds to the standard logical notions.

        // Let us now see who the walruses are:
        System.out.println();
        System.out.println("The walruses are:");
        System.out.println("--------------------------------");

        // To get the extension of entire predicate, Reasoner contains the following shortcut:
        Query getWalrus=reasoner.createQuery(walrus);

        getWalrus.open();
        while (!getWalrus.afterLast()) {
            Term[] tupleBuffer=getWalrus.tupleBuffer();
            System.out.println("'"+tupleBuffer[0].toString()+"' is a walrus.");
            getWalrus.next();
        }
        getWalrus.dispose();

        System.out.println("--------------------------------");

        // Do not forget to clean up!
        reasoner.dispose();
        ontologyManager.close();
    }
}
