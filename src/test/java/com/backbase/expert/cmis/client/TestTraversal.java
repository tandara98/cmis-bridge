package com.backbase.expert.cmis.client;

import static org.junit.Assert.*;

import org.junit.Test;

import com.backbase.expert.cmis.CMISCommandFactory;
import com.backbase.expert.cmis.model.CMISResponse;
import com.backbase.expert.cmis.model.command.Command;
import com.backbase.expert.cmis.model.command.Method;
import com.backbase.expert.cmis.model.command.MethodArguments;

public class TestTraversal extends BaseCMISTest{
	

	@Test
	public void test() {
		try{
			String path = "/Sites/extranet/documentLibrary/Portal/Editorial/root/en/Demos";
												
			CMISCommandFactory factory = getCommandFactory();
			
			MethodArguments args = new MethodArguments();
			args.add(MethodArguments.PARAM_CMIS_PATH, path);
			args.add("limit", "2");
			
			Method m = new Method();
			m.setName(Method.METHOD_TRAVERSE);
			m.setUsername(BaseCMISTest.USER);
			m.setPassword(BaseCMISTest.PASSWORD);
			m.setArguments(args);
			
			Command c = factory.buildCommand(m);
			CMISResponse response = c.execute();

			System.out.println("Traverse:" +  response.getBody() );
			
			assertNotNull( response );

		}
		catch( Exception e){
			// Ignore, no connection to CMIS Repository
			e.printStackTrace();
		}
	}

}
