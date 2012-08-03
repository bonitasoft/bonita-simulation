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

package org.bonitasoft.simulation.reporting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.csv4j.CSVFileProcessor;
import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.CSVWriter;

import org.bonitasoft.simulation.engine.DefinitionPool;
import org.bonitasoft.simulation.model.instance.SimActivityInstance;
import org.bonitasoft.simulation.model.instance.SimDataInstance;
import org.bonitasoft.simulation.model.instance.SimProcessInstance;
import org.bonitasoft.simulation.model.process.SimActivity;
import org.bonitasoft.simulation.model.process.SimBooleanData;
import org.bonitasoft.simulation.model.process.SimData;
import org.bonitasoft.simulation.model.process.SimLiteralsData;
import org.bonitasoft.simulation.model.process.SimNumberData;
import org.bonitasoft.simulation.model.process.SimProcess;

/**
 * @author Romain Bioteau
 *
 */
public class CSVSimReportStorage implements ISimulationStore {

	private static final String PROCESS_INSTANCE = "BonitaSimulation_ProcessInstances_"; //$NON-NLS-1$
	private static final String FINISHED_PROCESS_INSTANCE = "BonitaSimulation_FinishedProcessInstances_"; //$NON-NLS-1$


	private SimpleDateFormat timeStamp;
	private String timeStampString;

	private Map<String,SimActivityInstance> instances;
//	private CSVWriter processCsvWriter;
//	private FileWriter processFileWriter;
//	private FileWriter finishedProcessFileWriter;
//	private CSVWriter finishProcessCsvWriter;
	private String processInstancesTmpFile;
	private String finishedProcessInstancesTmpFile;
	private boolean flushStore;



	public CSVSimReportStorage(String workingDirectory,String processName, boolean flushStore) throws IOException{
		this.timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss") ; //$NON-NLS-1$
		this.timeStampString = timeStamp.format(System.currentTimeMillis()) ;
		this.processInstancesTmpFile = workingDirectory+File.separatorChar+PROCESS_INSTANCE+processName+timeStampString+".csv" ; //$NON-NLS-1$
		this.finishedProcessInstancesTmpFile = workingDirectory+File.separatorChar+FINISHED_PROCESS_INSTANCE+processName+"_"+timeStampString+".csv" ; //$NON-NLS-1$ //$NON-NLS-2$

//		this.processFileWriter = new FileWriter(processInstancesTmpFile) ;
//		this.finishedProcessFileWriter = new FileWriter(finishedProcessInstancesTmpFile) ;
//		this.processCsvWriter = new CSVWriter(processFileWriter);
//		this.finishProcessCsvWriter = new CSVWriter(finishedProcessFileWriter);
		this.flushStore = flushStore ;
	}
	

	public String getStoredInstanceFilename(){
		return processInstancesTmpFile ;
	}
	
	public String getFinishedStoredInstanceFilename(){
		return finishedProcessInstancesTmpFile ;
	}

	public List<SimProcessInstance> getStoredProcessInstances(final int count) throws Exception {

		if(!flushStore && count > 0){
			copyFile(processInstancesTmpFile);
		}

		final List<SimProcessInstance> processInstances = new ArrayList<SimProcessInstance>();
		instances = new HashMap<String, SimActivityInstance>();
		CSVFileProcessor csvProcessor =  new CSVFileProcessor();
		csvProcessor.setHasHeader(false);


		csvProcessor.processFile(processInstancesTmpFile, new CSVLineProcessor() {

			private int currentLine = -1;

			public void processHeaderLine(int arg0, List<String> arg1) {
				//NO HEADER
			}

			public void processDataLine(int lineNumber, List<String> values) {
				if(count < 0){
					processInstances.add(createProcessInstanceFromString(values)) ;
				}else{
					if(lineNumber <= count){
						processInstances.add(createProcessInstanceFromString(values)) ;
					}
				}
				currentLine = lineNumber ;
			}


			public boolean continueProcessing() {
				return count  > 0  ? currentLine  <= count: true;
			}
		}) ;

		removeLines(processInstancesTmpFile,count);

		return processInstances;
	}

	private void copyFile(String filename) throws Exception {
		File outputFile = new File(filename+"copy.csv"); //$NON-NLS-1$
		if(!outputFile.exists()){ //$NON-NLS-1$
			File inputFile = new File(filename);
		
			FileReader in = new FileReader(inputFile);
			FileWriter out = new FileWriter(outputFile);
			int c;

			while ((c = in.read()) != -1)
				out.write(c);

			in.close();
			out.close();
		}
	}

	private void removeLines(String filePath, int lineNumber) throws Exception {

		File inputFile = new File(filePath);
		File newFile = new File(filePath+"tmp") ;
		
		FileReader fr = new FileReader(inputFile) ;
		BufferedReader in = new BufferedReader(fr);
	
		FileWriter fw =	new FileWriter(newFile);
		BufferedWriter out = new BufferedWriter(fw);
		
		String temp;
		int i = 1 ;
		while ((temp = in.readLine()) != null) {
			if(i >= lineNumber){
				out.write(temp);
				out.newLine();
			}
			i++ ;
		}
		out.flush();
		fr.close();
		fw.close();
		in.close();
		out.close();
		
		if(!inputFile.delete()){
			throw new Exception("Fails to delete temporary buffer file");
		}
		if(!newFile.renameTo(inputFile)){
			throw new Exception("Fails to rename temporary buffer file");
		}

	}

	public List<SimProcessInstance> getStoredFinishedProcessInstances(final int count) throws Exception {
	
		if(!flushStore && count > 0){
			copyFile(finishedProcessInstancesTmpFile);
		}

		final List<SimProcessInstance> processInstances = new ArrayList<SimProcessInstance>();
		instances = new HashMap<String, SimActivityInstance>();
		CSVFileProcessor csvProcessor =  new CSVFileProcessor();
		csvProcessor.setHasHeader(false);
		csvProcessor.processFile(finishedProcessInstancesTmpFile, new CSVLineProcessor() {

			private int currentLine = -1;

			public void processHeaderLine(int arg0, List<String> arg1) {
				//NO HEADER
			}

			public void processDataLine(int lineNumber, List<String> values) {
				if(count < 0){
					processInstances.add(createProcessInstanceFromString(values)) ;
				}else{
					if(lineNumber <= count){
						processInstances.add(createProcessInstanceFromString(values)) ;
					}
				}
				currentLine = lineNumber ;
			}

			public boolean continueProcessing() {
				return count  > 0  ? currentLine <= count: true;
			}
		}) ;

		removeLines(finishedProcessInstancesTmpFile,count);

		return processInstances;
	}


	private SimProcessInstance createProcessInstanceFromString(List<String> values) {
		String defName = values.get(0) ;
		String uuid = values.get(1);
		long startDate = Long.parseLong(values.get(2));
		long endDate = Long.parseLong(values.get(3));

		SimProcess procDef = DefinitionPool.getInstance().getProcessDefinition(defName);
		SimProcessInstance simProcInstance = new SimProcessInstance(procDef, uuid,startDate) ;
		simProcInstance.setEndDate(endDate) ;

		List<SimDataInstance> data = createDataInstancesFromString(simProcInstance, values.get(4)) ;
		for(SimDataInstance d : data){
			simProcInstance.addDataInstance(d);
		}

		if(values.size() > 5){

			for(int i = 5 ; i < values.size() ; i++){
				SimActivityInstance instance = createActivityInstanceFromString(simProcInstance,values.get(i)) ;
				instances.put(instance.getInstanceUUID(), instance) ;

				if(((SimActivity) instance.getDefinition()).isStartElement()){
					simProcInstance.addStartElement(instance);
				}
			}

			for(int i = 5 ; i < values.size() ; i++){
				buildProcessInstance(simProcInstance,values.get(i)) ;
			}
		}

		return simProcInstance;
	}

	private void buildProcessInstance(SimProcessInstance simProcInstance,String value) {

		String activityProp = value.substring(value.indexOf('(')+1,value.indexOf(')'));
		String[] values = activityProp.split(","); //$NON-NLS-1$
		String uuid = values[0] ;
		if(instances.get(uuid) != null){
			SimActivityInstance instance = instances.get(uuid) ;
			String[] nextValues = value.split(":") ; //$NON-NLS-1$
			if(nextValues.length == 2){
				StringTokenizer nextValue = new StringTokenizer(nextValues[1], ","); //$NON-NLS-1$
				while (nextValue.hasMoreTokens()) {
					String nextActivityUUID = nextValue.nextToken();
					if(instances.get(nextActivityUUID) != null){
						instance.addNext(instances.get(nextActivityUUID));
					}
				}
			}
		}

	}

	private SimActivityInstance createActivityInstanceFromString(SimProcessInstance parentProcessInstance,String value) {


		String activityName = value.substring(0, value.indexOf('(')) ;
		String activityProp = value.substring(value.indexOf('('),value.indexOf(')'));
		String[] values = activityProp.split(","); //$NON-NLS-1$
		String uuid = values[0].substring(1) ;
		long startDate = Long.parseLong(values[1]) ;
		long executionDate = Long.parseLong(values[2]) ;
		long finishDate = Long.parseLong(values[3]) ;
		int skipped = Integer.parseInt(values[4]) ;


		SimActivity def = DefinitionPool.getInstance().getActivityDefinition(parentProcessInstance.getDefinition().getName(),activityName);

		SimActivityInstance instance = new SimActivityInstance(def, uuid, parentProcessInstance);
		instance.setStartDate(startDate) ;
		instance.setExecutionDate(executionDate);
		instance.setFinishDate(finishDate);
		instance.setSkip(skipped);

		return instance;
	}

	private List<SimDataInstance> createDataInstancesFromString(SimProcessInstance process,String value) {
		List<SimDataInstance> data = new ArrayList<SimDataInstance>();
		StringTokenizer stToken = new StringTokenizer(value, ";") ; //$NON-NLS-1$

		while(stToken.hasMoreTokens()){
			String d = stToken.nextToken() ;
			String[] dataValues = d.split(",") ; //$NON-NLS-1$
			SimData simDataDefinition = DefinitionPool.getInstance().getDataDefinition(process.getDefinition().getName(), dataValues[0]);
			SimDataInstance dataInstance = null ;
			if(simDataDefinition instanceof SimBooleanData){
				dataInstance = new SimDataInstance(simDataDefinition, process.getInstanceUUID(), Boolean.valueOf(dataValues[1]));
			}else if(simDataDefinition instanceof SimNumberData){
				dataInstance = new SimDataInstance(simDataDefinition, process.getInstanceUUID(), Double.valueOf(dataValues[1]));
			}else if(simDataDefinition instanceof SimLiteralsData){
				dataInstance = new SimDataInstance(simDataDefinition, process.getInstanceUUID(), dataValues[1]);
			}

			data.add(dataInstance) ;
		}

		return data;
	}



	public void storeProcessInstances(List<SimProcessInstance> instances) throws Exception {

		FileWriter processFileWriter = new FileWriter(processInstancesTmpFile) ;
		CSVWriter processCsvWriter = new CSVWriter(processFileWriter);
	
		
		for(SimProcessInstance instance : instances){
			processCsvWriter.writeLine(createStringFromProcessInstance(instance,false));
		}
		processFileWriter.flush() ;
		processFileWriter.close();
		

	}


	private List<String> createStringFromProcessInstance(SimProcessInstance instance,boolean finished) {
		DefinitionPool.getInstance().addProcessDefinition((SimProcess) instance.getDefinition());
		List<String> result = new ArrayList<String>();

		result.add(instance.getDefinition().getName());
		result.add(instance.getInstanceUUID());
		result.add(((Long)instance.getStartDate()).toString());
		result.add(((Long)instance.getEndDate()).toString());

		StringBuilder dataValuesBuilder = new StringBuilder();

		for(SimDataInstance di : instance.getDataInstance()){
			dataValuesBuilder.append(di.getDefinition().getName()).append(",").append(di.getValue().toString()).append(";");//$NON-NLS-1$ //$NON-NLS-2$ 
		}

		String dataValues = dataValuesBuilder.toString();
		if(dataValues.length() > 0){
			dataValues = dataValues.substring(0, dataValues.length()-1);
		}

		result.add(dataValues) ;
		if(finished){
			Set<SimActivityInstance> parsed = new HashSet<SimActivityInstance>() ;
			parseFinishedProcessInstance(instance.getStartElemInstances() ,result,parsed) ;
		}else{
			parseProcessInstance(instance.getStartElemInstances() ,result) ;
		}


		return result;
	}

	private void parseProcessInstance(Set<SimActivityInstance> activityInstances, List<String> result) {
		for(SimActivityInstance a : activityInstances){
			DefinitionPool.getInstance().addActivityDefinition(((SimActivity)a.getDefinition()).getParentProcessName(),(SimActivity) a.getDefinition()) ;
			result.add(a.toString());
			if(a.hasNext()){
				parseProcessInstance(a.getNext(),result) ;
			}

		}

	}

	private void parseFinishedProcessInstance(Set<SimActivityInstance> activityInstances, List<String> result,Set<SimActivityInstance> parsed) {
		for(SimActivityInstance a : activityInstances){
			DefinitionPool.getInstance().addActivityDefinition(((SimActivity)a.getDefinition()).getParentProcessName(),(SimActivity) a.getDefinition()) ;
			if(a.getExecutionDate() != 0 ){
				result.add(a.toString());
				parsed.add(a);
				if(a.hasNext()){
					for(SimActivityInstance nextInstance : a.getNext()){
						if(!parsed.contains(nextInstance)){
							parseFinishedProcessInstance(a.getNext(),result,parsed) ;
						}
					}
				}
			}
		}
	}

	public void storeProcessInstance(SimProcessInstance instance) throws Exception {
		FileWriter processFileWriter = new FileWriter(processInstancesTmpFile,true) ;
		CSVWriter processCsvWriter = new CSVWriter(processFileWriter);
		processCsvWriter.writeLine(createStringFromProcessInstance(instance,false));
		processFileWriter.flush() ;
		processFileWriter.close();
	}

	public void closeStore() throws Exception{
		new File(processInstancesTmpFile).delete() ;
		new File(finishedProcessInstancesTmpFile).delete() ;
	}

	public void storeFinishedProcessInstance(SimProcessInstance instance)throws Exception {
	
		FileWriter finishedProcessFileWriter = new FileWriter(finishedProcessInstancesTmpFile,true) ;
		CSVWriter finishProcessCsvWriter = new CSVWriter(finishedProcessFileWriter);
		finishProcessCsvWriter.writeLine(createStringFromProcessInstance(instance,true));
		finishedProcessFileWriter.flush() ;
		finishedProcessFileWriter.close();

	}

	public void storeFinishedProcessInstance(List<SimProcessInstance> instances) throws Exception {
	
		FileWriter finishedProcessFileWriter = new FileWriter(finishedProcessInstancesTmpFile) ;
		CSVWriter finishProcessCsvWriter = new CSVWriter(finishedProcessFileWriter);
		for(SimProcessInstance instance : instances){
			finishProcessCsvWriter.writeLine(createStringFromProcessInstance(instance,true));
		}
		finishedProcessFileWriter.flush() ;
		finishedProcessFileWriter.close();

	}

	public int size() throws FileNotFoundException {
		FileReader reader = new FileReader(finishedProcessInstancesTmpFile) ;
		LineNumberReader lnr = new LineNumberReader(reader) ;
		int result = 0 ;
		try {
			while(lnr.readLine() != null){
				result++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(reader != null){
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(lnr != null){
					lnr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		return result ;
	}

}
