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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.bonitasoft.simulation.model.Period;
import org.bonitasoft.simulation.model.RepartitionType;
import org.bonitasoft.simulation.model.TimeUnit;
import org.bonitasoft.simulation.model.calendar.SimCalendar;
import org.bonitasoft.simulation.model.calendar.SimCalendarPeriod;
import org.bonitasoft.simulation.model.calendar.SimCalendarTime;
import org.bonitasoft.simulation.model.loadprofile.InjectionPeriod;
import org.bonitasoft.simulation.model.loadprofile.LoadProfile;
import org.bonitasoft.simulation.model.process.JoinType;
import org.bonitasoft.simulation.model.process.NumericRange;
import org.bonitasoft.simulation.model.process.ResourceAssignement;
import org.bonitasoft.simulation.model.process.SimActivity;
import org.bonitasoft.simulation.model.process.SimBooleanData;
import org.bonitasoft.simulation.model.process.SimLiteral;
import org.bonitasoft.simulation.model.process.SimLiteralsData;
import org.bonitasoft.simulation.model.process.SimNumberData;
import org.bonitasoft.simulation.model.process.SimProcess;
import org.bonitasoft.simulation.model.process.SimTransition;
import org.bonitasoft.simulation.model.resource.Resource;

/**
 * @author Romain Bioteau
 *
 */
public class SimulationTestUtil {

	public static final int RD_QUANTITY =5;
	public static final int MANAGER = 3;

	public static SimProcess createProcessDefinitionHourScaled() throws Exception {
		SimProcess proc = new SimProcess("TestSimulationProcess");
		SimActivity start = new SimActivity("Start",proc.getName(),true,true);

		List<Resource> ressources = createResources() ;

		SimActivity a1 = new SimActivity("Activity1",proc.getName(),4*3600000,0,6*3600000,false,false) ;
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(1),2*3600000,1));
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(0),4*3600000,1));


		SimActivity a2 = new SimActivity("Activity2",proc.getName(),60*60000,0,2*3600000,false,false) ;
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(1),60*60000,1));
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(0),60*60000,1));

		SimTransition t1 = new SimTransition("t1",a1,false,0.3);
		SimTransition t2 = new SimTransition("t2",a2,false,0.7);

		start.addOutgoingTransition(t1);
		start.addOutgoingTransition(t2);

		proc.addStartElement(start);
		return proc ;

	}


	public static SimProcess createProcessDefinitionMinuteScaled() throws Exception {
		SimProcess proc = new SimProcess("TestSimulationProcess");
		SimActivity start = new SimActivity("Start",proc.getName(),true,true);

		List<Resource> ressources = createResources() ;

		SimActivity a1 = new SimActivity("Activity1",proc.getName(),30*60000,0,0,false,false) ;//30MIN
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(1),15*60000,1));
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(0),30*60000,1));


		SimActivity a2 = new SimActivity("Activity2",proc.getName(),15*60000,0,0,false,false) ;//15MIN
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(1),15*60000,1));
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(0),15*60000,1));

		SimTransition t1 = new SimTransition("t1",a1,false,0.5);
		SimTransition t2 = new SimTransition("t2",a2,false,0.5);

		start.addOutgoingTransition(t1);
		start.addOutgoingTransition(t2);

		proc.addStartElement(start);
		return proc ;

	}

	public static SimProcess createProcessDefinition() throws Exception {
		SimProcess proc = new SimProcess("TestSimulationProcess");
		SimActivity start = new SimActivity("Start",proc.getName(),true,true);

		List<Resource> ressources = createResources() ;

		SimActivity a1 = new SimActivity("Activity1",proc.getName(),4*3600000,0,0,false,false) ;
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(1),1*3600000,1));
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(0),4*3600000,1));


		SimActivity a2 = new SimActivity("Activity2",proc.getName(),60*60000,0,0,false,false) ;
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(1),30*60000,1));
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(0),60*60000,1));

		SimActivity a3 = new SimActivity("Activity3",proc.getName(),10*60000,0,0,false,false) ;
		a3.addResourceAssignement(new ResourceAssignement(ressources.get(1),10*60000,1));


		SimTransition t1 = new SimTransition("t1",a1,false,0.3);
		SimTransition t2 = new SimTransition("t2",a2,false,0.7);

		SimTransition t3 = new SimTransition("t3",a3,false,1.0);
		SimTransition t4 = new SimTransition("t4",a3,false,1.0);

		a2.addOutgoingTransition(t3);
		a1.addOutgoingTransition(t4);

		start.addOutgoingTransition(t1);
		start.addOutgoingTransition(t2);

		proc.addStartElement(start);
		return proc ;

	}



	public static SimProcess createProcessDefinitionBasedOnData() throws Exception {
		SimProcess proc = new SimProcess("TestSimulationProcessWithData");

		proc.addData(new SimBooleanData("approved", 0.5)); //$NON-NLS-1$

		NumericRange range = new NumericRange(800, 1200, 1.0, null) ;
		proc.addData(new SimNumberData("amount", Collections.singletonList(range))); //$NON-NLS-1$

		SimLiteral l1 = new SimLiteral("high", 0.5);
		SimLiteral l2 = new SimLiteral("low", 0.5);
		List<SimLiteral> list = new ArrayList<SimLiteral>();
		list.add(l1);
		list.add(l2);
		proc.addData(new SimLiteralsData("risk",list)); //$NON-NLS-1$

		SimActivity start = new SimActivity("Start",proc.getName(),true,true);

		List<Resource> ressources = createResources() ;

		SimActivity a1 = new SimActivity("Activity1",proc.getName(),4*3600000,0,0,false,false) ;
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(1),2*3600000,1));
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(0),4*3600000,1));


		SimActivity a2 = new SimActivity("Activity2",proc.getName(),60*60000,0,0,false,false) ;
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(1),60*60000,1));
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(0),60*60000,1));


		SimActivity a3 = new SimActivity("Activity3",proc.getName(),60*60000,0,0,false,false) ;
		a3.addResourceAssignement(new ResourceAssignement(ressources.get(1),60*60000,1));
		a3.addResourceAssignement(new ResourceAssignement(ressources.get(0),60*60000,1));

		SimActivity a4 = new SimActivity("Activity4",proc.getName(),60*60000,0,0,false,false) ;
		a4.addResourceAssignement(new ResourceAssignement(ressources.get(1),60*60000,1));
		a4.addResourceAssignement(new ResourceAssignement(ressources.get(0),60*60000,1));

		SimTransition a1Toa3 = new SimTransition("a1ToA3",a3,true,"risk == \"low\"");
		SimTransition a2Toa4 = new SimTransition("a2ToA4",a4,true,"approved");

		SimTransition t1 = new SimTransition("t1",a1,true,"amount > 1000");
		SimTransition t2 = new SimTransition("t2",a2,true,"amount <= 1000");

		a1.addOutgoingTransition(a1Toa3);
		a2.addOutgoingTransition(a2Toa4);

		start.addOutgoingTransition(t1);
		start.addOutgoingTransition(t2);

		proc.addStartElement(start);
		return proc ;

	}


	public static LoadProfile createLoadProfileWithMediumDensity() throws Exception {
		Calendar myInstance = GregorianCalendar.getInstance() ;
		List<InjectionPeriod> injections = new ArrayList<InjectionPeriod>();

		myInstance.set(2010, 6, 1, 0, 0);
		long p1Start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.MONTH, 50) ;
		long p1End = myInstance.getTimeInMillis() ;
		Period period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,5000,RepartitionType.CONSTANT));
		LoadProfile lp = new LoadProfile(createWorkingWeekCalendar(), injections);
		return lp ;
	}

	public static LoadProfile createLoadProfileWithHighDensity() throws Exception {
		Calendar myInstance = GregorianCalendar.getInstance() ;
		List<InjectionPeriod> injections = new ArrayList<InjectionPeriod>();

		myInstance.set(2010, 6, 1, 0, 0);
		long p1Start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.MONTH, 10) ;
		long p1End = myInstance.getTimeInMillis() ;
		Period period1 = new Period(p1Start, p1End) ;


		injections.add(new InjectionPeriod(period1,5000,RepartitionType.CONSTANT));
		LoadProfile lp = new LoadProfile(createWorkingWeekCalendar(), injections);
		return lp ;
	}

	public static LoadProfile createLoadProfileWithLowDensity() throws Exception {
		Calendar myInstance = GregorianCalendar.getInstance() ;
		List<InjectionPeriod> injections = new ArrayList<InjectionPeriod>();

		myInstance.set(2010, 6, 1, 0, 0);
		long p1Start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.MONTH, 30) ;
		long p1End = myInstance.getTimeInMillis() ;
		Period period1 = new Period(p1Start, p1End) ;


		injections.add(new InjectionPeriod(period1,5000,RepartitionType.CONSTANT));
		LoadProfile lp = new LoadProfile(createWorkingWeekCalendar(), injections);
		return lp ;
	}

	public static LoadProfile createLoadProfileWithVeryLowDensity() throws Exception {
		Calendar myInstance = GregorianCalendar.getInstance() ;
		List<InjectionPeriod> injections = new ArrayList<InjectionPeriod>();

		myInstance.set(2010, 6, 1, 0, 0);
		long p1Start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.DAY_OF_MONTH, 10) ;
		long p1End = myInstance.getTimeInMillis() ;
		Period period1 = new Period(p1Start, p1End) ;

		
		injections.add(new InjectionPeriod(period1,1,RepartitionType.CONSTANT));
		LoadProfile lp = new LoadProfile(createWorkingWeekCalendar(), injections);
		return lp ;
	}


	public static SimProcess createRealProcWithData() throws Exception{

		List<Resource> ressources = createRealResources(); 

		SimProcess proc = new SimProcess("RealProcessWithData",SimulationTestUtil.getHoursIntoMilliseconds(100));

		List<NumericRange> nr = new ArrayList<NumericRange>();
		nr.add(new NumericRange(500, 1500, 0.5, RepartitionType.CONSTANT));
		nr.add(new NumericRange(1501,2500, 0.5, RepartitionType.CONSTANT));
		proc.addData(new SimNumberData("amount", nr));

		List<SimLiteral> literals = new ArrayList<SimLiteral>();
		literals.add(new SimLiteral("high",0.6));
		literals.add(new SimLiteral("low",0.4));
		proc.addData(new SimLiteralsData("risk", literals)) ;

		SimActivity start = new SimActivity("Start",proc.getName(),false,true);
		proc.addStartElement(start);

		SimActivity a1 = new SimActivity("Activity1",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(4),SimulationTestUtil.getHoursIntoMilliseconds(6),SimulationTestUtil.getHoursIntoMilliseconds(10),false,false) ;
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(3),SimulationTestUtil.getHoursIntoMilliseconds(2),1));


		SimActivity a2 = new SimActivity("Activity2",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(2),0,0,false,false) ;
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(2),1));
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(2),1));


		SimTransition t1 = new SimTransition("t1",a1,true,"amount > 1500");
		SimTransition t2 = new SimTransition("t2",a2,true,"amount <= 1500");

		start.addOutgoingTransition(t1);
		start.addOutgoingTransition(t2);


		SimActivity a3 = new SimActivity("Activity3",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,false) ;

		a3.addData(new SimLiteralsData("risk", "if(amount < 800){return \"high\"}else{return \"low\"}"));
		a3.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		a3.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(1),1));

		SimActivity waitMessage1 = new SimActivity("Receive Order",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,true) ; //$NON-NLS-1$


		SimTransition t3 = new SimTransition("t3",a3,false,1.0);
		SimTransition t4 = new SimTransition("t4",waitMessage1,false,1.0);

		a2.addOutgoingTransition(t3);
		a1.addOutgoingTransition(t4);

		SimActivity gate1 = new SimActivity("Gate1",proc.getName(),0,0,0,false,false) ; //$NON-NLS-1$

		SimTransition t5 = new SimTransition("t5",gate1,false,1.0);
		SimTransition t6 = new SimTransition("t6",gate1,false,1.0);


		a3.addOutgoingTransition(t5) ;
		waitMessage1.addOutgoingTransition(t6) ;



		SimActivity activity5 = new SimActivity("Activity5",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,true,false) ; //$NON-NLS-1$
		activity5.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		activity5.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(1),1));

		SimTransition t15 = new SimTransition("t15",activity5,false,1.0);
		gate1.addOutgoingTransition(t15) ;

		SimActivity activity6 = new SimActivity("Activity6",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(5),0,0,false,false) ; //$NON-NLS-1$
		activity6.addResourceAssignement(new ResourceAssignement(ressources.get(2),SimulationTestUtil.getHoursIntoMilliseconds(5),1));
		activity6.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(3),1));




		//		SimTransition t7 = new SimTransition("t7",activity5,false,0.2);
		SimTransition t8 = new SimTransition("t8",activity6,false,1.0);

		//		activity5.addOutgoingTransition(t7);
		activity5.addOutgoingTransition(t8) ;

		SimActivity gate2 = new SimActivity("Gate2",JoinType.XOR,proc.getName(),false) ; //$NON-NLS-1$
		SimTransition t9 = new SimTransition("t9",gate2,false,1.0);
		activity6.addOutgoingTransition(t9) ; 

		SimActivity activity7 = new SimActivity("Activity7",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,false) ; //$NON-NLS-1$
		activity7.addResourceAssignement(new ResourceAssignement(ressources.get(3),SimulationTestUtil.getHoursIntoMilliseconds(1),1));

		SimActivity wait = new SimActivity("wait",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(2),0,0,false,false) ; //$NON-NLS-1$


		SimActivity activity8 = new SimActivity("Activity8",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(4),0,0,false,false) ; //$NON-NLS-1$
		activity8.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(4),3));

		SimTransition t10 = new SimTransition("t10",activity7,false,1.0);
		SimTransition t11 = new SimTransition("t11",wait,false,1.0);
		SimTransition tWait = new SimTransition("waitToA8",activity8,false,1.0);


		wait.addOutgoingTransition(tWait);

		gate2.addOutgoingTransition(t10) ;
		gate2.addOutgoingTransition(t11) ;

		SimActivity gate3 = new SimActivity("Gate3",JoinType.AND,proc.getName(),true) ; //$NON-NLS-1$


		SimTransition t8Gate3 = new SimTransition("t8Gate3",gate3,false,1.0);
		SimTransition t7Gate3 = new SimTransition("t7Gate3",gate3,false,1.0);
		activity8.addOutgoingTransition(t8Gate3);
		activity7.addOutgoingTransition(t7Gate3);


		SimActivity wait2 = new SimActivity("Receive Confirmation",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,false) ; //$NON-NLS-1$
		SimTransition t12 = new SimTransition("t12",wait2,false,1.0);
		gate3.addOutgoingTransition(t12) ;

		SimActivity activity9 = new SimActivity("Activity9",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,false) ; //$NON-NLS-1$
		activity9.addResourceAssignement(new ResourceAssignement(ressources.get(3),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		activity9.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(1),1));

		SimActivity activity10 = new SimActivity("Activity10",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(2),0,0,false,false) ; //$NON-NLS-1$
		activity10.addResourceAssignement(new ResourceAssignement(ressources.get(2),SimulationTestUtil.getHoursIntoMilliseconds(2),1));


		SimTransition t13 = new SimTransition("t13",activity9,false,1.0);
		SimTransition t14 = new SimTransition("t14",activity10,false,1.0);

		wait2.addOutgoingTransition(t13) ;
		wait2.addOutgoingTransition(t14) ;

		return proc ;
	}

	public static SimProcess createRealProc() throws Exception{


		List<Resource> ressources = createRealResources();

		SimProcess proc = new SimProcess("RealProcess",SimulationTestUtil.getHoursIntoMilliseconds(10));
		SimActivity start = new SimActivity("Start",proc.getName(),true,true);
		start.addResourceAssignement(new ResourceAssignement(ressources.get(1),0,1)) ;
		proc.addStartElement(start);

		SimActivity a1 = new SimActivity("Activity1",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(4),SimulationTestUtil.getHoursIntoMilliseconds(6),SimulationTestUtil.getHoursIntoMilliseconds(10),false,false) ;
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(3),SimulationTestUtil.getHoursIntoMilliseconds(2),1));


		SimActivity a2 = new SimActivity("Activity2",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(2),0,0,false,false) ;
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(2),1));
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(2),1));


		SimTransition t1 = new SimTransition("t1",a1,false,0.3);
		SimTransition t2 = new SimTransition("t2",a2,false,0.7);

		start.addOutgoingTransition(t1);
		start.addOutgoingTransition(t2);


		SimActivity a3 = new SimActivity("Activity3",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,false) ;
		a3.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		a3.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(1),1));

		SimActivity waitMessage1 = new SimActivity("Receive Order",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,true) ; //$NON-NLS-1$


		SimTransition t3 = new SimTransition("t3",a3,false,1.0);
		SimTransition t4 = new SimTransition("t4",waitMessage1,false,1.0);

		a2.addOutgoingTransition(t3);
		a1.addOutgoingTransition(t4);

		SimActivity gate1 = new SimActivity("Gate1",proc.getName(),0,0,0,false,false) ; //$NON-NLS-1$

		SimTransition t5 = new SimTransition("t5",gate1,false,1.0);
		SimTransition t6 = new SimTransition("t6",gate1,false,1.0);


		a3.addOutgoingTransition(t5) ;
		waitMessage1.addOutgoingTransition(t6) ;



		SimActivity activity5 = new SimActivity("Activity5",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,true,false) ; //$NON-NLS-1$
		activity5.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		activity5.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(1),1));

		SimTransition t15 = new SimTransition("t15",activity5,false,1.0);
		gate1.addOutgoingTransition(t15) ;

		SimActivity activity6 = new SimActivity("Activity6",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(5),0,0,false,false) ; //$NON-NLS-1$
		activity6.addResourceAssignement(new ResourceAssignement(ressources.get(2),SimulationTestUtil.getHoursIntoMilliseconds(5),1));
		activity6.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(3),1));




		//		SimTransition t7 = new SimTransition("t7",activity5,false,0.2);
		SimTransition t8 = new SimTransition("t8",activity6,false,1.0);

		//		activity5.addOutgoingTransition(t7);
		activity5.addOutgoingTransition(t8) ;

		SimActivity gate2 = new SimActivity("Gate2",JoinType.XOR,proc.getName(),false) ; //$NON-NLS-1$
		SimTransition t9 = new SimTransition("t9",gate2,false,1.0);
		activity6.addOutgoingTransition(t9) ; 

		SimActivity activity7 = new SimActivity("Activity7",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,false) ; //$NON-NLS-1$
		activity7.addResourceAssignement(new ResourceAssignement(ressources.get(3),SimulationTestUtil.getHoursIntoMilliseconds(1),1));

		SimActivity wait = new SimActivity("wait",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(2),0,0,false,false) ; //$NON-NLS-1$


		SimActivity activity8 = new SimActivity("Activity8",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(4),0,0,false,false) ; //$NON-NLS-1$
		activity8.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(4),3));

		SimTransition t10 = new SimTransition("t10",activity7,false,1.0);
		SimTransition t11 = new SimTransition("t11",wait,false,1.0);
		SimTransition tWait = new SimTransition("waitToA8",activity8,false,1.0);


		wait.addOutgoingTransition(tWait);

		gate2.addOutgoingTransition(t10) ;
		gate2.addOutgoingTransition(t11) ;

		SimActivity gate3 = new SimActivity("Gate3",JoinType.AND,proc.getName(),true) ; //$NON-NLS-1$


		SimTransition t8Gate3 = new SimTransition("t8Gate3",gate3,false,1.0);
		SimTransition t7Gate3 = new SimTransition("t7Gate3",gate3,false,1.0);
		activity8.addOutgoingTransition(t8Gate3);
		activity7.addOutgoingTransition(t7Gate3);


		SimActivity wait2 = new SimActivity("Receive Confirmation",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,false) ; //$NON-NLS-1$
		SimTransition t12 = new SimTransition("t12",wait2,false,1.0);
		gate3.addOutgoingTransition(t12) ;

		SimActivity activity9 = new SimActivity("Activity9",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,false) ; //$NON-NLS-1$
		activity9.addResourceAssignement(new ResourceAssignement(ressources.get(3),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		activity9.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(1),1));

		SimActivity activity10 = new SimActivity("Activity10",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(2),0,0,false,false) ; //$NON-NLS-1$
		activity10.addResourceAssignement(new ResourceAssignement(ressources.get(2),SimulationTestUtil.getHoursIntoMilliseconds(2),1));


		SimTransition t13 = new SimTransition("t13",activity9,false,1.0);
		SimTransition t14 = new SimTransition("t14",activity10,false,1.0);

		wait2.addOutgoingTransition(t13) ;
		wait2.addOutgoingTransition(t14) ;

		return proc ;
	}


	public static List<Resource> createRealResources() throws Exception {
		List<Resource> ressources = new ArrayList<Resource>();
		Resource employee = new Resource("R&D Team","Developer",40,40,SimulationTestUtil.createWorkingWeekCalendar(),"$",TimeUnit.HOUR,0,20);
		Resource saler = new Resource("Sales Team","Saler",30,20,SimulationTestUtil.createWorkingWeekCalendar(),"Û",TimeUnit.HOUR,0,25);
		Resource trucks = new Resource("Truck","Truck",30,20,SimulationTestUtil.createWorkingWeekCalendar(),"$",TimeUnit.DAY,0,40);
		Resource it = new Resource("IT","IT",30,20,SimulationTestUtil.createWorkingWeekCalendar(),"$",TimeUnit.HOUR,0,15);

		ressources.add(employee);
		ressources.add(saler);
		ressources.add(trucks);
		ressources.add(it) ;
		return ressources ;
	}


	public static List<Resource> createResources() throws Exception {
		List<Resource> ressources = new ArrayList<Resource>();
		Resource employee = new Resource("R&D Team","Developer",RD_QUANTITY,RD_QUANTITY,createWorkingWeekCalendar(),"$",TimeUnit.HOUR,0,20);
		Resource managers = new Resource("Manager Team","Manager",MANAGER,MANAGER,createWorkingWeekCalendar(),"$",TimeUnit.HOUR,0,40);
		ressources.add(employee);
		ressources.add(managers);
		return ressources ;
	}

	public static SimCalendar createWorkingWeekCalendar() throws Exception {
		SimCalendar calendar = new SimCalendar();

		Set<SimCalendarPeriod> workingDay = new HashSet<SimCalendarPeriod>() ; 
		SimCalendarPeriod morning = new SimCalendarPeriod(new SimCalendarTime(8,0),new SimCalendarTime(12,0));
		SimCalendarPeriod afternoon = new SimCalendarPeriod(new SimCalendarTime(13,30),new SimCalendarTime(19,30));
		workingDay.add(morning) ;
		workingDay.add(afternoon) ;

		calendar.addSimCalendarDay(Calendar.MONDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.TUESDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.WEDNESDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.THURSDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.FRIDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.SATURDAY, new TreeSet<SimCalendarPeriod>());
		calendar.addSimCalendarDay(Calendar.SUNDAY, new TreeSet<SimCalendarPeriod>());

		return calendar;
	}
	
	public static SimCalendar createWorkingAllWeekCalendar() throws Exception {
		SimCalendar calendar = new SimCalendar();

		Set<SimCalendarPeriod> workingDay = new HashSet<SimCalendarPeriod>() ; 
		SimCalendarPeriod morning = new SimCalendarPeriod(new SimCalendarTime(0,0),new SimCalendarTime(0,0));
		workingDay.add(morning) ;


		calendar.addSimCalendarDay(Calendar.MONDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.TUESDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.WEDNESDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.THURSDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.FRIDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.SATURDAY,workingDay);
		calendar.addSimCalendarDay(Calendar.SUNDAY,workingDay);

		return calendar;
	}

	public static LoadProfile createLoadProfileWithVeryHighDensity() throws Exception {
		Calendar myInstance = GregorianCalendar.getInstance() ;
		List<InjectionPeriod> injections = new ArrayList<InjectionPeriod>();

		myInstance.set(2010, 6, 1, 0, 0);
		long p1Start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.MONTH, 10) ;
		long p1End = myInstance.getTimeInMillis() ;
		Period period1 = new Period(p1Start, p1End) ;


		injections.add(new InjectionPeriod(period1,5000,RepartitionType.CONSTANT));
		LoadProfile lp = new LoadProfile(createWorkingWeekCalendar(), injections);
		return lp ;
	}


	public static long getHoursIntoMilliseconds(int nbHours) {
		return ((long)nbHours) * 3600 *1000 ;
	}


	public static SimProcess createCycleProc() throws Exception {
		List<Resource> ressources = createRealResources();

		SimProcess proc = new SimProcess("CycleProc");
		SimActivity start = new SimActivity("Start",proc.getName(),false,true);
		proc.addStartElement(start);

		SimActivity a1 = new SimActivity("Activity1",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(4),SimulationTestUtil.getHoursIntoMilliseconds(6),SimulationTestUtil.getHoursIntoMilliseconds(10),false,false) ;
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(3),SimulationTestUtil.getHoursIntoMilliseconds(2),1));
		
		SimTransition t1 = new SimTransition("t1",a1,false,1.0);
		start.addOutgoingTransition(t1);
		
		SimActivity a2 = new SimActivity("Activity2",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(2),0,0,false,false) ;
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(2),1));
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(2),1));


		SimTransition t2 = new SimTransition("t2",a2,false,1.0);
		a1.addOutgoingTransition(t2) ;
	
		
		SimActivity a3 = new SimActivity("Activity3",JoinType.AND,proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,false) ;
		a3.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		a3.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(1),1));

	
		SimTransition t3 = new SimTransition("t3",a3,false,1.0);
		SimTransition t6 = new SimTransition("t6",a3,false,1.0);
		a2.addOutgoingTransition(t3);
		a1.addOutgoingTransition(t6);

		SimActivity activity4 = new SimActivity("Activity4",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,true,true) ; //$NON-NLS-1$
		activity4.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		activity4.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(1),1));

		SimTransition t5 = new SimTransition("t5",activity4,false,1.0);
		a3.addOutgoingTransition(t5) ;


		SimActivity end = new SimActivity("End",proc.getName(),0,0,0,false,false) ; //$NON-NLS-1$

		SimTransition t8 = new SimTransition("t8",end,false,0.2);
		SimTransition t9 = new SimTransition("t9",a1,false,0.8);

		activity4.addOutgoingTransition(t9);
		activity4.addOutgoingTransition(t8) ;


		return proc ;
	}


	public static SimProcess createXorProc() throws Exception {
		List<Resource> ressources = createRealResources();

		SimProcess proc = new SimProcess("CycleProc");
		SimActivity start = new SimActivity("Start",proc.getName(),false,true);
		proc.addStartElement(start);

		SimActivity a1 = new SimActivity("Activity1",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(4),SimulationTestUtil.getHoursIntoMilliseconds(6),SimulationTestUtil.getHoursIntoMilliseconds(10),false,false) ;
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(3),SimulationTestUtil.getHoursIntoMilliseconds(2),1));
		
		SimTransition t1 = new SimTransition("t1",a1,false,1.0);
		
		
		SimActivity a2 = new SimActivity("Activity2",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(2),0,0,false,false) ;
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(24),1));
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(24),1));

		SimTransition t2 = new SimTransition("t1",a2,false,1.0);
		start.addOutgoingTransition(t1);
		start.addOutgoingTransition(t2);

		SimTransition t3 = new SimTransition("t3",a1,false,1.0);
		a2.addOutgoingTransition(t3);


		SimActivity end = new SimActivity("End",proc.getName(),0,0,0,false,false) ; //$NON-NLS-1$
		SimTransition t8 = new SimTransition("t8",end,false,1);
		
		a1.addOutgoingTransition(t8);

		return proc ;
	}

	public static SimProcess createANDProc() throws Exception {


		SimProcess proc = new SimProcess("ANDProc");
	
		SimActivity start = new SimActivity("Start",proc.getName(),false,true);
		proc.addStartElement(start);

		SimActivity a1 = new SimActivity("Activity1",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(4),SimulationTestUtil.getHoursIntoMilliseconds(6),SimulationTestUtil.getHoursIntoMilliseconds(10),false,false) ;

		
		SimTransition t1 = new SimTransition("t1",a1,false,1.0);
		
		SimActivity a2 = new SimActivity("Activity2",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(2),0,0,false,false) ;

		SimTransition t2 = new SimTransition("t2",a2,false,1.0);
		start.addOutgoingTransition(t1);
		start.addOutgoingTransition(t2);

		SimActivity join = new SimActivity("Join",JoinType.AND,proc.getName(),0,0,0,false,false) ; //$NON-NLS-1$
		SimTransition t3 = new SimTransition("t3",join,false,1.0);
		a2.addOutgoingTransition(t3);

		SimTransition t4 = new SimTransition("t4",join,false,1.0);
		a1.addOutgoingTransition(t4);

		
		
		SimActivity end = new SimActivity("End",proc.getName(),0,0,0,false,false) ; //$NON-NLS-1$
		SimTransition t8 = new SimTransition("t8",end,false,1);
		
		join.addOutgoingTransition(t8);

		return proc ;
	}


	public static List<Resource> createRealResourcesWithUnlimitedRD() throws Exception {
		List<Resource> ressources = new ArrayList<Resource>();
		Resource employee = new Resource("R&D Team","Developer",-1,40,SimulationTestUtil.createWorkingWeekCalendar(),"$",TimeUnit.HOUR,0,20);
		Resource saler = new Resource("Sales Team","Saler",30,20,SimulationTestUtil.createWorkingWeekCalendar(),"Û",TimeUnit.HOUR,0,25);
		Resource trucks = new Resource("Truck","Truck",30,20,SimulationTestUtil.createWorkingWeekCalendar(),"$",TimeUnit.DAY,0,40);
		Resource it = new Resource("IT","IT",30,20,SimulationTestUtil.createWorkingWeekCalendar(),"$",TimeUnit.HOUR,0,15);

		ressources.add(employee);
		ressources.add(saler);
		ressources.add(trucks);
		ressources.add(it) ;
		return ressources ;
	}


	public static SimProcess createRealProcWithUnlimitedRD() throws Exception {
		List<Resource> ressources = createRealResourcesWithUnlimitedRD() ;

		SimProcess proc = new SimProcess("RealProcess",SimulationTestUtil.getHoursIntoMilliseconds(10));
		SimActivity start = new SimActivity("Start",proc.getName(),true,true);
		start.addResourceAssignement(new ResourceAssignement(ressources.get(1),0,1)) ;
		proc.addStartElement(start);

		SimActivity a1 = new SimActivity("Activity1",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(4),SimulationTestUtil.getHoursIntoMilliseconds(6),SimulationTestUtil.getHoursIntoMilliseconds(10),false,false) ;
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		a1.addResourceAssignement(new ResourceAssignement(ressources.get(3),SimulationTestUtil.getHoursIntoMilliseconds(2),1));


		SimActivity a2 = new SimActivity("Activity2",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(2),0,0,false,false) ;
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(2),1));
		a2.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(2),1));


		SimTransition t1 = new SimTransition("t1",a1,false,0.3);
		SimTransition t2 = new SimTransition("t2",a2,false,0.7);

		start.addOutgoingTransition(t1);
		start.addOutgoingTransition(t2);


		SimActivity a3 = new SimActivity("Activity3",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,false) ;
		a3.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		a3.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(1),1));

		SimActivity waitMessage1 = new SimActivity("Receive Order",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,true) ; //$NON-NLS-1$


		SimTransition t3 = new SimTransition("t3",a3,false,1.0);
		SimTransition t4 = new SimTransition("t4",waitMessage1,false,1.0);

		a2.addOutgoingTransition(t3);
		a1.addOutgoingTransition(t4);

		SimActivity gate1 = new SimActivity("Gate1",proc.getName(),0,0,0,false,false) ; //$NON-NLS-1$

		SimTransition t5 = new SimTransition("t5",gate1,false,1.0);
		SimTransition t6 = new SimTransition("t6",gate1,false,1.0);


		a3.addOutgoingTransition(t5) ;
		waitMessage1.addOutgoingTransition(t6) ;



		SimActivity activity5 = new SimActivity("Activity5",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,true,false) ; //$NON-NLS-1$
		activity5.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		activity5.addResourceAssignement(new ResourceAssignement(ressources.get(1),SimulationTestUtil.getHoursIntoMilliseconds(1),1));

		SimTransition t15 = new SimTransition("t15",activity5,false,1.0);
		gate1.addOutgoingTransition(t15) ;

		SimActivity activity6 = new SimActivity("Activity6",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(5),0,0,false,false) ; //$NON-NLS-1$
		activity6.addResourceAssignement(new ResourceAssignement(ressources.get(2),SimulationTestUtil.getHoursIntoMilliseconds(5),1));
		activity6.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(3),1));




		//		SimTransition t7 = new SimTransition("t7",activity5,false,0.2);
		SimTransition t8 = new SimTransition("t8",activity6,false,1.0);

		//		activity5.addOutgoingTransition(t7);
		activity5.addOutgoingTransition(t8) ;

		SimActivity gate2 = new SimActivity("Gate2",JoinType.XOR,proc.getName(),false) ; //$NON-NLS-1$
		SimTransition t9 = new SimTransition("t9",gate2,false,1.0);
		activity6.addOutgoingTransition(t9) ; 

		SimActivity activity7 = new SimActivity("Activity7",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,false) ; //$NON-NLS-1$
		activity7.addResourceAssignement(new ResourceAssignement(ressources.get(3),SimulationTestUtil.getHoursIntoMilliseconds(1),1));

		SimActivity wait = new SimActivity("wait",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(2),0,0,false,false) ; //$NON-NLS-1$


		SimActivity activity8 = new SimActivity("Activity8",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(4),0,0,false,false) ; //$NON-NLS-1$
		activity8.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(4),3));

		SimTransition t10 = new SimTransition("t10",activity7,false,1.0);
		SimTransition t11 = new SimTransition("t11",wait,false,1.0);
		SimTransition tWait = new SimTransition("waitToA8",activity8,false,1.0);


		wait.addOutgoingTransition(tWait);

		gate2.addOutgoingTransition(t10) ;
		gate2.addOutgoingTransition(t11) ;

		SimActivity gate3 = new SimActivity("Gate3",JoinType.AND,proc.getName(),true) ; //$NON-NLS-1$


		SimTransition t8Gate3 = new SimTransition("t8Gate3",gate3,false,1.0);
		SimTransition t7Gate3 = new SimTransition("t7Gate3",gate3,false,1.0);
		activity8.addOutgoingTransition(t8Gate3);
		activity7.addOutgoingTransition(t7Gate3);


		SimActivity wait2 = new SimActivity("Receive Confirmation",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,false) ; //$NON-NLS-1$
		SimTransition t12 = new SimTransition("t12",wait2,false,1.0);
		gate3.addOutgoingTransition(t12) ;

		SimActivity activity9 = new SimActivity("Activity9",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(1),0,0,false,false) ; //$NON-NLS-1$
		activity9.addResourceAssignement(new ResourceAssignement(ressources.get(3),SimulationTestUtil.getHoursIntoMilliseconds(1),1));
		activity9.addResourceAssignement(new ResourceAssignement(ressources.get(0),SimulationTestUtil.getHoursIntoMilliseconds(1),1));

		SimActivity activity10 = new SimActivity("Activity10",proc.getName(),SimulationTestUtil.getHoursIntoMilliseconds(2),0,0,false,false) ; //$NON-NLS-1$
		activity10.addResourceAssignement(new ResourceAssignement(ressources.get(2),SimulationTestUtil.getHoursIntoMilliseconds(2),1));


		SimTransition t13 = new SimTransition("t13",activity9,false,1.0);
		SimTransition t14 = new SimTransition("t14",activity10,false,1.0);

		wait2.addOutgoingTransition(t13) ;
		wait2.addOutgoingTransition(t14) ;

		return proc ;
	}

}
