package com.backbase.expert.cmis.model.command.alfresco;

import static org.junit.Assert.*;

import org.junit.Test;

import com.backbase.expert.cmis.CMISCommandFactory;
import com.backbase.expert.cmis.client.BaseCMISTest;
import com.backbase.expert.cmis.model.CMISResponse;
import com.backbase.expert.cmis.model.command.Command;
import com.backbase.expert.cmis.model.command.Method;
import com.backbase.expert.cmis.model.command.MethodArguments;

public class TestGetByAspects extends BaseCMISTest{

	@Test
	public void test() {
		try{
			CMISCommandFactory factory = getCommandFactory();
			
			Method method = new Method();
			method.setUsername(BaseCMISTest.USER);
			method.setPassword(BaseCMISTest.PASSWORD);
			method.setName("aspects");	
			
			MethodArguments arguments = new MethodArguments();
			arguments.add(MethodArguments.PARAM_CMIS_PATH, "/Sites/extranet/documentLibrary/Portal/Editorial/root/en/demos/");
			arguments.add("ext:browsers", new String[]{"IE6", "IE7"} );
//			arguments.add("ext:features", "IE7");
			arguments.add("limit", "5");
			arguments.add("offset", "0");
			
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
