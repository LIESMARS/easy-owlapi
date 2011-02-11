// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.complexversion.engine;

import java.util.HashSet;
import java.util.Set;

import cz.cvut.kbss.owl2query.complexversion.modelold.Query;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryAtom;
import cz.cvut.kbss.owl2query.complexversion.modelold.SizeEstimate;
import cz.cvut.kbss.owl2query.complexversion.model.Term;
import cz.cvut.kbss.owl2query.complexversion.util.ATermUtils;


/**
 * <p>
 * Title: Computation of size estimate for a knowledge base and a query.
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
public class QuerySizeEstimator {

	public static void computeSizeEstimate(final Query query) {
		final SizeEstimate sizeEstimate = query.getKB().getSizeEstimate();

		final Set<Term> concepts = new HashSet<Term>();
		final Set<Term> properties = new HashSet<Term>();
//		boolean fullDone = false;
		for (final QueryAtom atom : query.getAtoms()) {
//			if (!fullDone) {
//				switch (atom.getPredicate()) {
//				case Type:
//					if (query.getDistVars()
//							.contains(atom.getArguments().get(1))) {
//						fullDone = true;
//					}
//					break;
//				case PropertyValue:
//					if (query.getDistVars()
//							.contains(atom.getArguments().get(1))) {
//						fullDone = true;
//					}
//					break;
//				case SameAs:
//				case DifferentFrom:
//					break;
//				default:
////					fullDone = true;
//					;
//				}
//				if (fullDone) {
//					sizeEstimate.computeAll();
//				}
//			}

			for (final Term argument : atom.getArguments()) {
				if (!ATermUtils.isVar(argument)) {
					if ((query.getKB().isClass(argument) || ATermUtils
							.isComplexClass(argument))
							&& !sizeEstimate.isComputed(argument)) {
						concepts.add(argument);
					}

					if (query.getKB().isProperty(argument)
							&& !sizeEstimate.isComputed(argument)) {
						properties.add(argument);
					}
				}
			}
		}

		sizeEstimate.compute(concepts, properties);
	}
}