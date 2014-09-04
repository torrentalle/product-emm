package org.wso2.apkgenerator.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class StackLogger {
	static private FileHandler fileHTML = null;
	static private Formatter formatterHTML = null;
	static private LogRecord rec;
	static private Logger logger = null;

	static public void log(String title, String message) {

		rec = new LogRecord(Level.ALL, appendMessage(title, message));
		// Get the global logger to configure it
		if (logger == null)
			logger = Logger.getLogger("MDM-Auto Generator-Log");
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.INFO);

		if (fileHTML == null)
	        try {
	            fileHTML = new FileHandler(Constants.WORKING_DIR + "Logging.html");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

		// create HTML Formatter
		if (formatterHTML == null){
			formatterHTML = new HtmlFormatter();
			fileHTML.setFormatter(formatterHTML);
			logger.addHandler(fileHTML);
			
		}
		logger.info(rec.getMessage());

	}

	private static String appendMessage(String title, String message) {

		return title + "<br/><br/>" + message;

	}
}
