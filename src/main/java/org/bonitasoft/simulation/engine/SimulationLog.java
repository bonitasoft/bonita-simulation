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
 
package org.bonitasoft.simulation.engine;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Romain Bioteau
 *
 */
public class SimulationLog {

	private static final String SIMULATION_LOGGER = "simulationLogger"; //$NON-NLS-1$

	public static void log(Throwable t) {
		Logger.getLogger(SIMULATION_LOGGER).log(Level.ALL, t.getMessage(), t);
	}

	public static void log(String message) {
		Logger.getLogger(SIMULATION_LOGGER).log(Level.ALL,message);
	}
	
}
