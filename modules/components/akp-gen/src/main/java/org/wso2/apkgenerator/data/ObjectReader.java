package org.wso2.apkgenerator.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.apkgenerator.util.StackLogger;

public class ObjectReader {
	public static JSONObject res;
	public ObjectReader(String jsObj){
		try {
	        res=new JSONObject(jsObj);
        } catch (JSONException e) {
	        StackLogger.log("Error in converting String to JSONObject", e.getStackTrace().toString());
        }
	}
	
	public String read(String strName){

		try {
	        return res.getString(strName);
        } catch (JSONException e) {
        	StackLogger.log("Error in getting String "+strName+" from JSONObject", e.getStackTrace().toString());
        }
		catch (Exception e) {
        	StackLogger.log("Exception ", e.getStackTrace().toString());
        }
		return null;
	}
	
	
}
