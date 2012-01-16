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
package org.bonitasoft.simulation.reporting.jdbc;


/**
 * @author Romain Bioteau
 *
 */
public class SimulationTableStatements {

	public final static String CREATE_RESOURCE_INFO_TABLE_STATEMENT = "CREATE table RESOURCE_INFO ("+
							"NAME       		VARCHAR(256) NOT NULL PRIMARY KEY," +
							"QUANTITY		  	INT, "+
							"TARGET_QUANTITY  	INT,"+
							"COST_UNIT  		VARCHAR(128),"+
							"COST_PER_USE	  	DOUBLE,"+
							"COST_TIME		  	DOUBLE, "+
							"COST_TIME_UNIT  	VARCHAR(128))";
	
	public final static String CREATE_PROCESS_TABLE_STATEMENT = "CREATE table PROCESS ("+
							"NAME       		VARCHAR(128) NOT NULL PRIMARY KEY," +
							"MIN_EXEC_TIME  	FLOAT, "+
							"AVG_EXEC_TIME  	FLOAT,"+
							"MAX_EXEC_TIME  	FLOAT,"+
							"MIN_WAIT_TIME  	FLOAT,"+
							"AVG_WAIT_TIME  	FLOAT, "+
							"MAX_WAIT_TIME  	FLOAT,"+
							"TOTAL_INSTANCES  	INT,"+
							"INSTANCES_OVER_MAX INT,"+
							"TOTAL_EXEC_TIME	FLOAT,"+
							"TOTAL_WAIT_TIME    FLOAT)";
	
	public final static String CREATE_PROCESS_TIME_TABLE_STATEMENT = "CREATE table PROCESS_TIME ("+
							"TIME       		TIMESTAMP NOT NULL PRIMARY KEY," +
							"MIN_EXEC_TIME  	FLOAT, "+
							"AVG_EXEC_TIME  	FLOAT,"+
							"MAX_EXEC_TIME  	FLOAT,"+
							"MAX_TIME  			FLOAT, "+
							"MIN_WAIT_TIME  	FLOAT,"+
							"AVG_WAIT_TIME  	FLOAT, "+
							"MAX_WAIT_TIME  	FLOAT)";
	
	public final static String CREATE_PROCESS_COST_TABLE_STATEMENT = "CREATE table PROCESS_COST ("+
							"COST_UNIT       	VARCHAR(128) NOT NULL PRIMARY KEY," +
							"MIN_COST  			DOUBLE, "+
							"AVG_COST  			DOUBLE,"+
							"MAX_COST  			DOUBLE)";
	
	public final static String CREATE_ACTIVITY_TABLE_STATEMENT = "CREATE table ACTIVITY ("+
							"NAME       		VARCHAR(128) NOT NULL PRIMARY KEY," +
							"MIN_EXEC_TIME  	FLOAT, "+
							"AVG_EXEC_TIME  	FLOAT,"+
							"MAX_EXEC_TIME  	FLOAT,"+
							"MIN_WAIT_TIME  	FLOAT,"+
							"AVG_WAIT_TIME  	FLOAT, "+
							"MAX_WAIT_TIME  	FLOAT,"+
							"TOTAL_INSTANCES  	INT,"+
							"INSTANCES_OVER_MAX INT,"+
							"TOTAL_EXEC_TIME	FLOAT,"+
							"TOTAL_WAIT_TIME    FLOAT)";
	
	public final static String CREATE_ACTIVITY_TIME_TABLE_STATEMENT = "CREATE table ACTIVITY_TIME ("+
							"TIME       		TIMESTAMP NOT NULL, "+
							"NAME				VARCHAR(128) NOT NULL," +	
							"MAXIMUM_TIME	  	FLOAT, "+
							"ESTIMATED_TIME  	FLOAT, "+
							"MIN_EXEC_TIME  	FLOAT, "+
							"AVG_EXEC_TIME  	FLOAT,"+
							"MAX_EXEC_TIME  	FLOAT,"+
							"MIN_WAIT_TIME  	FLOAT,"+
							"AVG_WAIT_TIME  	FLOAT, "+
							"MAX_WAIT_TIME  	FLOAT,"+
							"PRIMARY KEY(TIME,NAME))";
	
	public final static String CREATE_ACTIVITY_RESOURCE_TIME_TABLE_STATEMENT = "CREATE table ACTIVITY_RESOURCE_TIME ("+
							"TIME       		TIMESTAMP NOT NULL, "+
							"ACTIVITY_NAME		VARCHAR(128) NOT NULL," +	
							"RESOURCE_NAME		VARCHAR(128) NOT NULL," +	
							"WAIT_TIME  		FLOAT,"+
							"PRIMARY KEY(TIME,ACTIVITY_NAME,RESOURCE_NAME))";
	
	public final static String CREATE_RESOURCE_TABLE_STATEMENT = "CREATE table RESOURCE ("+
							"NAME       	VARCHAR(128) NOT NULL PRIMARY KEY," +
							"COST_UNIT 		VARCHAR(128),"+
							"TOTAL_USE  	DOUBLE, "+
							"MIN_USE  		DOUBLE,"+
							"AVG_USE	  	DOUBLE,"+
							"MAX_USE  		DOUBLE,"+
							"TOTAL_COST  	DOUBLE, "+
							"MIN_COST	  	DOUBLE,"+
							"AVG_COST	  	DOUBLE,"+
							"MAX_COST	  	DOUBLE,"+
							"TOTAL_CONS		INT,"+
							"MIN_CONS		INT,"+
							"AVG_CONS		DOUBLE,"+
							"MAX_CONS    	INT)";
	
	public final static String CREATE_RESOURCE_TIME_TABLE_STATEMENT = "CREATE table RESOURCE_TIME ("+
							"NAME       	VARCHAR(128) NOT NULL," +
							"TIME  			TIMESTAMP, "+
							"MIN_CONS 	 	INT, "+
							"AVG_CONS  		DOUBLE,"+
							"MAX_CONS	  	INT,"+
							"MAX_RES  		INT,"+
							"TARGET_RES  	INT,"+
							"USE  			DOUBLE, "+
							"COST		  	DOUBLE,"+
							"PROC_WAIT_TIME	FLOAT,"+
							"PRIMARY KEY(TIME,NAME))";
	
	public final static String CREATE_NUMBER_DATA_TABLE_STATEMENT = "CREATE table NUMBER_DATA (" +
							"NAME       	VARCHAR(128) NOT NULL PRIMARY KEY," +
							"MIN_VALUE		DOUBLE, "+
							"AVG_VALUE 	 	DOUBLE, "+
							"MAX_VALUE 		DOUBLE)";
	
	public final static String CREATE_LITERAL_DATA_TABLE_STATEMENT = "CREATE table LITERAL_DATA (" +
							"NAME       	VARCHAR(128) NOT NULL," +
							"LITERAL		VARCHAR(128) NOT NULL, "+
							"REPARTITION 	DOUBLE, "+
							"PRIMARY KEY(NAME,LITERAL))";
	
	public final static String CREATE_LOADPROFILE_TABLE_STATEMENT = "CREATE table LOADPROFILE (" +
							"PERIOD_NAME     	VARCHAR(16) NOT NULL," +						
							"START_PERIOD     	TIMESTAMP NOT NULL," +
							"END_PERIOD			TIMESTAMP NOT NULL, "+
							"NB_INSTANCES 		INT,"+
							"REPARTION_TYPE 	VARCHAR(128),"+
							"PRIMARY KEY(START_PERIOD,END_PERIOD))";

	public static final String INSERT_RESOURCE_INFO_ROW_PREPARED_STATEMENT = "INSERT INTO RESOURCE_INFO VALUES(?,?,?,?,?,?,?)";
	
	public static final String INSERT_PROCESS_ROW_PREPARED_STATEMENT = "INSERT INTO PROCESS VALUES(?,?,?,?,?,?,?,?,?,?,?)";
	
	public static final String INSERT_PROCESS_TIME_ROW_PREPARED_STATEMENT = "INSERT INTO PROCESS_TIME VALUES(?,?,?,?,?,?,?,?)";
	
	public static final String INSERT_PROCESS_COST_ROW_PREPARED_STATEMENT = "INSERT INTO PROCESS_COST VALUES(?,?,?,?)";

	public static final String INSERT_ACTIVITY_ROW_PREPARED_STATEMENT = "INSERT INTO ACTIVITY VALUES(?,?,?,?,?,?,?,?,?,?,?)";
	
	public static final String INSERT_ACTIVITY_RESOURCE_ROW_PREPARED_STATEMENT = "INSERT INTO ACTIVITY_RESOURCE_TIME VALUES(?,?,?,?)";
	
	public static final String INSERT_ACTIVITY_TIME_ROW_PREPARED_STATEMENT = "INSERT INTO ACTIVITY_TIME VALUES(?,?,?,?,?,?,?,?,?,?)";
	
	public static final String INSERT_RESOURCE_ROW_PREPARED_STATEMENT = "INSERT INTO RESOURCE VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	public static final String INSERT_RESOURCE_TIME_ROW_PREPARED_STATEMENT = "INSERT INTO RESOURCE_TIME VALUES(?,?,?,?,?,?,?,?,?,?)";
	
	public static final String INSERT_NUMBER_DATA_ROW_PREPARED_STATEMENT = "INSERT INTO NUMBER_DATA VALUES(?,?,?,?)";
	
	public static final String INSERT_LITERAL_DATA_ROW_PREPARED_STATEMENT = "INSERT INTO LITERAL_DATA VALUES(?,?,?)";
	
	public static final String INSERT_LOADPROFILE_ROW_PREPARED_STATEMENT = "INSERT INTO LOADPROFILE VALUES(?,?,?,?,?)";
}
