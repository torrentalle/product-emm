package org.wso2.apkgenerator.util;

import java.io.File;

public class Constants {
	public static String WORKING_DIR = "/home/inoshp/Documents/BKS/";
	public static String jksFolder = "jks/";
	public static String BKS_File = "emm_truststore.bks";
	public static String PEM_file = "ca_cert.pem";
	public static String RA_file = "ra_cert.pem";
	public static String IA_file = "ia.crt";
	public static String ANDROID_AGENT="emm-agent-android";
	public static String COMMON_UTIL = ANDROID_AGENT + File.separator + "src" +
	                                   File.separator + "org" + File.separator + "wso2" +
	                                   File.separator + "emm" + File.separator + "agent" +
	                                   File.separator + "utils" + File.separator +
	                                   "CommonUtilities.java";

	public static String PK12_CA_PASSWORD = "cacert";
	public static String PK12_RA_PASSWORD = "racert";
	public static String PK12_CA_ALIAS = "cacert";
	public static String PK12_RA_ALIAS = "racert";
	public static String CA_P12 = "ca.p12";
	public static String RA_P12 = "ra.p12";
	public static String AI_P12 = "ia.p12";

	public static String TRUSTSTORE_PASS = "wso2carbon";
	public static String WSO2CARBON = "wso2carbon";
	public static String WSO2CARBON_JKS = "wso2carbon.jks";
	public static String CLIENT_TRUST_JKS = "client-truststore.jks";
	public static String WSO2MOBILEMDM_JKS = "wso2emm.jks";

	public static String WSO2CARBON_JKS_PASS = "wso2carbon";
	public static String WSO2MOBILEMDM_JKS_PASSWORD = "wso2carbon";

	public static String ALGORITHM = "RSA";
	public static String PROVIDER = "BC";
	public static String ENCRIPTION = "SHA1withRSA";
	public static String ANDROID_AGENT_RAW = ANDROID_AGENT + File.separator + "res" +
	                                     File.separator + "raw"+File.separator;
    public static String ANDROID_AGENT_POM_FAKE = ANDROID_AGENT + File.separator + "pom.xml.txt";
    public static String ANDROID_AGENT_POM = ANDROID_AGENT + File.separator + "pom.xml";
	public static String ANDROID_AGENT_APK=ANDROID_AGENT+File.separator+"target"+File.separator+"MDMAgent.apk";
	public static String MAVEN_HOME_FOLDER="apache-maven-3.2.1";
	
}
