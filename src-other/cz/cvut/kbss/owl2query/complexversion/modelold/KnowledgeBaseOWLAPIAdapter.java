package cz.cvut.kbss.owl2query.complexversion.modelold;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyManager;

import cz.cvut.kbss.owl2query.complexversion.model.Term;
import cz.cvut.kbss.owl2query.complexversion.util.Bool;


public class KnowledgeBaseOWLAPIAdapter implements KnowledgeBase {

	OWLOntologyManager m;
	
	@Override
	public void addIndividual(Term obj) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void addPropertyValue(Term pred, Term subj, Term obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addType(Term subj, Term obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public KnowledgeBase copy(boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void ensureConsistency() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ABox getABox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getAllClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getAllEquivalentClasses(Term c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Term> getAnnotations(Term subject,
			Term property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Term> getComplements(Term known) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getDataProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Term> getDataPropertyValues(Term predicate,
			Term subject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getDataPropertyValues(Term predicate, Term name,
			Datatype dtype) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Term> getDifferents(Term known) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Set<Term>> getDisjointClasses(Term known) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection getDomains(Term pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Term> getEquivalentClasses(Term Term) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getEquivalentProperties(Term Term) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getFunctionalProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getIndividuals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends Term> getIndividualsWithAnnotation(
			Term property, Term object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Term> getIndividualsWithProperty(Term pvP,
			Term pvIL) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getInstances(Term rolledUpClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getInstances(Term ic, boolean direct) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getInverseFunctionalProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Term> getInverses(Term known) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Term> getObjectProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection getObjectPropertyValues(Term pred, Term subj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Term> getPropertyValues(Term pvP, Term pvI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RBox getRBox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection getRanges(Term pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Taxonomy<Term> getRoleTaxonomy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getSames(Term known) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SizeEstimate getSizeEstimate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Set<Term>> getSubClasses(Term Term, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Set<Term>> getSubProperties(Term Term, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Set<Term>> getSuperClasses(Term scLHS, boolean direct) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Set<Term>> getSuperProperties(Term spLHS,
			boolean direct) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getSymmetricProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TBox getTBox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Taxonomy getTaxonomy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getTransitiveProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Set<Term>> getTypes(Term Term, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bool hasKnownPropertyValue(Term Term,
			Term Term2, Term Term3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPropertyValue(Term Term, Term Term2,
			Term Term3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAnnotation(Term i, Term property, Term i2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAnnotationProperty(Term t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClass(Term o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClassified() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isComplement(Term Term, Term Term2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void isConsistent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDatatypeProperty(Term pred) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDifferentFrom(Term Term, Term Term2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDisjoint(Term Term, Term Term2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEquivalentClass(Term Term, Term Term2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEquivalentProperty(Term Term,
			Term Term2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFunctionalProperty(Term Term) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIndividual(Term s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInverse(Term Term, Term Term2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInverseFunctionalProperty(Term Term) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Bool isKnownType(Term Term, Term Term2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isObjectProperty(Term predicate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOntologyProperty(Term p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProperty(Term p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSameAs(Term Term, Term Term2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void isSatisfiable(Term arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void isSatisfiable(Object makeNot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSubClassOf(Term Term, Term Term2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSubPropertyOf(Term Term, Term Term2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSymmetricProperty(Term Term) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTransitiveProperty(Term Term) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isType(Term testInd, Term testClass) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<?> retrieveIndividualsWithProperty(Term Term) {
		// TODO Auto-generated method stub
		return null;
	}

}
