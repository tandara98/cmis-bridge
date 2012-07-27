package com.backbase.expert.cmis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.RepositoryCapabilities;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

public class RepositoryInfoValidator {
	
	
	private static final String CMIS_ENDPOINT_TEST_SERVER = "http://localhost:8080/chemistry-opencmis-server-inmemory-war-0.6.0-SNAPSHOT/atom/";
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		// Create a SessionFactory and set up the SessionParameter map
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> parameters = new HashMap<String, String>();

        // connection settings - we are connecting to a public cmis repo,
        // using the AtomPUB binding
        parameters.put(SessionParameter.ATOMPUB_URL, CMIS_ENDPOINT_TEST_SERVER);
        parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        
        // Optional
        parameters.put(SessionParameter.USER, "dummyuser");
        parameters.put(SessionParameter.PASSWORD, "dummysecret");
        
        // create session with the first (and only) repository
        List<Repository> repositories = new ArrayList<Repository>();
        repositories = sessionFactory.getRepositories(parameters);
        Repository repository = repositories.get(0);
        parameters.put(SessionParameter.REPOSITORY_ID, repository.getId());
        Session session = sessionFactory.createSession(parameters);
		
		
		 System.out.println("Printing repository capabilities...");
	     final RepositoryInfo repInfo = session.getRepositoryInfo();
	     RepositoryCapabilities cap = repInfo.getCapabilities();
	     System.out.println("\nNavigation Capabilities");
	     System.out.println("-----------------------");
	     System.out.println("Get descendants supported: " + (cap.isGetDescendantsSupported()?"true":"false"));
	     System.out.println("Get folder tree supported: " + (cap.isGetFolderTreeSupported()?"true":"false"));
	     System.out.println("\nObject Capabilities");
	     System.out.println("-----------------------");
	     System.out.println("Content Stream: " + cap.getContentStreamUpdatesCapability().value());
	     System.out.println("Changes: " + cap.getChangesCapability().value());
	     System.out.println("Renditions: " + cap.getRenditionsCapability().value()); 
	     System.out.println("\nFiling Capabilities");
	     System.out.println("-----------------------");        
	     System.out.println("Multifiling supported: " + (cap.isMultifilingSupported()?"true":"false"));
	     System.out.println("Unfiling supported: " + (cap.isUnfilingSupported()?"true":"false"));
	     System.out.println("Version specific filing supported: " + (cap.isVersionSpecificFilingSupported()?"true":"false"));
	     System.out.println("\nVersioning Capabilities");
	     System.out.println("-----------------------");        
	     System.out.println("PWC searchable: " + (cap.isPwcSearchableSupported()?"true":"false"));
	     System.out.println("PWC updatable: " + (cap.isPwcUpdatableSupported()?"true":"false"));
	     System.out.println("All versions searchable: " + (cap.isAllVersionsSearchableSupported()?"true":"false"));
	     System.out.println("\nQuery Capabilities");
	     System.out.println("-----------------------");        
	     System.out.println("Query: " + cap.getQueryCapability().value());
	     System.out.println("Join: " + cap.getJoinCapability().value());
	     System.out.println("\nACL Capabilities");
	     System.out.println("-----------------------");        
	     System.out.println("ACL: " + cap.getAclCapability().value()); 
	     System.out.println("End of  repository capabilities");


	}

}
