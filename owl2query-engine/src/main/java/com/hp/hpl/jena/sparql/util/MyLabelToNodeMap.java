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
package com.hp.hpl.jena.sparql.util;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.core.VarAlloc;

public class MyLabelToNodeMap extends LabelToNodeMap {

	public MyLabelToNodeMap(boolean genVars, VarAlloc allocator) {
		super(genVars, allocator);
		// TODO Auto-generated constructor stub
	}

	public Node asNode(String label)
    {
        Node n = bNodeLabels.get(label) ;
        if ( n != null )
            return n ;
        String name = label;
        if(label.startsWith("_:"))
        	name = label.substring(2);
        	
        n = Var.alloc('?' + name); //allocNode() ; line changed
        bNodeLabels.put(label, n) ;
        return n ;
    }
	
}
