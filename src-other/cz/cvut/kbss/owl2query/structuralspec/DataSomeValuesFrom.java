package cz.cvut.kbss.owl2query.structuralspec;

import java.util.List;

public interface DataSomeValuesFrom extends ClassExpression {

	DataRange getDataRange();
	
	/**
	 * A list of properties to be used with an n-ary DataRange 
	 */
	List<DataPropertyExpression> getDataPropertyExpressions();
}
