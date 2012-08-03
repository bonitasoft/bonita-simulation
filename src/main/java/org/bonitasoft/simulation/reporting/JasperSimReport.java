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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

import org.bonitasoft.simulation.engine.RuntimeResource;
import org.bonitasoft.simulation.model.loadprofile.InjectionPeriod;
import org.bonitasoft.simulation.model.loadprofile.LoadProfile;
import org.bonitasoft.simulation.model.process.SimNumberData;
import org.bonitasoft.simulation.reporting.jdbc.DerbyJDBCStore;

/**
 * @author Romain Bioteau
 *
 */
public class JasperSimReport extends SimReport {

	protected DerbyJDBCStore jdbcStore;


	public JasperSimReport(String workspace ,LoadProfile lp,ISimulationStore iSimulationStore,Map<String, RuntimeResource> resourceConso , 
			long simStartDate, long simEndDate, long timespan,long executionStart) throws Exception {
		super(workspace,lp, iSimulationStore, simStartDate, simEndDate, timespan, resourceConso,executionStart);
		this.jdbcStore = new DerbyJDBCStore(workspace) ; 

	}

	protected String sotreReport(String processName) throws Exception {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss"); //$NON-NLS-1$
		String timeStamp = sdf.format(System.currentTimeMillis()) ;
		String fileName = workspace+File.separatorChar+processName+"_"+timeStamp+"_report.pdf" ;

		Connection connection = jdbcStore.getConnection() ;

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("PROCESS_NAME", processName) ;
		parameters.put("SHOW_LINK", "Open PDF Version") ;
		parameters.put("PDF_LINK",fileName) ;
		//TODO : see JRParameter.REPORT_CLASS_LOADER to avoid to use this subreport parameter
		parameters.put("SUBREPORT_DIR", workspace+File.separatorChar) ;
		parameters.put("REPORT_CREATION_DATE", "Creation Date: "+new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date())) ;	
		parameters.put("SIMULATION_START_DATE","Simulation Start Date : " + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date(simStartDate)) ) ;
		parameters.put("SIMULATION_END_DATE", "Simulation End Date : " + new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date(simEndDate))) ;
		parameters.put("SIMULATION_DURATION","Simulation Duration : " + getDurationString(simEndDate - simStartDate) ) ;
		parameters.put("EXECUTION_TIME", "Execution time : "+  getDurationString( System.currentTimeMillis() -  executionStart)) ;
		parameters.put("NB_INSTANCES","Number of simulated Instances : "+ String.valueOf(loadProfile.getTotalInjectedInstances())) ;
		//parameters.put(JRParameter.REPORT_CLASS_LOADER, getClass().getClassLoader());

//		JasperDesign processDesign = JRXmlLoader.load(JasperSimReport.class.getResourceAsStream("/ProcessReport.jrxml"));
//		JasperDesign activityDesign = JRXmlLoader.load(JasperSimReport.class.getResourceAsStream("/ActivityReport.jrxml"));
//		JasperDesign resourceDesign = JRXmlLoader.load(JasperSimReport.class.getResourceAsStream("/ResourceReport.jrxml"));
//		JasperDesign numDataDesign = JRXmlLoader.load(JasperSimReport.class.getResourceAsStream("/NumericDataReport.jrxml"));
//		JasperDesign litDataDesign = JRXmlLoader.load(JasperSimReport.class.getResourceAsStream("/LiteralDataReport.jrxml"));
		

		
		JasperReport processReport = (JasperReport) JRLoader.loadObject(copyReportToWorkspaceForGeneration("ProcessReport.jasper"));
		//(JasperReport) JRLoader.loadObject(JasperSimReport.class.getResourceAsStream("/ProcessReport.jasper"));
		//JasperCompileManager.compileReport(processDesign);
		copyReportToWorkspaceForGeneration("ActivityReport.jasper");
		copyReportToWorkspaceForGeneration("ResourceReport.jasper");
		copyReportToWorkspaceForGeneration("NumericDataReport.jasper");
		copyReportToWorkspaceForGeneration("LiteralDataReport.jasper");
		
		//		JasperCompileManager.compileReportToFile(activityDesign,workspace+File.separatorChar+"ActivityReport.jasper");
//		JasperCompileManager.compileReportToFile(resourceDesign,workspace+File.separatorChar+"ResourceReport.jasper");
//		JasperCompileManager.compileReportToFile(numDataDesign,workspace+File.separatorChar+"NumericDataReport.jasper");
//		JasperCompileManager.compileReportToFile(litDataDesign,workspace+File.separatorChar+"LiteralDataReport.jasper");


		JasperPrint jasperPrint = JasperFillManager.fillReport(processReport, parameters, connection);

		String htmlfileName = workspace+File.separatorChar+processName+"_"+timeStamp+"_report.html";
		JasperExportManager.exportReportToHtmlFile(jasperPrint,htmlfileName );

		parameters.put("SHOW_LINK", "") ;
		jasperPrint = JasperFillManager.fillReport(processReport, parameters, connection);

		JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
		
		new File(workspace+File.separatorChar+"ProcessReport.jasper").delete();
		new File(workspace+File.separatorChar+"ActivityReport.jasper").delete();
		new File(workspace+File.separatorChar+"ResourceReport.jasper").delete();
		new File(workspace+File.separatorChar+"NumericDataReport.jasper").delete();
		new File(workspace+File.separatorChar+"LiteralDataReport.jasper").delete();

		jdbcStore.close() ;


		return htmlfileName;
	}

	protected String getDurationString(long duration) {


		int nbOfDays = (int) (duration/(long)((long)3600000*(long)24)) ; ;
		duration = duration - ((long)(nbOfDays*(long)3600000*(long)24)) ;
		int nbOfHours = (int) (duration / (long)3600000) ;
		duration = duration - (long)(nbOfHours*3600000L) ;
		int nbOfMinutes = (int) (duration / (long)60000) ;
		duration = duration - (long)(nbOfMinutes*60000L) ;
		int nbOfSecond = (int) (duration /(long) 1000) ;

		StringBuilder sb = new StringBuilder() ;
		if(nbOfDays > 0){
			sb.append(nbOfDays + " days ") ;
		}
		if(nbOfHours > 0){
			sb.append(nbOfHours + " hours ") ;
		}

		if(nbOfMinutes > 0){
			sb.append(nbOfMinutes + " minutes ") ;
		}

		if(nbOfSecond > 0){
			sb.append(nbOfSecond + " seconds ") ;
		}
		return sb.toString();
	}

	protected void fillResourceSheet(List<ResourceResultSet> resultSets) throws Exception {
		for(ResourceResultSet rSet : resultSets){
			jdbcStore.insertResource(rSet.getResource().getName(),
					rSet.getResource().getCostUnit(),
					rSet.getTotalUse(),
					rSet.getTotalMinUse(), 
					rSet.getTotalAvgUse(),
					rSet.getTotalMaxUse(), 
					rSet.getTotalCost(), 
					rSet.getTotalMinCost(), 
					rSet.getTotalAvgCost(), 
					rSet.getTotalMaxCost(), 
					rSet.getTotalConsumption(), 
					rSet.getTotalMinConsumption(), 
					rSet.getTotalAvgConsumption(), 
					rSet.getTotalMaxConsumption());

			jdbcStore.insertResourceInfo(rSet.getResource().getName(), 
					resourceConso.get(rSet.getResource().getName()).getInstances().size(), 
					rSet.getResource().getTargetQuantity(), 
					rSet.getResource().getCostUnit(), 
					rSet.getResource().getFixedCost(),
					rSet.getResource().getTimeCost(),
					rSet.getResource().getTimeUnit().name()) ;
		}
	}

	protected void fillLiteralsDataSheet(List<DataResultSet> resultSets) throws Exception {
		for(DataResultSet rSet : resultSets){
			if(rSet.getData() instanceof SimNumberData){
				//DO NOTHING
			}else{
				for(String literal : rSet.getLiteralsRepartition().keySet()){
					jdbcStore.insertLiteralData(rSet.getData().getName(), literal, rSet.getLiteralsRepartition().get(literal)) ;
				}
			}
		}
	}

	protected void fillNumberDataSheet(List<DataResultSet> resultSets) throws SQLException {
		for(DataResultSet rSet : resultSets){
			if(rSet.getData() instanceof SimNumberData){
				jdbcStore.insertNumberData(rSet.getData().getName(), rSet.getTotalMinValue(), rSet.getTotalAvgValue(), rSet.getTotalMaxValue()) ;
			}
		}
	}

	protected void fillActivitySheet(List<ActivityResultSet> resultSets) throws Exception {
		for(ActivityResultSet rSet : resultSets){
			jdbcStore.insertActivity(rSet.getActivity().getName(),
					getMillisecondsIntoHours(rSet.getTotalMinTime()), 
					getMillisecondsIntoHours(rSet.getTotalAvgTime()), 
					getMillisecondsIntoHours(rSet.getTotalMaxTime()),
					getMillisecondsIntoHours(rSet.getTotalMinWaitingTime()), 
					getMillisecondsIntoHours(rSet.getTotalAvgWaitingTime()), 
					getMillisecondsIntoHours(rSet.getTotalMaxWaitingTime()), 
					rSet.getNumberOfInstances(),
					rSet.getNbInstanceOverMax(),
					getMillisecondsIntoHours(rSet.getTotalTime()), 
					getMillisecondsIntoHours(rSet.getTotalWaitingTime())) ;
		}

	}

	protected void fillResourceTimeSheet(List<ResourceResultSet> resultSets) throws Exception {
		for(ResourceResultSet rSet : resultSets){
			Set<Long> times = rSet.getAvgUse().keySet() ;
			Set<Long> times2 = resourceConso.get(rSet.getResource().getName()).getMaximumWorkingInstance().keySet() ;
			if(times2.size() < times.size()){
				times = times2 ;
			}
			for(Long t : times){
				int q = rSet.getResource().getMaximumQuantity() ;
				if(q < 0){
					q = 0 ;
				}
				jdbcStore.insertResourceTime(rSet.getResource().getName(),
						t,
						resourceConso.get(rSet.getResource().getName()).getMinimumWorkingInstance().get(t),
						resourceConso.get(rSet.getResource().getName()).getAverageWorkingInstance().get(t),
						resourceConso.get(rSet.getResource().getName()).getMaximumWorkingInstance().get(t),
						q,
						rSet.getResource().getTargetQuantity(),
						rSet.getAvgUse().get(t),
						rSet.getAvgCost().get(t),
						getMillisecondsIntoHours(resourceConso.get(rSet.getResource().getName()).getProcessWaitingFor().get(t))) ;
			}

		}
	}

	protected void fillActivityTimeSheet(List<ActivityResultSet> resultSets) throws SQLException {
		for(ActivityResultSet rSet : resultSets){
			Set<Long> times = rSet.getAvgTime().keySet();
			for(Long t : times){
				jdbcStore.insertActivityTime(t,
						rSet.getActivity().getName(),
						getMillisecondsIntoHours(rSet.getActivity().getMaximumTime()),
						getMillisecondsIntoHours(rSet.getActivity().getEstimatedTime()),
						getMillisecondsIntoHours(rSet.getMinTime().get(t)),
						getMillisecondsIntoHours(rSet.getAvgTime().get(t)),
						getMillisecondsIntoHours(rSet.getMaxTime().get(t)),
						getMillisecondsIntoHours(rSet.getMinWaitingTime().get(t)),
						getMillisecondsIntoHours(rSet.getAvgWaitingTime().get(t)),
						getMillisecondsIntoHours(rSet.getMaxWaitingTime().get(t))) ;

				for(String resourceName : resourceConso.keySet()){
					RuntimeResource r = resourceConso.get(resourceName) ;

					if(r.getWaitingFor().get(rSet.getActivity().getName()) != null){
						float waitingTime ;
						if(r.getWaitingFor().get(rSet.getActivity().getName()).get(t) == null){
							waitingTime = 0 ;
						}else{
							waitingTime = getMillisecondsIntoHours(r.getWaitingFor().get(rSet.getActivity().getName()).get(t)) ;
						}
						
						jdbcStore.insertActivityResource(t,rSet.getActivity().getName(),resourceName,waitingTime) ;
					}
				}

			}
		}
	}

	@Override
	protected void fillProcessCostSheet(ProcessResultSet processResultSet)
	throws Exception {
		Set<String> units = new HashSet<String>() ;

		for(String u :  processResultSet.getAvgCost().keySet()){
			units.add(u) ;
		}

		for(String u :  processResultSet.getMinCost().keySet()){
			units.add(u) ;
		}

		for(String u :  processResultSet.getMaxCost().keySet()){
			units.add(u) ;
		}

		for(String unit : units){

			double min = 0 ;
			if(processResultSet.getMinCost().get(unit) != null){
				min = processResultSet.getMinCost().get(unit);
			}

			double avg = 0 ;
			if(processResultSet.getMinCost().get(unit) != null){
				avg = processResultSet.getAvgCost().get(unit);
			}

			double max = 0 ;
			if(processResultSet.getMinCost().get(unit) != null){
				max = processResultSet.getMaxCost().get(unit);
			}

			jdbcStore.insertProcessCost(unit,min,avg,max); 
		}


	}

	protected void fillDataReportSheet(List<DataResultSet> resultSets) {}


	protected void fillResourceReportSheet() throws Exception {}

	protected void fillActivityReportSheet(List<ActivityResultSet> resultSets) {}

	protected void fillProcessTimeSheet(ProcessResultSet processResultSet) throws SQLException {

		Set<Long> times = processResultSet.getAvgTime().keySet();
		for(Long t : times){
			jdbcStore.insertProcessTime(t,
					getMillisecondsIntoHours(processResultSet.getMinTime().get(t)),
					getMillisecondsIntoHours(processResultSet.getAvgTime().get(t)),
					getMillisecondsIntoHours(processResultSet.getMaxTime().get(t)),
					getMillisecondsIntoHours(processResultSet.getProcess().getMaximumTime()),
					getMillisecondsIntoHours(processResultSet.getMinWaitingTime().get(t)),
					getMillisecondsIntoHours(processResultSet.getAvgWaitingTime().get(t)),
					getMillisecondsIntoHours(processResultSet.getMaxWaitingTime().get(t))) ;
		}

	}

	protected void fillProcessSheet(ProcessResultSet processResultSet) throws Exception {
		jdbcStore.insertProcess(processResultSet.getProcess().getName(),
				getMillisecondsIntoHours(processResultSet.getTotalMinTime()),
				getMillisecondsIntoHours(processResultSet.getTotalAvgTime()),
				getMillisecondsIntoHours(processResultSet.getTotalMaxTime()),
				getMillisecondsIntoHours(processResultSet.getTotalMinWaitingTime()),
				getMillisecondsIntoHours(processResultSet.getTotalAvgWaitingTime()),
				getMillisecondsIntoHours(processResultSet.getTotalMaxWaitingTime()),
				processResultSet.getNumberOfInstances(),
				processResultSet.getNbInstanceOverMax(),
				getMillisecondsIntoHours(processResultSet.getTotalTime()),
				getMillisecondsIntoHours(processResultSet.getTotalWaitingTime())) ;
	}

	protected void fillLoadProfileSheet() throws Exception {
		int i = 1 ;
		for(InjectionPeriod p : loadProfile.getInjectionPeriods()){
			jdbcStore.insertLoadprofile("P"+i,p.getPeriod().getBegin(), p.getPeriod().getEnd(), p.getNumberOfInstance(),p.getRepartition().toString());
			i++ ;
		}
	}

	protected float getMillisecondsIntoHours(long nbMillisecond) {
		return (float) ( nbMillisecond/  (float)(3600000) );
	}

	protected void fillGeneralSheet() {}

	protected File copyReportToWorkspaceForGeneration(String reportName) {
		InputStream inputStream = null;
		OutputStream out = null;
		try {			
			File f = new File(workspace+File.separatorChar+reportName);
			inputStream = getReportAsStream(reportName);
			out = new FileOutputStream(f);
			byte buf[]=new byte[1024];
			int len;
			while((len=inputStream.read(buf))>0)
				out.write(buf,0,len);
			out.close();
			inputStream.close();
			return f;
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	protected InputStream getReportAsStream(String reportName) {
		return JasperSimReport.class.getResourceAsStream("/"+reportName);
	}
}
