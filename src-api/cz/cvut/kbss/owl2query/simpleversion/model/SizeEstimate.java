package cz.cvut.kbss.owl2query.simpleversion.model;

import java.util.Set;

public interface SizeEstimate<G> {

	void compute(Set<G> concepts, Set<G> properties);

	boolean isComputed(G predicate);

	void computeAll();

	double avg(G pred);

	int size(G pred);

	int getClassCount();

	long getCost(KBOperation getDirectInstances);

	double avgInstancesPerClass(boolean direct);

	double avgSubjectsPerProperty();

	double avgPairsPerProperty();

	int classesPerInstance(G instance, boolean direct);

	double avgClassesPerInstance(boolean direct);

	double sames(G saLHS);

	double avgSamesPerInstance();

	int getInstanceCount();

	double differents(G dfLHS);

	double avgDifferentsPerInstance();

	double superClasses(G clazzLHS, boolean direct);

	double avgSuperClasses(boolean direct);

	double equivClasses(G clazzLHS);

	double avgEquivClasses();

	double avgSubClasses(boolean direct);

	// int disjoints(G dwLHS);
	//
	// int complements(G coLHS);
	//
	// double avgComplementClasses();
	//
	double avgSuperProperties(boolean direct);

	double superProperties(G spLHS, boolean direct);

	double equivProperties(G spLHS);

	double avgEquivProperties();

	double avgSubProperties(boolean direct);

	// int inverses(G ioLHS);
	//
	// double avgInverseProperties();
	//
	int getObjectPropertyCount();

	int getDataPropertyCount();
	//
	// int getFunctionalPropertyCount();
	//
	// int getInverseFunctionalPropertyCount();
	//
	// int getTransitivePropertyCount();
	//
	// int getSymmetricPropertyCount();

}
