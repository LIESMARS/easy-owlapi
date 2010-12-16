package cz.cvut.kbss.owl2query.structuralspec;

public interface IRI extends AnnotationValue, AnnotationSubject {

	class IRIImpl implements IRI {
		
		String iri;
		
		IRIImpl(String iri) {
			this.iri = iri;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((iri == null) ? 0 : iri.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IRIImpl other = (IRIImpl) obj;
			if (iri == null) {
				if (other.iri != null)
					return false;
			} else if (!iri.equals(other.iri))
				return false;
			return true;
		}
	}
}
