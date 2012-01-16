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

import org.bonitasoft.simulation.model.RepartitionType;

/**
 * @author Romain Bioteau
 *
 */
public class NumericRange {

	private Number minRange ;
	private Number maxRange ;
	private double probability ;
	private RepartitionType repartition ;
	
	public NumericRange(Number minRange,Number maxRange,double probability,RepartitionType repartition ){
	  this.minRange = minRange;
	  this.maxRange = maxRange;
	  this.probability = probability;
	  this.repartition = repartition;
	}

	public Number getMinRange() {
		return minRange;
	}

	public Number getMaxRange() {
		return maxRange;
	}

	public double getProbability() {
		return probability;
	}

	public RepartitionType getRepartition() {
		return repartition;
	}
	
}
