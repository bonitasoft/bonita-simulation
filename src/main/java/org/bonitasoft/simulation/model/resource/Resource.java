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
 
package org.bonitasoft.simulation.model.resource;

import org.bonitasoft.simulation.model.TimeUnit;
import org.bonitasoft.simulation.model.calendar.SimCalendar;


/**
 * @author Romain Bioteau
 *
 */
public class Resource {

	private String name ;
	private int targetQuantity ;
	private String type ;
	private SimCalendar planning ;
	private int maximumQuantity ;
	private String costUnit ;
	private TimeUnit timeUnit ;
	private double fixedCost ;
	private double timeCost ;

	public Resource(String name,String type,int maximumQuantity,int targetQuantity,SimCalendar planning,String costUnit,TimeUnit timeUnit,double fixedCost,double timeCost){
	  this.name = name;
	  this.type = type;
	  this.targetQuantity = targetQuantity;
	  this.maximumQuantity = maximumQuantity;
	  this.planning = planning;
	  this.costUnit = costUnit;
	  this.timeUnit = timeUnit;
	  this.fixedCost = fixedCost;
	  this.timeCost = timeCost;
	}

	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public int getTargetQuantity() {
		return targetQuantity;
	}
	public int getMaximumQuantity() {
		return maximumQuantity;
	}
	public SimCalendar getPlanning() {
		return planning;
	}
	public String getCostUnit() {
		return costUnit;
	}
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}
	public double getFixedCost() {
		return fixedCost;
	}
	public double getTimeCost() {
		return timeCost;
	}

}
