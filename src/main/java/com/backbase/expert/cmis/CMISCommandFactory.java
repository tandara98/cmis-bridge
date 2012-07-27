package com.backbase.expert.cmis;

import com.backbase.expert.cmis.model.command.Command;
import com.backbase.expert.cmis.model.command.Method;

/**
 * Build a CMIS Bridge {@link Command} object. The execute method can be called by the 'Invoker' class to get a 
 * {@link com.backbase.expert.cmis.model.CMISResponse} object.
 * 
 * The Command is create by looking at the method name.
 * 
 * @author BartH
 *
 */
public interface CMISCommandFactory {
	
	Command buildCommand( Method method ) throws Exception;

}
