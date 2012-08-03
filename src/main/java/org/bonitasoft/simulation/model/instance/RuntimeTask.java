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
 
package org.bonitasoft.simulation.model.instance;



/**
 * @author Romain Bioteau
 *
 */
public class RuntimeTask implements Comparable<RuntimeTask>{

	private SimActivityInstance task ;
	private long startDate ;
	
	public RuntimeTask(final SimActivityInstance simFlowElement, final long startDate){
		this.task = simFlowElement ;
		this.startDate = startDate ;
	}

	public SimActivityInstance getTask() {
		return task;
	}
	
	
	public long getStartDate() {
		return startDate;
	}

	public int compareTo(final RuntimeTask task) {
		if (startDate < task.getStartDate()){
			return -1 ;
		} else if(startDate > task.getStartDate()){
			return 1 ;
		} else if(getTask().equals(task.getTask()) && getStartDate() == task.getStartDate()){
			return 0 ;
		}
		
		return 1 ;
	}

	public void setStartDate(long newDate) {
		this.startDate = newDate ;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SimActivityInstance){
			return getTask().equals(obj) ;
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return (int) (startDate + task.hashCode());
	}
}
