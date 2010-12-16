package cz.cvut.kbss.owl2query.structuralspec;

import java.util.Collections;
import java.util.Set;

public interface Declaration extends Axiom {
	
	public Entity getEntity();
	
	class DeclarationImpl implements Declaration {

		final Set<Annotation> axiomAnnotations;
		
		final Entity e;
		
		public DeclarationImpl( final Entity e ) {
			this(e, Collections.EMPTY_SET);
		}

		public DeclarationImpl( final Entity e, final Set<Annotation> axiomAnnotations ) {
			this.e = e;
			this.axiomAnnotations = axiomAnnotations;
		}
		
		@Override
		public Entity getEntity() {
			return e;
		}

		@Override
		public Set<Annotation> getAxiomAnnotations() {
			return axiomAnnotations;
		}
		
	}
	
}
