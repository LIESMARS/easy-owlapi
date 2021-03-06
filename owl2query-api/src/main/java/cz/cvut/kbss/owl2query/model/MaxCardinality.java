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
package cz.cvut.kbss.owl2query.model;

public abstract class MaxCardinality<T> extends QueryExpression<T> {
	protected int card;

	public MaxCardinality(final int card, final Term<T> ope) {
		super(ope);

		this.card = card;
	}

	public MaxCardinality(final int card, final Term<T> ope, final Term<T> ce) {
		super(ope, ce);

		this.card = card;
	}
	
	public String toString() {
		return "MaxCardinality("+card+", " + terms+")";
	}

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }
        
}
