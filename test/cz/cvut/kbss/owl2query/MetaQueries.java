package cz.cvut.kbss.owl2query;

import java.net.URI;

import junit.framework.TestCase;

import org.mindswap.pellet.KnowledgeBase;
import org.mindswap.pellet.utils.ATermUtils;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import aterm.ATermAppl;
import cz.cvut.kbss.owl2query.engine.OWL2QueryEngine;
import cz.cvut.kbss.owl2query.model.OWL2Ontology;
import cz.cvut.kbss.owl2query.model.OWL2Query;
import cz.cvut.kbss.owl2query.model.QueryResult;
import cz.cvut.kbss.owl2query.model.Variable;
import cz.cvut.kbss.owl2query.model.owlapi.OWLAPIv3OWL2Ontology;
import cz.cvut.kbss.owl2query.model.pellet.PelletOWL2Ontology;

public class MetaQueries extends TestCase {

	final String BASE_URI = "http://krizik.felk.cvut.cz/";

	public void test1() {
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl i1 = ATermUtils.makeTermAppl(BASE_URI + "i1");
		final ATermAppl c1 = ATermUtils.makeTermAppl(BASE_URI + "c1");

		kb.addIndividual(i1);
		kb.addClass(c1);
		kb.addType(i1, c1);

		final PelletOWL2Ontology o = new PelletOWL2Ontology(kb);
		final OWL2Query<ATermAppl> q = query1(o, c1);

		final QueryResult<ATermAppl> qr = OWL2QueryEngine.exec(q);

		System.out.println(qr);
	}

	public void test2() {
		final OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		OWLOntology o;
		try {
			o = m.createOntology(IRI.create(URI.create(BASE_URI)));

			final OWLIndividual i1 = m.getOWLDataFactory()
					.getOWLNamedIndividual(IRI.create(BASE_URI + "i1"));
			final OWLClass c1 = m.getOWLDataFactory().getOWLClass(
					IRI.create(BASE_URI + "c1"));

			m.applyChange(new AddAxiom(o, m.getOWLDataFactory()
					.getOWLClassAssertionAxiom(c1, i1)));

			final OWLAPIv3OWL2Ontology ont = new OWLAPIv3OWL2Ontology(m,
					o, new ReasonerFactory().createReasoner(o));
			final OWL2Query<OWLObject> q = query1(ont, c1);

			final QueryResult<OWLObject> qr = OWL2QueryEngine.exec(q);

			System.out.println(qr);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyChangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private <T> OWL2Query<T> query1(
			final cz.cvut.kbss.owl2query.model.OWL2Ontology<T> ont,
			final T c1) {
		final Variable<T> varX = ont.getFactory().variable("x");

		final OWL2Query<T> q = ont.getFactory().createQuery(ont);
		q.Type(ont.getFactory().wrap(c1), varX);
		q.addDistVar(varX);
		q.addResultVar(varX);
		return q;
	}

	private String query1Sparql() {
		return "SELECT ?x WHERE {?x a <" + BASE_URI + "c1> }";
	}

	private String query2Sparql() {
		return "SELECT ?x WHERE {?x a <" + BASE_URI + "c1> . ?x <" + BASE_URI
				+ "p> ?y}";
	}

	public void test1Sparql() {
		final KnowledgeBase kb = new KnowledgeBase();

		final ATermAppl i1 = ATermUtils.makeTermAppl(BASE_URI + "i1");
		final ATermAppl c1 = ATermUtils.makeTermAppl(BASE_URI + "c1");

		kb.addIndividual(i1);
		kb.addClass(c1);
		kb.addType(i1, c1);

		final PelletOWL2Ontology o = new PelletOWL2Ontology(kb);
		final QueryResult<ATermAppl> qr = OWL2QueryEngine.exec(query1Sparql(),
				o);

		System.out.println(qr);
	}

	public void test2Sparql() {
		final OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		try {
			OWLOntology o = m.createOntology(IRI.create(URI.create(BASE_URI)));

			final OWLNamedIndividual i1 = m.getOWLDataFactory()
					.getOWLNamedIndividual(IRI.create(BASE_URI + "i1"));
			final OWLClass c1 = m.getOWLDataFactory().getOWLClass(
					IRI.create(BASE_URI + "c1"));

			m.applyChange(new AddAxiom(o, m.getOWLDataFactory()
					.getOWLDeclarationAxiom(c1)));

			m.applyChange(new AddAxiom(o, m.getOWLDataFactory()
					.getOWLClassAssertionAxiom(c1, i1)));

			final OWL2Ontology<OWLObject> ont = new OWLAPIv3OWL2Ontology(m,
					o, new ReasonerFactory().createReasoner(o));
			final QueryResult<OWLObject> qr = OWL2QueryEngine.exec(
					query1Sparql(), ont);

			System.out.println(qr);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
