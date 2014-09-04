package org.wso2.apkgenerator.generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.wso2.apkgenerator.util.Constants;
import org.wso2.apkgenerator.util.FileOperator;
import org.wso2.apkgenerator.util.StackLogger;

public class Apk {

    // generate APK and
    public static String generateApk(String commonUtilPath, String serverIp, String password,
                                     String zipFileName, String zipPath) {
        String fileContent = FileOperator.readFile(commonUtilPath);
        changeContent(commonUtilPath, fileContent, serverIp, password);
        renameApk();
        buildApk();

        String apkPath = Constants.WORKING_DIR + Constants.ANDROID_AGENT_APK;

        String wso2carbon = Constants.WORKING_DIR + Constants.WSO2CARBON_JKS;
        String client_truststore = Constants.WORKING_DIR + Constants.CLIENT_TRUST_JKS;
        String wso2mobilemdm = Constants.WORKING_DIR + Constants.WSO2MOBILEMDM_JKS;

        FileOperator.createZip(zipPath + zipFileName, new String[] { apkPath, wso2carbon,
                client_truststore,
                wso2mobilemdm });

        return zipPath + zipFileName;
    }

    private static void renameApk() {
        File f = null;
        File f1 = null;
        boolean bool = false;

        try{
            // create new File objects
            f = new File(Constants.WORKING_DIR+Constants.ANDROID_AGENT_POM_FAKE);
            f1 = new File(Constants.WORKING_DIR+Constants.ANDROID_AGENT_POM);


            // rename file
            bool = f.renameTo(f1);

        }catch(Exception e){
            // if any error occurs
            e.printStackTrace();
        }
    }


    // change the content of the commonUtils file and change ip address and
    // password
    private static void changeContent(String path, String content, String hostName, String password) {
        System.out.println(path + "  -  ");
        int startInd = content.indexOf("String SERVER_IP = \"");
        int lastInd = content.indexOf("\";", startInd);
        String changedContent =
                content.substring(0, startInd) + "String SERVER_IP = \"" +
                        hostName + content.substring(lastInd);

        startInd = changedContent.indexOf("String TRUSTSTORE_PASSWORD = \"");
        lastInd = changedContent.indexOf("\";", startInd);
        changedContent =
                changedContent.substring(0, startInd) + "String TRUSTSTORE_PASSWORD = \"" +
                        password + changedContent.substring(lastInd);
        FileOperator.fileWrite(path, changedContent);

    }

    // build the apk using maven
    static void buildApk() {
        List<String> PUBLISH_GOALS = Arrays.asList("clean", "package");
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(Constants.WORKING_DIR + Constants.ANDROID_AGENT +
                File.separator));
        request.setGoals(PUBLISH_GOALS);

        DefaultInvoker invoker = new DefaultInvoker();
        Constants.MAVEN_HOME_FOLDER = getMavenHome("MAVEN_HOME");
        invoker.setMavenHome(new File("/home/inoshp/Documents/Software/apache-maven-3.2.1/"));
        //System.out.println("PRint    >>" + Constants.MAVEN_HOME_FOLDER);

        try {
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            System.out.println("Error while executing maven invoker" +e.getMessage().toString());
            StackLogger.log("Error while executing maven invoker", e.getStackTrace()
                    .toString());

        }
    }

    private static String getMavenHome(String var) {
        String home = "";
        Map<String, String> variables = System.getenv();

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            //System.out.println(name + "=" + value);
            if (name.equalsIgnoreCase(var)) {
                home = value;
            }
        }
        return home;
    }

}
