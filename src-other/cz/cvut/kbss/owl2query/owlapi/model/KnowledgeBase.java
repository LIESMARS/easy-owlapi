package cz.cvut.kbss.owl2query.owlapi.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;


import cz.cvut.kbss.owl2query.owlapi.util.Bool;


public interface KnowledgeBase {

	SizeEstimate getSizeEstimate();

	Collection getDomains(ATermAppl pred);

	Collection getRanges(ATermAppl pred);

	boolean isClassified();

	Taxonomy getTaxonomy();

	Set<ATermAppl> getAllEquivalentClasses(ATermAppl c);

	boolean isIndividual(ATermAppl s);

	boolean isClass(ATermAppl o);

	boolean isProperty(ATermAppl p);

	boolean isDatatypeProperty(ATermAppl pred);

	void isConsistent();

	void addPropertyValue(ATermAppl pred, ATermAppl subj, ATermAppl obj);

	void addIndividual(ATermAppl obj);

	void addType(ATermAppl subj, ATermAppl obj);

	KnowledgeBase copy(boolean b);

	Set<ATermAppl> getIndividuals();

	RBox getRBox();

	Set<ATermAppl> getInstances(ATermAppl rolledUpClass);

	ABox getABox();

	List<ATermAppl> getDataPropertyValues(ATermAppl predicate, ATermAppl subject);

	Collection<?> retrieveIndividualsWithProperty(ATermAppl aTermAppl);

	boolean isObjectProperty(ATermAppl predicate);

	void ensureConsistency();

	boolean isAnnotationProperty(ATermAppl t);

	Bool isKnownType(ATermAppl aTermAppl, ATermAppl aTermAppl2);

	Bool hasKnownPropertyValue(ATermAppl aTermAppl, ATermAppl aTermAppl2,
			ATermAppl aTermAppl3);

	boolean isType(ATermAppl testInd, ATermAppl testClass);

	boolean isTransitiveProperty(ATermAppl aTermAppl);

	boolean isSymmetricProperty(ATermAppl aTermAppl);

	boolean isInverseFunctionalProperty(ATermAppl aTermAppl);

	boolean isFunctionalProperty(ATermAppl aTermAppl);

	boolean isInverse(ATermAppl aTermAppl, ATermAppl aTermAppl2);

	Set<ATermAppl> getEquivalentProperties(ATermAppl aTermAppl);

	boolean isSubPropertyOf(ATermAppl aTermAppl, ATermAppl aTermAppl2);

	Set<Set<ATermAppl>> getSubProperties(ATermAppl aTermAppl, boolean b);

	boolean isEquivalentProperty(ATermAppl aTermAppl, ATermAppl aTermAppl2);

	boolean isComplement(ATermAppl aTermAppl, ATermAppl aTermAppl2);

	boolean isDisjoint(ATermAppl aTermAppl, ATermAppl aTermAppl2);

	boolean isSubClassOf(ATermAppl aTermAppl, ATermAppl aTermAppl2);

	Collection<ATermAppl> getEquivalentClasses(ATermAppl aTermAppl);

	Set<Set<ATermAppl>> getSubClasses(ATermAppl aTermAppl, boolean b);

	boolean isEquivalentClass(ATermAppl aTermAppl, ATermAppl aTermAppl2);

	boolean isDifferentFrom(ATermAppl aTermAppl, ATermAppl aTermAppl2);

	boolean isSameAs(ATermAppl aTermAppl, ATermAppl aTermAppl2);

	boolean hasPropertyValue(ATermAppl aTermAppl, ATermAppl aTermAppl2,
			ATermAppl aTermAppl3);

	Set<Set<ATermAppl>> getTypes(ATermAppl aTermAppl, boolean b);

	TBox getTBox();

	void isSatisfiable(ATermAppl arg);

	void isSatisfiable(Object makeNot);

	Set<ATermAppl> getClasses();

	Set<ATermAppl> getInstances(ATermAppl ic, boolean direct);

	Set<ATermAppl> getAllClasses();

	Collection<ATermAppl> getPropertyValues(ATermAppl pvP, ATermAppl pvI);

	Collection<ATermAppl> getIndividualsWithProperty(ATermAppl pvP,
			ATermAppl pvIL);

	Set<ATermAppl> getProperties();

	Collection<ATermAppl> getObjectProperties();

	Set<ATermAppl> getSames(ATermAppl known);

	Collection<ATermAppl> getDifferents(ATermAppl known);

	boolean isAnnotation(ATermAppl i, ATermAppl property, ATermAppl i2);

	Collection<ATermAppl> getAnnotations(ATermAppl subject, ATermAppl property);

	Collection<? extends ATermAppl> getIndividualsWithAnnotation(
			ATermAppl property, ATermAppl object);

	Set<Set<ATermAppl>> getSuperClasses(ATermAppl scLHS, boolean direct);

	Collection<Set<ATermAppl>> getDisjointClasses(ATermAppl known);

	Collection<ATermAppl> getComplements(ATermAppl known);

	Taxonomy<ATermAppl> getRoleTaxonomy();

	Set<Set<ATermAppl>> getSuperProperties(ATermAppl spLHS, boolean direct);

	Collection<ATermAppl> getInverses(ATermAppl known);

	Set<ATermAppl> getSymmetricProperties();

	Set<ATermAppl> getDataProperties();

	Set<ATermAppl> getFunctionalProperties();

	Set<ATermAppl> getInverseFunctionalProperties();

	Set<ATermAppl> getTransitiveProperties();

	List getDataPropertyValues(ATermAppl predicate, ATermAppl name,
			Datatype dtype);

	Collection getObjectPropertyValues(ATermAppl pred, ATermAppl subj);

	boolean isOntologyProperty(ATermAppl p);
}
