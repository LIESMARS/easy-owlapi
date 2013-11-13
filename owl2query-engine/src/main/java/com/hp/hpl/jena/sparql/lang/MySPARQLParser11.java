/*******************************************************************************
 * Copyright (C) 2011 Czech Technical University in Prague                                                                                                                                                        
 *                                                                                                                                                                                                                
 * This program is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your option) any 
 * later version. 
 *                                                                                                                                                                                                                
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
 * details. You should have received a copy of the GNU General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.hp.hpl.jena.sparql.lang;

import java.io.InputStream;
import java.io.Reader;

import com.hp.hpl.jena.sparql.lang.sparql_11.SPARQLParser11;
import com.hp.hpl.jena.sparql.lang.sparql_11.SPARQLParser11TokenManager;
import com.hp.hpl.jena.sparql.util.LabelToNodeMap;
import com.hp.hpl.jena.sparql.util.MyLabelToNodeMap;

public class MySPARQLParser11 extends SPARQLParser11 {
	
	
	
	// label => bNode for construct templates patterns
//    final LabelToNodeMap bNodeLabels = new MyLabelToNodeMap(false, null); line change
    final LabelToNodeMap bNodeLabels = new MyLabelToNodeMap(false, null);
    
    // label => bNode (as variable) for graph patterns
//    final LabelToNodeMap anonVarLabels = new MyLabelToNodeMap(false, null); line change
    final LabelToNodeMap anonVarLabels = new MyLabelToNodeMap(false, null);
	
    
    //contructor change
	public MySPARQLParser11(SPARQLParser11TokenManager tm) {
		super(tm);
		init();
	}

	//contructor change
	public MySPARQLParser11(InputStream stream, java.lang.String encoding) {
		super(stream, encoding);
		init();
	}

	//contructor change
	public MySPARQLParser11(InputStream stream) {
		super(stream);
		init();
	}

	//contructor change
	public MySPARQLParser11(Reader stream) {
		super(stream);
		init();
	}
	
	//helper method
	protected void init(){
		((ParserBase)this).activeLabelMap = anonVarLabels;
	}
	
	//method changed
	protected void setInConstructTemplate(boolean b)
    {
        inConstructTemplate = b ;
        if ( inConstructTemplate )
        	//activeLabelMap = bNodeLabels ; line changed 
        	((ParserBase)this).activeLabelMap = bNodeLabels ;
        else 
        	//activeLabelMap = anonVarLabels ; line changed
        	((ParserBase)this).activeLabelMap = anonVarLabels ;
    }
	
}
