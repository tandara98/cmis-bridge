package com.backbase.expert.cmis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;

/**
 * <p>To setup a embedded CMIS server see: 
 * 	<a href="http://chemistry.apache.org/java/developing/repositories/dev-repositories-inmemory.html">
 * 		http://chemistry.apache.org/java/developing/repositories/dev-repositories-inmemory.html
 *	</a>
 * </p>
 * <p>
 * For more examples see 
 * <a href="https://svn.apache.org/repos/asf/chemistry/opencmis/trunk/chemistry-opencmis-samples/">
 * 	https://svn.apache.org/repos/asf/chemistry/opencmis/trunk/chemistry-opencmis-samples/
 * </a>
 * </p>
 * 
 * @author BartH
 *
 */
public abstract class MainTest {
	
	private static final String CMIS_ENDPOINT_TEST_SERVER = "http://localhost:8080/chemistry-opencmis-server-inmemory-war-0.6.0-SNAPSHOT/atom/";
	
	
	
	/**
	 * 
	 * TODO: Node the OpenCMIS session is not thread save. This is why alfresco uses ThreadLocal.
	 * 
	 * @param args
	 */
    public static void main(String args[]) {
        
        System.out.println(MainTest.class.getName() + " started");

        // Create a SessionFactory and set up the SessionParameter map
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> parameters = new HashMap<String, String>();

        // connection settings - we are connecting to a public cmis repo,
        // using the AtomPUB binding
        parameters.put(SessionParameter.ATOMPUB_URL, CMIS_ENDPOINT_TEST_SERVER);
        parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        parameters.put(SessionParameter.USER, "dummyuser");
        parameters.put(SessionParameter.PASSWORD, "dummysecret");

        // find all the repositories at this URL - there should only be one.
        List<Repository> repositories = new ArrayList<Repository>();
        repositories = sessionFactory.getRepositories(parameters);
        for (Repository r : repositories) {
            System.out.println("Found repository: " + r.getName());
        }

        // create session with the first (and only) repository
        Repository repository = repositories.get(0);
        parameters.put(SessionParameter.REPOSITORY_ID, repository.getId());
        Session session = sessionFactory.createSession(parameters);

        System.out.println("Got a connection to repository: " + repository.getName() + ", with id: "
                + repository.getId());

        // Get everything in the root folder and print the names of the objects
        Folder root = session.getRootFolder();
        ItemIterable<CmisObject> children = root.getChildren();
        System.out.println("Found the following objects in the root folder:-");
        for (CmisObject o : children) {
            System.out.println(o.getName() + "\t"+ o.getCreationDate().getTime() + "\t"+ o.getId());           
        }
        
        
        System.out.println(MainTest.class.getName() + " ended");
    }
}
