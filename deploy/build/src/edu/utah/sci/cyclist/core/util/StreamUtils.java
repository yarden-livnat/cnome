package edu.utah.sci.cyclist.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;


public class StreamUtils {

	public static final String PREFIX = "stream2file";
    public static final String SUFFIX = ".tmp";

    /**
     * Converts InputStream to File.
     * @param InputStream - the InputStream to convert.
     * @return File - the converted mode.
     */
    public static File stream2file (InputStream in){
    	try {
	    	final File tempFile = File.createTempFile(PREFIX, SUFFIX);
	        tempFile.deleteOnExit();
			FileUtils.copyInputStreamToFile(in, tempFile);
			return tempFile;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
        
    }
}
