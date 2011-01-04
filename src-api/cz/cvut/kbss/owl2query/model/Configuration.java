package cz.cvut.kbss.owl2query.model;

public class Configuration {

	public static boolean UNDIST_VAR_ALL_FAST = true;

	public static boolean SIMPLIFY_QUERY = false;

	// boolean USE_CACHING = true;
	public static boolean OPTIMIZE_DOWN_MONOTONIC = false;

	/**
	 * Sampling ratio for the size estimator.
	 */
	public static double SAMPLING_RATIO = 0.01;

	/**
	 * Maximal number of query atoms to be reordered by static reordering
	 * method.
	 */
	public static int STATIC_REORDERING_LIMIT = 10;
	public static boolean TREAT_ALL_VARS_DISTINGUISHED = false;
	public static boolean FULL_SIZE_ESTIMATE = false;
	public static boolean USE_PSEUDO_NOMINALS = false;
}
