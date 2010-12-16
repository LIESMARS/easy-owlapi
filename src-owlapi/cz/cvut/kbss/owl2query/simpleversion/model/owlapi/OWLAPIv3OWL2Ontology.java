package cz.cvut.kbss.owl2query.simpleversion.model.owlapi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectInverseOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import cz.cvut.kbss.owl2query.simpleversion.model.Hierarchy;
import cz.cvut.kbss.owl2query.simpleversion.model.InternalReasonerException;
import cz.cvut.kbss.owl2query.simpleversion.model.OWL2Ontology;
import cz.cvut.kbss.owl2query.simpleversion.model.OWL2QueryFactory;
import cz.cvut.kbss.owl2query.simpleversion.model.OWLObjectType;
import cz.cvut.kbss.owl2query.simpleversion.model.SizeEstimate;
import cz.cvut.kbss.owl2query.simpleversion.model.SizeEstimateImpl;

public class OWLAPIv3OWL2Ontology implements OWL2Ontology<OWLObject> {

	private static final Logger LOG = Logger
			.getLogger(OWLAPIv3OWL2Ontology.class.getName());

	private OWLOntology o;
	private OWLOntologyManager m;
	private OWLDataFactory f;
	private OWLReasoner r;
	private OWL2QueryFactory<OWLObject> factory;
	private SizeEstimate<OWLObject> sizeEstimate;

	private OWLReasoner structuralReasoner;

	public OWLAPIv3OWL2Ontology(final OWLOntologyManager m,
			final OWLOntology o, final OWLReasoner r) {
		this.o = o;
		this.m = m;
		this.f = m.getOWLDataFactory();
		this.r = r;
		structuralReasoner = new StructuralReasonerFactory().createReasoner(o);
		structuralReasoner.precomputeInferences(InferenceType.values());
		this.factory = new OWLAPIv3QueryFactory(m, o);

		this.sizeEstimate = new SizeEstimateImpl<OWLObject>(this);
	}

	private OWLNamedIndividual asOWLNamedIndividual(final OWLObject e) {
		if (e instanceof OWLNamedIndividual) {
			return (OWLNamedIndividual) e;
		} else if (e instanceof OWLEntity) {
			return f.getOWLNamedIndividual(((OWLEntity) e).getIRI());
		} else {
			throw new InternalReasonerException();
		}
	}

	private OWLLiteral asOWLLiteral(final OWLObject e) {
		if (e instanceof OWLLiteral) {
			return (OWLLiteral) e;
		} else {
			throw new InternalReasonerException();
		}
	}

	private OWLClassExpression asOWLClassExpression(final OWLObject e) {
		if (e instanceof OWLClassExpression) {
			return (OWLClassExpression) e;
		} else if (e instanceof OWLEntity) {
			final OWLEntity ee = (OWLEntity) e;
			// if (o.containsClassInSignature(ee.getIRI())) {
			return f.getOWLClass(ee.getIRI());
			// }
		}

		return null;
	}

	private OWLPropertyExpression<?, ?> asOWLPropertyExpression(
			final OWLObject e) {
		if (e instanceof OWLPropertyExpression<?, ?>) {
			return (OWLPropertyExpression<?, ?>) e;
		} else if (e instanceof OWLEntity) {
			final OWLEntity ee = (OWLEntity) e;
			if (is(ee, OWLObjectType.OWLObjectProperty)) {
				return f.getOWLObjectProperty(ee.getIRI());
			} else if (o.containsDataPropertyInSignature(ee.getIRI())) {
				return f.getOWLDataProperty(ee.getIRI());
			}
		}

		return null;
	}

	private OWLObjectProperty asOWLObjectProperty(final OWLObject e) {
		if (e instanceof OWLObjectProperty) {
			return (OWLObjectProperty) e;
		} else if (e instanceof OWLEntity) {
			final OWLEntity ee = (OWLEntity) e;
			if (is(ee, OWLObjectType.OWLObjectProperty)) {
				return f.getOWLObjectProperty(ee.getIRI());
			}
		}

		return null;
	}

	@Override
	public Set<OWLClass> getClasses() {
		return o.getClassesInSignature();
	}

	@Override
	public Set<OWLObjectProperty> getObjectProperties() {
		return o.getObjectPropertiesInSignature();
	}

	@Override
	public Set<OWLDataProperty> getDataProperties() {
		return o.getDataPropertiesInSignature();
	}

	@Override
	public Set<OWLNamedIndividual> getIndividuals() {
		return o.getIndividualsInSignature();
	}

	@Override
	public Set<? extends OWLObject> getDifferents(OWLObject i) {
		if (!(i instanceof OWLEntity)) {
			throw new InternalReasonerException();
		}

		return r.getDifferentIndividuals(asOWLNamedIndividual((OWLEntity) i))
				.getFlattened();
	}

	@Override
	public Set<? extends OWLObject> getDomains(OWLObject pred) {
		final OWLPropertyExpression<?, ?> ope = asOWLPropertyExpression(pred);

		if (ope != null) {
			if (ope.isAnonymous()) {
				throw new InternalReasonerException();
			} else if (ope.isObjectPropertyExpression()) {
				r.getObjectPropertyDomains(
						((OWLEntity) ope).asOWLObjectProperty(), true); // TODO
			} else if (ope.isDataPropertyExpression()) {
				r.getDataPropertyDomains(((OWLEntity) ope).asOWLDataProperty(),
						true); // TODO
			}
		}

		throw new InternalReasonerException();
	}

	public Set<? extends OWLObject> getEquivalentClasses(OWLObject ce) {
		final OWLClassExpression c = asOWLClassExpression(ce);

		return r.getEquivalentClasses(c).getEntities();
	}

	@Override
	public Set<? extends OWLObject> getInverses(OWLObject ope) {
		final OWLPropertyExpression<?, ?> opex = asOWLPropertyExpression(ope);

		if (opex.isObjectPropertyExpression()) {

			if (opex.isAnonymous()) {
				return r.getEquivalentObjectProperties(
						((OWLObjectPropertyExpression) opex).getNamedProperty())
						.getEntities();
			} else {
				return r.getInverseObjectProperties(
						((OWLObjectPropertyExpression) opex).getNamedProperty())
						.getEntities();
			}
		}
		throw new InternalReasonerException();
	}

	@Override
	public Set<? extends OWLObject> getRanges(OWLObject pred) {
		final OWLPropertyExpression<?, ?> ope = asOWLPropertyExpression(pred);

		if (ope != null) {
			if (ope.isAnonymous()) {
				throw new InternalReasonerException();
			} else if (ope.isObjectPropertyExpression()) {
				return r.getObjectPropertyRanges(
						((OWLEntity) ope).asOWLObjectProperty(), true)
						.getFlattened(); // TODO
			} else if (ope.isDataPropertyExpression()) {
				// return r.getDataPropertyRanges(((OWLEntity)
				// ope).asOWLDataProperty());
				return ((OWLEntity) ope).asOWLDataProperty().getRanges(o); // TODO
			}
		}
		throw new InternalReasonerException();
	}

	@Override
	public Set<? extends OWLObject> getSames(OWLObject i) {
		if (!(i instanceof OWLEntity)) {
			throw new InternalReasonerException();
		}

		return r.getSameIndividuals(asOWLNamedIndividual((OWLEntity) i))
				.getEntities();
	}

	@Override
	public Set<OWLClass> getTypes(OWLObject i, boolean direct) {
		if (!(i instanceof OWLEntity)) {
			throw new InternalReasonerException();
		}

		return r.getTypes(asOWLNamedIndividual(i), direct).getFlattened();
	}

	@Override
	public boolean is(OWLObject e, final OWLObjectType... tt) {
		boolean result = false;

		for (final OWLObjectType t : tt) {
			switch (t) {
			case OWLLiteral:
				result = e instanceof OWLLiteral;
				break;
			case OWLAnnotationProperty:
				if (e instanceof OWLEntity) {
					result = o
							.containsAnnotationPropertyInSignature(((OWLEntity) e)
									.getIRI());
				}
				break;
			case OWLDataProperty:
				if (e instanceof OWLEntity) {
					result = o.containsDataPropertyInSignature(((OWLEntity) e)
							.getIRI())
							|| e.equals(f.getOWLTopDataProperty())
							|| e.equals(f.getOWLBottomDataProperty());
				}
				break;
			case OWLObjectProperty:
				if (e instanceof OWLEntity) {
					result = o
							.containsObjectPropertyInSignature(((OWLEntity) e)
									.getIRI())
							|| (e.equals(f.getOWLTopObjectProperty()) || e
									.equals(f.getOWLBottomObjectProperty()));
				}
				break;
			case OWLClass:
				if (e instanceof OWLEntity) {
					result = o.containsClassInSignature(((OWLEntity) e)
							.getIRI())
							|| e.equals(f.getOWLThing())
							|| e.equals(f.getOWLNothing());
				}
				break;
			case OWLNamedIndividual:
				if (e instanceof OWLEntity) {
					result = o.containsIndividualInSignature(((OWLEntity) e)
							.getIRI());
				}

				break;
			default:
				break;
			}
			if (result) {
				break;
			}
		}

		return result;
	}

	@Override
	public boolean isSameAs(OWLObject i1, OWLObject i2) {
		final OWLIndividual ii1 = asOWLNamedIndividual(i1);
		final OWLIndividual ii2 = asOWLNamedIndividual(i2);

		if (i1.equals(i2)) {
			return true;
		}

		return r.isEntailed(f.getOWLSameIndividualAxiom(ii1, ii2));
	}

	@Override
	public boolean isDifferentFrom(OWLObject i1, OWLObject i2) {
		if ((!(i1 instanceof OWLEntity)) || (!(i2 instanceof OWLEntity))) {
			throw new InternalReasonerException();
		}

		return r.isEntailed(f.getOWLDifferentIndividualsAxiom(
				asOWLNamedIndividual((OWLEntity) i1),
				asOWLNamedIndividual((OWLEntity) i2)));
	}

	@Override
	public boolean isTypeOf(OWLObject ce, OWLObject i, boolean direct) {
		final OWLNamedIndividual ii = asOWLNamedIndividual((OWLEntity) i);
		final OWLClassExpression cce = asOWLClassExpression(ce);

		if (direct) {
			return r.getInstances(cce, true).containsEntity(ii);
		} else {
			return r.isEntailed(f.getOWLClassAssertionAxiom(cce, ii));
		}
	}

	@Override
	public void ensureConsistency() {
		if (LOG.isLoggable(Level.CONFIG)) {
			LOG.config("Ensure consistency");
		}

		if (LOG.isLoggable(Level.CONFIG)) {
			LOG.config("	* isConsistent ?");
		}
		if (!r.isConsistent()) {
			throw new InternalReasonerException();
		}
		if (LOG.isLoggable(Level.CONFIG)) {
			LOG.config("	* true");
		}
	}

	@Override
	public Set<? extends OWLObject> getIndividualsWithProperty(OWLObject pvP,
			OWLObject pvIL) {
		final OWLPropertyExpression<?, ?> pex = asOWLPropertyExpression(pvP);

		final Set<OWLObject> set = new HashSet<OWLObject>();

		if (pex != null) {
		
		if (pex.isObjectPropertyExpression()) {
			final OWLNamedIndividual object = asOWLNamedIndividual(pvIL);

			for (final OWLNamedIndividual i : getIndividuals()) {
				if (r.isEntailed(f.getOWLObjectPropertyAssertionAxiom(
						(OWLObjectPropertyExpression) pex, i, object))) {
					set.add(i);
				}
			}
		} else if (pex.isDataPropertyExpression()) {
			final OWLLiteral object = asOWLLiteral(pvIL);

			for (final OWLNamedIndividual i : getIndividuals()) {
				if (r.isEntailed(f.getOWLDataPropertyAssertionAxiom(
						(OWLDataPropertyExpression) pex, i, object))) {
					set.add(i);
				}
			}
		}
		}

		return set;
	}

	public Set<? extends OWLObject> getPropertyValues(OWLObject pvP,
			OWLObject pvI) {
		final OWLPropertyExpression<?, ?> pex = asOWLPropertyExpression(pvP);
		final OWLNamedIndividual ni = asOWLNamedIndividual(pvI);

		if (pex != null) {
			if (pex.isObjectPropertyExpression()) {
				return r.getObjectPropertyValues(ni,
						(OWLObjectPropertyExpression) pex).getFlattened();
			} else if (pex.isDataPropertyExpression()) {
				return r.getDataPropertyValues(ni, (OWLDataProperty) pex); // TODO
			}
		}

		throw new InternalReasonerException();
	}

	public SizeEstimate<OWLObject> getSizeEstimate() {
		return sizeEstimate;
	}

	public boolean hasPropertyValue(OWLObject p, OWLObject s, OWLObject o) {
		final OWLPropertyExpression<?, ?> pex = asOWLPropertyExpression(p);

		if (pex.isObjectPropertyExpression()) {
			return r.isEntailed(f.getOWLObjectPropertyAssertionAxiom(
					(OWLObjectPropertyExpression) pex, asOWLNamedIndividual(s),
					asOWLNamedIndividual(o)));
		} else if (pex.isDataPropertyExpression()) {
			return r.isEntailed(f.getOWLDataPropertyAssertionAxiom(
					(OWLDataPropertyExpression) pex, asOWLNamedIndividual(s),
					asOWLLiteral(o)));

		}
		return false;
	}

	public boolean isClassAlwaysNonEmpty(OWLObject sc) {
		final OWLAxiom axiom = f.getOWLSubClassOfAxiom(
				asOWLClassExpression(sc), f.getOWLNothing());

		try {
			m.applyChange(new AddAxiom(o, axiom));

			boolean classAlwaysNonEmpty = !r.isConsistent();

			m.applyChange(new RemoveAxiom(o, axiom));

			return classAlwaysNonEmpty;
		} catch (OWLOntologyChangeException e) {
			throw new InternalReasonerException();
		}
	}

	public boolean isClassified() {
		// TODO
		// return r.isClassified();
		return false;
	}

	public boolean isSatisfiable(OWLObject arg) {
		return r.isSatisfiable(asOWLClassExpression(arg));
	}

	public Set<? extends OWLObject> retrieveIndividualsWithProperty(
			OWLObject odpe) {
		final OWLPropertyExpression<?, ?> ope = asOWLPropertyExpression(odpe);

		final Set<OWLObject> set = new HashSet<OWLObject>();
		try {
			if (ope.isObjectPropertyExpression()) {
				for (final OWLNamedIndividual i : getIndividuals()) {
					if (!r.getObjectPropertyValues(i,
							(OWLObjectPropertyExpression) ope).isEmpty()) {
						set.add(i);
					}
				}
			} else if (ope.isObjectPropertyExpression()) {
				for (final OWLNamedIndividual i : getIndividuals()) {
					if (!r.getObjectPropertyValues(i,
							(OWLObjectPropertyExpression) ope).isEmpty()) {
						set.add(i);
					}
				}
			}
		} catch (Exception e) {
			throw new InternalReasonerException(e);
		}
		return set;
	}

	public Map<OWLObject, Boolean> getKnownInstances(final OWLObject ce) {
		final Map<OWLObject, Boolean> m = new HashMap<OWLObject, Boolean>();
		final OWLClassExpression cex = asOWLClassExpression(ce);

		for (final OWLObject x : getIndividuals()) {
			m.put(x, false);
		}

		if (!cex.isAnonymous()) {
			for (final OWLObject x : cex.asOWLClass().getIndividuals(o)) {
				m.put(x, true);
			}
		}

		return m;
	}

	public Boolean isKnownTypeOf(OWLObject ce, OWLObject i) {
		final OWLIndividual ii = asOWLNamedIndividual(i);

		if (ii.getTypes(o).contains(ce)) {
			return true;
		}

		return null;
	}

	public Boolean hasKnownPropertyValue(OWLObject p, OWLObject s, OWLObject ob) {
		final OWLIndividual is = asOWLNamedIndividual(s);
		final OWLPropertyExpression<?, ?> pex = asOWLPropertyExpression(p);

		if (pex != null) {
			if (pex.isObjectPropertyExpression()) {
				final OWLObjectPropertyExpression ope = ((OWLObjectPropertyExpression) p)
						.getSimplified();

				if (ope instanceof OWLObjectInverseOf) {
					final OWLObjectPropertyExpression opeInv = ope
							.getInverseProperty().getSimplified();

					for (final OWLObjectPropertyAssertionAxiom ax : o
							.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
						if (ax.getObject().equals(s)
								&& ax.getProperty().equals(opeInv)
								&& ax.getSubject().equals(ob)) {
							return true;
						}
					}
					return false;
				} else {
					return is.getObjectPropertyValues(
							(OWLObjectPropertyExpression) pex, o).contains(ob);
				}
			} else if (pex.isDataPropertyExpression()) {
				return is.getDataPropertyValues(o).get(pex).contains(ob);
			}
		}

		return false;
	}

	public Set<? extends OWLObject> getInstances(OWLObject ic, boolean direct) {
		final OWLClassExpression c = asOWLClassExpression(ic);

		return r.getInstances(c, direct).getFlattened();
	}

	@Override
	public OWL2QueryFactory<OWLObject> getFactory() {
		return factory;
	}

	@Override
	public boolean isRealized() {
		// try {
		// return r.isRealised();
		// } catch (OWLReasonerException e) {
		// throw new InternalReasonerException();
		// }
		// TODO
		return false;
	}

	@Override
	public boolean isComplexClass(OWLObject c) {
		return (c instanceof OWLClassExpression);
	}

	@Override
	public Collection<? extends OWLObject> getKnownPropertyValues(
			OWLObject pvP, OWLObject pvI) {

		final OWLPropertyExpression p = asOWLPropertyExpression(pvP);
		final OWLNamedIndividual ni = asOWLNamedIndividual(pvI);

		Collection result;

		if ( p == null || ni == null ) {
			return Collections.emptySet();
		}
		
		if (p.isObjectPropertyExpression()) {
			result = structuralReasoner.getObjectPropertyValues(ni,
					(OWLObjectPropertyExpression) p).getFlattened();
		} else if (p.isDataPropertyExpression()) {
			result = structuralReasoner.getDataPropertyValues(ni,
					(OWLDataProperty) p);
		} else {
			throw new InternalReasonerException();
		}

		if (result == null) {
			result = Collections.emptySet();
		}

		return result;
	}

	private final Hierarchy<OWLObject, OWLClass> classHierarchy = new Hierarchy<OWLObject, OWLClass>() {

		@Override
		public Set<OWLClass> getEquivs(OWLObject ce) {
			final OWLClassExpression cex = asOWLClassExpression(ce);

			return r.getEquivalentClasses(cex).getEntities();
		}

		@Override
		public Set<OWLClass> getSubs(OWLObject superCE, boolean direct) {
			final OWLClassExpression cex = asOWLClassExpression(superCE);

			return r.getSubClasses(cex, direct).getFlattened();
		}

		@Override
		public Set<OWLClass> getSupers(OWLObject superCE, boolean direct) {
			final OWLClassExpression cex = asOWLClassExpression(superCE);

			return r.getSuperClasses(cex, direct).getFlattened();
		}

		@Override
		public Set<OWLClass> getTops() {
			return Collections.singleton(f.getOWLThing());
		}

		@Override
		public Set<OWLClass> getBottoms() {
			return Collections.singleton(f.getOWLNothing());
		}
	};

	@Override
	public Hierarchy<OWLObject, OWLClass> getClassHierarchy() {
		return classHierarchy;
	}

	private final Hierarchy<OWLObject, OWLClass> toldClassHierarchy = new Hierarchy<OWLObject, OWLClass>() {

		@Override
		public Set<OWLClass> getEquivs(OWLObject ce) {
			final OWLClassExpression cex = asOWLClassExpression(ce);
			if (cex.isAnonymous()) {
				return Collections.emptySet();
			} else {
				return structuralReasoner.getEquivalentClasses(cex)
						.getEntities();
				// final Set<OWLClass> set = new HashSet<OWLClass>();
				// for (final OWLClassExpression oce : cex.asOWLClass()
				// .getEquivalentClasses(o)) {
				// if (!oce.isAnonymous()) {
				// set.add(oce.asOWLClass());
				// }
				// }
				// return set;
			}
		}

		@Override
		public Set<OWLClass> getSubs(OWLObject superCE, boolean direct) {
			final OWLClassExpression cex = asOWLClassExpression(superCE);
			if (cex.isAnonymous()) {
				return Collections.emptySet();
			} else {
				// final Set<OWLClass> set = new HashSet<OWLClass>();
				return structuralReasoner.getSubClasses(cex, direct)
						.getFlattened();
				//
				// for (final OWLClassExpression oce : cex.asOWLClass()
				// .getSubClasses(o)) {
				// if (!oce.isAnonymous() && !set.contains(oce.asOWLClass())) {
				// set.add(oce.asOWLClass());
				// if (!direct) {
				// set.addAll(getSubs(oce, direct));
				// }
				// }
				// }
				//
				// return set;
			}
		}

		@Override
		public Set<OWLClass> getSupers(OWLObject superCE, boolean direct) {
			final OWLClassExpression cex = asOWLClassExpression(superCE);
			if (cex.isAnonymous()) {
				return Collections.emptySet();
			} else {
				return structuralReasoner.getSuperClasses(cex, direct)
						.getFlattened();
				//
				// final Set<OWLClass> set = new HashSet<OWLClass>();
				// for (final OWLClassExpression oce : cex.asOWLClass()
				// .getSuperClasses(o)) {
				// if (!oce.isAnonymous() && !set.contains(oce.asOWLClass())) {
				// set.add(oce.asOWLClass());
				// if (!direct) {
				// set.addAll(getSupers(oce, direct));
				// }
				// }
				// }
				//
				// return set;
			}
		}

		@Override
		public Set<OWLClass> getTops() {
			return Collections.singleton(f.getOWLThing());
		}

		@Override
		public Set<OWLClass> getBottoms() {
			return Collections.singleton(f.getOWLNothing());
		}
	};

	@Override
	public Hierarchy<OWLObject, OWLClass> getToldClassHierarchy() {
		return toldClassHierarchy;
	}

	private final Hierarchy<OWLObject, OWLProperty> propertyHierarchy = new Hierarchy<OWLObject, OWLProperty>() {

		@Override
		public Set<OWLProperty> getEquivs(OWLObject ce) {
			final OWLPropertyExpression cex = asOWLPropertyExpression(ce);

			if (cex.isDataPropertyExpression()) {
				return new HashSet<OWLProperty>(r.getEquivalentDataProperties(
						(OWLDataProperty) cex).getEntities());
			} else if (cex.isObjectPropertyExpression()) {
				final Set<OWLProperty> props = new HashSet<OWLProperty>();
				for (final OWLObjectPropertyExpression ex : r
						.getEquivalentObjectProperties((OWLObjectProperty) cex)
						.getEntities()) {
					if (ex.isAnonymous()) {
						continue;
					} else {
						props.add(ex.asOWLObjectProperty());
					}
				}
				return props;
			} else {
				throw new InternalReasonerException();
			}
		}

		@Override
		public Set<OWLProperty> getSubs(OWLObject superCE, boolean direct) {
			final OWLPropertyExpression cex = asOWLPropertyExpression(superCE);
			if (cex.equals(f.getOWLBottomObjectProperty())) {
				return Collections.emptySet();
			} else if (cex.equals(f.getOWLTopObjectProperty())) {
				final Set<OWLProperty> set = new HashSet<OWLProperty>();
				set.addAll(getObjectProperties());

				if (direct) {
					for (OWLProperty op : new HashSet<OWLProperty>(set)) {
						if (!getSupers(op, true).contains(
								f.getOWLTopObjectProperty())) {
							set.remove(op);
						}
					}
				}

				if (set.isEmpty()) {
					set.add(f.getOWLBottomObjectProperty());
				}

				return set;
			} else if (cex.equals(f.getOWLTopDataProperty())) {
				final Set<OWLProperty> set = new HashSet<OWLProperty>();
				set.addAll(getDataProperties());

				if (direct) {
					for (OWLProperty op : new HashSet<OWLProperty>(set)) {
						if (!getSupers(op, true).contains(
								f.getOWLTopDataProperty())) {
							set.remove(op);
						}
					}
				}

				if (set.isEmpty()) {
					set.add(f.getOWLBottomObjectProperty());
				}

				return set;
			}

			final Set<OWLProperty> set = new HashSet<OWLProperty>();

			if (cex.isDataPropertyExpression()) {
				set.addAll(r
						.getSubDataProperties((OWLDataProperty) cex, direct)
						.getFlattened());
				if (!direct || set.isEmpty()) {
					set.add(f.getOWLBottomDataProperty());
				}

			} else if (cex.isObjectPropertyExpression()) {
				final Set<OWLProperty> props = new HashSet<OWLProperty>();
				for (final OWLObjectPropertyExpression ex : r
						.getSubObjectProperties((OWLObjectProperty) cex, direct)
						.getFlattened()) {
					if (ex.isAnonymous()) {
						continue;
					} else {
						set.add(ex.asOWLObjectProperty());
					}
				}

				if (!direct || set.isEmpty()) {
					set.add(f.getOWLBottomObjectProperty());
				}
			} else {
				throw new InternalReasonerException();
			}

			return set;
		}

		@Override
		public Set<OWLProperty> getSupers(OWLObject superCE, boolean direct) {
			final OWLPropertyExpression cex = asOWLPropertyExpression(superCE);

			if (cex.equals(f.getOWLTopObjectProperty())) {
				return Collections.emptySet();
			} else if (cex.equals(f.getOWLBottomObjectProperty())) {
				final Set<OWLProperty> set = new HashSet<OWLProperty>();
				set.addAll(getObjectProperties());

				if (direct) {
					for (OWLProperty op : new HashSet<OWLProperty>(set)) {
						if (!getSubs(op, true).contains(
								f.getOWLBottomObjectProperty())) {
							set.remove(op);
						}
					}
				}

				if (set.isEmpty()) {
					set.add(f.getOWLTopObjectProperty());
				}

				return set;
			} else if (cex.equals(f.getOWLBottomDataProperty())) {
				final Set<OWLProperty> set = new HashSet<OWLProperty>();
				set.addAll(getDataProperties());

				if (direct) {
					for (OWLProperty op : new HashSet<OWLProperty>(set)) {
						if (!getSubs(op, true).contains(
								f.getOWLBottomDataProperty())) {
							set.remove(op);
						}
					}
				}

				if (set.isEmpty()) {
					set.add(f.getOWLTopDataProperty());
				}

				return set;
			}
			final Set<OWLProperty> set = new HashSet<OWLProperty>();

			if (cex.isDataPropertyExpression()) {
				set.addAll(r.getSuperDataProperties((OWLDataProperty) cex,
						direct).getFlattened());

				if (!direct || set.isEmpty()) {
					set.add(f.getOWLTopDataProperty());
				}
			} else if (cex.isObjectPropertyExpression()) {
				final Set<OWLProperty> props = new HashSet<OWLProperty>();
				for (final OWLObjectPropertyExpression ex : r
						.getSuperObjectProperties(
								(OWLObjectPropertyExpression) cex, direct)
						.getFlattened()) {
					if (ex.isAnonymous()) {
						continue;
					} else {
						set.add(ex.asOWLObjectProperty());
					}
				}
				if (!direct || set.isEmpty()) {
					set.add(f.getOWLTopObjectProperty());
				}
			} else {
				throw new InternalReasonerException();
			}

			return set;
		}

		@Override
		public Set<OWLProperty> getTops() {
			return new HashSet<OWLProperty>(Arrays.asList(
					f.getOWLTopObjectProperty(), f.getOWLTopDataProperty()));
		}

		@Override
		public Set<OWLProperty> getBottoms() {
			return new HashSet<OWLProperty>(Arrays.asList(
					f.getOWLBottomObjectProperty(),
					f.getOWLBottomDataProperty()));
		}
	};

	@Override
	public Hierarchy<OWLObject, OWLProperty> getPropertyHierarchy() {
		return propertyHierarchy;
	}

	@Override
	public Set<OWLProperty> getFunctionalProperties() {
		final Set<OWLProperty> set = new HashSet<OWLProperty>();

		for (final OWLObjectProperty p : getObjectProperties()) {
			if (r.isEntailed(f.getOWLFunctionalObjectPropertyAxiom(p
					.asOWLObjectProperty()))) {
				set.add(p);
			}
		}

		for (final OWLDataProperty p : getDataProperties()) {
			if (r.isEntailed(f.getOWLFunctionalDataPropertyAxiom(p
					.asOWLDataProperty()))) {
				set.add(p);
			}
		}

		return set;
	}

	@Override
	public Set<OWLObjectProperty> getAsymmetricProperties() {
		final Set<OWLObjectProperty> set = new HashSet<OWLObjectProperty>();

		for (final OWLObjectProperty p : getObjectProperties()) {
			if (r.isEntailed(f.getOWLAsymmetricObjectPropertyAxiom(p))) {
				set.add(p);
			}
		}

		return set;
	}

	@Override
	public Set<? extends OWLObject> getInverseFunctionalProperties() {
		final Set<OWLObjectProperty> set = new HashSet<OWLObjectProperty>();

		for (final OWLObjectProperty p : getObjectProperties()) {
			if (r.isEntailed(f.getOWLInverseFunctionalObjectPropertyAxiom(p))) {
				set.add(p);
			}
		}

		return set;
	}

	@Override
	public Set<? extends OWLObject> getIrreflexiveProperties() {
		final Set<OWLObjectProperty> set = new HashSet<OWLObjectProperty>();

		for (final OWLObjectProperty p : getObjectProperties()) {
			if (r.isEntailed(f.getOWLIrreflexiveObjectPropertyAxiom(p))) {
				set.add(p);
			}
		}

		return set;
	}

	@Override
	public Set<? extends OWLObject> getReflexiveProperties() {
		final Set<OWLObjectProperty> set = new HashSet<OWLObjectProperty>();

		for (final OWLObjectProperty p : getObjectProperties()) {
			if (r.isEntailed(f.getOWLReflexiveObjectPropertyAxiom(p))) {
				set.add(p);
			}
		}

		return set;
	}

	@Override
	public Set<? extends OWLObject> getSymmetricProperties() {
		final Set<OWLObjectProperty> set = new HashSet<OWLObjectProperty>();

		for (final OWLObjectProperty p : getObjectProperties()) {
			if (r.isEntailed(f.getOWLSymmetricObjectPropertyAxiom(p))) {
				set.add(p);
			}
		}

		return set;
	}

	@Override
	public Set<? extends OWLObject> getTransitiveProperties() {
		final Set<OWLObjectProperty> set = new HashSet<OWLObjectProperty>();

		for (final OWLObjectProperty p : getObjectProperties()) {
			if (r.isEntailed(f.getOWLTransitiveObjectPropertyAxiom(p))) {
				set.add(p);
			}
		}

		return set;
	}

	@Override
	public boolean isAsymmetricProperty(OWLObject Term) {
		return r.isEntailed(f
				.getOWLAsymmetricObjectPropertyAxiom(asOWLObjectProperty(Term)));
	}

	@Override
	public boolean isFunctionalProperty(OWLObject Term) {
		final OWLPropertyExpression p = asOWLPropertyExpression(Term);

		if (p instanceof OWLObjectProperty) {
			return r.isEntailed(f
					.getOWLFunctionalObjectPropertyAxiom(asOWLObjectProperty(Term)));
		} else if (p instanceof OWLDataProperty) {
			return r.isEntailed(f
					.getOWLFunctionalDataPropertyAxiom((OWLDataProperty) Term));
		} else {
			return false;
		}
	}

	@Override
	public boolean isInverseFunctionalProperty(OWLObject Term) {
		return r.isEntailed(f
				.getOWLInverseFunctionalObjectPropertyAxiom(asOWLObjectProperty(Term)));
	}

	@Override
	public boolean isIrreflexiveProperty(OWLObject Term) {
		return r.isEntailed(f
				.getOWLIrreflexiveObjectPropertyAxiom(asOWLObjectProperty(Term)));
	}

	@Override
	public boolean isReflexiveProperty(OWLObject Term) {
		return r.isEntailed(f
				.getOWLReflexiveObjectPropertyAxiom(asOWLObjectProperty(Term)));
	}

	@Override
	public boolean isSymmetricProperty(OWLObject Term) {
		return r.isEntailed(f
				.getOWLSymmetricObjectPropertyAxiom(asOWLObjectProperty(Term)));

	}

	@Override
	public boolean isTransitiveProperty(OWLObject Term) {
		return r.isEntailed(f
				.getOWLTransitiveObjectPropertyAxiom(asOWLObjectProperty(Term)));
	}
}
