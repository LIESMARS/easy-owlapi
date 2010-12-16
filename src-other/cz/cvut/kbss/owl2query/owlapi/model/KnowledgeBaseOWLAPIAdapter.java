package cz.cvut.kbss.owl2query.owlapi.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLOntologyManager;

import cz.cvut.kbss.owl2query.owlapi.util.Bool;


public class KnowledgeBaseOWLAPIAdapter implements KnowledgeBase {

	OWLOntologyManager m;
	
	@Override
	public void addIndividual(ATermAppl obj) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void addPropertyValue(ATermAppl pred, ATermAppl subj, ATermAppl obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addType(ATermAppl subj, ATermAppl obj) {
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
	public Set<ATermAppl> getAllClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ATermAppl> getAllEquivalentClasses(ATermAppl c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ATermAppl> getAnnotations(ATermAppl subject,
			ATermAppl property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ATermAppl> getClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ATermAppl> getComplements(ATermAppl known) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ATermAppl> getDataProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ATermAppl> getDataPropertyValues(ATermAppl predicate,
			ATermAppl subject) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List getDataPropertyValues(ATermAppl predicate, ATermAppl name,
			Datatype dtype) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ATermAppl> getDifferents(ATermAppl known) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Set<ATermAppl>> getDisjointClasses(ATermAppl known) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection getDomains(ATermAppl pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ATermAppl> getEquivalentClasses(ATermAppl aTermAppl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ATermAppl> getEquivalentProperties(ATermAppl aTermAppl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ATermAppl> getFunctionalProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ATermAppl> getIndividuals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends ATermAppl> getIndividualsWithAnnotation(
			ATermAppl property, ATermAppl object) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ATermAppl> getIndividualsWithProperty(ATermAppl pvP,
			ATermAppl pvIL) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ATermAppl> getInstances(ATermAppl rolledUpClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ATermAppl> getInstances(ATermAppl ic, boolean direct) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ATermAppl> getInverseFunctionalProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ATermAppl> getInverses(ATermAppl known) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ATermAppl> getObjectProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection getObjectPropertyValues(ATermAppl pred, ATermAppl subj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ATermAppl> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ATermAppl> getPropertyValues(ATermAppl pvP, ATermAppl pvI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RBox getRBox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection getRanges(ATermAppl pred) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Taxonomy<ATermAppl> getRoleTaxonomy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ATermAppl> getSames(ATermAppl known) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SizeEstimate getSizeEstimate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Set<ATermAppl>> getSubClasses(ATermAppl aTermAppl, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Set<ATermAppl>> getSubProperties(ATermAppl aTermAppl, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Set<ATermAppl>> getSuperClasses(ATermAppl scLHS, boolean direct) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Set<ATermAppl>> getSuperProperties(ATermAppl spLHS,
			boolean direct) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ATermAppl> getSymmetricProperties() {
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
	public Set<ATermAppl> getTransitiveProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Set<ATermAppl>> getTypes(ATermAppl aTermAppl, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bool hasKnownPropertyValue(ATermAppl aTermAppl,
			ATermAppl aTermAppl2, ATermAppl aTermAppl3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPropertyValue(ATermAppl aTermAppl, ATermAppl aTermAppl2,
			ATermAppl aTermAppl3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAnnotation(ATermAppl i, ATermAppl property, ATermAppl i2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAnnotationProperty(ATermAppl t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClass(ATermAppl o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClassified() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isComplement(ATermAppl aTermAppl, ATermAppl aTermAppl2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void isConsistent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isDatatypeProperty(ATermAppl pred) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDifferentFrom(ATermAppl aTermAppl, ATermAppl aTermAppl2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDisjoint(ATermAppl aTermAppl, ATermAppl aTermAppl2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEquivalentClass(ATermAppl aTermAppl, ATermAppl aTermAppl2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEquivalentProperty(ATermAppl aTermAppl,
			ATermAppl aTermAppl2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isFunctionalProperty(ATermAppl aTermAppl) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isIndividual(ATermAppl s) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInverse(ATermAppl aTermAppl, ATermAppl aTermAppl2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInverseFunctionalProperty(ATermAppl aTermAppl) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Bool isKnownType(ATermAppl aTermAppl, ATermAppl aTermAppl2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isObjectProperty(ATermAppl predicate) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOntologyProperty(ATermAppl p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProperty(ATermAppl p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSameAs(ATermAppl aTermAppl, ATermAppl aTermAppl2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void isSatisfiable(ATermAppl arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void isSatisfiable(Object makeNot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSubClassOf(ATermAppl aTermAppl, ATermAppl aTermAppl2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSubPropertyOf(ATermAppl aTermAppl, ATermAppl aTermAppl2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSymmetricProperty(ATermAppl aTermAppl) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTransitiveProperty(ATermAppl aTermAppl) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isType(ATermAppl testInd, ATermAppl testClass) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<?> retrieveIndividualsWithProperty(ATermAppl aTermAppl) {
		// TODO Auto-generated method stub
		return null;
	}

}
