package cz.cvut.kbss.owl2query;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import cz.cvut.kbss.owl2query.engine.OWL2QueryEngine;
import cz.cvut.kbss.owl2query.model.OWL2Ontology;
import cz.cvut.kbss.owl2query.model.OWL2Query;
import cz.cvut.kbss.owl2query.model.QueryResult;
import cz.cvut.kbss.owl2query.model.ResultBinding;
import cz.cvut.kbss.owl2query.model.Variable;
import cz.cvut.kbss.owl2query.model.owlapi.OWLAPIv3OWL2Ontology;
import cz.cvut.kbss.owl2query.parser.QueryParseException;
import cz.cvut.kbss.owl2query.parser.arq.SparqlARQParser;

public class QueryTester {

	interface ReasonerPlugin<G> {
		public void loadOntology(final Map<URI, URI> mapping,
				final String... ontologyURIs);

		public void loadQuery(final String queryURI);

		public String getAbbr();

		public QueryResult<G> exec();
	}

//	public final static ReasonerPlugin<ATermAppl> pellet = new ReasonerPlugin<ATermAppl>() {
//
//		private OWL2Ontology<ATermAppl> o;
//		private OWL2Query<ATermAppl> q;
//
//		public String getAbbr() {
//			return "pellet";
//		}
//
//		public QueryResult<ATermAppl> exec() {
//			return OWL2QueryEngine.exec(q);
//		}
//
//		public void loadOntology(Map<URI, URI> mapping, String... ontologyURIs) {
//			final JenaLoader l = new JenaLoader();
//			final KnowledgeBase kb = l.createKB(ontologyURIs);
//			o = new PelletOWL2Ontology(kb);
//		}
//
//		public void loadQuery(String queryURI) {
//			try {
//				q = new cz.cvut.kbss.owl2query.parser.arq.SparqlARQParser<ATermAppl>()
//						.parse(new FileInputStream(new File(URI
//								.create(queryURI))), o);
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (QueryParseException e) {
//				e.printStackTrace();
//			}
//		}
//
//	};
//
//	public final static ReasonerPlugin<Object> kaon2 = new ReasonerPlugin<Object>() {
//
//		private OWL2Ontology<Object> o;
//		private OWL2Query<Object> q;
//
//		public String getAbbr() {
//			return "kaon2";
//		}
//
//		public QueryResult<Object> exec() {
//			return OWL2QueryEngine.exec(q);
//		}
//
//		public void loadOntology(Map<URI, URI> mapping, String... ontologyURIs) {
//			OntologyManager m;
//			try {
//				m = KAON2Manager.newOntologyManager();
//				Ontology ox = null;
//				for (final String s : ontologyURIs) {
//					ox = m.openOntology(s, Collections
//							.<String, Object> emptyMap());
//				}
//
//				Reasoner r = ox.createReasoner();
//				o = new KaonOWL2Ontology(m, ox, r);
//			} catch (KAON2Exception e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//		}
//
//		public void loadQuery(String queryURI) {
//			try {
//				q = new cz.cvut.kbss.owl2query.parser.arq.SparqlARQParser<Object>()
//						.parse(new FileInputStream(new File(URI
//								.create(queryURI))), o);
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			} catch (QueryParseException e) {
//				e.printStackTrace();
//			}
//		}
//
//	};

	public static ReasonerPlugin<OWLObject> getGenericOWLAPIv3(
			final OWLReasonerFactory f) {

		return new ReasonerPlugin<OWLObject>() {
			public String getAbbr() {
				return "OWLAPI-" + f.getReasonerName().substring(0, 1);
			}

			private OWL2Ontology<OWLObject> o;

			private OWL2Query<OWLObject> q;

			public QueryResult<OWLObject> exec() {
				return OWL2QueryEngine.exec(q);
			}

			public void loadOntology(final Map<URI, URI> mapping,
					String... ontologyURIs) {
				final OWLOntologyManager m = OWLManager
						.createOWLOntologyManager();
				m.addIRIMapper(new OWLOntologyIRIMapper() {

					public IRI getDocumentIRI(IRI arg0) {
						final URI mm = mapping.get(arg0.toURI());

						if (mm != null) {
							return IRI.create(mm);
						} else {
							return arg0;
						}
					}
				});
				try {
					for (final String uri : ontologyURIs) {
						if (uri.startsWith("file:")) {
							m.loadOntologyFromOntologyDocument(new File(URI
									.create(uri)));
						} else {
							m.loadOntology(IRI.create(uri));
						}
					}
					OWLOntologyMerger merger = new OWLOntologyMerger(m);
					OWLOntology merged = merger.createMergedOntology(m, IRI
							.create("http://temp"));

					OWLReasoner r = f.createReasoner(merged);

					// r.prepareReasoner();

					o = new OWLAPIv3OWL2Ontology(m, merged, r);
				} catch (OWLOntologyCreationException e) {
					e.printStackTrace();
				}
			}

			public void loadQuery(String queryURI) {
				try {
					q = new SparqlARQParser<OWLObject>()
							.parse(new FileInputStream(new File(URI
									.create(queryURI))), o);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (QueryParseException e) {
					e.printStackTrace();
				}
			}
		};
	}

	// public static ReasonerPlugin<OWLObject> getGenericOWLAPIv3(
	// final OWLReasonerFactory f) {
	//
	// return new ReasonerPlugin<OWLObject>() {
	// public String getAbbr() {
	// return "OWLAPI-" + f.getReasonerName().substring(0, 1);
	// }
	//
	// private OWL2Ontology<OWLObject> o;
	//
	// private OWL2Query<OWLObject> q;
	//
	// @Override
	// public QueryResult<OWLObject> exec() {
	// return OWL2QueryEngine.exec(q);
	// }
	//
	// @Override
	// public void loadOntology(final Map<URI, URI> mapping,
	// String... ontologyURIs) {
	// final OWLOntologyManager m = OWLManager
	// .createOWLOntologyManager();
	// m.addIRIMapper(new OWLOntologyIRIMapper() {
	//
	// @Override
	// public URI getPhysicalURI(IRI arg0) {
	// final URI mm = mapping.get(arg0.toURI());
	//
	// if (mm != null) {
	// return mm;
	// } else {
	// return arg0.toURI();
	// }
	// }
	// });
	// try {
	// for (final String uri : ontologyURIs) {
	// if (uri.startsWith("file:")) {
	// m.loadOntologyFromPhysicalURI(URI.create(uri));
	// } else {
	// m.loadOntology(IRI.create(uri));
	// }
	// }
	// OWLOntologyMerger merger = new OWLOntologyMerger(m);
	// OWLOntology merged = merger.createMergedOntology(m, IRI
	// .create("http://temp"));
	//
	// OWLReasoner r = f.createReasoner(m, m.getOntologies());
	// r.classify();
	//
	// o = new OWLAPIv3OWL2Ontology(m, merged, r);
	// } catch (OWLOntologyCreationException e) {
	// e.printStackTrace();
	// } catch (OWLOntologyChangeException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (OWLReasonerException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// @Override
	// public void loadQuery(String queryURI) {
	// try {
	// q = new
	// cz.cvut.kbss.owl2query.parser.arq.SparqlARQParser<OWLObject>()
	// .parse(new FileInputStream(new File(URI
	// .create(queryURI))), o);
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// } catch (QueryParseException e) {
	// e.printStackTrace();
	// }
	// }
	// };
	// }

	public final <T> QueryResult<T> run(final ReasonerPlugin<T> plugin,
			final String queryURI, final String mappingFile,
			final String... ontologyURIs) {
		final long now = System.currentTimeMillis();
		plugin.loadOntology(MappingFileParser
				.getMappings(new File(mappingFile)), ontologyURIs);
		System.out.print((System.currentTimeMillis() - now) + "\t|\t");
		plugin.loadQuery(queryURI);
		System.out.print((System.currentTimeMillis() - now) + "\t|\t");
		final QueryResult<T> qr = plugin.exec();

		printResults(qr, new PrintWriter(System.out));

		System.out.print((System.currentTimeMillis() - now) + "\t|\t"
				+ qr.size() + "\t|\t");

		return qr;
	}

	private <T> void printResults(final QueryResult<T> res, final PrintWriter w) {
		byte colWidth = 50;
		w.write(Character.LINE_SEPARATOR);
		for (final Variable<T> var : res.getResultVars()) {
			for (int i = 0; i < colWidth / 2; i++) {
				w.write(" ");
			}

			w.write(var.getName());

			for (int i = colWidth / 2; i < colWidth; i++) {
				w.write(" ");
			}
		}
		w.write(Character.LINE_SEPARATOR);
		for (int i = 0; i < colWidth * res.getResultVars().size(); i++) {
			w.write("=");
		}

		w.write(Character.LINE_SEPARATOR);
		for (Iterator<ResultBinding<T>> rb = res.iterator(); rb.hasNext();) {
			ResultBinding<T> r = rb.next();
			for (final Variable<T> var : res.getResultVars()) {
				w.printf("%-"+colWidth+"s", r.get(var).asGroundTerm().toString());
			}
			w.write(Character.LINE_SEPARATOR);
		}
		w.flush();
	}
}
