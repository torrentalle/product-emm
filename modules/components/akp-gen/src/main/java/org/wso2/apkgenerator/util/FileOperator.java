package org.wso2.apkgenerator.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.bouncycastle.openssl.PEMWriter;

public class FileOperator {
	private static FileInputStream fis;
	
	public static String getPath(String path){
		try {
			return path.replaceAll("/", Matcher.quoteReplacement(File.separator));
		}catch (Exception e) {
        	StackLogger.log("Common error when getting file path", e.getStackTrace().toString());
        }
		return null;
	}
	
	public static byte[] fileToByteArr(String filePath){
		File file = new File(filePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        
        try {
           	fis = new FileInputStream(file);
            for (int readNum; (readNum = fis.read(buf)) != -1;) {
                bos.write(buf, 0, readNum); //no doubt here is 0
               //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
            }
        } catch (IOException e) {
        	StackLogger.log("Error occured while converting file: "+filePath+" to a byteArray", e.getStackTrace().toString());
        }
        return bos.toByteArray();
	}
	
	public static void copyFile(String source, String dest){
		InputStream input = null;
		OutputStream output = null;
		try {
			input = new FileInputStream(new File(source));
			output = new FileOutputStream(new File(dest));
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} catch (FileNotFoundException e) {
			StackLogger.log("cannot find one of the files, while trying to copy file :"+
					source+"\n to its destination: "+dest, e.getStackTrace().toString());
        } catch (IOException e) {
        	StackLogger.log("Error opening/working with file, while trying to copy file :"+
					source+"\n to its destination: "+dest, e.getStackTrace().toString());
        } finally {
			try {
	            input.close();
	            output.close();
            } catch (IOException e) {
            	StackLogger.log("Error in closing file Stream , while trying to copy file "+
    					source+"\n to its destination: "+dest, e.getStackTrace().toString());
            }
			
		}
	}
	
	public static String readFile(String path) {
		String content="";
		try {
	        content = new Scanner(new File(path)).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		return content;
	}
	
	public static void fileWrite(String path, String content){
		PrintWriter out;
        try {
	        out = new PrintWriter(path);
	        out.println(content);
			out.close();
        } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}

	public static void createZip(String zipFilePath, String [] files){
		FileOutputStream fout;
        try {
	        fout = new FileOutputStream(zipFilePath);
	        ZipOutputStream zout = new ZipOutputStream(fout);
			for(int x=0;x<files.length;x++){
				File f=new File(files[x]);
				FileInputStream in = new FileInputStream(files[x]);
		        zout.putNextEntry(new ZipEntry(f.getName())); 

		        byte[] b = new byte[1024];

		        int count;

		        while ((count = in.read(b)) > 0) {
		           // System.out.println();
		            zout.write(b, 0, count);
		        }
		        in.close();
			}
			zout.close();
        } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
	}
	
	public static void writePem(String path, Object file){
		FileOutputStream fos3;
        try {
	        fos3 = new FileOutputStream(path);
	        PEMWriter pemWriter3 = new PEMWriter(new PrintWriter(fos3));
			pemWriter3.writeObject(file);
		    pemWriter3.flush();
		    pemWriter3.close();
        } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } 
		
	}
	
}
