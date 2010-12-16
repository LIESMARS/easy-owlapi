package cz.cvut.owl2query.parser;

import cz.cvut.owl2query.model.QueryEvaluationException;
import cz.cvut.owl2query.model.Query;
import cz.cvut.owl2query.model.term.QueryTermFactory;
import cz.cvut.owl2query.model.KnowledgeBase;
import cz.cvut.owl2query.model.QueryAtom;
import cz.cvut.owl2query.model.QueryAtomFactory;
import cz.cvut.owl2query.model.QueryImpl;
import cz.cvut.owl2query.model.QueryPredicate;
import cz.cvut.owl2query.model.Variable;
import cz.cvut.owl2query.model.term.impl.OWLAPIKnowledgeBaseOWLReasonerWrapper;
import cz.cvut.owl2query.model.term.OWLClassArgument;
import cz.cvut.owl2query.model.term.OWLIndividualArgument;
import cz.cvut.owl2query.model.term.OWLIndividualOrLiteralArgument;
import cz.cvut.owl2query.model.term.OWLPropertyArgument;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.semanticweb.owl.apibinding.OWLManager;


/**
 * <p>
 * Title: SPARQL-DL Functional Syntax Parser
 * </p>
 * <p>
 * Description: 
 * </p>
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * <p>
 * Company: Czech Technical University in Prague. <http://www.cvut.cz>
 * </p>
 * 
 * @author Petr Kremen
 */
public class FunctionalSparqlDLSyntaxParser implements QueryParser {

    private static final Logger log = Logger.getLogger(FunctionalSparqlDLSyntaxParser.class.getName());

    private final static String queryPatternAtom = "[\\w]+\\([^(),]+(?:,[^(),]+)*\\)";
    private final static Pattern queryPattern = Pattern.compile("("+queryPatternAtom+")");

    private final static Pattern atomPattern = Pattern.compile("(\\w+)\\(([^(),]+)(?:,([^(),]+))*\\)");

    private KnowledgeBase kb;
    private QueryTermFactory f;

    public Query parse(String queryString, KnowledgeBase kb) throws QueryEvaluationException {
        return parse(new ByteArrayInputStream(queryString.getBytes()), kb);
    }

    public Query parse(InputStream stream, KnowledgeBase kb) throws QueryEvaluationException {
        log.info("Parsing query ...");
        if ( kb == null ) {
            throw new QueryEvaluationException("When parsing a query a valid KnowledgeBase instance must be supplied, but got 'null'");
        }

        this.kb = kb;
        this.f = kb.getFactory();

        final BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        String line = null;
        String query = "";

        try {
            final Query q = new QueryImpl(kb, false);

            while ( ( line = br.readLine() ) != null ) {
                query +=line;
            }

            final Matcher m = queryPattern.matcher(query);

            while(m.find()) {
                String nextMatch = query.substring(m.start(),m.end());
                parseAtom(nextMatch, q);
            }

            return q;
        } catch(Exception e) {
            String message = "The query cannot be parsed.";
            log.severe(message);
            throw new QueryEvaluationException(message,e);
        }
    }

    private OWLIndividualArgument parseOWLIndividualArgument(String s, Query q) {
        if (s.startsWith("?")) {
            final Variable a = f.asIndividualVariable(s.substring(1));
            q.addResultVar(a);
            return a;
        } else if (s.startsWith("!")) {
            return f.asIndividualVariable(s.substring(1));
        } else {
            return f.asIndividual(resolve(s));
        }
    }

    private OWLIndividualOrLiteralArgument parseOWLIndividualOrLiteralArgument(String s, Query q) {
        if (s.startsWith("?")) {
            final Variable a = f.asIndividualOrLiteralVariable(s.substring(1));
            q.addResultVar(a);
            return a;
        } else if (s.startsWith("!")) {
            return f.asIndividualOrLiteralVariable(s.substring(1));
        } else {
            // TODO
            return f.asIndividual(resolve(s));
        }
    }

    private OWLClassArgument parseOWLClassArgument(String s, Query q) {
        if (s.startsWith("?")) {
            final Variable a = f.asClassVariable(s.substring(1));
            q.addResultVar(a);
            return a;
        } else if (s.startsWith("!")) {
            throw new QueryEvaluationException("Queries with undistinguished CLASS variables are not supported.");
        } else {
            return f.asClass(resolve(s));
        }
    }

    private OWLPropertyArgument parseOWLPropertyArgument(String s, Query q) {
        if (s.startsWith("?")) {
            final Variable a = f.asPropertyVariable(s.substring(1));
            q.addResultVar(a);
            return a;
        } else if (s.startsWith("!")) {
            throw new QueryEvaluationException("Queries with undistinguished PROPERTY variables are not supported.");
        } else {
            if ( kb.isObjectProperty( URI.create(resolve(s)) ) ) {
                return f.asObjectProperty(resolve(s));
            } else if ( kb.isObjectProperty( URI.create(resolve(s)) ) ) {
                return f.asDataProperty(resolve(s));
            } else {
                log.info("The type of property '"+resolve(s)+"' cannot be determined - guessing ObjectProperty");
                return f.asObjectProperty(resolve(s));
            }
        }
    }

    private void parseAtom(final String s, final Query q) throws QueryEvaluationException {
        log.info("Parsing atom: '"+s+"' ...");
        final Matcher m  = atomPattern.matcher(s);

        if ( m.matches() ) {
            final String predicate = m.group(1);
            final List<String> arguments = new ArrayList<String>();
            for( int i = 2; i <= m.groupCount(); i++) {
                arguments.add(m.group(i));
            }
            log.info("            - predicate: '"+predicate+"'");
            log.info("            - arguments: '"+arguments+"'");

            // CONSTRUCTION OF THE QUERY ATOM
            final QueryPredicate predEnum;

            try {
                QueryAtom atom;
                predEnum = QueryPredicate.valueOf(predicate);
            
                switch(predEnum) {
                    case Type :
                        atom = QueryAtomFactory.TypeAtom(parseOWLIndividualArgument(arguments.get(0), q),parseOWLClassArgument(arguments.get(1), q));
                        break;
                    case PropertyValue :
                        atom = QueryAtomFactory.PropertyValueAtom(parseOWLIndividualArgument(arguments.get(0), q),parseOWLPropertyArgument(arguments.get(1), q),parseOWLIndividualOrLiteralArgument(arguments.get(2), q));
                        break;
                    case SubClassOf :
                        atom = QueryAtomFactory.SubClassOfAtom(parseOWLClassArgument(arguments.get(0), q),parseOWLClassArgument(arguments.get(1), q));
                        break;
                    case ComplementOf :
                        atom = QueryAtomFactory.ComplementOfAtom(parseOWLClassArgument(arguments.get(0), q),parseOWLClassArgument(arguments.get(1), q));
                        break;
                    case DisjointWith :
                        atom = QueryAtomFactory.DisjointWithAtom(parseOWLClassArgument(arguments.get(0), q),parseOWLClassArgument(arguments.get(1), q));
                        break;
                    case SubPropertyOf :
                        // TODO
                        atom = QueryAtomFactory.SubPropertyOfAtom(parseOWLPropertyArgument(arguments.get(0), q),parseOWLPropertyArgument(arguments.get(1), q));
                        break;

            // NONMONOTONIC EXTENSION TO SPARQL-DL
                    case DirectSubClassOf :
                        atom = QueryAtomFactory.DirectSubClassOfAtom(parseOWLClassArgument(arguments.get(0), q),parseOWLClassArgument(arguments.get(1), q));
                        break;
                    case StrictSubClassOf :
                        atom = QueryAtomFactory.StrictSubClassOfAtom(parseOWLClassArgument(arguments.get(0), q),parseOWLClassArgument(arguments.get(1), q));
                        break;
                    default: throw new QueryEvaluationException("The query atom predicate '"+predicate+"' is not supported yet for queries.");
                }

                q.add(atom);
                log.info("Query atom: '"+s+"' ...");
            } catch(IllegalArgumentException e) {
                throw new QueryEvaluationException("The query atom predicate '" + predicate + "' is unknown !");
            }
        } else {
            String message = "The query atom does not conform to the functional syntax.";
            log.severe(message);
            throw new QueryEvaluationException(message);
        }
    }

    private String resolve( final String s ) {
        return s;
    }

    public static void main( final String[] args) {
        FunctionalSparqlDLSyntaxParser p = new FunctionalSparqlDLSyntaxParser();
        KnowledgeBase kb = new OWLAPIKnowledgeBaseOWLReasonerWrapper(OWLManager.createOWLOntologyManager(), null, null);
        try {
            final Query q = p.parse("SubClassOf(X,Y),SubPropertyOf(X,Y),Type(X,Y)", kb);

            System.out.println(q);
        } catch (QueryEvaluationException ex) {
            Logger.getLogger(FunctionalSparqlDLSyntaxParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
