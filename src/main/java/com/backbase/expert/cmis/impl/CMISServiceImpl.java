package com.backbase.expert.cmis.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backbase.expert.cmis.CMISService;
import com.backbase.expert.cmis.client.exception.RepositoryUnavailableException;

/**
 * This class is the basic example of abstracting the CMIS Interfaces. Only some
 * basic operations are allowed.
 * 
 * @author BartH
 * 
 */
public class CMISServiceImpl implements CMISService {

	private static final Logger log = LoggerFactory
			.getLogger(CMISServiceImpl.class);

	private String serviceUrl;
	
	private String repositoryId;
	
	private SessionFactory sessionFactory;
	
	public CMISServiceImpl( String serviceUrl ) {
		this(serviceUrl, null );
	}
	
	public CMISServiceImpl( String serviceUrl, String repositoryId ) {
		// Create session factory
		this.serviceUrl = serviceUrl;
		this.repositoryId = repositoryId;
		this.sessionFactory = SessionFactoryImpl.newInstance();
	}
	
	public Session getSession(String user, String password) throws Exception {
		
		log.debug("getSession()");
		
		// Create the paramaters to connect with
		Map<String, String> parameters = initializeParameters( serviceUrl, user, password );
		
		Session session = null;
		
		// Try to find the default repository id if none is specified 
		if( this.repositoryId == null ){
			Repository repository = findFirstRepository( parameters );
			session = repository.createSession();
		}
		else{
			// Add the repository parameter
			log.debug("Connection to {}", this.repositoryId);
			parameters.put( SessionParameter.REPOSITORY_ID, this.repositoryId );

			// Create the session
			session = sessionFactory.createSession( parameters );
		}
		
		return session;
	}
	
	/**
	 * 
	 * @param serviceUrl The CMIS endpoint
	 * @param user The CMIS username
	 * @param password The CMIS Password
	 * @return A list of initial parameters specific for the CMIS ATOM protocol
	 */
	private Map<String, String> initializeParameters(String serviceUrl, String user,String password) {
		
		Map<String, String>  parameters= new HashMap<String, String>();

		// user credentials
		parameters.put(SessionParameter.USER, user);
		parameters.put(SessionParameter.PASSWORD, password);

		// connection settings
		parameters.put(SessionParameter.ATOMPUB_URL, serviceUrl);
		parameters.put(SessionParameter.BINDING_TYPE,
				BindingType.ATOMPUB.value());
		parameters.put(SessionParameter.READ_TIMEOUT, "15000");
		parameters.put(SessionParameter.LOCALE_ISO639_LANGUAGE, "en-us");
		
		return parameters;

	}

	/**
	 * Try to find the default (the first occurence) repository id by listing all reposotories and
	 * get the first one if there are any.
	 * 
	 * @param parameters List of parameter to connect to the CMIS endpoint
	 * @return The id of the first repository or null if there aren't any.
	 * @throws Exception In case of a connection problem
	 */
	private Repository findFirstRepository( final Map<String, String> parameters ) throws Exception{
		
		Repository repository  = null;
		try{
			List<Repository> repositories = sessionFactory.getRepositories(parameters);
			if(repositories != null && repositories.size() > 0){
				repository = repositories.get(0);
			}
		}
		catch(CmisConnectionException e) { 
		    // The server is unreachable
			throw new RepositoryUnavailableException("The server is unreachable",  e );
		}
		catch(CmisRuntimeException e) {
		    // The user/password have probably been rejected by the server.
			throw new RepositoryUnavailableException("The user/password have probably been rejected by the server.",  e );
		}
		catch( Exception e ){
			// Unknow exceptio, try to print the session parameters
			List<String> parametersNormalized = new ArrayList<String>(parameters.size());
			for(String key : parameters.keySet() ){
				parametersNormalized.add(  String.format("\n\t-%s=%s", key, parameters.get(key)) );
			}
			throw new RepositoryUnavailableException("Could not find first repository. Used parameters: "+ StringUtils.join(parametersNormalized, ";\n"),  e );
		}
		
		return repository;
	}


}
