package org.wso2.apkgenerator.generators;

import org.json.JSONObject;
import org.wso2.apkgenerator.data.CSRData;
import org.wso2.apkgenerator.data.ObjectReader;
import org.wso2.apkgenerator.util.Constants;
import org.wso2.apkgenerator.util.FileOperator;
import org.wso2.apkgenerator.util.StackLogger;
import java.io.File;

import java.lang.Exception;
import java.lang.System;

public class Invoker {
	CertificateGenerator gen;
	public String generateApk(String jsonStr) {
		
		ObjectReader reader = new ObjectReader(jsonStr);
		
		Constants.WORKING_DIR = FileOperator.getPath(reader.read("workingDir"));// assign working directory
		Constants.TRUSTSTORE_PASS = reader.read("password");// and password
		String zipFileName = reader.read("usersname") + "_" + reader.read("company") + ".zip";// final zip
																// file name
		// store data needed to create certificates
		CSRData csrDate =
		                  new CSRData(reader);

		try {
			gen = new CertificateGenerator(csrDate);
			gen.generator();// generate certificates
			// convert generated certs and keys to JKS
			KeyStoreGenerator.convertCertsToKeyStore(gen.keyPairCA, gen.keyPairRA, gen.keyPairSSL,
			                                         gen.caCert, gen.raCert, gen.sslCert);

		} catch (Exception e) {
        	StackLogger.log("Common Exception", e.getStackTrace().toString());
		}

		// Generate BKS using CA pem
		BksGenerator.generateBKS(gen.caCert);
		// Copy BKS to Android source folder
		FileOperator.copyFile(Constants.WORKING_DIR + Constants.BKS_File,
		                      Constants.WORKING_DIR + Constants.ANDROID_AGENT_RAW +
		                              Constants.BKS_File);

        try {
            boolean success = (new File(reader.read("zipPath")+"/Apk/")).mkdirs();
            if (!success) {
                System.out.println("failed");
            }
        }
        catch(Exception e){
            StackLogger.log("Error when creting directory to store the apk",
                    e.getStackTrace().toString());
        }

		// generate apk using maven and create a zip
		return Apk.generateApk(Constants.WORKING_DIR + Constants.COMMON_UTIL, reader.read("serverIp"),
		                       Constants.TRUSTSTORE_PASS, zipFileName, reader.read("zipPath")+"/Apk/");
	}
}
