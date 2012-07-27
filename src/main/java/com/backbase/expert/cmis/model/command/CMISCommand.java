package com.backbase.expert.cmis.model.command;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backbase.expert.cmis.CMISHelper;
import com.backbase.expert.cmis.CMISService;
import com.backbase.expert.cmis.model.CMISResponse;

public abstract class CMISCommand implements Command{
	
	private static final Logger log = LoggerFactory.getLogger(CMISCommand.class);
	
	private CMISService service;
	private CmisObject object;
	private CMISHelper helper;
	private Method method;
	
	public CMISCommand(){
	}
	
	public CMISCommand( final CMISService service ){
		this( service, null );
	}
	
	public CMISCommand( final CMISService service, final CMISHelper helper ){
		this.service = service;
		
		setHelper( helper );			
	}
	
	public void setHelper( final CMISHelper helper ){
		this.helper = helper;
	}
	
	public CMISHelper getHelper(){
		Method method = this.getMethod();
		Session session = this.getSession(method);
		
		this.helper.setSession(session);
		
		return this.helper;
	}
	
	public CMISService getService() {
		return service;
	}

	public void setService(CMISService service) {
		this.service = service;
	}

	public CmisObject getObject() {
		return object;
	}

	public void setObject(CmisObject object) {
		this.object = object;
	}
	
	public Session getSession( Method method ) {
		try {
			return this.getService().getSession(method.getUsername(), method.getPassword() );
		} 
		catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public abstract CMISResponse execute() throws Exception;
	
}
