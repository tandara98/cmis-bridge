package com.backbase.expert.cmis.model.command;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backbase.expert.cmis.CMISHelper;
import com.backbase.expert.cmis.client.exception.CMISTypeNotSupportedException;
import com.backbase.expert.cmis.client.exception.PathNotFoundException;
import com.backbase.expert.cmis.impl.CMISHelperImpl;
import com.backbase.expert.cmis.model.CMISResponse;

public class GetMethodlessCommand extends CMISCommand {
	
	private static final Logger log = LoggerFactory.getLogger(GetMethodlessCommand.class);
	
	@Override
	public CMISResponse execute() throws Exception {
		
		Method method = this.getMethod();
		
		CMISResponse response = null;
		Command command = null;
		
		CMISHelper helper = this.getHelper();
		CmisObject object = helper.getByPath( method.getPath() );

		if (object != null) {

			// Document
			if (CMISHelperImpl.isDocument(object)) {
				command = new GetDocumentCommand();			
			}

			// Folder
			else if (CMISHelperImpl.isFolder(object)) {
				command = new GetFolderCommand();
			} 
			else {
				log.error("Type {} is not supported', method={}", object
						.getBaseTypeId().value());
				
				throw new CMISTypeNotSupportedException("Type '"
						+ object.getBaseTypeId().value()
						+ "' is not supported");
			}
			
			if( command != null ){
				command.setService(this.getService());
				command.setHelper(this.getHelper());
				command.setObject( object );
				command.setMethod(method);
				response = command.execute();
			}
			
		} 
		else {
			throw new PathNotFoundException(
					"CMIS did not find any objects on this path '"
							+ method.getPath() + "'");
		}
		
		return response;
	}

}
