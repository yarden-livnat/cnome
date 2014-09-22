package edu.utah.sci.cyclist.core.util;

import org.apache.log4j.Logger;

public class OsUtil {
	
	private static String[] OS_Arc = {"64", "386", "arm"};
	private static String[] OS = {"windows", "linux", "darwin", "mac"};

	/**
	 * Returns the current operating system and its bits in a string,
	 * with the format: "os-bits"
	 * @return String
	 */
	public static String getOsDef(){
		Logger log = Logger.getLogger(OsUtil.class);
		String os = System.getProperty("os.name").toLowerCase();
		String osFormatted = convertOs(os);
		String osArc = System.getProperty("os.arch").toLowerCase();
		String osArcFormatted = convertOsArc(osArc);
		
		return osFormatted+"-"+osArcFormatted;
	}
	
	/*
	 * Converts the different formats of os (e.g. "windows 7", "windows xp" etc.), into a
	 * one common format(e.g. "windows").
	 */
	private static String convertOs(String osName){
		for(String os : OS){
			if(osName.contains(os)){
				return os;
			}
		}
		return osName;
	}
	
	/*
	 * Converts the different formats of bits returned by the os, into a
	 * one common format.
	 */
	private static String convertOsArc(String arc){
		for(String osArc : OS_Arc){
			if(arc.contains(osArc)){
				return osArc;
			}
		}
		return arc;
	}
}
