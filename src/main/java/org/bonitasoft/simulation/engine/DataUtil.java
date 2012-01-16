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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bonitasoft.simulation.model.instance.SimActivityInstance;
import org.bonitasoft.simulation.model.instance.SimDataInstance;
import org.bonitasoft.simulation.model.instance.SimProcessInstance;
import org.bonitasoft.simulation.model.process.NumericRange;
import org.bonitasoft.simulation.model.process.SimActivity;
import org.bonitasoft.simulation.model.process.SimBooleanData;
import org.bonitasoft.simulation.model.process.SimData;
import org.bonitasoft.simulation.model.process.SimLiteral;
import org.bonitasoft.simulation.model.process.SimLiteralsData;
import org.bonitasoft.simulation.model.process.SimNumberData;
import org.bonitasoft.simulation.model.process.SimTransition;

/**
 * @author Romain Bioteau
 *
 */
public class DataUtil {

	private static Random RANDOM ;
	private  GroovyShell shell ;
	private HashMap<String, Script> scripts;
	private static DataUtil INSTANCE;
	
	public static DataUtil getInstance(){
		if(INSTANCE == null){
			INSTANCE = new DataUtil();
			RANDOM = new Random();
		}
		return INSTANCE;
	}
	
	public static DataUtil createInstance(){
		INSTANCE = new DataUtil();
		RANDOM = new Random();
		return INSTANCE;
	}
	
	public Random getRandom() {
		return RANDOM ;
	}
	
	private DataUtil(){
		this.shell =  new GroovyShell( Thread.currentThread().getContextClassLoader());
		this.scripts = new HashMap<String, Script>() ;
	}

	public Object evaluateGroovyExpression(final String expression, final Binding binding) throws Exception {
		if(scripts.get(expression) == null){
			scripts.put(expression, shell.parse(expression)) ;
		}
		
		final Script script =  scripts.get(expression) ;
		script.setBinding(binding);
		return script.run();
	}

	public static Object generateDataInstance(final SimData d) {
		if(d instanceof SimBooleanData){
			return generateBoolean((SimBooleanData) d);
		}else if(d instanceof SimNumberData){
			return generateNumeric((SimNumberData) d);
		}else if(d instanceof SimLiteralsData){
			return generateLiteral((SimLiteralsData) d);
		}
		return null;
	}

	public static Object generateLiteral(final SimLiteralsData d) {
		final List<List<Integer>> probabilities = new ArrayList<List<Integer>>();
		for(int i = 0 ; i<d.getLitterals().size() ; i++){
			final List<Integer> p = new ArrayList<Integer>();
			for(int j = (int) (i > 0 ? computeStart(d.getLitterals(),i): 0) ; j < (i > 0 ? probabilities.get(i-1).get(probabilities.get(i-1).size()-1)+1 + d.getLitterals().get(i).getProbability()*100 : d.getLitterals().get(i).getProbability()*100); j++){
				p.add(j) ;
			}
			probabilities.add(p);
		}

	
		int r = RANDOM.nextInt(100) ;
		for(int i = 0 ; i< probabilities.size() ; i++ ){
			if(probabilities.get(i).contains(r)){
				return d.getLitterals().get(i).getLitValue();
			}
		}

		return d.getLitterals().get(probabilities.size()-1).getLitValue() ;//HANDLE CASE 33%,33%,33%
	}

	private static int computeStart(final List<SimLiteral> litterals, final int i) {
		int size = 0 ;
		for(int j = i-1 ; j>=0;j-- ){
			size = (int) (size + litterals.get(j).getProbability()*100) ;
		}
		return size;
	}

	public static Object generateNumeric(final SimNumberData d) {
		final List<List<Integer>> probabilities = new ArrayList<List<Integer>>();
		for(int i = 0 ; i<d.getRanges().size() ; i++){
			final List<Integer> p = new ArrayList<Integer>();
			for(int j = (int) (i > 0 ?computeRangeStart(d.getRanges(),i) : 0) ; j < (i > 0 ? probabilities.get(i-1).get(probabilities.get(i-1).size()-1)+1+d.getRanges().get(i).getProbability()*100 : d.getRanges().get(i).getProbability()*100); j++){
				p.add(j) ;
			}
			probabilities.add(p);
		}
	
		final int r = RANDOM.nextInt(100) ;
		for(int i = 0 ; i< probabilities.size() ; i++ ){
			if(probabilities.get(i).contains(r)){
				return getRangeValue(d.getRanges().get(i));
			}
		}
		return getRangeValue(d.getRanges().get(probabilities.size()-1)) ; //HANDLE CASE 33%,33%,33%
	}

	private static int computeRangeStart(final List<NumericRange> ranges, final int i) {
		int size = 0 ;
		for(int j = i-1 ; j>=0;j-- ){
			size = (int) (size + ranges.get(j).getProbability()*100) ;
		}
		return size;
	}

	public static Number getRangeValue(final NumericRange numericRange) {
		return nextDouble(numericRange.getMinRange().doubleValue(),numericRange.getMaxRange().doubleValue());
	}

	private static double nextDouble(double min, double max){
		double result = RANDOM.nextDouble()*(max-min)+min;
		if (result >= max)
			result = nextDouble(min, max);
		return Math.round(result);
	}

	public static Object generateBoolean(final SimBooleanData d) {
	
		final int r = RANDOM.nextInt(100) ;
		if (r < d.getProbabilityofTrue()*100){
			return true ;
		}else{
			return false ;
		}
	}

	public void updateData(SimProcessInstance processInstance,SimDataInstance processData, SimData activityData) throws Exception {

		if(activityData.getExpression()!= null && activityData.getExpression().trim().length() > 0){
			Binding binding = createProcessVariableBinding(processInstance);
			processData.setValue(evaluateGroovyExpression(activityData.getExpression(), binding));	
		}

	}

	private static Binding createProcessVariableBinding(
			SimProcessInstance processInstance) {
		Map<String, Object> variables = new HashMap<String, Object>();
		for(SimDataInstance di : processInstance.getDataInstance()){
			variables.put(di.getDefinition().getName(), di.getValue()) ;
		}
		return new Binding(variables);
	}

	public void evaluateActivityData(SimActivityInstance task) throws Exception {
		for(SimData d : ((SimActivity)task.getDefinition()).getData()){
			SimDataInstance di = getDataInstance(task.getProcessInstance(),d) ;
			updateData(task.getProcessInstance(),di,d);
		}
	}

	public static SimDataInstance getDataInstance(SimProcessInstance parentProcessInstance, SimData d) {

		for(SimDataInstance di : parentProcessInstance.getDataInstance()){
			if(di.getDefinition().getName().equals(d.getName())){
				return di ;
			}
		}
		return null;
	}

	public boolean evaluateTransition(SimProcessInstance processInstance,SimTransition t) throws Exception {
		String expression = t.getExpression() ;
		Object result = evaluateGroovyExpression(expression,createProcessVariableBinding(processInstance)) ;
		if(result.equals(Boolean.TRUE)){
			return true;
		}else if(result.equals(Boolean.FALSE)){
			return false;
		}else{
			throw new Exception("Transition evaluation failed to return a boolean result, expression : "+expression) ; //$NON-NLS-1$
		}


	}

}
