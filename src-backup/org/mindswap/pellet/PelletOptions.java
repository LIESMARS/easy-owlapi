package org.mindswap.pellet;

public interface PelletOptions {

	boolean SIMPLIFY_QUERY = false;
	int SAMPLING_RATIO = 0;
	boolean USE_NAIVE_QUERY_ENGINE = false;
	boolean USE_CACHING = false;
	boolean OPTIMIZE_DOWN_MONOTONIC = false;
	int STATIC_REORDERING_LIMIT = 0;
	boolean TREAT_ALL_VARS_DISTINGUISHED = false;
	boolean FULL_SIZE_ESTIMATE = false;
	boolean USE_PSEUDO_NOMINALS = false;

}
