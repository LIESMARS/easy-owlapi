package cz.cvut.owl2query.model2.term.impl;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.AxiomType;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLConstant;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSubPropertyAxiom;

import cz.cvut.owl2query.model.GroundTermType;
import cz.cvut.owl2query.model.InternalReasonerException;
import cz.cvut.owl2query.model.KnowledgeBase;
import cz.cvut.owl2query.model.QueryEvaluationException;
import cz.cvut.owl2query.model.QueryTermFactory;
import cz.cvut.owl2query.model.SizeEstimate;
import cz.cvut.owl2query.model.Term;

public class OWLAPIKnowledgeBaseOWLReasonerWrapper implements KnowledgeBase {

	private static final Logger log = Logger
			.getLogger(OWLAPIKnowledgeBaseOWLReasonerWrapper.class.getName());
	private final OWLOntologyManager m;
	private final OWLOntology o;
	private final OWLReasoner r;
	private final QueryTermFactoryImpl<OWLDescription, OWLProperty, org.semanticweb.owl.model.OWLObjectProperty, org.semanticweb.owl.model.OWLDataProperty, org.semanticweb.owl.model.OWLIndividual, OWLConstant> f;
	private Boolean consistent = null;
	private boolean classified = false;
	private boolean realized = false;

	public OWLAPIKnowledgeBaseOWLReasonerWrapper(final OWLOntologyManager m,
			final OWLReasoner r, final OWLOntology o) {
		this.r = r;
		this.m = m;
		this.o = o;
		this.f = new QueryTermFactoryImpl<OWLDescription, OWLProperty, org.semanticweb.owl.model.OWLObjectProperty, org.semanticweb.owl.model.OWLDataProperty, org.semanticweb.owl.model.OWLIndividual, OWLConstant>(
				new OWLAPIObjectFactoryWrapper(m.getOWLDataFactory()));
	}

	public SizeEstimate getSizeEstimate() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public GroundTermImpl<OWLDescription> getOWLThing() {
		return f.getOWLThing();
	}

	public GroundTermImpl<OWLDescription> getOWLNothing() {
		return f.getOWLNothing();
	}

	public void classify() {
		if (!isClassified()) {
			try {
				r.classify();
				classified = true;
			} catch (OWLReasonerException e) {
				log.severe(e.getMessage());
			}
		}
	}

	public void realize() {
		if (!isRealized()) {
			try {
				r.realise();
				realized = true;
			} catch (OWLReasonerException e) {
				log.severe(e.getMessage());
			}
		}
	}

	public QueryTermFactory getFactory() {
		return f;
	}
	
	public boolean isObjectProperty(URI p) {
		return m.contains(p); // TODO
	}

	public boolean isConsistent() {
		if (consistent == null) {
			try {
				consistent = r.isConsistent(o);
			} catch (OWLReasonerException ex) {
				Logger.getLogger(
						OWLAPIKnowledgeBaseOWLReasonerWrapper.class.getName())
						.log(Level.SEVERE, null, ex);
			}
		}

		return consistent;
	}

	public boolean isClassified() {
		return classified;
	}

	public boolean isRealized() {
		return realized;
	}

	public void ensureConsistency() {
		if (!isConsistent()) {
			throw new InternalReasonerException(
					"The ontology is not consistent.");
		}
	}

	public Set<Term> getObjectPropertyDomains(Term op) {
		try {
			if (!(op instanceof GroundTermImpl)) {
				throw new InternalReasonerException(
						"Expected default implementation, but got '"
								+ op.getClass() + "'");
			}
			final GroundTermImpl<org.semanticweb.owl.model.OWLObjectProperty> opp = (GroundTermImpl<org.semanticweb.owl.model.OWLObjectProperty>) op;
			final Set<Set<OWLDescription>> domains = r
					.getDomains(opp.getTerm());
			final Set<Term> set = new HashSet<Term>();

			for (final Set<OWLDescription> ds : domains) {
				for (final OWLDescription d : ds) {
					set.add(new GroundTermImpl<OWLDescription>(d,
							GroundTermType.Class));
				}
			}
			return set;
		} catch (OWLReasonerException ex) {
			log.log(Level.SEVERE, null, ex);
			return Collections.emptySet();
		}
	}

	public Set<Term> getDataPropertyDomains(Term dp) {
		try {
			if (!(dp instanceof GroundTermImpl)) {
				throw new InternalReasonerException(
						"Expected default implementation, but got '"
								+ dp.getClass() + "'");
			}
			final GroundTermImpl<org.semanticweb.owl.model.OWLDataProperty> opp = (GroundTermImpl<org.semanticweb.owl.model.OWLDataProperty>) dp;
			final Set<Set<OWLDescription>> domains = r
					.getDomains(opp.getTerm());
			final Set<Term> set = new HashSet<Term>();

			for (final Set<OWLDescription> ds : domains) {
				for (final OWLDescription d : ds) {
					set.add(new GroundTermImpl<OWLDescription>(d,
							GroundTermType.Class));
				}
			}
			return set;
		} catch (OWLReasonerException ex) {
			log.log(Level.SEVERE, null, ex);
			return Collections.emptySet();
		}
	}

	@Override
	public boolean isAnnotationProperty(URI uri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDataProperty(URI uri) {
		return o.containsDataPropertyReference(uri);
	}

	@Override
	public Collection<Term> getDataPropertyValues(Term instance, Term property,
			boolean told) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Term> getInstances(Term clazz, boolean direct,
			boolean told) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Term> getObjectPropertyValues(Term instance,
			Term property, boolean told) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Term> getTypes(Term instances, boolean told) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean hasPropertyValue(Term property, Term subject, Term object,
			boolean told) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean isSubClassOf(Term subC, Term superC, boolean direct,
			boolean told) {
		if (subC.isVariable() || superC.isVariable()) {
			throw new QueryEvaluationException(
					"The term to be checked must be ground.");
		}

		final OWLClass subC2 = ((GroundTermImpl<OWLClass>) subC).getTerm();
		final OWLClass superC2 = ((GroundTermImpl<OWLClass>) superC).getTerm();

		if (told) {
			// this condition is rather weak since we do not have the tableau
			// model available.
			for (final OWLSubClassAxiom a : o.getAxioms(AxiomType.SUBCLASS)) {
				if (a.getSubClass().equals(subC2)
						&& (a.getSuperClass().equals(superC2))) {
					return true;
				}
			}

			return null;
		} else {
			try {
				return r.isSubClassOf(subC2, superC2);
			} catch (OWLReasonerException e) {
				log.log(Level.SEVERE, null, e);
				throw new QueryEvaluationException(e);
			}
		}
	}

	@Override
	public Boolean isSubPropertyOf(Term subP, Term superP, boolean direct,
			boolean told) {
		if (subP.isVariable() || superP.isVariable()) {
			throw new QueryEvaluationException(
					"The term to be checked must be ground.");
		}

		final OWLProperty subP2 = ((GroundTermImpl<OWLProperty>) subP).getTerm();
		final OWLProperty superP2 = ((GroundTermImpl<OWLProperty>) superP).getTerm();

		if (told) {
			// this condition is rather weak since we do not have the tableau
			// model available.
			for (final OWLSubPropertyAxiom a : o.getAxioms(AxiomType.SUB_OBJECT_PROPERTY)) {
				if (a.getSubProperty().equals(subP2)
						&& (a.getSuperProperty().equals(superP2))) {
					return true;
				}
			}

			for (final OWLSubPropertyAxiom a : o.getAxioms(AxiomType.SUB_DATA_PROPERTY)) {
				if (a.getSubProperty().equals(subP2)
						&& (a.getSuperProperty().equals(superP2))) {
					return true;
				}
			}

			return null;
		} else {
			try {
				if (subP2.isOWLObjectProperty()) {
					return r.getSubProperties((OWLObjectProperty) superP2).contains(subP2);					
				} else if (subP2.isOWLDataProperty()) {
					return r.getSubProperties((OWLDataProperty) superP2).contains(subP2);
				} else {
					throw new QueryEvaluationException("Unexpected property type.");					
				}
			} catch (OWLReasonerException e) {
				log.log(Level.SEVERE, null, e);
				throw new QueryEvaluationException(e);
			}
		}
	}

	@Override
	public Boolean isType(Term instance, Term clazz, boolean direct,
			boolean told) {

		if (instance.isVariable() || clazz.isVariable()) {
			throw new QueryEvaluationException(
					"The term to be checked must be ground.");
		}

		final OWLClass cls = ((GroundTermImpl<OWLClass>) clazz).getTerm();
		final OWLIndividual ins = ((GroundTermImpl<OWLIndividual>) instance)
				.getTerm();

		if (told) {
			// this condition is rather weak since we do not have the tableau
			// model available.
			for (final OWLClassAssertionAxiom a : o
					.getAxioms(AxiomType.CLASS_ASSERTION)) {
				if (a.getDescription().equals(cls)
						&& (a.getIndividual().equals(ins))) {
					return true;
				}
			}

			return null;
		} else {
			try {
				return r.getIndividuals(cls, direct).contains(ins);
			} catch (OWLReasonerException e) {
				log.log(Level.SEVERE, null, e);
				throw new QueryEvaluationException(e);
			}
		}
	}

	@Override
	public Set<Term> getClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getDataProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getIndividuals() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Term> getObjectProperties() {
		// TODO Auto-generated method stub
		return null;
	}
}
