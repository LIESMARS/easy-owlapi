package org.mindswap.pellet.utils;

import java.util.Set;

import cz.cvut.kbss.owl2query.owlapi.model.ATermAppl;

public interface SizeEstimate {

	void compute(Set<ATermAppl> concepts, Set<ATermAppl> properties);

	boolean isComputed(ATermAppl predicate);

	void computeAll();

	double avg(ATermAppl pred);

	double size(ATermAppl pred);

	int getClassCount();

	double getCost(KBOperation getDirectInstances);

	int avgInstancesPerClass(boolean direct);

	double avgSubjectsPerProperty();

	double avgPairsPerProperty();

	int getPropertyCount();

	int classesPerInstance(ATermAppl instance, boolean direct);

	double avgClassesPerInstance(boolean direct);

	int sames(ATermAppl saLHS);

	double avgSamesPerInstance();

	double getInstanceCount();

	int differents(ATermAppl dfLHS);

	double avgDifferentsPerInstance();

	int superClasses(ATermAppl clazzLHS, boolean direct);

	double avgSuperClasses(boolean direct);

	int equivClasses(ATermAppl clazzLHS);

	double avgEquivClasses();

	double avgSubClasses(boolean direct);

	int disjoints(ATermAppl dwLHS);

	int complements(ATermAppl coLHS);

	double avgComplementClasses();

	double avgSuperProperties(boolean direct);

	int superProperties(ATermAppl spLHS, boolean direct);

	int equivProperties(ATermAppl spLHS);

	double avgEquivProperties();

	int avgSubProperties(boolean direct);

	int inverses(ATermAppl ioLHS);

	double avgInverseProperties();

	int getObjectPropertyCount();

	int getDataPropertyCount();

	int getFunctionalPropertyCount();

	int getInverseFunctionalPropertyCount();

	int getTransitivePropertyCount();

	int getSymmetricPropertyCount();

}
