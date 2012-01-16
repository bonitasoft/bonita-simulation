/**
 * Copyright (C) 2010 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package org.bonitasoft.simulation.model.process;

/**
 * @author Romain Bioteau
 *
 */
public class SimTransition extends SimNamedElement {

	private double probability ;
	private boolean isDataBased ;
	private String expression ;
	private SimActivity target ;
	private SimActivity source;
	
	public SimTransition(String name,SimActivity target,boolean isDataBased){
		super(name);
		this.isDataBased = isDataBased;
		this.target = target;
		target.addIncomingTransition(this);
	}

	public SimTransition(String name,SimActivity target,boolean isDataBased,String expression){
		this(name,target,isDataBased);
		this.expression = expression;
		
	}
	
	public SimTransition(String name,SimActivity target,boolean isDataBased,double probability){
		this(name,target,isDataBased);
		this.probability = probability;
	}

	public SimTransition() {
		super(""); //$NON-NLS-1$
	}

	public double getProbability() {
		return probability;
	}

	public boolean isDataBased() {
		return isDataBased;
	}

	public String getExpression() {
		return expression;
	}

	public SimActivity getTarget() {
		return target;
	}

	public SimActivity getSource() {
		return source;
	}

	public void setSource(SimActivity simActivity) {
		this.source = simActivity ;
	}
	
}
