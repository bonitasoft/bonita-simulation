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

import groovy.lang.Binding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.bonitasoft.simulation.engine.DataUtil;
import org.bonitasoft.simulation.model.process.NumericRange;
import org.bonitasoft.simulation.model.process.SimBooleanData;
import org.bonitasoft.simulation.model.process.SimLiteral;
import org.bonitasoft.simulation.model.process.SimLiteralsData;
import org.bonitasoft.simulation.model.process.SimNumberData;

/**
 * @author Romain Bioteau
 *
 */
public class TestSimulationData extends TestCase {

	private static final int SAMPLE_SIZE = 50000;

	public void testBooleanData(){
		SimBooleanData d = new SimBooleanData("testBoolean",0.2);
		int nbOfTrue = 0 ;
		int nbOfFalse = 0 ;
		for(int i =0 ; i<SAMPLE_SIZE ; i++){
			if(DataUtil.generateBoolean(d) == Boolean.TRUE){
				nbOfTrue++ ;
			}else{
				nbOfFalse++ ;
			}
		}

		assertEquals(SAMPLE_SIZE, nbOfFalse+nbOfTrue);
		assertTrue((double)nbOfTrue/SAMPLE_SIZE > 0.18 &&  (double)nbOfTrue/SAMPLE_SIZE < 0.22) ;

		d = new SimBooleanData("testBoolean", 0.50);
		nbOfTrue = 0 ;
		nbOfFalse = 0 ;
		for(int i =0 ; i<SAMPLE_SIZE ; i++){
			if(DataUtil.generateBoolean(d) == Boolean.TRUE){
				nbOfTrue++ ;
			}else{
				nbOfFalse++ ;
			}
		}

		assertEquals(SAMPLE_SIZE, nbOfFalse+nbOfTrue);
		assertTrue((double)nbOfTrue/SAMPLE_SIZE > 0.48 &&  (double)nbOfTrue/SAMPLE_SIZE < 0.52) ;

	}

	public void testNumericData(){


		List<NumericRange> ranges = new ArrayList<NumericRange>();
		NumericRange r1 = new NumericRange(0,1000,0.5,null);
		NumericRange r2 = new NumericRange(1001,1500,0.5,null);


		ranges.add(r1);
		ranges.add(r2);

		SimNumberData d = new SimNumberData("testNumeric", ranges) ;

		int nbInR1 = 0;
		int nbInR2 = 0;
		for(int i =0 ; i<SAMPLE_SIZE ; i++){
			Object v = DataUtil.generateNumeric(d) ;
			assertTrue(((Number)v).doubleValue() >= 0 && ((Number)v).doubleValue() < 1501) ;
			if(((Number)v).doubleValue() <= 1000){
				nbInR1++ ;
			}else if(((Number)v).doubleValue() <=1500){
				assertTrue(((Number)v).doubleValue() >1000) ;
				nbInR2++ ;
			}
		}

		assertEquals(SAMPLE_SIZE, nbInR1+nbInR2);
		assertTrue((double)nbInR1/SAMPLE_SIZE > 0.48 &&  (double)nbInR1/SAMPLE_SIZE < 0.52) ;
		assertTrue((double)nbInR2/SAMPLE_SIZE > 0.48 &&  (double)nbInR2/SAMPLE_SIZE < 0.52) ;

		ranges.clear() ;

		NumericRange r3 = new NumericRange(1501,2000,0.25,null);
		NumericRange r4 = new NumericRange(2001,2500,0.25,null);
		NumericRange r5 = new NumericRange(r1.getMinRange(), r1.getMaxRange() , 0.25, r1.getRepartition());
		NumericRange r6 = new NumericRange(r2.getMinRange(), r2.getMaxRange() , 0.25, r2.getRepartition());

		ranges.add(r3);
		ranges.add(r4);
		ranges.add(r5);
		ranges.add(r6);

		d = new SimNumberData("testNumeric", ranges) ;

		nbInR1 = 0 ;
		nbInR2 = 0 ;
		int nbInR3 = 0;
		int nbInR4 = 0;
		for(int i =0 ; i<SAMPLE_SIZE ; i++){
			Object v = DataUtil.generateNumeric(d) ;
			assertTrue(((Number)v).doubleValue() >= 0 && ((Number)v).doubleValue() <= 2500) ;
			if(((Number)v).doubleValue() <= 1000){
				nbInR1++ ;
			}else if(((Number)v).doubleValue() <=1500){
				assertTrue(((Number)v).doubleValue() >1000) ;
				nbInR2++ ;
			}else if(((Number)v).doubleValue() <=2000){
				assertTrue(((Number)v).doubleValue() >1500) ;
				nbInR3++ ;
			}else if(((Number)v).doubleValue() <=2500){
				assertTrue(((Number)v).doubleValue() >2000) ;
				nbInR4++ ;
			}
		}

		assertEquals(SAMPLE_SIZE, nbInR1+nbInR2+nbInR3+nbInR4);
		assertTrue((double)nbInR1/SAMPLE_SIZE > 0.23 &&  (double)nbInR1/SAMPLE_SIZE < 0.27) ;
		assertTrue((double)nbInR2/SAMPLE_SIZE > 0.23 &&  (double)nbInR2/SAMPLE_SIZE < 0.27) ;
		assertTrue((double)nbInR3/SAMPLE_SIZE > 0.23 &&  (double)nbInR3/SAMPLE_SIZE < 0.27) ;
		assertTrue((double)nbInR4/SAMPLE_SIZE > 0.23 &&  (double)nbInR4/SAMPLE_SIZE < 0.27) ;

	}

	public void testLiteralData(){


		List<SimLiteral> literals = new ArrayList<SimLiteral>();

		SimLiteral l1 = new SimLiteral("low",0.33) ;	
		SimLiteral l2 = new SimLiteral("medium",0.33);
		SimLiteral l3 = new SimLiteral("high",0.33);

		literals.add(l1);
		literals.add(l2);
		literals.add(l3);

		SimLiteralsData d = new SimLiteralsData("testLiteral",literals) ;
		int nbOfL1 = 0;
		int nbOfL2 = 0;
		int nbOfL3 = 0;
		for(int i =0 ; i<SAMPLE_SIZE ; i++){
			Object v = DataUtil.generateLiteral(d) ;
			if(v.equals(l1.getLitValue())){
				nbOfL1++ ;
			}else if(v.equals(l2.getLitValue())){
				nbOfL2++ ;
			}else if(v.equals(l3.getLitValue())){
				nbOfL3++ ;
			}
		}

		assertEquals(SAMPLE_SIZE, nbOfL1+nbOfL2+nbOfL3);
		assertTrue((double)nbOfL1/SAMPLE_SIZE > 0.30 &&  (double)nbOfL1/SAMPLE_SIZE < 0.35) ;
		assertTrue((double)nbOfL2/SAMPLE_SIZE > 0.30 &&  (double)nbOfL2/SAMPLE_SIZE < 0.35) ;
		assertTrue((double)nbOfL3/SAMPLE_SIZE > 0.30 &&  (double)nbOfL3/SAMPLE_SIZE < 0.35) ;

	}
	
	public void testEvaluateGroovyExpression() throws Exception{
		Map<String, Object> variables = new HashMap<String, Object>() ;
		variables.put("a",2);
		assertEquals(Boolean.TRUE,DataUtil.getInstance().evaluateGroovyExpression("a==2",new Binding(variables))) ;
		
		variables.put("a",3);
		assertEquals(Boolean.FALSE,DataUtil.getInstance().evaluateGroovyExpression("a==2",new Binding(variables))) ;
	}
}
