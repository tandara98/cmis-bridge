package com.backbase.expert.cmis.client;

import static org.junit.Assert.*;

import org.junit.Test;

import com.backbase.expert.cmis.CMISCommandFactory;
import com.backbase.expert.cmis.model.CMISResponse;
import com.backbase.expert.cmis.model.command.Command;
import com.backbase.expert.cmis.model.command.Method;
import com.backbase.expert.cmis.model.command.MethodArguments;

public class TestGetFolder extends BaseCMISTest{
	

	@Test
	public void test() {
		try{
			CMISCommandFactory factory = getCommandFactory();
			
			Method method = new Method();
			method.setUsername(BaseCMISTest.USER);
			method.setPassword(BaseCMISTest.PASSWORD);
			
			MethodArguments arguments = new MethodArguments();
			arguments.add(MethodArguments.PARAM_CMIS_PATH, "/Sites/extranet/documentLibrary/Portal/Editorial/root/en/demos/portal");
			
			method.setArguments(arguments);
			
			Command command = factory.buildCommand( method) ;
			CMISResponse r = command.execute();		
			
			System.out.println("body:" + r.getBody() );

			assertNotNull( r.getBody());
		}
		catch( Exception e){
			// Ignore, no connection to CMIS Repository
			e.printStackTrace();
		}
	}
	
	@Test
	public void test2() {
		try{
			CMISCommandFactory factory = getCommandFactory();
			
			Method method = new Method();
			method.setUsername(BaseCMISTest.USER);
			method.setPassword(BaseCMISTest.PASSWORD);
			
			MethodArguments arguments = new MethodArguments();
			arguments.add(MethodArguments.PARAM_CMIS_PATH, "/Sites/extranet/documentLibrary/Portal/Editorial/root/en/demos/portal");
			arguments.add(MethodArguments.PARAM_CMIS_LIMIT, "1");
			arguments.add(MethodArguments.PARAM_CMIS_OFFSET, "0");
			
			method.setArguments(arguments);
			
			Command command = factory.buildCommand( method) ;
			CMISResponse r = command.execute();		
			
			System.out.println("body:" + r.getBody() );

			assertNotNull( r.getBody());
		}
		catch( Exception e){
			// Ignore, no connection to CMIS Repository
			e.printStackTrace();
		}
	}

}
