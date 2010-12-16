package cz.cvut.kbss.owl2query.simpleversion.engine;

import java.util.HashSet;
import java.util.Set;

import cz.cvut.kbss.owl2query.simpleversion.model.OWLObjectType;
import cz.cvut.kbss.owl2query.simpleversion.model.SizeEstimate;
import cz.cvut.kbss.owl2query.simpleversion.model.Term;

class QuerySizeEstimator {

	public static <G> void computeSizeEstimate(final InternalQuery<G> query) {
		final SizeEstimate<G> sizeEstimate = query.getOntology()
				.getSizeEstimate();

		final Set<G> concepts = new HashSet<G>();
		final Set<G> properties = new HashSet<G>();
		// boolean fullDone = false;
		for (final QueryAtom<G> atom : query.getAtoms()) {
			// if (!fullDone) {
			// switch (atom.getPredicate()) {
			// case Type:
			// if (query.getDistVars()
			// .contains(atom.getArguments().get(1))) {
			// fullDone = true;
			// }
			// break;
			// case PropertyValue:
			// if (query.getDistVars()
			// .contains(atom.getArguments().get(1))) {
			// fullDone = true;
			// }
			// break;
			// case SameAs:
			// case DifferentFrom:
			// break;
			// default:
			// // fullDone = true;
			// ;
			// }
			// if (fullDone) {
			// sizeEstimate.computeAll();
			// }
			// }

			for (final Term<G> argument : atom.getArguments()) {
				if (argument.isGround()) {
					if ((query.getOntology().is(
							argument.asGroundTerm().getWrappedObject(),
							OWLObjectType.OWLClass) // ||
					// !argument.isURI()
					&& !sizeEstimate.isComputed(argument.asGroundTerm()
							.getWrappedObject()))) {
						concepts
								.add(argument.asGroundTerm().getWrappedObject());
					}

					if ((query.getOntology().is(argument.asGroundTerm()
							.getWrappedObject(),
							OWLObjectType.OWLObjectProperty,
							OWLObjectType.OWLDataProperty,
							OWLObjectType.OWLAnnotationProperty))
							&& !sizeEstimate.isComputed(argument.asGroundTerm()
									.getWrappedObject())) {
						properties.add(argument.asGroundTerm()
								.getWrappedObject());
					}
				}
			}
		}

		sizeEstimate.compute(concepts, properties);
	}
}
