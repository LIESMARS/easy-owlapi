package cz.cvut.owl2query.model.term.impl;

import cz.cvut.owl2query.model.InternalReasonerException;
import cz.cvut.owl2query.model.KnowledgeBase;
import cz.cvut.owl2query.model.Term;
import cz.cvut.owl2query.model.term.QueryTermFactory;
import cz.cvut.owl2query.model.SizeEstimate;
import cz.cvut.owl2query.model.term.OWLClass;
import cz.cvut.owl2query.model.term.OWLDataProperty;
import cz.cvut.owl2query.model.term.OWLIndividual;
import cz.cvut.owl2query.model.term.OWLLiteral;
import cz.cvut.owl2query.model.term.OWLObjectProperty;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLProperty;

public class OWLAPIKnowledgeBaseOWLReasonerWrapper implements KnowledgeBase {

    private static final Logger log = Logger.getLogger(OWLAPIKnowledgeBaseOWLReasonerWrapper.class.getName());
    private final OWLOntologyManager m;
    private final OWLOntology o;
    private final OWLReasoner r;
    private final QueryTermFactoryImpl<OWLDescription,OWLProperty, org.semanticweb.owl.model.OWLObjectProperty, org.semanticweb.owl.model.OWLDataProperty, org.semanticweb.owl.model.OWLIndividual, OWLConstant> f;
    private Boolean consistent = null;
    private boolean classified = false;
    private boolean realized = false;

    public OWLAPIKnowledgeBaseOWLReasonerWrapper(final OWLOntologyManager m, final OWLReasoner r, final OWLOntology o) {
        this.r = r;
        this.m = m;
        this.o = o;
        this.f = new QueryTermFactoryImpl<OWLDescription, OWLProperty, org.semanticweb.owl.model.OWLObjectProperty, org.semanticweb.owl.model.OWLDataProperty, org.semanticweb.owl.model.OWLIndividual, OWLConstant>(new OWLAPIObjectFactoryWrapper(m.getOWLDataFactory()));
    }

    public SizeEstimate getSizeEstimate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public OWLClassImpl<OWLDescription> getOWLThing() {
        return f.getOWLThing();
    }

    public OWLClassImpl<OWLDescription> getOWLNothing() {
        return f.getOWLNothing();
    }

    public void classify() {
        if (!isClassified()) {
            try {
                r.classify();
                classified = true;
            } catch (OWLReasonerException e) {
                log.severe(e.getMessage());
            }
        }
    }

    public void realize() {
        if (!isRealized()) {
            try {
                r.realise();
                realized = true;
            } catch (OWLReasonerException e) {
                log.severe(e.getMessage());
            }
        }
    }

    public QueryTermFactory getFactory() {
        return f;
    }

    public Collection<OWLIndividual> getIndividuals() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<OWLClass> getClasses() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<OWLObjectProperty> getObjectProperties() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<OWLDataProperty> getDataProperties() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<OWLIndividual> getInstances(OWLClass clazz, boolean direct, boolean told) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isInstanceOf(OWLIndividual instance, OWLClass clazz, boolean told) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<OWLClass> getTypes(OWLIndividual instances, boolean told) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<OWLIndividual> getObjectPropertyValues(OWLIndividual instance, OWLObjectProperty property, boolean told) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<OWLLiteral> getDataPropertyValues(OWLIndividual instance, OWLDataProperty property, boolean told) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isConsistent() {
        if (consistent == null) {
            try {
                consistent = r.isConsistent(o);
            } catch (OWLReasonerException ex) {
                Logger.getLogger(OWLAPIKnowledgeBaseOWLReasonerWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return consistent;
    }

    public boolean isClassified() {
        return classified;
    }

    public boolean isRealized() {
        return realized;
    }

    public void ensureConsistency() throws InternalReasonerException {
        if ( !isConsistent() ) {
            throw new InternalReasonerException("The ontology is not consistent.");
        }
    }

    public Collection<OWLClass> getObjectPropertyDomains(OWLObjectProperty op) {
        try {
            if (!(op instanceof OWLObjectPropertyImpl)) {
                throw new InternalReasonerException("Expected default implementation, but got '" + op.getClass() + "'");
            }
            final OWLObjectPropertyImpl<org.semanticweb.owl.model.OWLObjectProperty> opp = (OWLObjectPropertyImpl<org.semanticweb.owl.model.OWLObjectProperty>) op;
            final Set<Set<OWLDescription>> domains = r.getDomains(opp.getTerm());
            final Collection<OWLClass> set = new HashSet<OWLClass>();

            for( final Set<OWLDescription> ds : domains ) {
                for( final OWLDescription d : ds ) {
                    set.add(new OWLClassImpl<OWLDescription>(d));
                }
            }
            return set;
        } catch (OWLReasonerException ex) {
            log.log(Level.SEVERE, null, ex);
            return Collections.emptySet();
        }
    }

    public Collection<OWLClass> getDataPropertyDomains(OWLDataProperty dp) {
        try {
            if (!(dp instanceof OWLDataPropertyImpl)) {
                throw new InternalReasonerException("Expected default implementation, but got '" + dp.getClass() + "'");
            }
            final OWLDataPropertyImpl<org.semanticweb.owl.model.OWLDataProperty> opp = (OWLDataPropertyImpl<org.semanticweb.owl.model.OWLDataProperty>) dp;
            final Set<Set<OWLDescription>> domains = r.getDomains(opp.getTerm());
            final Collection<OWLClass> set = new HashSet<OWLClass>();

            for( final Set<OWLDescription> ds : domains ) {
                for( final OWLDescription d : ds ) {
                    set.add(new OWLClassImpl<OWLDescription>(d));
                }
            }
            return set;
        } catch (OWLReasonerException ex) {
            log.log(Level.SEVERE, null, ex);
            return Collections.emptySet();
        }
    }

    public boolean isObjectProperty(Term term) {
        return (term instanceof OWLObjectProperty);
    }

    public boolean isDataProperty(Term term) {
        return (term instanceof OWLDataProperty);
    }

    public boolean isClass(Term term) {
        return (term instanceof OWLClass);
    }

    public boolean isIndividual(Term term) {
        return (term instanceof OWLIndividual);
    }

    public boolean isLiteral(Term term) {
        return (term instanceof OWLLiteral);
    }


}
