package cz.cvut.owl2query.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Petr Kremen
 */
public class QueryImpl implements Query {
	// private static final Term DEFAULT_NAME = TermFactory.term( "query" );

	private static final String DEFAULT_NAME = "query";

	// COMMON PART
	private String name = DEFAULT_NAME;
	private List<QueryAtom> allAtoms;
	private KnowledgeBase kb;
	private List<Variable> resultVars;
	private Set<Variable> allVars;
	private Set<GroundTerm> individualsAndLiterals;
	private boolean ground;
	private boolean distinct;
	private Filter filter;
	private QueryParameters parameters;

	// // VARIABLES
	// private EnumMap<VarType, Set<Term>> distVars;
	public QueryImpl(final KnowledgeBase kb, final boolean distinct) {
		this.kb = kb;

		this.ground = true;
		this.allAtoms = new ArrayList<QueryAtom>();
		this.resultVars = new ArrayList<Variable>();
		this.allVars = new HashSet<Variable>();
		this.individualsAndLiterals = new HashSet<GroundTerm>();
		// this.distVars = new EnumMap<VarType, Set<Term>>(VarType.class);
		//
		// for (final VarType type : VarType.values()) {
		// distVars.put(type, new HashSet<Term>());
		// }

		this.distinct = distinct;
	}

	public QueryImpl(final Query query) {
		this(query.getKB(), query.isDistinct());

		this.name = query.getName();
		this.parameters = query.getQueryParameters();
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(final QueryAtom atom) {
		if (allAtoms.contains(atom)) {
			return;
		}
		allAtoms.add(atom);

		for (final Term a : atom.getArguments()) {
			if (a.isVariable()) {
				if (!allVars.contains(a.asVariable())) {
					allVars.add(a.asVariable());
				}
			} else {
				if (a.asGroundTerm().getType().equals(GroundTermType.Individual)
						|| a.asGroundTerm().getType().equals(GroundTermType.Literal)) {
					if (!individualsAndLiterals.contains(a.asGroundTerm())) {
						individualsAndLiterals.add(a.asGroundTerm());
					}
				}

			}
		}

		ground = ground && atom.isGround();
	}

	// /**
	// * {@inheritDoc}
	// */
	// public Set<Term> getDistVarsForType(final VarType type) {
	// return distVars.get(type);
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// public void addDistVar(Term a, final VarType type) {
	// Set<Term> set = distVars.get(type);
	//
	// if (!set.contains(a)) {
	// set.add(a);
	// }
	// }
	/**
	 * {@inheritDoc}
	 */
	public void addResultVar(Variable a) {
		resultVars.add(a);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<QueryAtom> getAtoms() {
		return Collections.unmodifiableList(allAtoms);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<GroundTerm> getConstants() {
		return Collections.unmodifiableSet(individualsAndLiterals);
	}

	// /**
	// * {@inheritDoc}
	// */
	// public Set<Term> getDistVars() {
	// final Set<Term> result = new HashSet<Term>();
	//
	// for (final VarType t : VarType.values()) {
	// result.addAll(distVars.get(t));
	// }
	//
	// return result;
	// }
	/**
	 * {@inheritDoc}
	 */
	public Set<Variable> getUndistVars() {
		final Set<Variable> result = new HashSet<Variable>(allVars);

		result.removeAll(getResultVars());

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Variable> getResultVars() {
		return Collections.unmodifiableList(resultVars);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Variable> getVars() {
		return Collections.unmodifiableSet(allVars);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isGround() {
		return ground;
	}

	/**
	 * {@inheritDoc}
	 */
	public KnowledgeBase getKB() {
		return kb;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setKB(KnowledgeBase kb) {
		this.kb = kb;
	}

	/**
	 * {@inheritDoc}
	 */
	public Query apply(final ResultBinding binding) {
		final List<QueryAtom> atoms = new ArrayList<QueryAtom>();

		for (final QueryAtom atom : getAtoms()) {
			atoms.add(atom.apply(binding));
		}

		final QueryImpl query = new QueryImpl(this);

		query.resultVars.addAll(this.resultVars);
		query.resultVars.removeAll(binding.getAllVariables());

		// for (final VarType type : VarType.values()) {
		// for (final Term atom : getDistVarsForType(type)) {
		// if (!binding.isBound(atom)) {
		// query.addDistVar(atom, type);
		// }
		// }
		// }

		for (final Variable atom : getResultVars()) {
			if (!binding.isBound(atom)) {
				query.addResultVar(atom);
			}
		}

		for (final QueryAtom atom : atoms) {
			query.add(atom);
		}

		return query;
	}

	/**
	 * {@inheritDoc} TODO
	 */
	public Term rollUpTo(final Term var, final Collection<Term> stopList,
			final boolean stopOnConstants) {
		// if( getDistVarsForType( VarType.LITERAL ).contains( var )
		// && !getDistVarsForType( VarType.INDIVIDUAL ).contains( var )
		// && !individualsAndLiterals.contains( var ) ) {
		// throw new InternalReasonerException(
		// "Trying to roll up to the variable '" + var
		// + "' which is not distinguished and individual." );
		// }
		//
		// ATermList classParts = ATermUtils.EMPTY_LIST;
		//
		// final Set<Term> visited = new HashSet<Term>();
		//
		// if( stopOnConstants ) {
		// visited.addAll( getConstants() );
		// }
		//
		// final Collection<QueryAtom> inEdges = findAtoms(
		// QueryPredicate.PropertyValue, null, null,
		// var );
		// for( final QueryAtom a : inEdges ) {
		// classParts = classParts.append( rollEdgeIn(
		// QueryPredicate.PropertyValue, a, visited,
		// stopList ) );
		// }
		//
		// final Collection<QueryAtom> outEdges = findAtoms(
		// QueryPredicate.PropertyValue, var, null,
		// null );
		// for( final QueryAtom a : outEdges ) {
		// classParts = classParts.append( rollEdgeOut(
		// QueryPredicate.PropertyValue, a, visited,
		// stopList ) );
		// }
		//
		// classParts = classParts.concat( getClasses( var ) );
		//
		// return ATermUtils.makeAnd( classParts );
		// }
		//
		// // TODO optimize - cache
		// private ATermList getClasses(final Term a) {
		// final List<Term> aterms = new ArrayList<Term>();
		//
		// for( final QueryAtom atom : findAtoms( QueryPredicate.Type, a, null )
		// ) {
		// final Term arg = atom.getArguments().get( 1 );
		// if( arg.isVariable() ) {
		// throw new InternalReasonerException(
		// "Variables as predicates are not supported yet" );
		// }
		// aterms.add( arg );
		// }
		//
		// if( !a.isVariable() ) {
		// aterms.add( ATermUtils.makeValue( a ) );
		// }
		//
		// return ATermUtils.makeList( aterms );
		// }
		//
		// /**
		// * TODO
		// */
		// private Term rollEdgeOut(final QueryPredicate allowed, final
		// QueryAtom atom,
		// final Set<Term> visited, final Collection<Term> stopList) {
		// switch ( atom.getPredicate() ) {
		// case PropertyValue:
		// final Term subj = atom.getArguments().get( 0 );
		// final Term pred = atom.getArguments().get( 1 );
		// final Term obj = atom.getArguments().get( 2 );
		//
		// if( ATermUtils.isVar( pred ) ) {
		// // variables as predicates are not supported yet.
		// return ATermUtils.TOP;
		// }
		//
		// visited.add( subj );
		//
		// if( visited.contains( obj ) ) {
		// ATermList temp = getClasses( obj );
		// if( temp.getLength() == 0 ) {
		// if( kb.isDatatypeProperty( pred ) )
		// // return ATermUtils.makeSoMin(pred, 1,
		// // ATermUtils.TOP_LIT);
		// return ATermUtils.makeSomeValues( pred, ATermUtils.TOP_LIT );
		// else
		// return ATermUtils.makeSomeValues( pred, ATermUtils.TOP );
		// }
		// else {
		// return ATermUtils.makeSomeValues( pred, ATermUtils.makeAnd( temp ) );
		// }
		//
		// }
		//
		// if( ATermUtils.isLiteral( obj ) ) {
		// Term type = ATermUtils.makeValue( obj );
		// return ATermUtils.makeSomeValues( pred, type );
		// }
		//
		// // TODO
		// // else if (litVars.contains(obj)) {
		// // Datatype dtype = getDatatype(obj);
		// //
		// // return ATermUtils.makeSomeValues(pred, dtype.getName());
		// // }
		//
		// ATermList targetClasses = getClasses( obj );
		//
		// for( final QueryAtom in : _findAtoms( stopList, allowed, null, null,
		// obj ) ) {
		// if( !in.equals( atom ) ) {
		// targetClasses = targetClasses.append( rollEdgeIn( allowed, in,
		// visited,
		// stopList ) );
		// }
		// }
		//
		// final List<QueryAtom> targetOuts = _findAtoms( stopList, allowed,
		// obj, null, null );
		//
		// if( targetClasses.isEmpty() ) {
		// if( targetOuts.size() == 0 ) {
		// // this is a simple leaf node
		// if( kb.isDatatypeProperty( pred ) )
		// return ATermUtils.makeSomeValues( pred, ATermUtils.TOP_LIT );
		// else
		// return ATermUtils.makeSomeValues( pred, ATermUtils.TOP );
		// }
		// else {
		// // not a leaf node, recurse over all outgoing edges
		// ATermList outs = ATermUtils.EMPTY_LIST;
		//
		// for( final QueryAtom currEdge : targetOuts ) {
		// outs = outs.append( rollEdgeOut( allowed, currEdge, visited, stopList
		// ) );
		// }
		//
		// return ATermUtils.makeSomeValues( pred, ATermUtils.makeAnd( outs ) );
		// }
		// }
		// else {
		// if( targetOuts.size() == 0 ) {
		// // this is a simple leaf node, but with classes specified
		// return ATermUtils.makeSomeValues( pred, ATermUtils.makeAnd(
		// targetClasses ) );
		// }
		// else {
		// // not a leaf node, recurse over all outgoing edges
		// ATermList outs = ATermUtils.EMPTY_LIST;
		//
		// for( final QueryAtom currEdge : targetOuts ) {
		// outs = outs.append( rollEdgeOut( allowed, currEdge, visited, stopList
		// ) );
		// }
		//
		// for( int i = 0; i < targetClasses.getLength(); i++ ) {
		// outs = outs.append( targetClasses.elementAt( i ) );
		// }
		//
		// return ATermUtils.makeSomeValues( pred, ATermUtils.makeAnd( outs ) );
		//
		// }
		// }
		// default:
		// throw new RuntimeException(
		// "This atom cannot be included to rolling-up : " + atom );
		// }
		// }
		//
		// // TODO this should die if called on a literal node
		// private Term rollEdgeIn(final QueryPredicate allowed, final QueryAtom
		// atom,
		// final Set<Term> visited, final Collection<Term> stopList) {
		// switch ( atom.getPredicate() ) {
		// case PropertyValue:
		// final Term subj = atom.getArguments().get( 0 );
		// final Term pred = atom.getArguments().get( 1 );
		// final Term obj = atom.getArguments().get( 2 );
		// Term invPred = kb.getRBox().getRole( pred ).getInverse().getName();
		//
		// if( pred.isVariable() ) {
		// throw new InternalReasonerException(
		// "Variables as predicates are not supported yet" );
		// // // TODO variables as predicates are not supported yet.
		// // return ATermUtils.TOP;
		// }
		//
		// visited.add( obj );
		//
		// if( visited.contains( subj ) ) {
		// ATermList temp = getClasses( subj );
		// if( temp.getLength() == 0 ) {
		// if( kb.isDatatypeProperty( invPred ) )
		// return ATermUtils.makeSomeValues( invPred, ATermUtils.TOP_LIT );
		// else
		// return ATermUtils.makeSomeValues( invPred, ATermUtils.TOP );
		// }
		// else {
		// return ATermUtils.makeSomeValues( invPred, ATermUtils.makeAnd( temp )
		// );
		// }
		// }
		//
		// ATermList targetClasses = getClasses( subj );
		//
		// final List<QueryAtom> targetIns = _findAtoms( stopList, allowed,
		// null, null, subj );
		//
		// for( final QueryAtom o : _findAtoms( stopList, allowed, subj, null,
		// null ) ) {
		// if( !o.equals( atom ) ) {
		// targetClasses = targetClasses.append( rollEdgeOut( allowed, o,
		// visited,
		// stopList ) );
		// }
		// }
		//
		// if( targetClasses.isEmpty() ) {
		// if( targetIns.isEmpty() ) {
		// // this is a simple leaf node
		// if( kb.isDatatypeProperty( pred ) )
		// return ATermUtils.makeSomeValues( invPred, ATermUtils.TOP_LIT );
		// else
		// return ATermUtils.makeSomeValues( invPred, ATermUtils.TOP );
		// }
		// else {
		// // not a leaf node, recurse over all incoming edges
		// ATermList ins = ATermUtils.EMPTY_LIST;
		//
		// for( QueryAtom currEdge : targetIns ) {
		// ins = ins.append( rollEdgeIn( allowed, currEdge, visited, stopList )
		// );
		// }
		//
		// return ATermUtils.makeSomeValues( invPred, ATermUtils.makeAnd( ins )
		// );
		// }
		// }
		// else {
		// if( targetIns.isEmpty() ) {
		// // this is a simple leaf node, but with classes specified
		//
		// return ATermUtils.makeSomeValues( invPred, ATermUtils.makeAnd(
		// targetClasses ) );
		// }
		// else {
		// // not a leaf node, recurse over all outgoing edges
		// ATermList ins = ATermUtils.EMPTY_LIST;
		//
		// for( QueryAtom currEdge : targetIns ) {
		// ins = ins.append( rollEdgeIn( allowed, currEdge, visited, stopList )
		// );
		// }
		//
		// for( int i = 0; i < targetClasses.getLength(); i++ ) {
		// ins = ins.append( targetClasses.elementAt( i ) );
		// }
		//
		// return ATermUtils.makeSomeValues( invPred, ATermUtils.makeAnd( ins )
		// );
		//
		// }
		// }
		// default:
		// throw new RuntimeException(
		// "This atom cannot be included to rolling-up : " + atom );
		//
		// }

		throw new UnsupportedOperationException("NOT IMPLEMENTED YET");
	}

	private List<QueryAtom> _findAtoms(final Collection<Term> stopList,
			final QueryPredicate predicate, final Term... args) {
		final List<QueryAtom> list = new ArrayList<QueryAtom>();
		for (final QueryAtom atom : allAtoms) {
			if (predicate.equals(atom.getPredicate())) {
				int i = 0;
				boolean add = true;
				for (final Term arg : atom.getArguments()) {
					final Term argValue = args[i++];
					if ((argValue != null && argValue != arg)
							|| stopList.contains(arg)) {
						add = false;
						break;
					}
				}

				if (add) {
					list.add(atom);
				}
			}
		}
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<QueryAtom> findAtoms(final QueryPredicate predicate,
			final Term... args) {
		return _findAtoms(Collections.<Term> emptySet(), predicate, args);
	}

	/**
	 * {@inheritDoc}
	 */
	public Query reorder(int[] ordering) {
		if (ordering.length != allAtoms.size()) {
			throw new QueryEvaluationException(
					"Ordering permutation must be of the same size as the query : "
							+ ordering.length);
		}
		final QueryImpl newQuery = new QueryImpl(this);

		// shallow copies for faster processing
		for (int j = 0; j < ordering.length; j++) {
			newQuery.allAtoms.add(allAtoms.get(ordering[j]));
		}

		newQuery.allVars = allVars;
		newQuery.individualsAndLiterals = individualsAndLiterals;
		newQuery.resultVars = resultVars;
		newQuery.ground = ground;

		return newQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	public void remove(QueryAtom atom) {
		if (!allAtoms.contains(atom)) {
			return;
		}

		allAtoms.remove(atom);

		final Set<Term> rest = new HashSet<Term>();

		boolean ground = true;

		for (final QueryAtom atom2 : allAtoms) {
			ground &= atom2.isGround();
			rest.addAll(atom2.getArguments());
		}

		this.ground = ground;

		final Set<Term> toRemove = new HashSet<Term>(atom.getArguments());
		toRemove.removeAll(rest);

		for (final Term a : toRemove) {
			allVars.remove(a);
			resultVars.remove(a);
			individualsAndLiterals.remove(a);
		}
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean multiLine) {
		final String indent = multiLine ? "     " : " ";
		final StringBuffer sb = new StringBuffer();

		sb.append(name + "(");
		for (int i = 0; i < resultVars.size(); i++) {
			Term var = resultVars.get(i);
			if (i > 0) {
				sb.append(", ");
			}
			sb.append(var);
		}
		sb.append(")");

		if (allAtoms.size() > 0) {
			sb.append(" :-");
			if (multiLine) {
				sb.append("\n");
			}
			for (int i = 0; i < allAtoms.size(); i++) {
				final QueryAtom a = allAtoms.get(i);
				if (i > 0) {
					sb.append(",");
					if (multiLine) {
						sb.append("\n");
					}
				}

				sb.append(indent);
				sb.append(a.toString()); // TODO qNameProvider
			}
		}

		sb.append(".");
		if (multiLine) {
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * {@inheritDoc}
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public void setQueryParameters(QueryParameters parameters) {
		this.parameters = parameters;
	}

	public QueryParameters getQueryParameters() {
		return parameters;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
