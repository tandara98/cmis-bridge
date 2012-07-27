package com.backbase.expert.cmis.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backbase.expert.cmis.CMISHelper;
import com.backbase.expert.cmis.CMISService;
import com.backbase.expert.cmis.CMISCommandFactory;
import com.backbase.expert.cmis.client.exception.MethodNotSupportedException;
import com.backbase.expert.cmis.model.command.Command;
import com.backbase.expert.cmis.model.command.GetBinaryCommand;
import com.backbase.expert.cmis.model.command.GetByTagCommand;
import com.backbase.expert.cmis.model.command.GetMethodlessCommand;
import com.backbase.expert.cmis.model.command.GetRenditionsByTagCommand;
import com.backbase.expert.cmis.model.command.GetTraversalCommand;
import com.backbase.expert.cmis.model.command.Method;

/**
 * Build a CMIS Command object. The execute can be called by the 'Invoker' class to get a 
 * {@link com.backbase.expert.cmis.model.CMISResponse} object.
 * 
 * Methods names that are supported:
 * <ul>
 * 
 * <li>Method empty</li> 
 * <li>{@link com.backbase.expert.cmis.model.command.Method#METHOD_BINARY}</li>
 * <li>{@link com.backbase.expert.cmis.model.command.Method#METHOD_BYTAG}</li>
 * <li>{@link com.backbase.expert.cmis.model.command.Method#METHOD_RENDITIONS_BYTAG}</li>
 * <li>{@link com.backbase.expert.cmis.model.command.Method#METHOD_TRAVERSE}</li>
 * <li>{@link com.backbase.expert.cmis.model.command.Method#METHOD_BINARY}</li>
 * 
 * </ul>
 * 
 * @author BartH
 *
 */
public class CMISCommandFactoryImpl implements CMISCommandFactory {

	private static final Logger log = LoggerFactory.getLogger(CMISCommandFactoryImpl.class);
	
	private CMISService cmisService;
	private CMISHelper helper;
	
	public CMISCommandFactoryImpl(CMISService cmisService, CMISHelper helper){
		this.cmisService = cmisService;
		this.helper = helper;
	}
	
	public CMISService getService(){
		return this.cmisService;
	}
	
	public CMISHelper getHelper(){
		return this.helper;
	}
	
	public Command buildCommand( Method method ) throws Exception{
		
		Command command = null;

		log.info("Looking for path={}, method={}, args={}", new String[]{
				method.getPath(), 
				method.getName(), 
				(method.getArguments() == null) ? "" : method.getArguments().toString() } 
		);
	
			
		// If there is no method defined
		if (StringUtils.isEmpty(method.getName())) {
			command = new GetMethodlessCommand();
		}

		else if (method.getName().equals(Method.METHOD_BINARY)) {			
			command = new GetBinaryCommand();
		}
		
		else if (method.getName().equals(Method.METHOD_BYTAG)) {
			command = new GetByTagCommand();
		}
		
		else if (method.getName().equals(Method.METHOD_RENDITIONS_BYTAG)) {
			command = new GetRenditionsByTagCommand();
		}

		else if (method.getName().equals(Method.METHOD_TRAVERSE)) {
			command = new GetTraversalCommand();
		}

		else {
			
			// Try to find a custom method
			command = buildCustomCommand( method );
			
			if( command == null ){
			
				String error = String.format("Method '%s' is not supported",
						method.getName());
				
				throw new MethodNotSupportedException(error);
			}
		}
		
		// Set the command properties
		if(command != null){
			command.setService( cmisService );
			command.setHelper( helper );
			command.setMethod( method );
		}
		
		return command;
		
	}
	
	/**
	 * The main extention point
	 * 
	 * @param cmisPath
	 * @param method
	 * @return
	 * @throws Exception
	 */
	public Command buildCustomCommand( Method method ) throws Exception{
		return null;
	}
	

}
