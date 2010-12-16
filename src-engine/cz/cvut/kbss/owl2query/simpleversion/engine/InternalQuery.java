package cz.cvut.kbss.owl2query.simpleversion.engine;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cz.cvut.kbss.owl2query.simpleversion.model.GroundTerm;
import cz.cvut.kbss.owl2query.simpleversion.model.OWL2Ontology;
import cz.cvut.kbss.owl2query.simpleversion.model.OWL2Query;
import cz.cvut.kbss.owl2query.simpleversion.model.Term;
import cz.cvut.kbss.owl2query.simpleversion.model.VarType;
import cz.cvut.kbss.owl2query.simpleversion.model.Variable;

public interface InternalQuery<G> extends OWL2Query<G> {

	/**
	 * Returns true if distinct results are required.
	 * 
	 * @return
	 */
	public boolean isDistinct();

	/**
	 * Returns variables that are of at least one of the given type.
	 * 
	 * @return
	 */
	public Set<Variable<G>> getDistVarsOfTypes(final VarType... type);

	/**
	 * Adds an query atom to the query.
	 * 
	 * @param atom
	 */
	public InternalQuery<G> add(final QueryAtom<G> atom);

	/**
	 * Return all the variables used in this query.
	 * 
	 * @return Set of variables
	 */
	public Set<Variable<G>> getVars();

	/**
	 * Return all undistinguished variables used in this query.
	 * 
	 * @return Set of variables
	 */
	public Set<Variable<G>> getUndistVars();

	/**
	 * Return individuals and literals used in this query.
	 * 
	 * @return
	 */
	public Set<GroundTerm<G>> getConstants();

	/**
	 * Return all the variables that will be in the results. For SPARQL, these
	 * are the variables in the SELECT clause.
	 * 
	 * @return Set of variables
	 */
	public List<Variable<G>> getResultVars();

	/**
	 * Return all the distinguished variables. These are variables that will be
	 * bound to individuals (or data values) existing in the KB.
	 * 
	 * @return Set of variables
	 */
	public Set<Variable<G>> getDistVars();

	/**
	 * Get all the atoms in the query.
	 * 
	 * @return
	 */
	public List<QueryAtom<G>> getAtoms();

	/**
	 * The KB that will be used to answer this query.
	 * 
	 * @return
	 */
	public OWL2Ontology<G> getOntology();

	/**
	 * ReplaG the variables in the query with the values specified in the
	 * binding and return a new query instanG (without modifying this query).
	 * 
	 * @param binding
	 * @return
	 */
	public InternalQuery<G> apply(
			Map<? extends Term<G>, ? extends Term<G>> binding);

	/**
	 * Rolls up the query to the given variable.
	 * 
	 * @param distVar
	 * @return
	 */
	public <X extends Term<G>> G rollUpTo(final Term<G> distVar,
			final Collection<X> stopList);

	public void remove(final QueryAtom<G> atom);

	/**
	 * Searches for given atom pattern. This also might be used for different
	 * types of rolling-up, involving various sets of allowed atom types.
	 * 
	 * @return query atoms in the order as they appear in the query
	 */
	public List<QueryAtom<G>> findAtoms(final QueryPredicate predicate,
			final Term<G>... arguments);

	public boolean canHaveResults();

	OWL2Query<G> Core(final Term<G> g, final GroundTerm<G> rollUp,
			final InternalQuery<G> q);
}
