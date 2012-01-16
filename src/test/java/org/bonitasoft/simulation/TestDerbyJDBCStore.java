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
package org.bonitasoft.simulation;

import java.io.File;
import java.sql.ResultSet;
import java.util.Date;

import junit.framework.TestCase;

import org.bonitasoft.simulation.reporting.jdbc.DerbyJDBCStore;

/**
 * @author Romain Bioteau
 *
 */
public class TestDerbyJDBCStore extends TestCase {

	private DerbyJDBCStore store;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		store = new DerbyJDBCStore(new File("target").getAbsolutePath()) ;
	}

	public void testDerbyConnection() throws Exception {

		store.insertProcess("test", 0, 10, 10, 12, 15, 5,100,50, 55, 65) ;
		ResultSet rSet = store.executeQuery("SELECT * FROM PROCESS") ;
		assertNotNull(rSet) ;
		assertTrue(rSet.next()) ;
		rSet.close() ;
	}


	public void testInsertProcess() throws Exception {
		store.insertProcess("test1", 0, 10, 10, 12, 15, 5,100,50, 55, 65) ;
		ResultSet rSet = store.executeQuery("SELECT * FROM PROCESS") ;
		assertNotNull(rSet) ;
		assertTrue(rSet.next()) ;
		rSet.close() ;
	}

	public void testInsertProcessTime() throws Exception {
		store.insertProcessTime(new Date().getTime(), 0, 10, 10,15, 12, 15, 5) ;
		ResultSet rSet = store.executeQuery("SELECT * FROM PROCESS_TIME") ;
		assertNotNull(rSet) ;
		assertTrue(rSet.next()) ;
		rSet.close() ;
	}
	
	public void testInsertProcessCost() throws Exception {
		store.insertProcessCost("$",5,10.5,54) ;
		store.insertProcessCost("Û",0,4.5,5.4) ;
		ResultSet rSet = store.executeQuery("SELECT * FROM PROCESS_COST") ;
		assertNotNull(rSet) ;
		assertTrue(rSet.next()) ;
		rSet.close() ;
	}


	public void testInsertActivity() throws Exception {
		store.insertActivity("a1", 0, 10, 10, 12, 15, 5, 100,50,55, 65) ;
		ResultSet rSet = store.executeQuery("SELECT * FROM ACTIVITY") ;
		assertNotNull(rSet) ;
		assertTrue(rSet.next()) ;
		rSet.close() ;
	}

	public void testInsertActivityTime() throws Exception {
		store.insertActivityTime(new Date().getTime(),"a1", 551515,6564,55500000L, 54564, 10, 12, 15, 5) ;
		ResultSet rSet = store.executeQuery("SELECT * FROM ACTIVITY_TIME") ;
		assertNotNull(rSet) ;
		assertTrue(rSet.next()) ;
		rSet.close() ;
	}

	public void testInsertResource() throws Exception {
		store.insertResource("r1","$", 0.5, 0, 0.35, 0.7, 1500, 100, 250, 700, 5454, 2, 10.5, 20) ;
		ResultSet rSet = store.executeQuery("SELECT * FROM RESOURCE") ;
		assertNotNull(rSet) ;
		assertTrue(rSet.next()) ;
		rSet.close() ;
	}

	public void testInsertResourceTime() throws Exception {
		store.insertResourceTime("r1",new Date().getTime(),0,2.5,5,10,6,0.8,550,54.8f);
		ResultSet rSet = store.executeQuery("SELECT * FROM RESOURCE_TIME") ;
		assertNotNull(rSet) ;
		assertTrue(rSet.next()) ;
		rSet.close() ;
	}

	public void testInsertNumberData() throws Exception {
		store.insertNumberData("d1", 500, 800.5,1200);
		ResultSet rSet = store.executeQuery("SELECT * FROM NUMBER_DATA") ;
		assertNotNull(rSet) ;
		assertTrue(rSet.next()) ;
		rSet.close() ;
	}

	public void testInsertLiteralData() throws Exception {
		store.insertLiteralData("d2", "high", 0.56);
		store.insertLiteralData("d2", "low", 0.44);
		ResultSet rSet = store.executeQuery("SELECT * FROM LITERAL_DATA") ;
		assertNotNull(rSet) ;
		assertTrue(rSet.next()) ;
		rSet.close() ;
	}

	public void testInsertLoadprofile() throws Exception {
		store.insertLoadprofile("P1",new Date().getTime(), new Date().getTime()+5000, 500,"CONSTANT");
		ResultSet rSet = store.executeQuery("SELECT * FROM LOADPROFILE") ;
		assertNotNull(rSet) ;
		assertTrue(rSet.next()) ;
		rSet.close() ;
	}

	public void testCloseDB() throws Exception {
		store.close() ;
		File[] work = new File("target").listFiles();
		for(File f : work){
			System.out.println(f.getName());
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		store.close() ;
		for(File f : new File("target").listFiles()){
			if(f.isDirectory()){
				assertTrue(!f.getName().startsWith("tmp"));
			}
		}
	}

}
