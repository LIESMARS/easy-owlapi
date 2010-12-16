package cz.cvut.kbss.owl2query.complexversion.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cvut.kbss.owl2query.complexversion.modelold.ABox;
import cz.cvut.kbss.owl2query.complexversion.modelold.DependencySet;
import cz.cvut.kbss.owl2query.complexversion.modelold.KnowledgeBase;
import cz.cvut.kbss.owl2query.complexversion.modelold.MultiQueryResults;
import cz.cvut.kbss.owl2query.complexversion.modelold.NotQueryAtom;
import cz.cvut.kbss.owl2query.complexversion.modelold.PelletOptions;
import cz.cvut.kbss.owl2query.complexversion.modelold.Query;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryAtom;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryAtomFactory;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryImpl;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryPredicate;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryResult;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryResultImpl;
import cz.cvut.kbss.owl2query.complexversion.modelold.ResultBinding;
import cz.cvut.kbss.owl2query.complexversion.modelold.ResultBindingImpl;
import cz.cvut.kbss.owl2query.complexversion.modelold.Query.VarType;
import cz.cvut.kbss.owl2query.complexversion.model.Term;
import cz.cvut.kbss.owl2query.complexversion.exceptions.InternalReasonerException;
import cz.cvut.kbss.owl2query.complexversion.parser.ARQParser;
import cz.cvut.kbss.owl2query.complexversion.parser.QueryParser;
import cz.cvut.kbss.owl2query.complexversion.util.ATermUtils;
import cz.cvut.kbss.owl2query.complexversion.util.Bool;
import cz.cvut.kbss.owl2query.complexversion.util.DisjointSet;
import cz.cvut.kbss.owl2query.complexversion.util.Pair;

public class QueryEngine {
	public static Logger log = Logger.getLogger(QueryEngine.class.getName());

	public static CoreStrategy STRATEGY = CoreStrategy.ALLFAST;

	public static QueryExec getQueryExec() {
		return new CombinedQueryEngine();
	}

	public static QueryParser getParser() {
		return new ARQParser();
	}

	public static boolean supports(final Query query,
			@SuppressWarnings("unused") final KnowledgeBase kb) {
		return getQueryExec().supports(query);
	}

	public static QueryResult exec(final Query query, final KnowledgeBase kb) {
		KnowledgeBase queryKB = query.getKB();
		query.setKB(kb);
		QueryResult result = exec(query);
		query.setKB(queryKB);
		return result;
	}

	public static QueryResult exec(final Query query) {
		query.getKB().ensureConsistency();

		if (query.getAtoms().isEmpty()) {
			final QueryResultImpl results = new QueryResultImpl(query);
			results.add(new ResultBindingImpl());
			return results;
		}

		// PREPROCESSING
		if (log.isLoggable(Level.FINE)) {
			log.fine("Preprocessing:\n" + query);
		}
		Query preprocessed = preprocess(query);

		// SIMPLIFICATION
		if (PelletOptions.SIMPLIFY_QUERY) {
			if (log.isLoggable(Level.FINE)) {
				log.fine("Simplifying:\n" + preprocessed);
			}

			// final Timer t = new Timer( "simplification" );
			// t.start();
			// TODO timer start

			simplify(preprocessed);
			// t.stop();
			// TODO timer stop

			// if( log.isLoggable( Level.FINE ) ) {
			// log.fine( "Simplification took " + t.getLast() + " ms." );
			// }
		}

		// SPLITTING
		if (log.isLoggable(Level.FINE)) {
			log.fine("Splitting:\n" + preprocessed);
		}
		// final Timer t = new Timer("query splitting");
		// t.start();
		// TODO timer start

		final List<Query> queries = split(preprocessed);
		// t.stop();
		// TODO timer stop

		// if (log.isLoggable(Level.FINE)) {
		// log.fine("Query splitting took " + t.getLast() + " ms.");
		// }

		if (queries.isEmpty()) {
			throw new InternalReasonerException(
					"Splitting query returned no results!");
		} else if (queries.size() == 1) {
			final QueryResult r = execSingleQuery(queries.get(0));
			return r;
		} else {
			final List<QueryResult> results = new ArrayList<QueryResult>(
					queries.size());
			for (final Query q : queries) {
				results.add(execSingleQuery(q));
			}

			return new MultiQueryResults(query.getResultVars(), results);
		}
	}

	private static boolean isObjectProperty(Term t, KnowledgeBase kb) {
		if (!ATermUtils.isVar(t) && !kb.isObjectProperty(t)) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Undefined object property used in query: " + t);
			return false;
		}

		return true;
	}

	private static boolean isDatatypeProperty(Term t, KnowledgeBase kb) {
		if (!ATermUtils.isVar(t) && !kb.isDatatypeProperty(t)) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Undefined datatype property used in query: " + t);
			return false;
		}

		return true;
	}

	private static boolean isProperty(Term t, KnowledgeBase kb) {
		if (!ATermUtils.isVar(t) && !kb.isObjectProperty(t)
				&& !kb.isDatatypeProperty(t) && !kb.isAnnotationProperty(t)) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Not an object/data/annotation property: " + t);
			return false;
		}

		return true;
	}

	private static boolean isIndividual(Term t, KnowledgeBase kb) {
		if (!ATermUtils.isVar(t) && !kb.isIndividual(t)) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Undefined individual used in query: " + t);
			return false;
		}

		return true;
	}

	private static boolean isClass(Term t, KnowledgeBase kb) {
		if (!ATermUtils.isVar(t) && !kb.isClass(t)) {
			if (log.isLoggable(Level.WARNING))
				log.warning("Undefined class used in query: " + t);
			return false;
		}

		return true;
	}

	private static boolean hasUndefinedTerm(Query query) {
		KnowledgeBase kb = query.getKB();
		boolean allDefined = true;

		for (QueryAtom atom : query.getAtoms()) {
			final List<Term> args = atom.getArguments();

			// TODO in various parts object/data property checks should be
			// strengthened
			switch (atom.getPredicate()) {
			case Type:
			case DirectType:
				allDefined &= isIndividual(args.get(0), kb);
				allDefined &= isClass(args.get(1), kb);
				break;
			case PropertyValue:
				Term s = args.get(0);
				Term p = args.get(1);
				Term o = args.get(2);
				allDefined &= isIndividual(s, kb);
				if (ATermUtils.isVar(o)) {
					allDefined &= isProperty(p, kb);
				} else if (ATermUtils.isLiteral(o)) {
					allDefined &= isDatatypeProperty(p, kb);
				} else {
					allDefined &= isObjectProperty(p, kb);
					allDefined &= isIndividual(o, kb);
				}
				break;
			case SameAs:
			case DifferentFrom:
				allDefined &= isIndividual(args.get(0), kb);
				allDefined &= isIndividual(args.get(1), kb);
				break;
			case DatatypeProperty:
				allDefined &= isDatatypeProperty(args.get(0), kb);
				break;
			case ObjectProperty:
			case Transitive:
			case InverseFunctional:
			case Symmetric:
				allDefined &= isObjectProperty(args.get(0), kb);
				break;
			case Functional:
				allDefined &= isProperty(args.get(0), kb);
				break;
			case InverseOf:
				allDefined &= isObjectProperty(args.get(0), kb);
				allDefined &= isObjectProperty(args.get(1), kb);
				break;
			case SubPropertyOf:
			case EquivalentProperty:
			case StrictSubPropertyOf:
			case DirectSubPropertyOf:
				allDefined &= isProperty(args.get(0), kb);
				allDefined &= isProperty(args.get(1), kb);
				break;
			case SubClassOf:
			case EquivalentClass:
			case DisjointWith:
			case ComplementOf:
			case StrictSubClassOf:
			case DirectSubClassOf:
				allDefined &= isClass(args.get(0), kb);
				allDefined &= isClass(args.get(1), kb);
				break;
			default:
				break;
			}

			if (!allDefined)
				break;
		}

		return !allDefined;
	}

	private static QueryResult execSingleQuery(Query query) {
		if (hasUndefinedTerm(query)) {
			return new QueryResultImpl(query);
		}

		// if (PelletOptions.SAMPLING_RATIO > 0) {
		// if (log.isLoggable( Level.FINE ))
		// log.fine("Reorder\n" + query);
		//
		// query = reorder(query);

		return getQueryExec().exec(query);
	}

	/**
	 * If a query has disconnected components such as C(x), D(y) then it should
	 * be answered as two separate queries. The answers to each query should be
	 * combined at the end by taking Cartesian product.(we combine results on a
	 * tuple basis as results are iterated. This way we avoid generating the
	 * full Cartesian product. Splitting the query ensures the correctness of
	 * the answer, e.g. rolling-up technique becomes applicable.
	 * 
	 * @param query
	 *            Query to be split
	 * @return List of queries (contains the initial query if the initial query
	 *         is connected)
	 */
	public static List<Query> split(Query query) {
		try {
			final Set<Term> resultVars = new HashSet<Term>(query
					.getResultVars());

			final DisjointSet<Term> disjointSet = new DisjointSet<Term>();

			for (final QueryAtom atom : query.getAtoms()) {
				Term toMerge = null;

				for (final Term arg : atom.getArguments()) {
					if (!ATermUtils.isVar(arg))
						continue;

					disjointSet.add(arg);
					if (toMerge != null) {
						disjointSet.union(toMerge, arg);
					}
					toMerge = arg;
				}
			}

			final Collection<Set<Term>> equivalenceSets = disjointSet
					.getEquivalanceSets();

			if (equivalenceSets.size() == 1)
				return Collections.singletonList(query);

			final Map<Term, Query> queries = new HashMap<Term, Query>();
			Query groundQuery = null;
			for (final QueryAtom atom : query.getAtoms()) {
				Term representative = null;
				for (final Term arg : atom.getArguments()) {
					if (ATermUtils.isVar(arg)) {
						representative = disjointSet.find(arg);
						break;
					}
				}

				Query newQuery = null;
				if (representative == null) {
					if (groundQuery == null) {
						groundQuery = new QueryImpl(query);
					}
					newQuery = groundQuery;
				} else {
					newQuery = queries.get(representative);
					if (newQuery == null) {
						newQuery = new QueryImpl(query);
						queries.put(representative, newQuery);
					}
					for (final Term arg : atom.getArguments()) {
						if (resultVars.contains(arg)) {
							newQuery.addResultVar(arg);
						}

						for (final VarType v : VarType.values()) {
							if (query.getDistVarsForType(v).contains(arg)) {
								newQuery.addDistVar(arg, v);
							}
						}
					}
				}

				newQuery.add(atom);
			}

			final List<Query> list = new ArrayList<Query>(queries.values());

			if (groundQuery != null) {
				list.add(0, groundQuery);
			}

			return list;
		} catch (RuntimeException e) {
			log.log(Level.WARNING,
					"Query split failed, continuing with query execution.", e);
			return Collections.singletonList(query);
		}
	}

	/**
	 * Simplifies the query.
	 * 
	 * @param query
	 */
	private static void simplify(Query query) {
		domainRangeSimplification(query);
	}

	private static Query preprocess(final Query query) {
		Query q = query;

		// SAMEAS
		// get rid of SameAs atoms that contain at least one undistinguished
		// variable.
		for (final QueryAtom atom : q.findAtoms(QueryPredicate.SameAs, null,
				null)) {
			final Term a1 = atom.getArguments().get(0);
			final Term a2 = atom.getArguments().get(1);

			if (!ATermUtils.isVar(a1) || q.getUndistVars().contains(a1)) {
				final ResultBinding b = new ResultBindingImpl();
				b.setValue(a2, a1);
				q = q.apply(b);
			} else if (!ATermUtils.isVar(a2) || q.getUndistVars().contains(a2)) {
				final ResultBinding b = new ResultBindingImpl();
				b.setValue(a1, a2);
				q = q.apply(b);
			}
		}

		// get rid of SameAs with same arguments - in a separate for cycle to
		// find all such atoms.
		for (final QueryAtom atom : q.findAtoms(QueryPredicate.SameAs, null,
				null)) {
			if (atom.getArguments().get(0).equals(atom.getArguments().get(1))) {
				q.remove(atom);
			}
		}

		// Undistinguished variables + CLASS and PROPERTY variables
		// TODO bug : queries Type(_:x,?x) and PropertyValue(_:x, ?x, . ) and
		// PropertyValue(., ?x, _:x) have to be enriched with one more atom
		// evaluating class/property DVs.
		for (final QueryAtom a : new HashSet<QueryAtom>(q.getAtoms())) {
			switch (a.getPredicate()) {
			case Type:
			case DirectType:
				final Term clazz = a.getArguments().get(1);

				if (q.getUndistVars().contains(a.getArguments().get(0))
						&& q.getDistVars().contains(clazz)) {
					q.add(QueryAtomFactory.SubClassOfAtom(clazz, clazz));
				}
				break;
			case PropertyValue:
				final Term property = a.getArguments().get(1);

				if ((q.getUndistVars().contains(a.getArguments().get(0)) || (q
						.getUndistVars().contains(a.getArguments().get(2))))
						&& q.getDistVars().contains(property)) {
					q.add(QueryAtomFactory
							.SubPropertyOfAtom(property, property));
				}
				break;
			default:
				break;
			}
		}

		return q;
	}

	public static CoreStrategy getStrategy(
			@SuppressWarnings("unused") final QueryAtom core) {
		return STRATEGY;
	}

	private static void domainRangeSimplification(Query query) {
		final Map<Term, Set<Term>> allInferredTypes = new HashMap<Term, Set<Term>>();

		final KnowledgeBase kb = query.getKB();
		final Set<Term> vars = query.getVars(); // getObjVars

		for (final Term var : vars) {
			final Set<Term> inferredTypes = new HashSet<Term>();

			// domain simplification
			for (final QueryAtom pattern : query.findAtoms(
					QueryPredicate.PropertyValue, var, null, null)) {
				if (!ATermUtils.isVar(pattern.getArguments().get(1))) {
					inferredTypes.addAll(kb.getDomains(pattern.getArguments()
							.get(1)));
				}
			}

			// range simplification
			for (final QueryAtom pattern : query.findAtoms(
					QueryPredicate.PropertyValue, null, null, var)) {
				if (!ATermUtils.isVar(pattern.getArguments().get(1))) {
					inferredTypes.addAll(kb.getRanges(pattern.getArguments()
							.get(1)));
				}
			}

			if (!inferredTypes.isEmpty())
				allInferredTypes.put(var, inferredTypes);
		}

		for (final QueryAtom atom : new ArrayList<QueryAtom>(query.getAtoms())) {
			if (atom.getPredicate() == QueryPredicate.Type) {
				final Term inst = atom.getArguments().get(0);
				final Term clazz = atom.getArguments().get(1);
				if (!ATermUtils.isVar(clazz)) {
					final Set<Term> inferred = allInferredTypes.get(inst);
					if ((inferred != null) && !inferred.isEmpty()) {
						if (inferred.contains(clazz)) {
							query.remove(atom);
						} else if (kb.isClassified()) {
							final Set<Term> subs = kb.getTaxonomy()
									.getFlattenedSubs(clazz, false);
							final Set<Term> eqs = kb
									.getAllEquivalentClasses(clazz);
							if (!Collections.disjoint(inferred, subs)
									|| !Collections.disjoint(inferred, eqs))
								query.remove(atom);
						}
					}
				}
			}
		}
	}

	/**
	 * Executes all boolean ABox atoms
	 * 
	 * @param query
	 * @return
	 */
	public static boolean execBooleanABoxQuery(final Query query) {
		// if (!query.getDistVars().isEmpty()) {
		// throw new InternalReasonerException(
		// "Executing execBoolean with nonboolean query : " + query);
		// }

		boolean querySatisfied;

		final KnowledgeBase kb = query.getKB();
		kb.ensureConsistency();

		// unless proven otherwise all (ground) triples are satisfied
		Bool allTriplesSatisfied = Bool.TRUE;

		for (final QueryAtom atom : query.getAtoms()) {
			// by default we don't know if triple is satisfied
			Bool tripleSatisfied = Bool.UNKNOWN;
			// we can only check ground triples
			if (atom.isGround()) {
				final List<Term> arguments = atom.getArguments();

				switch (atom.getPredicate()) {
				case Type:
					tripleSatisfied = kb.isKnownType(arguments.get(0),
							arguments.get(1));
					break;
				case Annotation:
				case PropertyValue:
					tripleSatisfied = kb.hasKnownPropertyValue(
							arguments.get(0), arguments.get(1), arguments
									.get(2));
					break;
				default:
					tripleSatisfied = Bool.UNKNOWN;
				}
			}

			// if we cannot decide the truth value of this triple (without a
			// consistency
			// check) then over all truth value cannot be true. However, we will
			// continue
			// to see if there is a triple that is obviously false
			if (tripleSatisfied.isUnknown())
				allTriplesSatisfied = Bool.UNKNOWN;
			else if (tripleSatisfied.isFalse()) {
				// if one triple is false then the whole query, which is the
				// conjunction of
				// all triples, is false. We can stop now.
				allTriplesSatisfied = Bool.FALSE;

				if (log.isLoggable(Level.FINER))
					log.finer("Failed atom: " + atom);

				break;
			}
		}

		// if we reached a verdict, return it
		if (allTriplesSatisfied.isKnown()) {
			querySatisfied = allTriplesSatisfied.isTrue();
		} else {
			// do the unavoidable consistency check
			if (!query.getConstants().isEmpty()) {
				final Term testInd = query.getConstants().iterator()
						.next();
				final Term testClass = query.rollUpTo(testInd, Collections
						.<Term> emptySet(), false);

				if (log.isLoggable(Level.FINER))
					log.finer("Boolean query: " + testInd + " -> " + testClass);

				querySatisfied = kb.isType(testInd, testClass);
			} else {
				final Term testVar = query.getUndistVars().iterator()
						.next();
				final Term testClass = query.rollUpTo(testVar, Collections
						.<Term> emptySet(), false);

				List<Pair<Term, DependencySet>> UC = kb.getTBox().getUC();
				Term newUC = ATermUtils.normalize(ATermUtils
						.makeNot(testClass));

				UC.add(new Pair<Term, DependencySet>(newUC,
						DependencySet.INDEPENDENT));

				ABox copy = kb.getABox().copy();
				copy.setInitialized(false);
				querySatisfied = !copy.isConsistent();

				UC.remove(UC.size() - 1);
			}
		}

		return querySatisfied;
	}

	public static boolean checkGround(final QueryAtom atom,
			final KnowledgeBase kb) {

		final List<Term> arguments = atom.getArguments();

		switch (atom.getPredicate()) {
		case Type:
			return kb.isType(arguments.get(0), arguments.get(1));
		case DirectType:
			return kb.getTypes(arguments.get(0), true).contains(
					arguments.get(1));
		case Annotation:
		case PropertyValue:
			return kb.hasPropertyValue(arguments.get(0), arguments.get(1),
					arguments.get(2));
		case SameAs:
			return kb.isSameAs(arguments.get(0), arguments.get(1));
		case DifferentFrom:
			return kb.isDifferentFrom(arguments.get(0), arguments.get(1));
		case EquivalentClass:
			return kb.isEquivalentClass(arguments.get(0), arguments.get(1));
		case SubClassOf:
			return kb.isSubClassOf(arguments.get(0), arguments.get(1));
		case DirectSubClassOf:
			for (final Set<Term> a : kb.getSubClasses(arguments.get(1),
					true)) {
				if (a.contains(arguments.get(0))) {
					return true;
				}
			}
			return false;
		case StrictSubClassOf:
			return kb.isSubClassOf(arguments.get(0), arguments.get(1))
					&& !kb.getEquivalentClasses(arguments.get(1)).contains(
							arguments.get(0));
		case DisjointWith:
			return kb.isDisjoint(arguments.get(0), arguments.get(1));
		case ComplementOf:
			return kb.isComplement(arguments.get(0), arguments.get(1));
		case EquivalentProperty:
			return kb.isEquivalentProperty(arguments.get(0), arguments.get(1));
		case SubPropertyOf:
			return kb.isSubPropertyOf(arguments.get(0), arguments.get(1));
		case DirectSubPropertyOf:
			for (final Set<Term> a : kb.getSubProperties(arguments.get(1),
					true)) {
				if (a.contains(arguments.get(0))) {
					return true;
				}
			}
			return false;
		case StrictSubPropertyOf:
			return kb.isSubPropertyOf(arguments.get(0), arguments.get(1))
					&& !kb.getEquivalentProperties(arguments.get(1)).contains(
							arguments.get(0));
		case InverseOf:
			return kb.isInverse(arguments.get(0), arguments.get(1));
		case ObjectProperty:
			return kb.isObjectProperty(arguments.get(0));
		case DatatypeProperty:
			return kb.isDatatypeProperty(arguments.get(0));
		case Functional:
			return kb.isFunctionalProperty(arguments.get(0));
		case InverseFunctional:
			return kb.isInverseFunctionalProperty(arguments.get(0));
		case Symmetric:
			return kb.isSymmetricProperty(arguments.get(0));
		case Transitive:
			return kb.isTransitiveProperty(arguments.get(0));
		case Not:
			return !checkGround(((NotQueryAtom) atom).getAtom(), kb);
		case Execute:
			return !exec(QueryRegistry.getQuery(atom), kb).isEmpty();
		default:
			throw new IllegalArgumentException("Unknown atom type : "
					+ atom.getPredicate());
		}
	}
}
