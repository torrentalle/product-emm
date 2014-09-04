package org.wso2.apkgenerator.generators;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;



public class Cert {
	public static void generateCert(String days, String distinguisedName, KeyPair pair) {
		System.out.println("generateCert");
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
			try{
			X509v3CertificateBuilder certBldr = new JcaX509v3CertificateBuilder(principal, serial,
			                                                                    validityBeginDate,
			                                                                    validityEndDate,
			                                                                    principal,
			                                                                    pair.getPublic());
			}
			catch(Exception e){
				System.out.println("exception");
			}
		//return cert;
		
	}
}
