package org.wso2.apkgenerator.generators;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.wso2.apkgenerator.util.Constants;
import org.wso2.apkgenerator.util.StackLogger;

public class KeyPairCreator {
	public static KeyPair getKeyPair(){
		System.out.println("keypair");
    	Security.addProvider(new BouncyCastleProvider());
    	KeyPairGenerator keyPairGenerator;
    	KeyPair keyPair=null;
    	try {
	        keyPairGenerator = KeyPairGenerator.getInstance(Constants.ALGORITHM, Constants.PROVIDER);
	        keyPairGenerator.initialize(1024, new SecureRandom());
	        keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
        	System.out.println(Constants.ALGORITHM+" cryptographic algorithm is requested but" +
        			" it is not available in the environment"+ e.getStackTrace().toString());
        	StackLogger.log(Constants.ALGORITHM+" cryptographic algorithm is requested but" +
        			" it is not available in the environment", e.getStackTrace().toString());
        } catch (NoSuchProviderException e) {
        	System.out.println(Constants.PROVIDER+" security provider is requested but it is not available in " +
        			"the environment. "+ e.getStackTrace().toString());
        	StackLogger.log(Constants.PROVIDER+" security provider is requested but it is not available in " +
        			"the environment. ", e.getStackTrace().toString());
        }
		return keyPair;
	    
    }
}
