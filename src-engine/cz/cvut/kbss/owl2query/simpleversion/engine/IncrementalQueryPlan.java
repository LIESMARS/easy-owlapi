package cz.cvut.kbss.owl2query.simpleversion.engine;

import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cvut.kbss.owl2query.simpleversion.model.ResultBinding;

class IncrementalQueryPlan<G> extends QueryPlan<G> {

	private static final Logger log = Logger
			.getLogger(IncrementalQueryPlan.class.getName());

	public final Stack<Integer> explored;

	private final List<QueryAtom<G>> atoms;

	private int size;

	private QueryCost<G> cost;

	public IncrementalQueryPlan(final InternalQuery<G> query) {
		super(query);

		QuerySizeEstimator.computeSizeEstimate(query);

		explored = new Stack<Integer>();

		atoms = query.getAtoms();

		size = atoms.size();

		cost = new QueryCost<G>(query.getOntology());

		reset();
	}

	@Override
	public QueryAtom<G> next(final ResultBinding<G> binding) {
		int best = -1;
		QueryAtom<G> bestAtom = null;
		double bestCost = Double.POSITIVE_INFINITY;

		for (int i = 0; i < size; i++) {
			if (!explored.contains(i)) {
				QueryAtom<G> atom = atoms.get(i);
				QueryAtom<G> atom2;

				if (atom.isGround()) {
					atom2 = atom;
				} else {
					atom2 = atom.apply(binding);

					// if( atom2.getPredicate().equals( QueryPredicate.Not ) &&
					// !atom2.isGround() ) {
					// continue;
					// }
				}

				final double atomCost = cost.estimate(atom2);

				if (log.isLoggable(Level.FINER)) {
					log.finer("Atom=" + atom + ", cost=" + cost
							+ ", best cost=" + bestCost);
				}
				if (atomCost <= bestCost) {
					bestCost = atomCost;
					bestAtom = atom2;
					best = i;
				}
			}
		}

		explored.add(best);

		if (log.isLoggable(Level.FINER)) {
			String treePrint = "";
			for (int j = 0; j < explored.size(); j++) {
				treePrint += " ";
			}
			treePrint += bestAtom + " : " + bestCost;

			log.finer(treePrint);
		}

		return bestAtom;
	}

	@Override
	public boolean hasNext() {
		return explored.size() < size;
	}

	@Override
	public void back() {
		explored.pop();
	}

	@Override
	public void reset() {
		explored.clear();
	}
}
