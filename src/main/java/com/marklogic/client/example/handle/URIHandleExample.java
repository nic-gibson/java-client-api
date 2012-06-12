/*
 * Copyright 2012 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.example.handle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.XMLDocumentManager;
import com.marklogic.client.example.cookbook.DocumentRead;
import com.marklogic.client.io.InputStreamHandle;

/**
 * URIHandleExample illustrates writing and reading content between a web service
 * and the database using the URIHandle example of a content handle extension.
 */
public class URIHandleExample {
	public static void main(String[] args) throws IOException {
		Properties props = loadProperties();

		// connection parameters for writer user
		String         host            = props.getProperty("example.host");
		int            port            = Integer.parseInt(props.getProperty("example.port"));
		String         writer_user     = props.getProperty("example.writer_user");
		String         writer_password = props.getProperty("example.writer_password");
		Authentication authType        = Authentication.valueOf(
				props.getProperty("example.authentication_type").toUpperCase()
				);

		run(host, port, writer_user, writer_password, authType);
	}

	public static void run(String host, int port, String user, String password, Authentication authType) {
		System.out.println("example: "+URIHandleExample.class.getName());

		String filename = "flipper.xml";

		// create the database client
		DatabaseClient dbClient = DatabaseClientFactory.newClient(
				host, port, user, password, authType
				);

		// create a manager for XML documents
		XMLDocumentManager docMgr = dbClient.newXMLDocumentManager();

		// create an identifier for the document
		String docId = "/example/"+filename;

		setUpExample(docMgr, docId, filename);

		// create the URI handle for a web service
		// for convenience, the example uses the database REST server as a web service
		URIHandle handle = new URIHandle(host, port, user, password, authType);

		// identify the base URI for all content at the web service
		// for convenience, the example uses the base URI for the database REST server
		String webserviceBase = "http://"+host+":"+port+"/v1/documents?uri=";

		// for digest authentication, either configure the HTTP client to buffer or
		// make an initial request that is repeatable; here the repeatable request
		// checks the existence of some content at the web service
		handle.check(webserviceBase+docId);

		// identify the target URI for the content written to the web service
		String webserviceId = "/webservice/"+filename;
		handle.set(webserviceBase+webserviceId);

		// read the content from the database and write to the web service target
		docMgr.read(docId, handle);

		System.out.println("Read "+docId+" from database and wrote "+webserviceId+" to web service");

		// create an identifier for content read from the web service
		String newId = "/dbcopy/"+filename;

		// read the content from the web service target and write to the database
		docMgr.write(newId, handle);

		System.out.println("Read "+webserviceId+" from web service and wrote "+docId+" to database");

		tearDownExample(docMgr, docId, webserviceId, newId);

		// release the client
		dbClient.release();
	}

	// set up by writing document content for the example to read
	public static void setUpExample(XMLDocumentManager docMgr, String docId, String filename) {
		InputStream docStream = DocumentRead.class.getClassLoader().getResourceAsStream(
				"data"+File.separator+filename);
		if (docStream == null)
			throw new RuntimeException("Could not read document example");

		InputStreamHandle handle = new InputStreamHandle(docStream);
		handle.set(docStream);

		docMgr.write(docId, handle);
	}

	// clean up by deleting the documents for the example
	public static void tearDownExample(XMLDocumentManager docMgr, String docId, String webserviceId, String newId) {
		docMgr.delete(docId);
		docMgr.delete(webserviceId);
		docMgr.delete(newId);
	}

	// get the configuration for the example
	public static Properties loadProperties() throws IOException {
		String propsName = "Example.properties";
		InputStream propsStream = JDOMHandleExample.class.getClassLoader().getResourceAsStream(propsName);
		if (propsStream == null)
			throw new RuntimeException("Could not read example properties");

		Properties props = new Properties();
		props.load(propsStream);

		return props;
	}
}