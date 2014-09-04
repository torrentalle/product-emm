package org.wso2.apkgenerator.data;

public class DistinguishName {
	CSRData names;

	public DistinguishName(CSRData source) {
		names = source;
	}

	public String getCAName() {

		String dnCA =
		              "C=" + names.getCountryCA() + ", ST=" + names.getStateCA() + ", L=" +
		                      names.getLocalityCA() + ", O=" + names.getOrganizationCA() + ", OU=" +
		                      names.getOrganizationUCA() + ", CN=" + names.getCommonNameCA();
		System.out.println(dnCA);
		return dnCA;

	}

	public String getRAName() {
		String dnRA =
		              "C=" + names.getCountryRA() + ", ST=" + names.getStateRA() + ", L=" +
		                      names.getLocalityRA() + ", O=" + names.getOrganizationRA() + ", OU=" +
		                      names.getOrganizationURA() + ", CN=" + names.getCommonNameRA();
		System.out.println(dnRA);
		return dnRA;
	}

	public String getSSLName() {
		String dnSSL =
		               "C=" + names.getCountrySSL() + ", ST=" + names.getStateSSL() + ", L=" +
		                       names.getLocalitySSL() + ", O=" + names.getOrganizationSSL() +
		                       ", OU=" + names.getOrganizationUSSL() + ", CN=" +
		                       names.getCommonNameSSL();
		System.out.println(dnSSL);
		return dnSSL;
	}

}
