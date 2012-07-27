package com.backbase.expert.cmis;

import org.apache.chemistry.opencmis.client.api.Session;

/**
 * The CMIS Service is resposible for creating the CMIS 'session' object.
 * 
 * @author BartH
 *
 */
public interface CMISService{
	
	Session getSession(String user, String password) throws Exception;
	
}
