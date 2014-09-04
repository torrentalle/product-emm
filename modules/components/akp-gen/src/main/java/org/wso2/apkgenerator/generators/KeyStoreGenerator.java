package org.wso2.apkgenerator.generators;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.wso2.apkgenerator.util.Constants;
import org.wso2.apkgenerator.util.FileOperator;
import org.wso2.apkgenerator.util.StackLogger;

public class KeyStoreGenerator {

	//manages the flow of cert to keystore conversion 
	public static void convertCertsToKeyStore(KeyPair keyPairCA, KeyPair keyPairRA,
	                                          KeyPair keyPairSSL, X509Certificate caCert,
	                                          X509Certificate raCert, X509Certificate sslCert) {
		System.out.println("CertificateGenerator");
		//insert CA and RA to mobile mdm JKS
		generateMobileMdm(keyPairCA.getPrivate(), new X509Certificate[] { caCert },
		                  Constants.PK12_CA_PASSWORD, Constants.WSO2MOBILEMDM_JKS,
		                  Constants.PK12_CA_ALIAS, false, Constants.WSO2MOBILEMDM_JKS_PASSWORD);
		generateMobileMdm(keyPairRA.getPrivate(), new X509Certificate[] { raCert, caCert },
		                  Constants.PK12_RA_PASSWORD, Constants.WSO2MOBILEMDM_JKS,
		                  Constants.PK12_RA_ALIAS, true, Constants.WSO2MOBILEMDM_JKS_PASSWORD);

		//take a copy of jks files in the jksFolder which is the temp folder and and copy to
		//working dir
		FileOperator.copyFile(Constants.WORKING_DIR + Constants.jksFolder +
		                      Constants.CLIENT_TRUST_JKS, Constants.WORKING_DIR +
		                                                  Constants.CLIENT_TRUST_JKS);
		FileOperator.copyFile(Constants.WORKING_DIR + Constants.jksFolder +
		                      Constants.WSO2CARBON_JKS, Constants.WORKING_DIR +
		                                                Constants.WSO2CARBON_JKS);
		//insert SSL cert to client trust store JKS and wso2carbon JKS
		generateCarbonJksFiles(keyPairSSL.getPrivate(), new X509Certificate[] { sslCert },
		                       Constants.WSO2CARBON, Constants.CLIENT_TRUST_JKS,
		                       Constants.WSO2CARBON, caCert, Constants.PK12_CA_ALIAS);
		generateCarbonJksFiles(keyPairSSL.getPrivate(), new X509Certificate[] { sslCert },
		                       Constants.WSO2CARBON, Constants.WSO2CARBON_JKS,
		                       Constants.WSO2CARBON, caCert, Constants.PK12_CA_ALIAS);

	}

	//create  a new JKS keystore and add certs to it.
	public static void generateMobileMdm(Key key, X509Certificate[] cert, String password,
	                                     String outFile, String alias, boolean createJks,
	                                     String storePass) {
		System.out.println("generating " + outFile);
		try {

			KeyStore keyStore = KeyStore.getInstance("JKS");
			if (createJks == true) {//this means instead of creating a new store, use the 
									//existing one
				FileInputStream fileInputStream =
				                                  new FileInputStream(Constants.WORKING_DIR +
				                                                      outFile);
				keyStore.load(fileInputStream, storePass.toCharArray());
				fileInputStream.close();
			} else {// create a new store
				keyStore.load(null, null);
			}

			keyStore.setKeyEntry(alias, (Key) key, password.toCharArray(), cert);
			// our destination jks
			FileOutputStream fileOutputStream =
			                                    new FileOutputStream(Constants.WORKING_DIR +
			                                                         outFile);
			keyStore.store(fileOutputStream, storePass.toCharArray());
			fileOutputStream.close();
		}
		catch (NoSuchAlgorithmException e1) {
        	StackLogger.log("cryptographic algorithm is requested but" +
        			" it is not available in the environment", e1.getStackTrace().toString());
        }
		catch (CertificateException e2) {
        	StackLogger.log("Error building certificate", 
       	             e2.getStackTrace().toString());
        }
		catch (IOException e3) {
        	StackLogger.log("file error while working with "+Constants.WORKING_DIR +outFile, 
           	             e3.getStackTrace().toString());
		}
		catch (KeyStoreException e4) {
			StackLogger.log("generic KeyStore exception working with JKS "+outFile, 
	       	             e4.getStackTrace().toString());
		}
		System.out.println("generating " + outFile + " done");
	}

	//input certs to carbon jks files in the server
	public static void generateCarbonJksFiles(Key key, X509Certificate[] cert, String password,
	                                          String outFile, String alias,
	                                          X509Certificate secondCert, String caAlias) {
		System.out.println("generating " + outFile);
		try {
			FileInputStream fileInputStream = new FileInputStream(Constants.WORKING_DIR + outFile);
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(fileInputStream, password.toCharArray());
			fileInputStream.close();
			if (secondCert != null)
				keyStore.setCertificateEntry(caAlias, secondCert);
			keyStore.setKeyEntry(alias, (Key) key, password.toCharArray(), cert);

			FileOutputStream fileOutputStream =
			                                    new FileOutputStream(Constants.WORKING_DIR +
			                                                         outFile);// our destination
																			  // jks
			keyStore.store(fileOutputStream, password.toCharArray());
			fileOutputStream.close();
		}
		catch (NoSuchAlgorithmException e1) {
        	StackLogger.log("cryptographic algorithm is requested but" +
        			" it is not available in the environment", e1.getStackTrace().toString());
        }
		catch (CertificateException e2) {
        	StackLogger.log("Error building certificate", 
       	             e2.getStackTrace().toString());
        }
		catch (IOException e3) {
        	StackLogger.log("file error while working with "+Constants.WORKING_DIR +outFile, 
           	             e3.getStackTrace().toString());
		}
		catch (KeyStoreException e4) {
			StackLogger.log("generic KeyStore exception working with JKS "+outFile, 
	       	             e4.getStackTrace().toString());
		}
		System.out.println("generating " + outFile + " done");
	}

}
