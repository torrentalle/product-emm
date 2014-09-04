package org.wso2.apkgenerator.generators;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.wso2.apkgenerator.util.Constants;
import org.wso2.apkgenerator.util.StackLogger;

public class X509V3Certificates {

	
	//Generate X509V3 certificates. CA, RA and SSL
	public static X509Certificate generateCert(String days, String distinguisedName, KeyPair pair) {
		X509Certificate cert = null;
			//1  hour before
			Date validityBeginDate = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
			int noOfDays = Integer.parseInt(days);
			//Add days to current time to get the validity period
			Date validityEndDate = new Date(System.currentTimeMillis() + noOfDays * 24 * 60 * 60 *
			                                1000);

			Random rand = new Random();
			int randomNum = rand.nextInt((100000 - 1000) + 1) + 1000;
			BigInteger serial = BigInteger.valueOf(System.currentTimeMillis() + randomNum);

			X500Principal principal = new X500Principal(distinguisedName);
			X509v3CertificateBuilder certBldr = new JcaX509v3CertificateBuilder(principal, serial,
			                                                                    validityBeginDate,
			                                                                    validityEndDate,
			                                                                    principal,
			                                                                    pair.getPublic());

			ContentSigner sigGen = null;
			try {
	            sigGen = new JcaContentSignerBuilder(Constants.ENCRIPTION)// creating a signature and self sign it
	            .setProvider(Constants.PROVIDER).build(pair.getPrivate());// by signing with its own key
            } catch (OperatorCreationException e) {
            	StackLogger.log("Error creating ContentSigner with JcaContentSignerBuilder" +
            			" with the private key provided", e.getStackTrace().toString());
            }
			try {
				//make basic constraint to tell this is a root CA
	            certBldr.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
            } catch (CertIOException e1) {
            	StackLogger.log("Error adding extension BasicConstraints", 
            	             e1.getStackTrace().toString());
            }
			try {
	            cert = new JcaX509CertificateConverter().setProvider(Constants.PROVIDER)
	                                                    .getCertificate(certBldr.build(sigGen));
            } catch (CertificateException e) {
            	StackLogger.log("Error building certificate", 
           	             e.getStackTrace().toString());
            }

		return cert;
	}

	// build intermediate certificate using CA, aka RA cert
	public static X509Certificate buildIntermediateCert(PublicKey intKey, PrivateKey caKey,
	                                                    X509Certificate caCert,
	                                                    String distinguishedName, String days)
{
		//1  hour before
		Date validityBeginDate = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
		int noOfDays = Integer.parseInt(days);
		//Add days to current time to get the validity period
		Date validityEndDate = new Date(System.currentTimeMillis() + noOfDays * 24 * 60 * 60 * 1000);
		X509v3CertificateBuilder certBldr = new JcaX509v3CertificateBuilder(caCert.getSubjectX500Principal(),
		                                                                    BigInteger.valueOf(1),
		                                                                    validityBeginDate,validityEndDate,
		                                                                    new X500Principal(distinguishedName),
		                                                                    intKey);

		JcaX509ExtensionUtils extUtils = null;
        try {
	        extUtils = new JcaX509ExtensionUtils();
        } catch (NoSuchAlgorithmException e1) {
        	StackLogger.log("cryptographic algorithm is requested but" +
        			" it is not available in the environment", e1.getStackTrace().toString());
        }
		try {
	        certBldr.addExtension(Extension.authorityKeyIdentifier, false,
	                              extUtils.createAuthorityKeyIdentifier(caCert))
	                .addExtension(Extension.subjectKeyIdentifier, false,
	                              extUtils.createSubjectKeyIdentifier(intKey))
	                              //mark it as a intermediate by setting constraint 0
	                .addExtension(Extension.basicConstraints, true, new BasicConstraints(0))
	                .addExtension(Extension.keyUsage,true,
	                              new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign |
	                                           KeyUsage.cRLSign | KeyUsage.dataEncipherment |
	                                           KeyUsage.keyAgreement | KeyUsage.keyEncipherment));
        } catch (CertificateEncodingException e2) {
        	StackLogger.log("Certificate Encroding issue while adding extensions"
        	             , e2.getStackTrace().toString());
        }
		catch (CertIOException e) {
			StackLogger.log("Error adding extension BasicConstraints", 
        	             e.getStackTrace().toString());
        }
		
		ContentSigner signer = null;
        try {
	        signer = new JcaContentSignerBuilder(Constants.ENCRIPTION).setProvider(Constants.PROVIDER)//sign with CA
	                                                                         .build(caKey);
        } catch (OperatorCreationException e) {
        	StackLogger.log("Error creating ContentSigner with JcaContentSignerBuilder" +
        			" with the private key provided", e.getStackTrace().toString());
        }
		try {
	        return new JcaX509CertificateConverter().setProvider(Constants.PROVIDER)
	                                                .getCertificate(certBldr.build(signer));
        } catch (CertificateException e) {
        	StackLogger.log("Error building certificate", 
           	             e.getStackTrace().toString());
        }
		return null;
	}

	//build our end certificate which is SSL and sign it with CA
	public static X509Certificate buildEndEntityCert(PublicKey entityKey, PrivateKey caKey,
	                                                 X509Certificate caCert,
	                                                 String distinguisedName, String days) {
		//1  hour before
		Date validityBeginDate = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
		int noOfDays = Integer.parseInt(days);
		//Add days to current time to get the validity period
		Date validityEndDate = new Date(System.currentTimeMillis() + noOfDays * 24 * 60 * 60 * 1000);		
		X509v3CertificateBuilder certBldr = new JcaX509v3CertificateBuilder(caCert.getSubjectX500Principal(),
		                                                                    BigInteger.valueOf(1),
		                                                                    validityBeginDate,validityEndDate,
		                                                                    new X500Principal(distinguisedName),
		                                                                    entityKey);

		JcaX509ExtensionUtils extUtils = null;
        try {
	        extUtils = new JcaX509ExtensionUtils();
        } catch (NoSuchAlgorithmException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
        }
		try {
	        certBldr.addExtension(Extension.authorityKeyIdentifier, false,
	                              extUtils.createAuthorityKeyIdentifier(caCert))
	                .addExtension(Extension.subjectKeyIdentifier, false,
	                              extUtils.createSubjectKeyIdentifier(entityKey))
	                              //mark it as an end certificate by seting constraint false
	                .addExtension(Extension.basicConstraints, true, new BasicConstraints(false))
	                .addExtension(Extension.keyUsage, true,
	                              new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
		} catch (CertificateEncodingException e1) {
			StackLogger.log("Certificate Encroding issue while adding extensions", e1.getStackTrace()
			                                                                      .toString());
		} catch (CertIOException e) {
			StackLogger.log("Error adding extension BasicConstraints", e.getStackTrace().toString());
		}
		ContentSigner signer = null;
        try {
	        signer = new JcaContentSignerBuilder(Constants.ENCRIPTION).setProvider(Constants.PROVIDER)//sign with CA
	                                                                         .build(caKey);
        } catch (OperatorCreationException e) {
        	StackLogger.log("Error creating ContentSigner with JcaContentSignerBuilder" +
        			" with the private key provided", e.getStackTrace().toString());
        }
		try {
	        return new JcaX509CertificateConverter().setProvider(Constants.PROVIDER)
	                                                .getCertificate(certBldr.build(signer));
        } catch (CertificateException e) {
        	StackLogger.log("Error building certificate", 
           	             e.getStackTrace().toString());
        }
		
		return null;
	}

}
