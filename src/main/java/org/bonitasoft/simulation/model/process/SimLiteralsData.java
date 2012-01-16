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

import java.util.Collections;
import java.util.List;

/**
 * @author Romain Bioteau
 *
 */
public class SimLiteralsData extends SimData{

	private List<SimLiteral> litterals ;
	
	public SimLiteralsData(String name,List<SimLiteral> litterals){
		super(name, null);
		this.litterals = litterals;
	}
	
	public SimLiteralsData(String name,String expression){
		super(name, expression);
	}

	public List<SimLiteral> getLitterals() {
		return Collections.unmodifiableList(litterals);
	}
	
}
