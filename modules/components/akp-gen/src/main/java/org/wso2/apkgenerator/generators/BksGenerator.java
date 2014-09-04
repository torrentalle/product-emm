package org.wso2.apkgenerator.generators;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.wso2.apkgenerator.util.Constants;
import org.wso2.apkgenerator.util.StackLogger;

public class BksGenerator {
	public static void generateBKS(X509Certificate caCert){		
	    KeyStore keystore;
        try {
        	
    	    Provider bcProvider = new BouncyCastleProvider(); 
    	    keystore = KeyStore.getInstance("BKS",bcProvider);
    	    keystore.load(null);
    	    keystore.setCertificateEntry("cert-alias", caCert);
    	    
    	    FileOutputStream fos = new FileOutputStream(Constants.WORKING_DIR+Constants.BKS_File);
            keystore.store(fos, Constants.TRUSTSTORE_PASS.toCharArray());
            fos.close();
        } catch (NoSuchAlgorithmException e1) {
        	StackLogger.log("cryptographic algorithm is requested but" +
        			" it is not available in the environment", e1.getStackTrace().toString());
        }
		catch (CertificateException e2) {
        	StackLogger.log("Error building certificate", 
       	             e2.getStackTrace().toString());
        }
		catch (IOException e3) {
        	StackLogger.log("file error while working with "+Constants.WORKING_DIR +Constants.BKS_File, 
           	             e3.getStackTrace().toString());
		}
		catch (KeyStoreException e4) {
			StackLogger.log("generic KeyStore exception working with BKS "+Constants.BKS_File, 
	       	             e4.getStackTrace().toString());
		}
        catch (Exception e4) {
			StackLogger.log("General error "+Constants.BKS_File, 
	       	             e4.getStackTrace().toString());
		}
       

	   // return context.getSocketFactory();
	}
}
