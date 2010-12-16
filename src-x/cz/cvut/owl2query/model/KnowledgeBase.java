package cz.cvut.owl2query.model;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

public interface KnowledgeBase {

    public QueryTermFactory getFactory();

    public SizeEstimate getSizeEstimate();

    public GroundTerm getOWLThing();

    public GroundTerm getOWLNothing();

    public Set<Term> getIndividuals();

    public Set<Term> getClasses();

    public Set<Term> getObjectPropertyDomains(final Term op);

    public Set<Term> getDataPropertyDomains(final Term dp);

    public Set<Term> getObjectProperties();

    public Set<Term> getDataProperties();

    public boolean isConsistent();

    public void ensureConsistency();

    public boolean isClassified();

    public void classify();

    public boolean isRealized();

    public void realize();

    public boolean isObjectProperty(URI uri);

    public boolean isDataProperty(URI uri);
    
    public boolean isAnnotationProperty(URI uri);

    public Boolean isType(Term instance, Term clazz, boolean direct, boolean told);

    public Boolean isSubClassOf(Term subC, Term superC, boolean direct, boolean told);

    public Boolean isSubPropertyOf(Term subP, Term superP, boolean direct, boolean told);

    public Boolean hasPropertyValue(Term property, Term subject,  Term object, boolean told);
    
    public Collection<Term> getInstances(Term clazz, boolean direct, boolean told);

    public Collection<Term> getTypes(Term instances, boolean told);

    public Collection<Term> getObjectPropertyValues(Term instance, Term property, boolean told);

    public Collection<Term> getDataPropertyValues(Term instance, Term property, boolean told);
}
