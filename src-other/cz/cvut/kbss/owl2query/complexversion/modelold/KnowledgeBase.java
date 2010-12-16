package cz.cvut.kbss.owl2query.complexversion.modelold;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import cz.cvut.kbss.owl2query.complexversion.model.Term;
import cz.cvut.kbss.owl2query.complexversion.util.Bool;


public interface KnowledgeBase {

	SizeEstimate getSizeEstimate();

	Collection getDomains(Term pred);

	Collection getRanges(Term pred);

	boolean isClassified();

	Taxonomy getTaxonomy();

	Set<Term> getAllEquivalentClasses(Term c);

	boolean isIndividual(Term s);

	boolean isClass(Term o);

	boolean isProperty(Term p);

	boolean isDatatypeProperty(Term pred);

	void isConsistent();

	void addPropertyValue(Term pred, Term subj, Term obj);

	void addIndividual(Term obj);

	void addType(Term subj, Term obj);

	KnowledgeBase copy(boolean b);

	Set<Term> getIndividuals();

	RBox getRBox();

	Set<Term> getInstances(Term rolledUpClass);

	ABox getABox();

	List<Term> getDataPropertyValues(Term predicate, Term subject);

	Collection<?> retrieveIndividualsWithProperty(Term Term);

	boolean isObjectProperty(Term predicate);

	void ensureConsistency();

	boolean isAnnotationProperty(Term t);

	Bool isKnownType(Term Term, Term Term2);

	Bool hasKnownPropertyValue(Term Term, Term Term2,
			Term Term3);

	boolean isType(Term testInd, Term testClass);

	boolean isTransitiveProperty(Term Term);

	boolean isSymmetricProperty(Term Term);

	boolean isInverseFunctionalProperty(Term Term);

	boolean isFunctionalProperty(Term Term);

	boolean isInverse(Term Term, Term Term2);

	Set<Term> getEquivalentProperties(Term Term);

	boolean isSubPropertyOf(Term Term, Term Term2);

	Set<Set<Term>> getSubProperties(Term Term, boolean b);

	boolean isEquivalentProperty(Term Term, Term Term2);

	boolean isComplement(Term Term, Term Term2);

	boolean isDisjoint(Term Term, Term Term2);

	boolean isSubClassOf(Term Term, Term Term2);

	Collection<Term> getEquivalentClasses(Term Term);

	Set<Set<Term>> getSubClasses(Term Term, boolean b);

	boolean isEquivalentClass(Term Term, Term Term2);

	boolean isDifferentFrom(Term Term, Term Term2);

	boolean isSameAs(Term Term, Term Term2);

	boolean hasPropertyValue(Term Term, Term Term2,
			Term Term3);

	Set<Set<Term>> getTypes(Term Term, boolean b);

	TBox getTBox();

	void isSatisfiable(Term arg);

	void isSatisfiable(Object makeNot);

	Set<Term> getClasses();

	Set<Term> getInstances(Term ic, boolean direct);

	Set<Term> getAllClasses();

	Collection<Term> getPropertyValues(Term pvP, Term pvI);

	Collection<Term> getIndividualsWithProperty(Term pvP,
			Term pvIL);

	Set<Term> getProperties();

	Collection<Term> getObjectProperties();

	Set<Term> getSames(Term known);

	Collection<Term> getDifferents(Term known);

	boolean isAnnotation(Term i, Term property, Term i2);

	Collection<Term> getAnnotations(Term subject, Term property);

	Collection<? extends Term> getIndividualsWithAnnotation(
			Term property, Term object);

	Set<Set<Term>> getSuperClasses(Term scLHS, boolean direct);

	Collection<Set<Term>> getDisjointClasses(Term known);

	Collection<Term> getComplements(Term known);

	Taxonomy<Term> getRoleTaxonomy();

	Set<Set<Term>> getSuperProperties(Term spLHS, boolean direct);

	Collection<Term> getInverses(Term known);

	Set<Term> getSymmetricProperties();

	Set<Term> getDataProperties();

	Set<Term> getFunctionalProperties();

	Set<Term> getInverseFunctionalProperties();

	Set<Term> getTransitiveProperties();

	List getDataPropertyValues(Term predicate, Term name,
			Datatype dtype);

	Collection getObjectPropertyValues(Term pred, Term subj);

	boolean isOntologyProperty(Term p);
}
