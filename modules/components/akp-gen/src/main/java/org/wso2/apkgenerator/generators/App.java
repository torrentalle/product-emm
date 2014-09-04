package org.wso2.apkgenerator.generators;


import org.wso2.apkgenerator.util.Constants;


/**
 * This is used for debuging purposes
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //Generate PEM
        Invoker i=new Invoker();
        String json="{\"workingDir\":\"/home/inoshp/Documents/BKS/\","+
        "\"countryCA\":\"SL\", \"stateCA\":\"WP\",\"localityCA\":\"Colombo\", \"organizationCA\":\"WSO2\", \"organizationUCA\":\"Mobile\","+
        "\"daysCA\":\"365\", \"commonNameCA\":\"kasun\",\"countryRA\":\"SL\", \"stateRA\":\"WP\",\"localityRA\":\"Colombo\", \"organizationRA\":\"WSO2\","+
        "\"organizationURA\":\"Mobile\",\"daysRA\":\"365\", \"commonNameRA\":\"kasun\",\"countrySSL\":\"SL\", \"stateSSL\":\"WP\",\"localitySSL\":\"Colombo\","+
        "\"organizationSSL\":\"WSO2\", \"organizationUSSL\":\"Mobile\",\"daysSSL\":\"365\", \"serverIp\":\"10.10.10.3\",\"password\":\"wso2carbon\"," +
        "\"usersname\":\"Kasun\",\"company\":\"WSO2\",\"zipPath\":\"/home/inoshp/Documents/BKS/\"}";
        
       String path=i.generateApk(json);
    		  
       System.out.println(path);
    }
}

