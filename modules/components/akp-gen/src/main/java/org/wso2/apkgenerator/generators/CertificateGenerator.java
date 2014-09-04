package org.wso2.apkgenerator.generators;

import java.security.cert.X509Certificate;
import java.security.KeyPair;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.wso2.apkgenerator.data.CSRData;
import org.wso2.apkgenerator.data.DistinguishName;
import org.wso2.apkgenerator.util.Constants;
import org.wso2.apkgenerator.util.FileOperator;


public class CertificateGenerator {
	
	DistinguishName dn;
	CSRData csrDataSrc;
	public KeyPair keyPairCA,keyPairRA,keyPairSSL;
	public X509Certificate caCert,raCert,sslCert;
	
	public CertificateGenerator(CSRData csrData){

		System.out.println("CertificateGenerator");
		csrDataSrc=csrData;//make a copy of the csr data
		dn= new DistinguishName(csrData);//pass data to initialize distinguished names
	}
	

	public void generator() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		//generate CA cert and keys
		keyPairCA = KeyPairCreator.getKeyPair();
		try{
			Cert.generateCert(csrDataSrc.getDaysCA(),dn.getCAName(), keyPairCA);
		caCert = X509V3Certificates.generateCert(csrDataSrc.getDaysCA(),dn.getCAName(), keyPairCA);
		}
		catch(Exception e){
			System.out.println("error "+e.getMessage());
		}
		
		//generate RA cert and keys
		keyPairRA = KeyPairCreator.getKeyPair();
	    raCert = X509V3Certificates.buildIntermediateCert(keyPairRA.getPublic(), keyPairCA.getPrivate(),
	                                                     caCert,dn.getRAName(),csrDataSrc.getDaysRA());		
	    //generate SSL cert and keys
	    keyPairSSL = KeyPairCreator.getKeyPair();
	    sslCert = X509V3Certificates.buildEndEntityCert(keyPairSSL.getPublic(), keyPairCA.getPrivate(), 
	                                                   caCert,dn.getSSLName(),csrDataSrc.getDaysSSL());
	    
	    
	    
	    FileOperator.writePem(Constants.WORKING_DIR+ Constants.PEM_file,caCert);
//	    FileOperator.writePem(Constants.WORKING_DIR+ "ca.crt",caCert);
//	    FileOperator.writePem(Constants.WORKING_DIR+ "ca_private.pem",keyPairCA.getPrivate());
//	    FileOperator.writePem(Constants.WORKING_DIR+ "ca_private.key",keyPairCA.getPrivate());
//	    
//	    FileOperator.writePem(Constants.WORKING_DIR+ "ra_cert.pem",raCert);
//	    FileOperator.writePem(Constants.WORKING_DIR+ "ra_private.pem",keyPairRA.getPrivate());
//	    
//	    FileOperator.writePem(Constants.WORKING_DIR+ "ia.crt",sslCert);
//	    FileOperator.writePem(Constants.WORKING_DIR+ "ia.key",keyPairSSL.getPrivate());
	}
	

	
	
}
