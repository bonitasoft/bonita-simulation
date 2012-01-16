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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * @author Romain Bioteau
 *
 */
public class DerbyJDBCStore {

	private Connection connection ;

	private String dbName;
	private String dbUrl;
	private Statement stmt = null ;
	private PreparedStatement preparedStmt = null ;

	public DerbyJDBCStore(String workspace) throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		File dbFile = File.createTempFile("tmp", "db", new File(workspace)) ;
		dbName = dbFile.getAbsolutePath() ;
		if(!dbFile.delete() ){
			dbFile.deleteOnExit() ;
		}
		this.dbUrl = "jdbc:derby:" + dbName ;
		this.connection = DriverManager.getConnection(dbUrl +";create=true", null);
		this.connection.setAutoCommit(false);

		initializeDB();
	}

	public void initializeDB() throws SQLException{
		try{
			execute("DROP TABLE PROCESS");
			execute("DROP TABLE PROCESS_TIME");
			execute("DROP TABLE PROCESS_COST");
			execute("DROP TABLE ACTIVITY");
			execute("DROP TABLE ACTIVITY_TIME");
			execute("DROP TABLE ACTIVITY_RESOURCE_TIME");
			execute("DROP TABLE RESOURCE");
			execute("DROP TABLE RESOURCE_TIME");
			execute("DROP TABLE RESOURCE_INFO");
			execute("DROP TABLE NUMBER_DATA");
			execute("DROP TABLE LITERAL_DATA");
			execute("DROP TABLE LOADPROFILE");
		}catch(SQLException e){

		}

		execute(SimulationTableStatements.CREATE_PROCESS_TABLE_STATEMENT) ;
		execute(SimulationTableStatements.CREATE_PROCESS_TIME_TABLE_STATEMENT) ;
		execute(SimulationTableStatements.CREATE_PROCESS_COST_TABLE_STATEMENT) ;
		execute(SimulationTableStatements.CREATE_ACTIVITY_TABLE_STATEMENT) ;
		execute(SimulationTableStatements.CREATE_ACTIVITY_TIME_TABLE_STATEMENT) ;
		execute(SimulationTableStatements.CREATE_ACTIVITY_RESOURCE_TIME_TABLE_STATEMENT) ;
		execute(SimulationTableStatements.CREATE_RESOURCE_TABLE_STATEMENT) ;
		execute(SimulationTableStatements.CREATE_RESOURCE_TIME_TABLE_STATEMENT) ;
		execute(SimulationTableStatements.CREATE_RESOURCE_INFO_TABLE_STATEMENT) ;
		execute(SimulationTableStatements.CREATE_LOADPROFILE_TABLE_STATEMENT) ;
		execute(SimulationTableStatements.CREATE_NUMBER_DATA_TABLE_STATEMENT) ;
		execute(SimulationTableStatements.CREATE_LITERAL_DATA_TABLE_STATEMENT) ;


	}

	public int insertProcess(String name,float minExecutionTime,float avgExecutionTime,float maxExecutionTime,float minWaitingTime, float avgWaitingTime, float maxWaitingTime,int nbInstances,int instancesOverMax, float totalExecutionTime,float totalWaitingTime) throws SQLException{
		preparedStmt = connection.prepareStatement(SimulationTableStatements.INSERT_PROCESS_ROW_PREPARED_STATEMENT) ;
		preparedStmt.setString(1, name) ;
		preparedStmt.setFloat(2, minExecutionTime) ;
		preparedStmt.setFloat(3, avgExecutionTime) ;
		preparedStmt.setFloat(4, maxExecutionTime) ;
		preparedStmt.setFloat(5, minWaitingTime) ;
		preparedStmt.setFloat(6, avgWaitingTime) ;
		preparedStmt.setFloat(7, maxWaitingTime) ;
		preparedStmt.setInt(8, nbInstances) ;
		preparedStmt.setInt(9, instancesOverMax) ;
		preparedStmt.setFloat(10, totalExecutionTime) ;
		preparedStmt.setFloat(11, totalWaitingTime) ;
		int r = preparedStmt.executeUpdate() ;
		preparedStmt.close() ;
		return r ;
	}

	public int insertProcessTime(long time,float minExecutionTime,float avgExecutionTime,float maxExecutionTime,float maxTime, float minWaitingTime, float avgWaitingTime, float maxWaitingTime) throws SQLException{
		preparedStmt = connection.prepareStatement(SimulationTableStatements.INSERT_PROCESS_TIME_ROW_PREPARED_STATEMENT) ;
		preparedStmt.setTimestamp(1, new Timestamp(time)) ;
		preparedStmt.setFloat(2, minExecutionTime) ;
		preparedStmt.setFloat(3, avgExecutionTime) ;
		preparedStmt.setFloat(4, maxExecutionTime) ;
		preparedStmt.setFloat(5, maxTime) ;
		preparedStmt.setFloat(6, minWaitingTime) ;
		preparedStmt.setFloat(7, avgWaitingTime) ;
		preparedStmt.setFloat(8, maxWaitingTime) ;
		int r = preparedStmt.executeUpdate() ;
		preparedStmt.close();
		return r ;
	}
	
	public int insertProcessCost(String unit, double minCost,double avgCost,double maxCost) throws SQLException{
		preparedStmt = connection.prepareStatement(SimulationTableStatements.INSERT_PROCESS_COST_ROW_PREPARED_STATEMENT) ;
		preparedStmt.setString(1, unit) ;
		preparedStmt.setDouble(2, minCost) ;
		preparedStmt.setDouble(3, avgCost) ;
		preparedStmt.setDouble(4, maxCost) ;
		int r = preparedStmt.executeUpdate() ;
		preparedStmt.close();
		return r ;
	}

	public int insertActivity(String name,float minExecutionTime,float avgExecutionTime,float maxExecutionTime,float minWaitingTime, float avgWaitingTime, float maxWaitingTime,int nbInstances,int instancesOverMax,float totalExecutionTime,float totalWaitingTime) throws SQLException{
		preparedStmt = connection.prepareStatement(SimulationTableStatements.INSERT_ACTIVITY_ROW_PREPARED_STATEMENT) ;
		preparedStmt.setString(1, name) ;
		preparedStmt.setFloat(2, minExecutionTime) ;
		preparedStmt.setFloat(3, avgExecutionTime) ;
		preparedStmt.setFloat(4, maxExecutionTime) ;
		preparedStmt.setFloat(5, minWaitingTime) ;
		preparedStmt.setFloat(6, avgWaitingTime) ;
		preparedStmt.setFloat(7, maxWaitingTime) ;
		preparedStmt.setInt(8, nbInstances) ;
		preparedStmt.setInt(9, instancesOverMax) ;
		preparedStmt.setFloat(10, totalExecutionTime) ;
		preparedStmt.setFloat(11, totalWaitingTime) ;
		int r = preparedStmt.executeUpdate() ;
		preparedStmt.close() ;
		return r ;
	}
	
	public int insertActivityResource(long time ,String name, String resourceName, float waitingTime) throws SQLException{
		preparedStmt = connection.prepareStatement(SimulationTableStatements.INSERT_ACTIVITY_RESOURCE_ROW_PREPARED_STATEMENT) ;
		preparedStmt.setTimestamp(1, new Timestamp(time)) ;
		preparedStmt.setString(2, name) ;
		preparedStmt.setString(3, resourceName) ;
		preparedStmt.setFloat(4, waitingTime) ;
		int r = preparedStmt.executeUpdate() ;
		preparedStmt.close() ;
		return r ;
	}

	public int insertActivityTime(long time,String name,float maximumTime,float estimatedTime,float minExecutionTime,float avgExecutionTime,float maxExecutionTime,float minWaitingTime, float avgWaitingTime, float maxWaitingTime) throws SQLException{
		preparedStmt = connection.prepareStatement(SimulationTableStatements.INSERT_ACTIVITY_TIME_ROW_PREPARED_STATEMENT) ;
		preparedStmt.setTimestamp(1, new Timestamp(time)) ;
		preparedStmt.setString(2, name) ;
		preparedStmt.setFloat(3, maximumTime) ;
		preparedStmt.setFloat(4, estimatedTime) ;
		preparedStmt.setFloat(5, minExecutionTime) ;
		preparedStmt.setFloat(6, avgExecutionTime) ;
		preparedStmt.setFloat(7, maxExecutionTime) ;
		preparedStmt.setFloat(8, minWaitingTime) ;
		preparedStmt.setFloat(9, avgWaitingTime) ;
		preparedStmt.setFloat(10, maxWaitingTime) ;
		int r = preparedStmt.executeUpdate() ;
		preparedStmt.close() ;
		return r ;
	}

	public int insertResource(String name,String costUnit,double totolUse,double minUse,double avgUse,double maxUse, double totalCost, double minCost,double avgCost,double maxCost,int totalCons,int minCons,double avgCons,int maxCons ) throws SQLException{
		preparedStmt = connection.prepareStatement(SimulationTableStatements.INSERT_RESOURCE_ROW_PREPARED_STATEMENT) ;
		preparedStmt.setString(1, name) ;
		preparedStmt.setString(2, costUnit) ;
		preparedStmt.setDouble(3, totolUse);
		preparedStmt.setDouble(4, minUse) ;
		preparedStmt.setDouble(5, avgUse) ;
		preparedStmt.setDouble(6, maxUse) ;
		preparedStmt.setDouble(7, totalCost) ;
		preparedStmt.setDouble(8, minCost) ;
		preparedStmt.setDouble(9, avgCost) ;
		preparedStmt.setDouble(10, maxCost) ;
		preparedStmt.setInt(11, totalCons) ;
		preparedStmt.setInt(12, minCons) ;
		preparedStmt.setDouble(13, avgCons) ;
		preparedStmt.setInt(14, maxCons) ;
		int r = preparedStmt.executeUpdate() ;
		preparedStmt.close() ;
		return r ;
	}

	public int insertResourceTime(String name,long time,int minCons,double avgCons,int maxCons,int maxResource,int targetResource,double use,double cost,float waitProcTime ) throws SQLException{
		preparedStmt = connection.prepareStatement(SimulationTableStatements.INSERT_RESOURCE_TIME_ROW_PREPARED_STATEMENT) ;
		preparedStmt.setString(1, name) ;
		preparedStmt.setTimestamp(2, new Timestamp(time));
		preparedStmt.setInt(3, minCons);
		preparedStmt.setDouble(4, avgCons) ;
		preparedStmt.setInt(5, maxCons) ;
		preparedStmt.setInt(6, maxResource) ;
		preparedStmt.setInt(7, targetResource) ;
		preparedStmt.setDouble(8, use) ;
		preparedStmt.setDouble(9, cost) ;
		preparedStmt.setFloat(10, waitProcTime) ;
		int r = preparedStmt.executeUpdate() ;
		preparedStmt.close() ;
		return r ;
	}

	public int insertResourceInfo(String name,int quantiy,int targetQuantity,String costUnit,double costPerUse, double timeCost, String timeCostUnit) throws SQLException{
		preparedStmt = connection.prepareStatement(SimulationTableStatements.INSERT_RESOURCE_INFO_ROW_PREPARED_STATEMENT) ;
		preparedStmt.setString(1, name) ;
		preparedStmt.setInt(2, quantiy) ;
		preparedStmt.setInt(3, targetQuantity) ;
		preparedStmt.setString(4, costUnit) ;
		preparedStmt.setDouble(5, costPerUse) ;
		preparedStmt.setDouble(6, timeCost) ;
		preparedStmt.setString(7, timeCostUnit) ;
		int r = preparedStmt.executeUpdate() ;
		preparedStmt.close() ;
		return r ;
	}
	
	public int insertLoadprofile(String periodName, long start,long end, int nbInstances,String repartitionType) throws SQLException{
		preparedStmt =connection.prepareStatement(SimulationTableStatements.INSERT_LOADPROFILE_ROW_PREPARED_STATEMENT); 
		preparedStmt.setString(1, periodName) ;
		preparedStmt.setTimestamp(2, new Timestamp(start)) ;
		preparedStmt.setTimestamp(3, new Timestamp(end));
		preparedStmt.setInt(4, nbInstances);
		preparedStmt.setString(5, repartitionType) ;
		int r = preparedStmt.executeUpdate() ;
		preparedStmt.close() ;
		return r ;
	}

	public int insertNumberData(String name,double min,double avg,double max) throws SQLException{
		preparedStmt = connection.prepareStatement(SimulationTableStatements.INSERT_NUMBER_DATA_ROW_PREPARED_STATEMENT) ;
		preparedStmt.setString(1, name) ;
		preparedStmt.setDouble(2,min);
		preparedStmt.setDouble(3,avg);
		preparedStmt.setDouble(4,max);
		int r = preparedStmt.executeUpdate() ;
		preparedStmt.close() ;
		return r ;
	}

	public int insertLiteralData(String name,String literal,double repartition) throws SQLException{
		preparedStmt = connection.prepareStatement(SimulationTableStatements.INSERT_LITERAL_DATA_ROW_PREPARED_STATEMENT) ;
		preparedStmt.setString(1, name) ;
		preparedStmt.setString(2,literal);
		preparedStmt.setDouble(3,repartition);
		int r = preparedStmt.executeUpdate() ;
		preparedStmt.close() ;
		return r ;
	}

	public boolean execute(String sqlStatement) throws SQLException{
		stmt = connection.createStatement() ;
		boolean r = stmt.execute(sqlStatement) ; 
		stmt.close() ;
		return r ;

	}

	public ResultSet executeQuery(String sqlStatement) throws SQLException{
		stmt= connection.createStatement() ;
		stmt.execute(sqlStatement) ;
		return stmt.getResultSet()  ;
	}

	private void deleteChildren(File f) {
		for(File child : f.listFiles()){
			if(child.listFiles() != null && child.listFiles().length > 0){
				deleteChildren(child);
			}
			child.delete();
			child.deleteOnExit() ;
		}
		f.delete();
		f.deleteOnExit() ;
	}
	public void close() throws SQLException {
		if(connection != null && !connection.isClosed())
			connection.commit();

		try{
			DriverManager.getConnection("jdbc:derby:;shutdown=true");
		}catch (SQLException e) {
			
		}
		if(connection != null && !connection.isClosed()){
			connection.close() ;
		}
		connection = null ;
		File dbFile = new File(dbName) ;
		if(dbFile.exists()){
			deleteChildren(dbFile) ;
		}
	}

	public Connection getConnection() {
		return connection;
	}

}
