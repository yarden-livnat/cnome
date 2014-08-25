package edu.utah.sci.cyclist.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import edu.utah.sci.cyclist.core.Resources1;
import edu.utah.sci.cyclist.core.model.CyclistDatasource;
import edu.utah.sci.cyclist.core.ui.wizards.SimulationWizard;

public class SimulationTablesPostProcessor {
	
	//Calling external applications for post processing of sqlite database.
	private static final String  EXTERNAL_APPS = "externalApps";
	private static final String WIN_64_POST_PROCESSING_APP = "cycpost-windows-amd64.exe";
	private static final String WIN_386_POST_PROCESSING_APP = "cycpost-windows-386.exe";
	private static final String LINUX_64_POST_PROCESSING_APP = "cycpost-linux-amd64";
	private static final String LINUX_386_POST_PROCESSING_APP = "cycpost-linux-386";
	private static final String LINUX_ARM_POST_PROCESSING_APP = "cycpost-linux-arm";
	private static final String DARWIN_64_POST_PROCESSING_APP = "cycpost-darwin-amd64";
	private static final String DARWIN_386_POST_PROCESSING_APP = "cycpost-darwin-386";
	
	private static final String WINDOWS_OS = "windows";
	private static final String LINUX_OS = "linux";
	private static final String DARWIN_OS = "darwin";
	private static final String MAC_OS = "mac";
	
	private static final String ARCHITECTURE_64 = "64";
	private static final String ARCHITECTURE_386 = "386";
	private static final String ARCHITECTURE_ARM = "arm";
	
	private static final String FIX_AGENTS_TABLE_PHASE1 = "UPDATE Agents set ExitTime = EnterTime+(select Duration from Info where Agents.SimId=Info.SimId ) "+
			  "where ExitTime is null and Lifetime = -1";

	private static final String FIX_AGENTS_TABLE_PHASE2 = "update Agents set ExitTime=(EnterTime+Lifetime) where ExitTime is null and  Lifetime != -1;";
	private static final String FACILITIES_TABLE_CREATE = "create table Facilities as " +
			  "select f.SimId as SimId, f.AgentId as AgentId, f.Spec, f.Prototype, i.AgentId as InstitutionId, cast(-1 as INTEGER) as RegionId, " +
			  "f.EnterTime, f.ExitTime, f.Lifetime from Agents as f, Agents as i where f.Kind = 'Facility' and i.Kind = 'Inst' and f.ParentId = i.AgentId;";
	private static final String FACILITIES_TABLE_INDEX = "create index Facilities_idx on Facilities (SimId ASC, AgentId ASC);";
	
	private static String[] UpdateTablesRunningOrderTbl = 
	{
		FIX_AGENTS_TABLE_PHASE1,
		FIX_AGENTS_TABLE_PHASE2,
	    FACILITIES_TABLE_CREATE,
	    FACILITIES_TABLE_INDEX,
	};
	
	public static void process(Connection conn, CyclistDatasource ds){
		Boolean result = updateSqliteSimTables(ds);
		if(result){
			//If result is true it means the new tables have been created by the external applications, 
			//Should also add the internal tables needed for the simulation database.
			createAdditionalTables(conn, ds);
		}	
	}
		
	/*
	 * Runs an application which creates the tables for the simulation.
	 * The application is chosen based on the operating system and its architecture.
	 * @param : Connection conn - the connection to the database.
	 * @param : CyclistDatasource ds - the data source to work on.
	 */
	private static Boolean updateSqliteSimTables(CyclistDatasource ds){
		Logger log = Logger.getLogger(SimulationWizard.class);
		String os = System.getProperty("os.name").toLowerCase();
		String osArch = System.getProperty("os.arch").toLowerCase();
		log.warn("os= " + os + " arch= " + osArch);
		String dsPath = ds.getProperties().getProperty("path");
		
		String currPath = Resources1.getCurrentPath();
		
		//Path always contains one directory below the main directory
		//e.g bin/ or the jar 
		//Go one directory up to be in the main level.
		//Then go to the external applications directory.
		int lastIndex = currPath.lastIndexOf("/");
		if(lastIndex >= 0){
			currPath = currPath.substring(0,lastIndex)+"/"+EXTERNAL_APPS;
		}
		
		Process process = null;
		try {	
			if(os.indexOf(WINDOWS_OS)>=0){
				if(osArch.indexOf(ARCHITECTURE_64) >= 0){
					currPath += "/" + WIN_64_POST_PROCESSING_APP;
				}else if(osArch.indexOf(ARCHITECTURE_386) >= 0){
					currPath += "/" + WIN_386_POST_PROCESSING_APP;
				}
			} else if(os.indexOf(LINUX_OS)>=0){
				if(osArch.indexOf(ARCHITECTURE_64)>=0){
					currPath ="/"+ currPath+ "/"+LINUX_64_POST_PROCESSING_APP;
				}else if(osArch.indexOf(ARCHITECTURE_386)>=0){
					currPath ="/"+ currPath+ "/"+LINUX_386_POST_PROCESSING_APP;
				}else if(osArch.indexOf(ARCHITECTURE_ARM)>=0){
					currPath ="/"+ currPath+ "/"+LINUX_ARM_POST_PROCESSING_APP;
				}
					
					File file = new File(currPath);
					file.setExecutable(true, false);
			        file.setReadable(true, false);
			        file.setWritable(true, false);
			} else if(os.indexOf(DARWIN_OS)>=0 || os.indexOf(MAC_OS)>=0){
				if(osArch.indexOf(ARCHITECTURE_64)>=0){
					currPath ="/"+ currPath+ "/"+DARWIN_64_POST_PROCESSING_APP;
				}else if(osArch.indexOf(ARCHITECTURE_386)>=0){
					currPath ="/"+ currPath+ "/"+DARWIN_386_POST_PROCESSING_APP;
				}
				
				File file = new File(currPath);
				file.setExecutable(true, false);
		        file.setReadable(true, false);
		        file.setWritable(true, false);
			}
			
			log.warn("path= " + currPath);
			
			//Indication whether or not the new tables have been produced.
		    Boolean isAlreadyProcessed = false;
		    
			if(!currPath.isEmpty()){
				process = Runtime.getRuntime().exec(new String[]{currPath,dsPath});
				InputStream is = process.getInputStream();
			    InputStreamReader isr = new InputStreamReader(is);
			    BufferedReader br = new BufferedReader(isr);
			    String line;
			    
			    while ((line = br.readLine()) != null) {
			      log.warn(line);
			      //Tables already exist - no need to reproduce additional tables.
			      if(line.indexOf("post processed") > -1){
			    	  isAlreadyProcessed = true;
			      }
			    }
			    System.out.println("Program terminated!");
			}else{
				//If no path to run the table creation application 
				//Send true, so the following process steps won't be run.
				isAlreadyProcessed = true;
			}
		    return !isAlreadyProcessed;
		} catch (Exception e) {
			log.warn("process exception = " + e.toString());
			return false;
		}
		
	}
	
	/*
	 * Runs SQL queries to create the facilities tables, based on the tables created by the post processing apps.
	 * @param conn - the connection to the database.
	 * @param ds - the data source to work on
	 * @throws Exception
	 */
	private static void createAdditionalTables(Connection conn, CyclistDatasource ds){
		Statement stmt;
		try {
			stmt = conn.createStatement();
			for(String queryName : UpdateTablesRunningOrderTbl){
				stmt.executeUpdate(queryName);
			}
		} catch (SQLException e) {
			System.out.println("Create Facilities table failed");
		}
		
	}
}
