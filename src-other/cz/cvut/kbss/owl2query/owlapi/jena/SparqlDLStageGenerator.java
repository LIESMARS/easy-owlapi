// Copyright (c) 2006 - 2008, Clark & Parsia, LLC. <http://www.clarkparsia.com>
// This source code is available under the terms of the Affero General Public License v3.
//
// Please see LICENSE.txt for full license terms, including the availability of proprietary exceptions.
// Questions, comments, or requests for clarification: licensing@clarkparsia.com

package cz.cvut.kbss.owl2query.owlapi.jena;

import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.main.Stage;
import com.hp.hpl.jena.sparql.engine.main.StageGenerator;
import com.hp.hpl.jena.sparql.engine.main.StageList;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description: A stage generator that generates one {@link SparqlDLStage} for
 * each {@link BasicPattern}
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company: Clark & Parsia, LLC. <http://www.clarkparsia.com>
 * </p>
 * 
 * @author Evren Sirin
 */ 
class SparqlDLStageGenerator implements StageGenerator {
	
	/*
	 * If this variable is true then queries with variable SPO statements are
	 * not handled by the SPARQL-DL engine but fall back to ARQ
	 */
	private boolean handleVariableSPO = true;
	
	public SparqlDLStageGenerator() {
		this(true);
	}
	
	public SparqlDLStageGenerator(boolean handleVariableSPO) {
		this.handleVariableSPO = handleVariableSPO;
	}
	
	public StageList compile(BasicPattern pattern, ExecutionContext execCxt) {
		StageList sList = new StageList();
		Stage stage = new SparqlDLStage( pattern, handleVariableSPO );
		sList.add( stage );
		return sList;
	}
}
