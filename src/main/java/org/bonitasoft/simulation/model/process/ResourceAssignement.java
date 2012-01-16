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

import org.bonitasoft.simulation.model.resource.Resource;

/**
 * @author Romain Bioteau
 *
 */
public class ResourceAssignement {

	private Resource resource ;
	private long duration ;
	private int quantity ;
	
	public ResourceAssignement(final Resource resource, final long duration, final int quantity){
		this.resource = resource;
		this.duration = duration;
		this.quantity = quantity;
	}
	
	public Resource getResource() {
		return resource;
	}

	public long getDuration() {
		return duration;
	}

	public int getQuantity() {
		return quantity;
	}

}
