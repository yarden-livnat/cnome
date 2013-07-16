package edu.utah.sci.cyclist.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;

import edu.utah.sci.cyclist.model.WorkDirectory;

public class WorkDirectoryController {
	private String SAVE_DIR = System.getProperty("user.dir") + "/.cnome/";
	private String GENERAL_CONFIG_FILE = SAVE_DIR+"generalConfig.xml";
	private String DEFAULT_WORKSPACE = System.getProperty("user.home");
	private ObservableList<String> _workspaces = FXCollections.observableArrayList();
	private int _lastId = 0;
	
	/**
	 * initialize the file which saves the general configuration for the application.
	 * (For example - all the existing workspaces).
	 * If file doesn't exist - create one and add the default workspaces.
	 * If exists - do nothing.
	 * 
	 * @return boolean - true if the file exists, false - if it doesn't.
	 */
	public Boolean initGeneralConfigFile(){
		// The user general config  file
		File saveFile = new File(GENERAL_CONFIG_FILE);
		
		//First time - create a new file.
		if (!saveFile.exists())
		{
			try {
					saveFile.createNewFile();
					// Create the root memento
					XMLMemento memento = XMLMemento.createWriteRoot("root");
						
					//When creating the global config file for the first time - add the default workspaces.
					IMemento workDirectories = memento.createChild("workDirectories");
					workDirectories.putInteger("lastChosenId", 0);
					new WorkDirectory(_lastId, DEFAULT_WORKSPACE, true ).save(workDirectories.createChild("workDirectory"));
					_lastId++;
					new WorkDirectory(_lastId, DEFAULT_WORKSPACE+"/software" ).save(workDirectories.createChild("workDirectory"));
					_lastId++;
					memento.save(new PrintWriter(saveFile));
					_workspaces.add(DEFAULT_WORKSPACE);
					_workspaces.add(DEFAULT_WORKSPACE+"/software");
					return false;
				} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
		}
		return true;
	}
	
	/**
	 * Reads the applications working directories information from the general application configuration file .
	 */
	public void restoreGeneralConfigFile(){
		
		// Check if the save file exists
		File saveFile = new File(GENERAL_CONFIG_FILE);
					
		// If we have a save file, read it in
		if(saveFile.exists()){
			Reader reader;
			try {
				reader = new FileReader(saveFile);
				// Create the root memento
				XMLMemento memento = XMLMemento.createReadRoot(reader);
				// Read in the data sources
				IMemento rootWorkDirectories = memento.getChild("workDirectories");
				if(rootWorkDirectories != null){
					IMemento[] workDirectories = rootWorkDirectories.getChildren("workDirectory");
					WorkDirectory workDir = new WorkDirectory();
					for(IMemento workDirectory: workDirectories){
						if (workDirectory != null){
							workDir.restore(workDirectory);
							_workspaces.add(workDir.getPath());
							if(workDir.getId() > _lastId){
								_lastId = workDir.getId();
							}
						}
					}
					_lastId++;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//If the file doesn't exist - create one with default values.
		else{
			initGeneralConfigFile();
		}
	}
	
	/**
	 * handles the cases when user adds new work directory or changes the chosen work directory.
	 */
	public void handleWorkDirectoriesListChangedEvent(Change<? extends String> arg0 ){
		// The user general config  file
		File saveFile = new File(GENERAL_CONFIG_FILE);
		
		//If the config file doesn't exist create a new file.
		if (!saveFile.exists()){
			initGeneralConfigFile();
		}
		
		ObservableList<? extends String> list = arg0.getList();
		if(list.size()>1){
			FileReader reader;
			try {
				reader = new FileReader(saveFile);
				// Create the root memento
				XMLMemento memento = XMLMemento.createReadRoot(reader);
				// Read in the data sources
				IMemento rootWorkDirectories = memento.getChild("workDirectories");
			
				//If 2 identical values - it means that the "..." button has been pressed before the o.k button.
				//So the new value should be saved in the general config file.
				if(list.get(0).equals(list.get(1))){			
					if(rootWorkDirectories != null){
						WorkDirectory workDirectory = new WorkDirectory(_lastId,list.get(0),true);
						workDirectory.save(rootWorkDirectories.createChild("workDirectory"));
						rootWorkDirectories.putInteger("lastChosenId", _lastId);
						_lastId++;
					}
				
				} else{
					//If the values in the list are different - it means that only the o.k button was pressed.
					//Only need to update the value to "last chosen".
					if(rootWorkDirectories != null){
						int lastId = rootWorkDirectories.getInteger("lastChosenId");
						IMemento[] workDirectories = rootWorkDirectories.getChildren("workDirectory");	
						WorkDirectory workDir = new WorkDirectory();
						for(IMemento workDirectory: workDirectories){
							if (workDirectory != null){
								workDir.restore(workDirectory);
								//If reached the chosen work directory
								String fixedDirecotry = list.get(1).replace("\\","/");
								if(workDir.getPath().replace("\\","/").replace("//", "/").equals(fixedDirecotry)){
									rootWorkDirectories.putInteger("lastChosenId", workDir.getId());
									break;
								}
								
							}
						}
					}
				}
				memento.save(new PrintWriter(saveFile));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * returns list of all the existing workspaces..
	 * 
	 * @return ObservableList<String>.
	 */
	public ObservableList<String> getWorkspaces()
	{
		return _workspaces;
	}
}
