package com.backbase.expert.cmis.model.command;

import org.apache.chemistry.opencmis.client.api.CmisObject;

import com.backbase.expert.cmis.CMISCommandFactory;
import com.backbase.expert.cmis.CMISHelper;
import com.backbase.expert.cmis.CMISService;
import com.backbase.expert.cmis.model.CMISResponse;

/**
 * <p>The CMIS command class abstracts the client CMIS implementation. Currently it wraps the apache chemistry client, but other clients can be wrapped as well in the future. 
 * </p>
 * <p>
 * This command class follows the 'Command pattern'. 
 * See <a href="http://en.wikipedia.org/wiki/Command_pattern">wikipedia</a> for more information about the command pattern.
 * </p>
 * <p>
 * A command needs a {@link CMISService}, {@link CMISHelper} and the {@link Method}. A {@link CMISCommandFactory} can be used to create a command.
 * </p>
 * 
 * @author BartH
 *
 */
public interface Command {
	
	/**
	 * Execute the command.  
	 * @return {@link CMISResponse}
	 * @throws Exception
	 */
	CMISResponse execute() throws Exception;
	
	CMISService getService();
	void setService(CMISService service);
	
	void setHelper( CMISHelper helper );
	CMISHelper getHelper();
	
	Method getMethod();
	void setMethod(Method method);
	
	CmisObject getObject();
	void setObject(CmisObject object);
	
}
