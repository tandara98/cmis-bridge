package com.backbase.expert.cmis;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;

public abstract class QueryTest {
	private static final String CMIS_ENDPOINT_TEST_SERVER = "http://localhost:8080/chemistry-opencmis-server-inmemory-war-0.6.0-SNAPSHOT/atom/";
	private Session session;

	private void getCmisClientSession() {
		// default factory implementation
		SessionFactory factory = SessionFactoryImpl.newInstance();
		Map<String, String> parameters = new HashMap<String, String>();
		// user credentials
		parameters.put(SessionParameter.USER, "dummyuser");
		parameters.put(SessionParameter.PASSWORD, "dummysecret");
		// connection settings
		parameters.put(SessionParameter.ATOMPUB_URL, CMIS_ENDPOINT_TEST_SERVER);
		parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
		// create session
		session = factory.getRepositories(parameters).get(0).createSession();
	}

	public void createTestArea() throws Exception {

		// creating a new folder
		Folder root = session.getRootFolder();
		Map<String, Object> folderProperties = new HashMap<String, Object>();
		folderProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
		folderProperties.put(PropertyIds.NAME, "testdata");

		Folder newFolder = root.createFolder(folderProperties);
		// create a new content in the folder
		String name = "testdata1.txt";
		// properties
		// (minimal set: name and object type id)
		Map<String, Object> contentProperties = new HashMap<String, Object>();
		contentProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
		contentProperties.put(PropertyIds.NAME, name);

		// content
		byte[] content = "CMIS Testdata One".getBytes();
		InputStream stream = new ByteArrayInputStream(content);
		ContentStream contentStream = new ContentStreamImpl(name, new BigInteger(content), "text/plain", stream);

		// create a major version
		Document newContent1 = newFolder.createDocument(contentProperties, contentStream, null);
		System.out.println("Document created: " + newContent1.getId());
	}

	private void doQuery() {
		ItemIterable<QueryResult> results = session
				.query("SELECT * FROM cmis:folder WHERE cmis:name='testdata'", false);
		for (QueryResult result : results) {
			String id = result.getPropertyValueById(PropertyIds.OBJECT_ID);
			System.out.println("doQuery() found id: " + id);
		}
	}

}