// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public
// License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of
// proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.complexversion.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.cvut.kbss.owl2query.complexversion.modelold.CoreNewImpl;
import cz.cvut.kbss.owl2query.complexversion.modelold.Filter;
import cz.cvut.kbss.owl2query.complexversion.modelold.KnowledgeBase;
import cz.cvut.kbss.owl2query.complexversion.modelold.PelletOptions;
import cz.cvut.kbss.owl2query.complexversion.modelold.Query;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryAtom;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryAtomFactory;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryPredicate;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryResult;
import cz.cvut.kbss.owl2query.complexversion.modelold.QueryResultImpl;
import cz.cvut.kbss.owl2query.complexversion.modelold.ResultBinding;
import cz.cvut.kbss.owl2query.complexversion.modelold.ResultBindingImpl;
import cz.cvut.kbss.owl2query.complexversion.modelold.Taxonomy;
import cz.cvut.kbss.owl2query.complexversion.modelold.TaxonomyNode;
import cz.cvut.kbss.owl2query.complexversion.modelold.Query.VarType;
import cz.cvut.kbss.owl2query.complexversion.model.Term;
import cz.cvut.kbss.owl2query.complexversion.exceptions.InternalReasonerException;
import cz.cvut.kbss.owl2query.complexversion.exceptions.UnsupportedQueryException;
import cz.cvut.kbss.owl2query.complexversion.util.ATermUtils;
import cz.cvut.kbss.owl2query.complexversion.util.CandidateSet;
import cz.cvut.kbss.owl2query.complexversion.util.DisjointSet;

/**
 * <p>
 * Title: Engine for queries with only distinguished variables.
 * </p>
 * <p>
 * Description: All variable name spaces are disjoint.
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
public class CombinedQueryEngine implements QueryExec {
	public static final Logger			log			= Logger.getLogger( CombinedQueryEngine.class
															.getName() );

	public static final QueryOptimizer	optimizer	= new QueryOptimizer();

	private KnowledgeBase				kb;

	protected QueryPlan					plan;

	protected Query						query;

	protected Query						oldQuery;

	private QueryResult					result;

	private Filter						filter;

	private Set<Term>				downMonotonic;

	private void prepare(Query query) {
		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Preparing plan ..." );
		}

		this.kb = query.getKB();
		if( kb == null ) {
			throw new RuntimeException( "No input data set is given for query!" );
		}

		this.result = new QueryResultImpl( query );

		this.oldQuery = query;
		this.query = setupCores( query );

		this.filter = oldQuery.getFilter();

		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "After setting-up cores : " + this.query );
		}

		this.plan = optimizer.getExecutionPlan( this.query );
		this.plan.reset();

		// warm up the reasoner by computing the satisfiability of classes
		// used in the query so that cached models can be used for instance
		// checking - TODO also non-named classes
		if( (PelletOptions.USE_CACHING) && !kb.isClassified() ) {
			for( final QueryAtom a : oldQuery.getAtoms() ) {
				for( final Term arg : a.getArguments() ) {
					if( kb.isClass( arg ) ) {
						kb.isSatisfiable( arg );
						kb.isSatisfiable( ATermUtils.makeNot( arg ) );
					}
				}
			}
		}

		if( PelletOptions.OPTIMIZE_DOWN_MONOTONIC ) {
			// TODO use down monotonic variables for implementation of
			// DirectType atom
			downMonotonic = new HashSet<Term>();
			setupDownMonotonicVariables( this.query );
			if( log.isLoggable( Level.FINE ) ) {
				log.fine( "Variables to be optimized : " + downMonotonic );
			}
		}
	}

	// computes cores of undistinguished variables
	private Query setupCores(final Query query) {
		final Iterator<Term> undistVarIterator = query.getUndistVars().iterator();
		if( !undistVarIterator.hasNext() ) {
			return query;
		}
		final DisjointSet<Object> coreVertices = new DisjointSet<Object>();

		final List<QueryAtom> toRemove = new ArrayList<QueryAtom>();

		while( undistVarIterator.hasNext() ) {
			final Term a = undistVarIterator.next();

			coreVertices.add( a );

			for( final QueryAtom atom : query.findAtoms( QueryPredicate.PropertyValue, a, null,
					null ) ) {
				coreVertices.add( atom );
				coreVertices.union( a, atom );

				final Term a2 = atom.getArguments().get( 2 );
				if( query.getUndistVars().contains( a2 ) ) {
					coreVertices.add( a2 );
					coreVertices.union( a, a2 );
				}
				toRemove.add( atom );
			}
			for( final QueryAtom atom : query.findAtoms( QueryPredicate.PropertyValue, null, null,
					a ) ) {
				coreVertices.add( atom );
				coreVertices.union( a, atom );

				final Term a2 = atom.getArguments().get( 0 );
				if( query.getUndistVars().contains( a2 ) ) {
					coreVertices.add( a2 );
					coreVertices.union( a, a2 );
				}
				toRemove.add( atom );
			}

			for( final QueryAtom atom : query.findAtoms( QueryPredicate.Type, a, null ) ) {
				coreVertices.add( atom );
				coreVertices.union( a, atom );
				toRemove.add( atom );
			}
		}

		final Query transformedQuery = query.apply( new ResultBindingImpl() );

		for( final Set<Object> set : coreVertices.getEquivalanceSets() ) {
			final Collection<QueryAtom> atoms = new ArrayList<QueryAtom>();

			for( final Object a : set ) {
				if( a instanceof QueryAtom ) {
					atoms.add( (QueryAtom) a );
				}
			}

			final CoreNewImpl c = (CoreNewImpl) QueryAtomFactory.Core( atoms,
					query.getUndistVars(), kb );

			transformedQuery.add( c );

			if( log.isLoggable( Level.FINE ) ) {
				log.fine( c.getUndistVars() + " : " + c.getDistVars() + " : " + c.getQuery().getAtoms() );
			}
		}

		for( final QueryAtom atom : toRemove ) {
			transformedQuery.remove( atom );
		}

		return transformedQuery;
	}

	// down-monotonic variables = Class variables in Type atoms and Property
	// variables in PropertyValue atoms
	private void setupDownMonotonicVariables(final Query query) {
		for( final QueryAtom atom : query.getAtoms() ) {
			Term arg;

			switch ( atom.getPredicate() ) {
			case PropertyValue:
			case Type:
				arg = atom.getArguments().get( 1 );
				if( ATermUtils.isVar( arg ) ) {
					downMonotonic.add( arg );
				}
				break;
			default:
				arg = null;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean supports(Query q) {
		// TODO cycles in undist vars and fully undist.vars queries are not
		// supported !!!
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public QueryResult exec(Query query) {
		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Executing query " + query );
		}

//		Timer timer = new Timer( "CombinedQueryEngine" );
//		timer.start();
		// TODO timer start
		prepare( query );
		branches = 0;
		exec( new ResultBindingImpl() );
		// TODO timer stop
//		timer.stop();

//		if( log.isLoggable( Level.FINE ) ) {
//			log.log( Level.FINE, "#B=" + branches + ", time=" + timer.getLast() + " ms." );
//		}

		return result;
	}

	private long	branches;

	private void exec(ResultBinding binding) {
		if( log.isLoggable( Level.FINE ) ) {
			branches++;
		}
		if( filter != null && !filter.accept( binding ) ) {
			return;
		}

		if( !plan.hasNext() ) {
			// TODO if result vars are not same as dist vars.
			if( !binding.isEmpty() || result.isEmpty() ) {
				if( log.isLoggable( Level.FINE ) ) {
					log.fine( "Found binding: " + binding );
				}

				if( !result.getResultVars().containsAll( binding.getAllVariables() ) ) {
					ResultBinding newBinding = new ResultBindingImpl();
					for( Term var : result.getResultVars() ) {
						Term value = binding.getValue( var );

						newBinding.setValue( var, value );
					}
					binding = newBinding;
				}

				result.add( binding );
			}
			if( log.isLoggable( Level.FINER ) ) {
				log.finer( "Returning ... binding=" + binding );
			}
			return;
		}

		final QueryAtom atom3 = plan.next( binding );

		QueryAtom current;

		if( atom3.isGround() ) {
			current = atom3;
		}
		else {
			current = atom3.apply( binding );
		}

		if( log.isLoggable( Level.FINER ) ) {
			log.finer( "Evaluating " + current );
		}
		if( current.isGround() && !(current.getPredicate().equals( QueryPredicate.UndistVarCore )) ) {
			if( QueryEngine.checkGround( current, kb ) ) {
				exec( binding );
			}
		}
		else {

			final List<Term> arguments = current.getArguments();

			boolean direct = false;
			boolean strict = false;

			switch ( current.getPredicate() ) {

			case DirectType:
				direct = true;
			case Type: // TODO implementation of downMonotonic vars
				final Term tI = arguments.get( 0 );
				final Term tC = arguments.get( 1 );

				Set<Term> instanceCandidates = null;
				if( tI.equals( tC ) ) {
					instanceCandidates = kb.getIndividuals().size() < kb.getClasses().size()
						? kb.getIndividuals()
						: kb.getClasses();
					for( final Term ic : instanceCandidates ) {
						if( direct
							? kb.getInstances( ic, direct ).contains( ic )
							: kb.isType( ic, ic ) ) {
							final ResultBinding candidateBinding = binding.clone();

							if( ATermUtils.isVar( tI ) ) {
								candidateBinding.setValue( tI, ic );
							}

							exec( candidateBinding );
						}
					}
				}
				else {
					final Set<Term> classCandidates;

					if( !ATermUtils.isVar( tC ) ) {
						classCandidates = Collections.singleton( tC );
						instanceCandidates = kb.getInstances( tC, direct );
					}
					else if( !ATermUtils.isVar( tI ) ) {
						// classCandidates = flatten(TaxonomyUtils.getTypes(kb
						// .getTaxonomy(), tI, direct)); // TODO
						classCandidates = flatten( kb.getTypes( tI, direct ) ); // TODO
						instanceCandidates = Collections.singleton( tI );
					}
					else {
						classCandidates = kb.getAllClasses();
					}

					// explore all possible bindings
					boolean loadInstances = (instanceCandidates == null);
					for( final Term cls : classCandidates ) {
						if( loadInstances ) {
							instanceCandidates = kb.getInstances( cls, direct );
						}
						for( final Term inst : instanceCandidates ) {
							runNext( binding, arguments, inst, cls );
						}
					} // finish explore bindings
				}
				break;

			case PropertyValue: // TODO implementation of downMonotonic vars
				final Term pvI = arguments.get( 0 );
				final Term pvP = arguments.get( 1 );
				final Term pvIL = arguments.get( 2 );

				Collection<Term> propertyCandidates = null;
				Collection<Term> subjectCandidates = null;
				Collection<Term> objectCandidates = null;

				boolean loadProperty = false;
				boolean loadSubjects = false;
				boolean loadObjects = false;

				if( !ATermUtils.isVar( pvP ) ) {
					propertyCandidates = Collections.singleton( pvP );
					if( !ATermUtils.isVar( pvI ) ) {
						subjectCandidates = Collections.singleton( pvI );
						objectCandidates = kb.getPropertyValues( pvP, pvI );
					}
					else if( !ATermUtils.isVar( pvIL ) ) {
						objectCandidates = Collections.singleton( pvIL );
						subjectCandidates = kb.getIndividualsWithProperty( pvP, pvIL );
					}
					loadProperty = false;
				}
				else {
					if( !ATermUtils.isVar( pvI ) ) {
						subjectCandidates = Collections.singleton( pvI );
					}

					if( !ATermUtils.isVar( pvIL ) ) {
						objectCandidates = Collections.singleton( pvIL );
					}
					else if( !plan.getQuery().getDistVarsForType( VarType.LITERAL ).contains( pvIL ) ) {
						propertyCandidates = kb.getObjectProperties();
					}

					if( propertyCandidates == null ) {
						propertyCandidates = kb.getProperties();
					}
					loadProperty = true;
				}

				loadSubjects = (subjectCandidates == null);
				loadObjects = (objectCandidates == null);

				for( final Term property : propertyCandidates ) {
					// TODO replace this nasty if-cascade with some map for
					// var
					// bindings.
					if( loadObjects && loadSubjects ) {
						if( pvI.equals( pvIL ) ) {
							if( pvI.equals( pvP ) ) {
								if( !kb.hasPropertyValue( property, property, property ) ) {
									continue;
								}
								runNext( binding, arguments, property, property, property );
							}
							else {
								for( final Term i : kb.getIndividuals() ) {
									if( !kb.hasPropertyValue( i, property, i ) ) {
										continue;
									}
									runNext( binding, arguments, i, property, i );
								}
							}
						}
						else {
							if( pvI.equals( pvP ) ) {
								for( final Term i : kb.getIndividuals() ) {
									if( !kb.hasPropertyValue( property, property, i ) ) {
										continue;
									}
									runNext( binding, arguments, property, property, i );
								}
							}
							else if( pvIL.equals( pvP ) ) {
								for( final Term i : kb.getIndividuals() ) {
									if( !kb.hasPropertyValue( i, property, property ) ) {
										continue;
									}
									runNext( binding, arguments, i, property, property );
								}
							}
							else {
								for( final Term subject : kb.getIndividuals() ) {
									for( final Term object : kb.getPropertyValues( property,
											subject ) ) {
										runNext( binding, arguments, subject, property, object );
									}
								}
							}
						}
					}
					else if( loadObjects ) {
						// subject is known.
						if( pvP.equals( pvIL ) ) {
							if( !kb.hasPropertyValue( subjectCandidates.iterator().next(),
									property, property ) ) {
								// terminate
								subjectCandidates = Collections.emptySet();
							}
						}

						for( final Term subject : subjectCandidates ) {
							for( final Term object : kb.getPropertyValues( property, subject ) ) {
								runNext( binding, arguments, subject, property, object );
							}
						}
					}
					else {
						// object is known.
						for( final Term object : objectCandidates ) {
							if( loadSubjects ) {
								if( pvI.equals( pvP ) ) {
									if( kb.hasPropertyValue( property, property, object ) ) {
										subjectCandidates = Collections.singleton( property );
									}
									else {
										// terminate
										subjectCandidates = Collections.emptySet();
									}
								}
								else {
									subjectCandidates = new HashSet<Term>( kb
											.getIndividualsWithProperty( property, object ) );
								}
							}

							for( final Term subject : subjectCandidates ) {
								if( loadProperty
										&& !kb.hasPropertyValue( subject, property, object ) ) {
									continue;
								}

								runNext( binding, arguments, subject, property, object );
							}
						}
					}
				} // finish visiting non-ground triple.
				break;

			case SameAs:
				// optimize - merge nodes
				final Term saI1 = arguments.get( 0 );
				final Term saI2 = arguments.get( 1 );

				for( final Term known : getSymmetricCandidates( VarType.INDIVIDUAL, saI1, saI2 ) ) {

					final Set<Term> dependents;

					if( saI1.equals( saI2 ) ) {
						dependents = Collections.singleton( known );
					}
					else {
						dependents = kb.getSames( known );
					}

					for( final Term dependent : dependents ) {
						runSymetricCheck( current, saI1, known, saI2, dependent, binding );
					}
				}
				break;

			case DifferentFrom:
				// optimize - different from map
				final Term dfI1 = arguments.get( 0 );
				final Term dfI2 = arguments.get( 1 );

				if( !dfI1.equals( dfI2 ) ) {
					for( final Term known : getSymmetricCandidates( VarType.INDIVIDUAL, dfI1,
							dfI2 ) ) {
						for( final Term dependent : kb.getDifferents( known ) ) {
							runSymetricCheck( current, dfI1, known, dfI2, dependent, binding );
						}
					}
				}
				else {
					if( log.isLoggable( Level.FINER ) ) {
						log.finer( "Atom " + current
								+ "cannot be satisfied in any consistent ontology." );
					}
				}
				// TODO What about undist vars ?
				// Query : PropertyValue(?x,p,_:x), Type(_:x, C),
				// DifferentFrom( _:x, x) .
				// Data : p(a,x) . p(b,y) . C(x) . C(y) .
				// Result: {b}
				//
				// Data : p(a,x) . (exists p (C and {y}))(b) . C(x) .
				// Result: {y}
				//
				// rolling-up to ?x : (exists p (C and not {x}))(?x) .
				//
				// More complex problems :
				// Query : PropertyValue(?x,p,_:x), Type(_:x, C),
				// DifferentFrom( _:x, _:y) . Type(_:y, T) .
				// Data : p(a,x) . C(x) .
				// Result: {a}
				//
				// Query : PropertyValue(?x,p,_:x), Type(_:x, C),
				// DifferentFrom( _:x, _:y) . Type(_:y, T) .
				// Data : p(x,x) . C(x) .
				// Result: {}
				//
				// Query : PropertyValue(?x,p,_:x), Type(_:x, C),
				// DifferentFrom( _:x, _:y) . Type(_:y, D) .
				// Data : p(a,x) . C(x) . D(a) .
				// Result: {a}
				//
				// rolling-up to ?x : (exists p (C and (not D)))(?x) .
				//
				// rolling-up to _:x of DifferentFrom(_:x,_:y) :
				// roll-up(_:x) and (not roll-up(_:y)).
				// but it is not complete if the rolling-up to _:y is not
				// complete, but just a preprocessing (for example _:y is in
				// a cycle).
				break;

			case Annotation:
				final Term aI = arguments.get( 0 );
				final Term aP = arguments.get( 1 );
				final Term aIL = arguments.get( 2 );

				propertyCandidates = null;
				subjectCandidates = null;
				objectCandidates = null;

				loadProperty = false;
				loadSubjects = false;
				loadObjects = false;

				propertyCandidates = Collections.singleton( aP );

				if( !ATermUtils.isVar( aI ) ) {
					subjectCandidates = Collections.singleton( aI );
					objectCandidates = kb.getPropertyValues( aP, aI );
				}
				else if( !ATermUtils.isVar( aIL ) ) {
					objectCandidates = Collections.singleton( aIL );
					subjectCandidates = kb.getIndividualsWithProperty( aP, aIL );
				}

				loadProperty = false;
				loadSubjects = (subjectCandidates == null);
				loadObjects = (objectCandidates == null);

				for( final Term property : propertyCandidates ) {
					if( loadObjects && loadSubjects ) {
						if( aI.equals( aIL ) ) {
							for( final Term i : kb.getIndividuals() ) {
								if( !kb.isAnnotation( i, property, i ) )
									continue;
								runNext( binding, arguments, i, property, i );
							}
						}
						else {
							for( final Term subject : kb.getIndividuals() ) {
								for( final Term object : kb.getAnnotations( subject, property ) ) {
									runNext( binding, arguments, subject, property, object );
								}
							}
						}
					}
					else if( loadObjects ) {
						// subject is known.
						for( final Term subject : subjectCandidates ) {
							for( final Term object : kb.getAnnotations( subject, property ) ) {
								runNext( binding, arguments, subject, property, object );
							}
						}
					}
					else {
						// object is known.
						for( final Term object : objectCandidates ) {
							if( loadSubjects )
								subjectCandidates = new HashSet<Term>( kb
										.getIndividualsWithAnnotation( property, object ) );

							for( final Term subject : subjectCandidates ) {
								if( loadProperty && !kb.isAnnotation( subject, property, object ) )
									continue;

								runNext( binding, arguments, subject, property, object );
							}
						}
					}
				}

				break;
			// throw new IllegalArgumentException("The annotation atom "
			// + current + " should be ground, but is not.");

			// TBOX ATOMS
			case DirectSubClassOf:
				direct = true;
			case StrictSubClassOf:
				strict = true;
			case SubClassOf:
				final Term scLHS = arguments.get( 0 );
				final Term scRHS = arguments.get( 1 );

				if( scLHS.equals( scRHS ) ) {
					// TODO optimization for downMonotonic variables
					for( final Term ic : kb.getClasses() ) {
						runNext( binding, arguments, ic, ic );
					}
				}
				else {
					final boolean lhsDM = isDownMonotonic( scLHS );
					final boolean rhsDM = isDownMonotonic( scRHS );

					if( lhsDM || rhsDM ) {
						downMonotonic( kb.getTaxonomy(), kb.getClasses(), lhsDM, scLHS, scRHS,
								binding, direct, strict );
					}
					else {
						final Set<Term> lhsCandidates;
						Set<Term> rhsCandidates = null;

						if( !ATermUtils.isVar( scLHS ) ) {
							lhsCandidates = Collections.singleton( scLHS );
							rhsCandidates = flatten( kb.getSuperClasses( scLHS, direct ) );

							rhsCandidates.addAll( kb.getEquivalentClasses( scLHS ) );

							if( strict ) {
								rhsCandidates.removeAll( kb.getEquivalentClasses( scLHS ) );
							}
							else if( !ATermUtils.isComplexClass( scLHS ) ) {
								rhsCandidates.add( scLHS );
							}
						}
						else if( !ATermUtils.isVar( scRHS ) ) {
							rhsCandidates = Collections.singleton( scRHS );
							lhsCandidates = flatten( kb.getSubClasses( scRHS, direct ) );

							lhsCandidates.addAll( kb.getEquivalentClasses( scRHS ) );

							if( strict ) {
								lhsCandidates.removeAll( kb.getEquivalentClasses( scRHS ) );
							}
							else if( !ATermUtils.isComplexClass( scRHS ) ) {
								lhsCandidates.add( scRHS );
							}
						}
						else {
							lhsCandidates = kb.getClasses();
						}

						boolean reload = (rhsCandidates == null);
						for( final Term subject : lhsCandidates ) {
							if( reload ) {
								rhsCandidates = flatten( kb.getSuperClasses( subject, direct ) );
								if( strict ) {
									rhsCandidates.removeAll( kb.getEquivalentClasses( subject ) );
								}
								else if( !ATermUtils.isComplexClass( subject ) ) {
									rhsCandidates.add( subject );
								}
							}
							for( final Term object : rhsCandidates ) {
								runNext( binding, arguments, subject, object );
							}
						}
					}
				}
				break;

			case EquivalentClass: // TODO implementation of downMonotonic vars
				final Term eqcLHS = arguments.get( 0 );
				final Term eqcRHS = arguments.get( 1 );

				for( final Term known : getSymmetricCandidates( VarType.CLASS, eqcLHS, eqcRHS ) ) {
					// TODO optimize - try just one - if success then take
					// all
					// found bindings and extend them for other equivalent
					// classes as well.
					// meanwhile just a simple check below

					final Collection<? extends Term> dependents;

					if( eqcLHS.equals( eqcRHS ) ) {
						dependents = Collections.singleton( known );
					}
					else {
						dependents = kb.getEquivalentClasses( known );
					}

					for( final Term dependent : dependents ) {
						int size = result.size();

						runSymetricCheck( current, eqcLHS, known, eqcRHS, dependent, binding );

						if( result.size() == size ) {
							// no binding found, so that there is no need to
							// explore other equivalent classes - they fail
							// as
							// well.
							break;
						}
					}
				}
				break;

			case DisjointWith: // TODO implementation of downMonotonic vars
				final Term dwLHS = arguments.get( 0 );
				final Term dwRHS = arguments.get( 1 );

				if( !dwLHS.equals( dwRHS ) ) {
					// TODO optimizeTBox
					for( final Term known : getSymmetricCandidates( VarType.CLASS, dwLHS,
							dwRHS ) ) {
						for( final Set<Term> dependents : kb.getDisjointClasses( known ) ) {
							for( final Term dependent : dependents ) {
								runSymetricCheck( current, dwLHS, known, dwRHS, dependent, binding );
							}
						}
					}
				}
				else {
					log.finer( "Atom " + current
							+ "cannot be satisfied in any consistent ontology." );
				}
				break;

			case ComplementOf: // TODO implementation of downMonotonic vars
				final Term coLHS = arguments.get( 0 );
				final Term coRHS = arguments.get( 1 );

				if( !coLHS.equals( coRHS ) ) {
					// TODO optimizeTBox
					for( final Term known : getSymmetricCandidates( VarType.CLASS, coLHS,
							coRHS ) ) {
						for( final Term dependent : kb.getComplements( known ) ) {
							runSymetricCheck( current, coLHS, known, coRHS, dependent, binding );
						}
					}
				}
				else {
					log.finer( "Atom " + current
							+ "cannot be satisfied in any consistent ontology." );
				}
				break;

			// RBOX ATOMS
			case DirectSubPropertyOf:
				direct = true;
			case StrictSubPropertyOf:
				strict = true;
			case SubPropertyOf:
				final Term spLHS = arguments.get( 0 );
				final Term spRHS = arguments.get( 1 );

				if( spLHS.equals( spRHS ) ) {
					// TODO optimization for downMonotonic variables
					for( final Term ic : kb.getProperties() ) {
						runNext( binding, arguments, ic, ic );
					}
				}
				else {
					final boolean lhsDM = isDownMonotonic( spLHS );
					final boolean rhsDM = isDownMonotonic( spRHS );

					if( lhsDM || rhsDM ) {
						downMonotonic( kb.getRoleTaxonomy(), kb.getProperties(), lhsDM, spLHS,
								spRHS, binding, direct, strict );
					}
					else {
						final Set<Term> spLhsCandidates;
						Set<Term> spRhsCandidates = null;

						if( !ATermUtils.isVar( spLHS ) ) {
							spLhsCandidates = Collections.singleton( spLHS );
							spRhsCandidates = flatten( kb.getSuperProperties( spLHS, direct ) );
							if( strict ) {
								spRhsCandidates.removeAll( kb.getEquivalentProperties( spLHS ) );
							}
							else {
								spRhsCandidates.add( spLHS );
							}
						}
						else if( !ATermUtils.isVar( spRHS ) ) {
							spRhsCandidates = Collections.singleton( spRHS );
							spLhsCandidates = flatten( kb.getSubProperties( spRHS, direct ) );
							if( strict ) {
								spLhsCandidates.removeAll( kb.getEquivalentProperties( spRHS ) );
							}
							else {
								spLhsCandidates.add( spRHS );
							}
						}
						else {
							spLhsCandidates = kb.getProperties();
						}
						boolean reload = (spRhsCandidates == null);
						for( final Term subject : spLhsCandidates ) {
							if( reload ) {
								spRhsCandidates = flatten( kb.getSuperProperties( subject, direct ) );
								if( strict ) {
									spRhsCandidates
											.removeAll( kb.getEquivalentProperties( subject ) );
								}
								else {
									spRhsCandidates.add( subject );
								}
							}
							for( final Term object : spRhsCandidates ) {
								runNext( binding, arguments, subject, object );
							}
						}
					}
				}
				break;

			case EquivalentProperty: // TODO implementation of downMonotonic
				// vars
				final Term eqpLHS = arguments.get( 0 );
				final Term eqpRHS = arguments.get( 1 );

				// TODO optimize - try just one - if success then take all
				// found
				// bindings and extend them for other equivalent classes as
				// well.
				// meanwhile just a simple check below
				for( final Term known : getSymmetricCandidates( VarType.PROPERTY, eqpLHS,
						eqpRHS ) ) {
					final Set<Term> dependents;

					if( eqpLHS.equals( eqpRHS ) ) {
						dependents = Collections.singleton( known );
					}
					else {
						dependents = kb.getEquivalentProperties( known );
					}

					for( final Term dependent : dependents ) {
						int size = result.size();
						runSymetricCheck( current, eqpLHS, known, eqpRHS, dependent, binding );
						if( result.size() == size ) {
							// no binding found, so that there is no need to
							// explore other equivalent classes - they fail
							// as
							// well.
							break;
						}

					}
				}
				break;

			case InverseOf: // TODO implementation of downMonotonic vars
				final Term ioLHS = arguments.get( 0 );
				final Term ioRHS = arguments.get( 1 );

				if( !ioLHS.equals( ioRHS ) ) {
					for( final Term known : getSymmetricCandidates( VarType.PROPERTY, ioLHS,
							ioRHS ) ) {

						// meanwhile workaround
						for( final Term dependent : kb.getInverses( known ) ) {
							runSymetricCheck( current, ioLHS, known, ioRHS, dependent, binding );
						}
					}
				}
				// not break here - if there is an atom InverseOf(?X,?X) we can
				// just
				// retrieve all symmetric properties.
			case Symmetric:
				runAllPropertyChecks( current, arguments.get( 0 ), kb.getSymmetricProperties(),
						binding );
				break;

			case ObjectProperty:
				runAllPropertyChecks( current, arguments.get( 0 ), kb.getObjectProperties(),
						binding );
				break;

			case DatatypeProperty:
				runAllPropertyChecks( current, arguments.get( 0 ), kb.getDataProperties(), binding );
				break;

			case Functional:
				runAllPropertyChecks( current, arguments.get( 0 ), kb.getFunctionalProperties(),
						binding );
				break;

			case InverseFunctional:
				runAllPropertyChecks( current, arguments.get( 0 ), kb
						.getInverseFunctionalProperties(), binding );
				break;

			case Transitive:
				runAllPropertyChecks( current, arguments.get( 0 ), kb.getTransitiveProperties(),
						binding );
				break;

			case UndistVarCore:
				// TODO Core IF
				final CoreNewImpl core = (CoreNewImpl) current.apply( binding );

				final Collection<Term> distVars = core.getDistVars();

				if( distVars.isEmpty() ) {
					final Collection<Term> constants = core.getConstants();
					if( constants.isEmpty() ) {
						if( QueryEngine.execBooleanABoxQuery( core.getQuery() ) )
							result.add( binding );
						// throw new RuntimeException(
						// "The query contains neither dist vars, nor constants,
						// yet evaluated by the CombinedQueryEngine !!! ");
					}
					else {
						final Term c = constants.iterator().next();
						final Term clazz = core.getQuery().rollUpTo( c,
								Collections.<Term>emptySet(), STOP_ROLLING_ON_CONSTANTS );

						if( kb.isType( c, clazz ) ) {
							exec( binding );
						}
					}
				}
				else if( distVars.size() == 1 ) {
					final Term var = distVars.iterator().next();
					final Term c = core.getQuery().rollUpTo( var, Collections.<Term>emptySet(),
							STOP_ROLLING_ON_CONSTANTS );
					final Collection<Term> instances = kb.getInstances( c );

					for( final Term a : instances ) {
						final ResultBinding candidateBinding = binding.clone();
						candidateBinding.setValue( var, a );
						exec( candidateBinding );
					}
				}
				else {
					// TODO
					// if (distVars.size() == 2
					// && core.getUndistVars().size() == 1
					// && !kb.getExpressivity().hasNominal()
					// && !kb.getExpressivity().hasTransitivity()) {
					// // TODO 1. undist. var. in distinguished manner
					// // TODO 2. identify both DV's
					// }

					final CoreStrategy s = QueryEngine.getStrategy( current );

					switch ( s ) {
					case SIMPLE:
						execSimpleCore( oldQuery, binding, distVars );
						break;
					case ALLFAST:
						execAllFastCore( oldQuery, binding, distVars, core.getUndistVars() );
						break;
					default:
						throw new InternalReasonerException( "Unknown core strategy." );
					}
				}

				break;
				
			case Not:
				throw new UnsupportedQueryException( "Unbound variables in a negation atom: "
						+ atom3 );
				
			case Execute:
				Query query = QueryRegistry.getQuery( atom3 ); 
				QueryResult result = QueryEngine.exec( query, kb );
				for( ResultBinding b : result ) {
					ResultBinding candidateBinding = binding.clone();
					candidateBinding.setValues( b );

					exec( candidateBinding );
				}
				break;
				
			default:
				throw new UnsupportedQueryException( "Unknown atom type '"
						+ current.getPredicate() + "'." );

			}
		}

		if( log.isLoggable( Level.FINER ) ) {
			log.finer( "Returning ... " + binding );
		}

		plan.back();
	}

	private void runAllPropertyChecks(QueryAtom current, Term var,
			Collection<Term> objectProperties, ResultBinding binding) {
		// TODO Auto-generated method stub
		
	}

	private boolean	STOP_ROLLING_ON_CONSTANTS	= false;

	private void execSimpleCore(final Query q, final ResultBinding binding,
			final Collection<Term> distVars) {
		final Map<Term, Set<Term>> varBindings = new HashMap<Term, Set<Term>>();

		final KnowledgeBase kb = q.getKB();

		for( final Term currVar : distVars ) {
			Term rolledUpClass = q.rollUpTo( currVar, Collections.<Term>emptySet(),
					STOP_ROLLING_ON_CONSTANTS );

			if( log.isLoggable( Level.FINER ) ) {
				log.finer( currVar + " rolled to " + rolledUpClass );
			}

			Set<Term> inst = kb.getInstances( rolledUpClass );
			varBindings.put( currVar, inst );
		}

		if( log.isLoggable( Level.FINER ) ) {
			log.finer( "Var bindings: " + varBindings );
		}

		final Set<Term> literalVars = q.getDistVarsForType( VarType.LITERAL );
		final Set<Term> individualVars = q.getDistVarsForType( VarType.INDIVIDUAL );

		boolean hasLiterals = !individualVars.containsAll( literalVars );

		for( final Iterator<ResultBinding> i = new BindingIterator( varBindings ); i.hasNext(); ) {
			final ResultBinding candidate = i.next().clone();
			candidate.setValues( binding );
			if( hasLiterals ) {
				for( final Iterator<ResultBinding> l = new LiteralIterator( q, candidate ); l
						.hasNext(); ) {
					final ResultBinding mappy = binding.clone();
					mappy.setValues( l.next() );
					if( QueryEngine.execBooleanABoxQuery( q.apply( mappy ) ) ) {
						exec( mappy );
					}
				}
			}
			else {
				if( QueryEngine.execBooleanABoxQuery( q.apply( candidate ) ) ) {
					exec( candidate );
				}
			}
		}
	}

	private Map<Term, Boolean> fastPrune(final Query q, final Term var) {
		// final Collection<Term> instances = new HashSet<Term>(kb
		// .getIndividuals());
		//
		// final KnowledgeBase kb = q.getKB();
		//
		//		
		//		
		// for (final QueryAtom atom : q.findAtoms(QueryPredicate.PropertyValue,
		// node, null, null)) {
		// instances.retainAll(kb.retrieveIndividualsWithProperty(atom
		// .getArguments().get(1)));
		// }
		// for (final QueryAtom atom : q.findAtoms(QueryPredicate.PropertyValue,
		// null, null, node)) {
		// instances.retainAll(kb.retrieveIndividualsWithProperty(ATermUtils
		// .makeInv(atom.getArguments().get(1))));
		// }
		// return instances;

		// final Term c = q.rollUpTo(var, Collections.EMPTY_SET, false);
		//
		// CandidateSet set = kb.getABox().getObviousInstances(c);
		// log.fine(c + " : " + set.getKnowns().size() + " : "
		// + set.getUnknowns().size());
		//
		// if (set.getUnknowns().isEmpty()) {
		// return set.getKnowns();
		// } else {
		// return kb.getInstances(q
		// .rollUpTo(var, Collections.EMPTY_SET, false));
		// }

		// return kb.getIndividuals();

		final Term c = q.rollUpTo( var, Collections.<Term>emptySet(), STOP_ROLLING_ON_CONSTANTS );
		if( log.isLoggable( Level.FINER ) ) {
			log.finer( var + " rolled to " + c );
		}

		CandidateSet<Term> set = kb.getABox().getObviousInstances( c );

		final Map<Term, Boolean> map = new HashMap<Term, Boolean>();

		for( final Object o : set.getKnowns() ) {
			map.put( (Term) o, true );
		}

		for( final Object o : set.getUnknowns() ) {
			map.put( (Term) o, false );
		}

		return map;
	}

	private void execAllFastCore(final Query q, final ResultBinding binding,
			final Collection<Term> distVars, final Collection<Term> undistVars) {
		if( distVars.isEmpty() ) {
			exec( binding );
		}
		else {
			final Term var = distVars.iterator().next();
			distVars.remove( var );

			final Map<Term, Boolean> instances = fastPrune( q, var );

			for( final Term b : instances.keySet() ) {
				final ResultBinding newBinding = binding.clone();

				newBinding.setValue( var, b );
				final Query q2 = q.apply( newBinding );

				if( instances.get( b ) || QueryEngine.execBooleanABoxQuery( q2 ) ) {
					execAllFastCore( q2, newBinding, distVars, undistVars );
				}
			}

			distVars.add( var );
		}
	}

	// private void execIteratedCore(final Query q, final ResultBinding binding,
	// final Collection<Term> distVars, CoreStrategy strategy) {
	// if (distVars.isEmpty()) {
	// exec(binding);
	// } else {
	// final Term var = distVars.iterator().next();
	// distVars.remove(var);
	//
	// boolean loadAll = (distVars.isEmpty() && !strategy
	// .equals(CoreStrategy.ALLFAST))
	// || strategy.equals(CoreStrategy.OPTIMIZED);
	//
	// final Collection<Term> instances;
	//
	// final KnowledgeBase kb = q.getKB();
	//
	// if (loadAll) {
	// final Term clazz = q.rollUpTo(var, Collections.EMPTY_SET,
	// false);
	//
	// if (log.isLoggable( Level.FINE )) {
	// log
	// .debug("Rolling up " + var + " to " + clazz
	// + " in " + q);
	// }
	//
	// instances = kb.getInstances(clazz);
	// } else {
	// instances = new HashSet<Term>(kb.getIndividuals());
	// for (final QueryAtom atom : q.findAtoms(
	// QueryPredicate.PropertyValue, var, null, null)) {
	// instances.retainAll(kb.retrieveIndividualsWithProperty(atom
	// .getArguments().get(1)));
	// }
	// for (final QueryAtom atom : q.findAtoms(
	// QueryPredicate.PropertyValue, null, null, var)) {
	// instances.retainAll(kb
	// .retrieveIndividualsWithProperty(ATermUtils
	// .makeInv(atom.getArguments().get(1))));
	// }
	//
	// }
	//
	// if (strategy.equals(CoreStrategy.FIRSTFAST)) {
	// strategy = CoreStrategy.OPTIMIZED;
	// }
	//
	// for (final Term b : instances) {
	// if (log.isLoggable( Level.FINE )) {
	// log.fine("trying " + var + " --> " + b);
	// }
	// final ResultBinding newBinding = binding.clone();
	//
	// newBinding.setValue(var, b);
	// final Query q2 = q.apply(newBinding);
	//
	// if (!loadAll || QueryEngine.execBooleanABoxQuery(q2)) {
	// execIteratedCore(q2, newBinding, distVars, strategy);
	// }
	// }
	//
	// distVars.add(var);
	// }
	// }

	private void downMonotonic(final Taxonomy<Term> taxonomy, final Collection<Term> all,
			final boolean lhsDM, final Term lhs, final Term rhs,
			final ResultBinding binding, boolean direct, boolean strict) {
		final Term downMonotonic = lhsDM
			? lhs
			: rhs;
		final Term theOther = lhsDM
			? rhs
			: lhs;
		Collection<Term> candidates;

		if( ATermUtils.isVar( theOther ) ) {
			candidates = all;
			// TODO more refined evaluation in case that both
			// variables are down-monotonic
		}
		else {
			final Term top = lhsDM
				? rhs
				: taxonomy.getTop().getName();

			if( ATermUtils.isComplexClass( top ) ) {
				candidates = kb.getEquivalentClasses( top );

				if( !strict && candidates.isEmpty() ) {
					candidates = flatten( kb.getSubClasses( top, true ) );
				}
			}
			else {
				candidates = Collections.singleton( top );
			}
		}

		for( final Term candidate : candidates ) {
			final ResultBinding newBinding = binding.clone();

			if( ATermUtils.isVar( theOther ) ) {
				newBinding.setValue( theOther, candidate );
			}

			// final Set<Term> toDo = lhsDM ? taxonomy.getFlattenedSubs(
			// ATermUtils.normalize(candidate), direct) :
			// taxonomy.getFlattenedSupers(ATermUtils.normalize(candidate),
			// direct);

			final Set<Term> toDo = lhsDM
				? flatten( taxonomy.getSubs( candidate, direct ) )
				: flatten( taxonomy.getSupers( candidate, direct ) );

			if( strict ) {
				toDo.removeAll( taxonomy.getEquivalents( candidate ) );
			}
			else {
				toDo.add( candidate );
			}

			runRecursively( taxonomy, downMonotonic, candidate, newBinding, new HashSet<Term>(
					toDo ), direct, strict );
		}
	}

	private boolean isDownMonotonic(final Term scLHS) {
		// TODO more refined condition to allow optimization for other atoms as
		// well - Type and
		// PropertyValue as well.

		return PelletOptions.OPTIMIZE_DOWN_MONOTONIC && downMonotonic.contains( scLHS );
	}

	private void runNext(final ResultBinding binding, final List<Term> arguments,
			final Term... values) {

		final ResultBinding candidateBinding = binding.clone();

		for( int i = 0; i < arguments.size(); i++ ) {
			if( ATermUtils.isVar( arguments.get( i ) ) ) {
				candidateBinding.setValue( arguments.get( i ), values[i] );
			}
		}

		exec( candidateBinding );
	}

	private Set<Term> getSymmetricCandidates(VarType forType, Term cA, Term cB) {
		final Set<Term> candidates;

		if( !ATermUtils.isVar( cA ) ) {
			candidates = Collections.singleton( cA );
		}
		else if( !ATermUtils.isVar( cB ) ) {
			candidates = Collections.singleton( cB );
		}
		else {
			switch ( forType ) {
			case CLASS:
				candidates = kb.getClasses();
				break;
			case PROPERTY:
				candidates = kb.getProperties();
				break;
			case INDIVIDUAL:
				candidates = kb.getIndividuals();
				break;
			default:
				throw new RuntimeException( "Uknown variable type : " + forType );
			}
		}

		return candidates;
	}

	private void runRecursively(final Taxonomy<Term> t, final Term downMonotonic,
			final Term rootCandidate, final ResultBinding binding, final Set<Term> toDo,
			final boolean direct, final boolean strict) {
		int size = result.size();

		if( log.isLoggable( Level.FINE ) ) {
			log.fine( "Trying : " + rootCandidate + ", done=" + toDo );
		}

		if( !strict ) {
			toDo.remove( rootCandidate );
			runNext( binding, Collections.singletonList( downMonotonic ), rootCandidate );
		}

		if( strict || result.size() > size ) {
			// final Set<Term> subs = t.getSFlattenedSubs(rootCandidate,
			// direct);
			final Set<Term> subs = flatten( t.getSubs( rootCandidate, direct ) );

			for( final Term subject : subs ) {
				if( !toDo.contains( subject ) ) {
					continue;
				}
				runRecursively( t, downMonotonic, subject, binding, toDo, false, false );
			}
		}
		else {
			if( log.isLoggable( Level.FINE ) ) {
				log.fine( "Skipping subs of " + rootCandidate );
			}
			// toDo.removeAll(t.getFlattenedSubs(rootCandidate, false));
			toDo.removeAll( flatten( t.getSubs( rootCandidate, false ) ) );
		}
	}

	private void runSymetricCheck(@SuppressWarnings("unused") QueryAtom current, Term cA, Term known, Term cB,
			Term dependent, ResultBinding binding) {
		final ResultBinding candidateBinding = binding.clone();

		if( !ATermUtils.isVar( cA ) ) {
			candidateBinding.setValue( cB, dependent );
		}
		else if( !ATermUtils.isVar( cB ) ) {
			candidateBinding.setValue( cA, dependent );
		}
		else {
			candidateBinding.setValue( cA, known );
			candidateBinding.setValue( cB, dependent );
		}

		exec( candidateBinding );
	}

	private void runAllPropertyChecks(@SuppressWarnings("unused") QueryAtom current, final Term var,
			final Set<Term> candidates, ResultBinding binding) {
		if( isDownMonotonic( var ) ) {
			for( final TaxonomyNode<Term> topNode : kb.getRoleTaxonomy().getTop().getSubs() ) {

				final Term top = topNode.getName();

				if( candidates.contains( top ) ) {
					runRecursively( kb.getRoleTaxonomy(), var, topNode.getName(), binding,
							new HashSet<Term>( candidates ), false, false );
				}
			}
		}
		else {
			for( final Term candidate : candidates ) {
				final ResultBinding candidateBinding = binding.clone();

				candidateBinding.setValue( var, candidate );

				exec( candidateBinding );
			}
		}
	}

	private Set<Term> flatten(final Set<Set<Term>> set) {
		final Set<Term> result = new HashSet<Term>();

		for( final Set<Term> set2 : set ) {
			for( final Term a : set2 ) {
				result.add( a );
			}
		}

		return result;
	}
}