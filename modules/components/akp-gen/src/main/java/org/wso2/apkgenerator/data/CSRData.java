package org.wso2.apkgenerator.data;

import java.io.Serializable;

import org.json.JSONObject;

public class CSRData implements Serializable{
	//store data needed to create certificates
	//and distinguished names
	String countryCA, stateCA, localityCA, organizationCA, organizationUCA, daysCA,commonNameCA;
	String countryRA, stateRA, localityRA, organizationRA, organizationURA, daysRA, commonNameRA;
    String countrySSL, stateSSL, localitySSL, organizationSSL, organizationUSSL, daysSSL, serverIp;
    
    public CSRData(ObjectReader reader){
		setCountryCA(reader.read("countryCA"));
		setStateCA(reader.read("stateCA"));
		setLocalityCA(reader.read("localityCA"));
		setOrganizationCA(reader.read("organizationCA"));
		setOrganizationUCA(reader.read("organizationUCA"));
		setDaysCA(reader.read("daysCA"));
		setCommonNameCA(reader.read("commonNameCA"));

		setCountryRA(reader.read("countryRA"));
		setStateRA(reader.read("stateRA"));
		setLocalityRA(reader.read("localityRA"));
		setOrganizationRA(reader.read("organizationRA"));
		setOrganizationURA(reader.read("organizationURA"));
		setDaysRA(reader.read("daysRA"));
		setCommonNameRA(reader.read("commonNameRA"));
    	
		setCountrySSL(reader.read("countrySSL"));
		setStateSSL(reader.read("stateSSL"));
		setLocalitySSL(reader.read("localitySSL"));
    	setOrganizationSSL(reader.read("organizationSSL"));
    	setOrganizationUSSL(reader.read("organizationUSSL"));
    	setDaysSSL(reader.read("daysSSL"));
    	setCommonNameSSL(reader.read("serverIp"));
    	
    }
    
    public String getCountryCA() {
		return countryCA;
	}
	public void setCountryCA(String countryCA) {
		this.countryCA = countryCA;
	}
	public String getStateCA() {
		return stateCA;
	}
	public void setStateCA(String stateCA) {
		this.stateCA = stateCA;
	}
	public String getLocalityCA() {
		return localityCA;
	}
	public void setLocalityCA(String localityCA) {
		this.localityCA = localityCA;
	}
	public String getOrganizationCA() {
		return organizationCA;
	}
	public void setOrganizationCA(String organizationCA) {
		this.organizationCA = organizationCA;
	}
	public String getOrganizationUCA() {
		return organizationUCA;
	}
	public void setOrganizationUCA(String organizationUCA) {
		this.organizationUCA = organizationUCA;
	}
	public String getDaysCA() {
		return daysCA;
	}
	public void setDaysCA(String daysCA) {
		this.daysCA = daysCA;
	}
	public String getCommonNameCA() {
		return commonNameCA;
	}
	public void setCommonNameCA(String commonNameCA) {
		this.commonNameCA = commonNameCA;
	}
	public String getCountryRA() {
		return countryRA;
	}
	public void setCountryRA(String countryRA) {
		this.countryRA = countryRA;
	}
	public String getStateRA() {
		return stateRA;
	}
	public void setStateRA(String stateRA) {
		this.stateRA = stateRA;
	}
	public String getLocalityRA() {
		return localityRA;
	}
	public void setLocalityRA(String localityRA) {
		this.localityRA = localityRA;
	}
	public String getOrganizationRA() {
		return organizationRA;
	}
	public void setOrganizationRA(String organizationRA) {
		this.organizationRA = organizationRA;
	}
	public String getOrganizationURA() {
		return organizationURA;
	}
	public void setOrganizationURA(String organizationURA) {
		this.organizationURA = organizationURA;
	}
	public String getDaysRA() {
		return daysRA;
	}
	public void setDaysRA(String daysRA) {
		this.daysRA = daysRA;
	}
	public String getCommonNameRA() {
		return commonNameRA;
	}
	public void setCommonNameRA(String commonNameRA) {
		this.commonNameRA = commonNameRA;
	}
	public String getCountrySSL() {
		return countrySSL;
	}
	public void setCountrySSL(String countrySSL) {
		this.countrySSL = countrySSL;
	}
	public String getStateSSL() {
		return stateSSL;
	}
	public void setStateSSL(String stateSSL) {
		this.stateSSL = stateSSL;
	}
	public String getLocalitySSL() {
		return localitySSL;
	}
	public void setLocalitySSL(String localitySSL) {
		this.localitySSL = localitySSL;
	}
	public String getOrganizationSSL() {
		return organizationSSL;
	}
	public void setOrganizationSSL(String organizationSSL) {
		this.organizationSSL = organizationSSL;
	}
	public String getOrganizationUSSL() {
		return organizationUSSL;
	}
	public void setOrganizationUSSL(String organizationUSSL) {
		this.organizationUSSL = organizationUSSL;
	}
	public String getDaysSSL() {
		return daysSSL;
	}
	public void setDaysSSL(String daysSSL) {
		this.daysSSL = daysSSL;
	}
	public String getCommonNameSSL() {
		return serverIp;
	}
	public void setCommonNameSSL(String serverIp) {
		this.serverIp = serverIp;
	}
	

}
