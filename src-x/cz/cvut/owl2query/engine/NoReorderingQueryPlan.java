// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.owl2query.engine;

import cz.cvut.owl2query.model.Query;
import cz.cvut.owl2query.model.QueryAtom;
import cz.cvut.owl2query.model.ResultBinding;

/**
 * <p>
 * Title: Query Plan that returns the atoms in the order as they appear in the query.
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
public class NoReorderingQueryPlan extends QueryPlan {

	private int index;

	private int size;

	public NoReorderingQueryPlan(Query query) {
		super(query);

		index = 0;

		size = query.getAtoms().size();
	}

	// @Override
	// public List<QueryAtom> getSortedAtoms() {
	// return query.getAtoms();
	// }

	@Override
	public QueryAtom next(ResultBinding binding) {
		return query.getAtoms().get(index++);
	}

	@Override
	public boolean hasNext() {
		return index < size;
	}

	@Override
	public void back() {
		index--;
	}

	@Override
	public void reset() {
		index = 0;
	}
}
