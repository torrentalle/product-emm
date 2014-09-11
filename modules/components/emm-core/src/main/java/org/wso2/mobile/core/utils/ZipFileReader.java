
/*
 * *
 *  *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package org.wso2.mobile.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.NSDictionary;

public class ZipFileReader {

	//ios CF Bundle keys
	private static final String IPA_BUNDLE_VERSION_KEY = "CFBundleVersion";
	private static final String IPA_BUNDLE_NAME_KEY = "CFBundleName";
	private static final String IPA_BUNDLE_IDENTIFIER_KEY = "CFBundleIdentifier";

	//Android attributes
	private static final String APK_VERSION_KEY = "versionName";
	private static final String APK_PACKAGE_KEY = "package";
	private static final String ANDROID_MANIFEST_FILE = "AndroidManifest.xml";

	//General attributes
	private static final String INFO_PLIST_PATTERN = "^(Payload/)(.)+(.app/Info.plist)$";
	private static final String APP_VERSION = "version";
	private static final String APP_PACKAGE = "package";
	private static final String APP_NAME = "name";

	private static final Log log = LogFactory.getLog(ZipFileReader.class);

	public static Document loadXMLFromString(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

	public String readAndroidManifestFile(String filePath) {
		String jsonString = "";
		try {
			ZipInputStream stream = new ZipInputStream(new FileInputStream(
					filePath));
			try {
				ZipEntry entry;
				while ((entry = stream.getNextEntry()) != null) {
					if (ANDROID_MANIFEST_FILE.equals(entry.getName())) {
						StringBuilder builder = new StringBuilder();
						jsonString = AndroidXMLParser.decompressXML(IOUtils
								                                            .toByteArray(stream));
					}
				}
			} finally {
				stream.close();
			}
			Document doc = loadXMLFromString(jsonString);
			doc.getDocumentElement().normalize();
			JSONObject obj = new JSONObject();
			obj.put(APP_VERSION,
			        doc.getDocumentElement().getAttribute(APK_VERSION_KEY));
			obj.put(APP_PACKAGE, doc.getDocumentElement().getAttribute(APK_PACKAGE_KEY));
			jsonString = obj.toJSONString();
		} catch (Exception e) {
			jsonString = "Exception occured while parsing the Manifest : " + e.getMessage();
			log.error(jsonString, e);
		}
		return jsonString;
	}

	public String readiOSManifestFile(String filePath, String name) {
		String plist = "";
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			File file = new File(filePath);
			ZipInputStream stream = new ZipInputStream(
					new FileInputStream(file));
			try {
				ZipEntry entry;
				while ((entry = stream.getNextEntry()) != null) {
					if (entry.getName().matches(INFO_PLIST_PATTERN)) {
						InputStream is = stream;

						int nRead;
						byte[] data = new byte[16384];

						while ((nRead = is.read(data, 0, data.length)) != -1) {
							buffer.write(data, 0, nRead);
						}

						buffer.flush();
						break;
					}
				}
				NSDictionary rootDict = (NSDictionary) BinaryPropertyListParser
						.parse(buffer.toByteArray());
				JSONObject obj = new JSONObject();
				obj.put(APP_VERSION, rootDict.objectForKey(IPA_BUNDLE_VERSION_KEY).toString());
				obj.put(APP_NAME, rootDict.objectForKey(IPA_BUNDLE_NAME_KEY).toString());
				obj.put(APP_PACKAGE,
				        rootDict.objectForKey(IPA_BUNDLE_IDENTIFIER_KEY).toString());
				plist = obj.toJSONString();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				stream.close();
			}
		} catch (Exception e) {
			plist = "Exception occured while reading the manifest : " + e.getMessage();
			log.error(plist, e);
		}
		return plist;
	}
}
