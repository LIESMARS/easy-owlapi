package cz.cvut.kbss.owl2query.model;

/**
 * @author Petr Kremen
 */
public class MultiFilterWrapper<G> implements Filter<G> {

	private enum FilterType {
		AND, OR;
	}

	private FilterType type;
	private Filter[] filters;

	private MultiFilterWrapper(final FilterType m, final Filter... filters) {
		this.type = m;
		this.filters = filters;
	}

	public boolean accept(final ResultBinding<G> binding) {
		switch (type) {
		case AND:
			for (final Filter f : filters) {
				if (!f.accept(binding)) {
					return false;
				}
			}
			return true;
		case OR:
			for (final Filter f : filters) {
				if (f.accept(binding)) {
					return true;
				}
			}
			return false;
		default:
			throw new RuntimeException("Filter type not supported : " + type);
		}
	}

	public static Filter and(final Filter... filters) {
		return new MultiFilterWrapper(FilterType.AND, filters);
	}

	public static Filter or(final Filter... filters) {
		return new MultiFilterWrapper(FilterType.OR, filters);
	}
}
