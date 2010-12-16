package cz.cvut.kbss.owl2query.complexversion.modelold;

import java.util.Set;

import cz.cvut.kbss.owl2query.complexversion.model.Term;



public interface SizeEstimate {

	void compute(Set<Term> concepts, Set<Term> properties);

	boolean isComputed(Term predicate);

	void computeAll();

	double avg(Term pred);

	double size(Term pred);

	int getClassCount();

	double getCost(KBOperation getDirectInstances);

	int avgInstancesPerClass(boolean direct);

	double avgSubjectsPerProperty();

	double avgPairsPerProperty();

	int getPropertyCount();

	int classesPerInstance(Term instance, boolean direct);

	double avgClassesPerInstance(boolean direct);

	int sames(Term saLHS);

	double avgSamesPerInstance();

	double getInstanceCount();

	int differents(Term dfLHS);

	double avgDifferentsPerInstance();

	int superClasses(Term clazzLHS, boolean direct);

	double avgSuperClasses(boolean direct);

	int equivClasses(Term clazzLHS);

	double avgEquivClasses();

	double avgSubClasses(boolean direct);

	int disjoints(Term dwLHS);

	int complements(Term coLHS);

	double avgComplementClasses();

	double avgSuperProperties(boolean direct);

	int superProperties(Term spLHS, boolean direct);

	int equivProperties(Term spLHS);

	double avgEquivProperties();

	int avgSubProperties(boolean direct);

	int inverses(Term ioLHS);

	double avgInverseProperties();

	int getObjectPropertyCount();

	int getDataPropertyCount();

	int getFunctionalPropertyCount();

	int getInverseFunctionalPropertyCount();

	int getTransitivePropertyCount();

	int getSymmetricPropertyCount();

}
