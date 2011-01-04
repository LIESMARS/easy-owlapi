package cz.cvut.kbss.owl2query;

import java.net.URI;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import cz.cvut.kbss.owl2query.engine.OWL2QueryEngine;
import cz.cvut.kbss.owl2query.model.OWL2Query;
import cz.cvut.kbss.owl2query.model.OWL2QueryFactory;
import cz.cvut.kbss.owl2query.model.QueryResult;
import cz.cvut.kbss.owl2query.model.VarType;
import cz.cvut.kbss.owl2query.model.Variable;
import cz.cvut.kbss.owl2query.model.owlapi.OWLAPIv3OWL2Ontology;

public class NotTests extends TestCase {

	private static final Logger LOG = Logger.getLogger(TestCase.class);

	final String BASE_URI = "http://krizik.felk.cvut.cz/";

	private final OWLReasonerFactory f = new ReasonerFactory();

	public void test2() {
		final OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology o;
		try {
			o = m.createOntology(IRI.create(URI.create(BASE_URI)));

			// data
			final OWLIndividual i1 = m.getOWLDataFactory()
					.getOWLNamedIndividual(IRI.create(BASE_URI + "i1"));
			final OWLIndividual i2 = m.getOWLDataFactory()
					.getOWLNamedIndividual(IRI.create(BASE_URI + "i2"));
			final OWLClass c1 = m.getOWLDataFactory().getOWLClass(
					IRI.create(BASE_URI + "c1"));
			final OWLObjectProperty p1 = m.getOWLDataFactory()
					.getOWLObjectProperty(IRI.create(BASE_URI + "p1"));

			m.applyChange(new AddAxiom(o, m.getOWLDataFactory()
					.getOWLClassAssertionAxiom(c1, i1)));
			m.applyChange(new AddAxiom(o, m.getOWLDataFactory()
					.getOWLObjectPropertyAssertionAxiom(p1, i1, i2)));

			final OWLAPIv3OWL2Ontology ont = new OWLAPIv3OWL2Ontology(m,
					o, f.createReasoner(o));

			// query
			final OWL2QueryFactory<OWLObject> f = ont.getFactory();

			final Variable<OWLObject> vX = f.variable("x");
			final OWL2Query<OWLObject> q = f.createQuery(ont).Type(f.wrap(c1),
					vX).Not(
					f.createQuery(ont).PropertyValue(f.wrap(p1), f.wrap(i1),
							f.wrap(i2)));
			q.addDistVar(vX);
			q.addResultVar(vX);

			// evaluation
			final QueryResult<OWLObject> qr = OWL2QueryEngine.exec(q);

			LOG.info(qr);
			assertEquals(0, qr.size());
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
			fail();
		} catch (OWLOntologyChangeException e) {
			e.printStackTrace();
			fail();
		}
	}
}
