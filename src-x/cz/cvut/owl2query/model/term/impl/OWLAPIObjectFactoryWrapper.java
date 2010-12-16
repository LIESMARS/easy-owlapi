package cz.cvut.owl2query.model.term.impl;

import java.net.URI;
import java.util.logging.Logger;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;

public class OWLAPIObjectFactoryWrapper implements OWLObjectFactory<OWLDescription,  OWLObjectProperty, OWLDataProperty, OWLIndividual, OWLConstant> {

    private static final Logger log = Logger.getLogger(OWLAPIObjectFactoryWrapper.class.getName());

    private final OWLDataFactory f;

    public OWLAPIObjectFactoryWrapper(OWLDataFactory f) {
        this.f = f;
    }

    public OWLClass getOWLThing() {
        return f.getOWLThing();
    }

    public OWLClass getOWLNothing() {
        return f.getOWLNothing();
    }

    public OWLIndividual asIndividual(String s) {
        return f.getOWLIndividual(URI.create(s));
    }

    public OWLConstant asLiteral(String s) {
        return f.getOWLTypedConstant(s);
    }

    public OWLDescription asClass(String s) {
        return f.getOWLClass(URI.create(s));
    }

    public OWLObjectProperty asObjectProperty(String s) {

        return f.getOWLObjectProperty(URI.create(s));
    }

    public OWLDataProperty asDataProperty(String s) {
        return f.getOWLDataProperty(URI.create(s));
    }
}
