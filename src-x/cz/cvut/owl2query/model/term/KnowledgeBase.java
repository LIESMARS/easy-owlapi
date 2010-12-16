package cz.cvut.owl2query.model.term;

import cz.cvut.owl2query.model.*;
import cz.cvut.owl2query.model.term.OWLClass;
import cz.cvut.owl2query.model.term.OWLDataProperty;
import cz.cvut.owl2query.model.term.OWLIndividual;
import cz.cvut.owl2query.model.term.OWLLiteral;
import cz.cvut.owl2query.model.term.OWLObjectProperty;
import cz.cvut.owl2query.model.term.QueryTermFactory;

import java.util.Collection;

public interface KnowledgeBase {

    public QueryTermFactory getFactory();

    public SizeEstimate getSizeEstimate();

    public OWLClass getOWLThing();

    public OWLClass getOWLNothing();

    public Collection<OWLIndividual> getIndividuals();

    public Collection<OWLClass> getClasses();

    public Collection<OWLClass> getDataPropertyDomains(final OWLDataProperty dp);

    public Collection<OWLClass> getObjectPropertyDomains(final OWLObjectProperty op);

    public Collection<OWLObjectProperty> getObjectProperties();

    public Collection<OWLDataProperty> getDataProperties();

    public boolean isConsistent();

    public void ensureConsistency();

    public boolean isClassified();

    public void classify();

    public boolean isRealized();

    public void realize();

    public Collection<OWLIndividual> getInstances(OWLClass clazz, boolean direct, boolean told);

    public boolean isInstanceOf(OWLIndividual instance, OWLClass clazz, boolean told);

    public Collection<OWLClass> getTypes(OWLIndividual instances, boolean told);

    public Collection<OWLIndividual> getObjectPropertyValues(OWLIndividual instance, OWLObjectProperty property, boolean told);

    public Collection<OWLLiteral> getDataPropertyValues(OWLIndividual instance, OWLDataProperty property, boolean told);
}
