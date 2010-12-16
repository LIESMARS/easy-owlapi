package cz.cvut.owl2query.engine;

import cz.cvut.owl2query.model.Query;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * Title: Optimizer of the query. Provides query atoms for the engine in
 * particular ordering.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Petr Kremen
 */
public class QueryOptimizer {

	private static final Logger LOG = Logger.getLogger(QueryOptimizer.class.getName());

	public QueryPlan getExecutionPlan(Query query) {
		if (QueryEngineOptions.SAMPLING_RATIO == 0) {
			return new NoReorderingQueryPlan(query);
		} else if (query.getAtoms().size() > QueryEngineOptions.STATIC_REORDERING_LIMIT) {
			if (LOG.isLoggable( Level.FINE )) {
				LOG.fine("Using incremental query plan.");
			}
			return new IncrementalQueryPlan(query);
		} else {
			if (LOG.isLoggable( Level.FINE )) {
				LOG.fine("Using full query plan.");
			}
			return new CostBasedQueryPlanNew(query);
		}

	}
}
