package com.backbase.expert.cmis.impl.alfresco;

import com.backbase.expert.cmis.CMISHelper;
import com.backbase.expert.cmis.CMISService;
import com.backbase.expert.cmis.impl.CMISCommandFactoryImpl;
import com.backbase.expert.cmis.model.command.Command;
import com.backbase.expert.cmis.model.command.Method;
import com.backbase.expert.cmis.model.command.alfresco.GetByAspects;

public class AlfrescoCMISCommandFactoryImpl extends CMISCommandFactoryImpl{

	public AlfrescoCMISCommandFactoryImpl(CMISService service, CMISHelper helper) {
		super( service, helper );
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
		Command command = null;
			
		// If there is no method defined
		if (method.getName().equals("aspects")) {
			command = new GetByAspects();
		}
		
		if( command != null ){
			command.setHelper( this.getHelper());
			command.setService(this.getService());
		}
		
		
		return command;
	}

}
