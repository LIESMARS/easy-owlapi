package org.mindswap.pellet.utils;

public interface Bool {

	Bool TRUE = null;
	Bool UNKNOWN = null;
	Bool FALSE = null;
	boolean isUnknown();
	boolean isFalse();
	boolean isKnown();
	boolean isTrue();

}
