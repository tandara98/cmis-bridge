package com.backbase.expert.cmis.client;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.backbase.expert.cmis.CMISCommandFactory;
import com.backbase.expert.cmis.model.CMISResponse;
import com.backbase.expert.cmis.model.command.Command;
import com.backbase.expert.cmis.model.command.Method;
import com.backbase.expert.cmis.model.command.MethodArguments;

public class TestTop extends BaseCMISTest{
	

	@Test
	public void test() {
		try{
			
			CMISCommandFactory factory = getCommandFactory();
			
			MethodArguments args = new MethodArguments();
			args.add(MethodArguments.PARAM_CMIS_PATH, "/Data Dictionary/");
			args.add(MethodArguments.PARAM_CMIS_LIMIT, "5");
			args.add(MethodArguments.PARAM_CMIS_TAGS, "top");
			
			Method m = new Method();
			m.setName(Method.METHOD_BYTAG);
			m.setUsername(BaseCMISTest.USER);
			m.setPassword(BaseCMISTest.PASSWORD);
			m.setArguments(args);
			
			Command c = factory.buildCommand(m);
			CMISResponse response = c.execute();
			
			
			System.out.println( response.getBody() );
		}
		catch( Exception e){
			// Ignore, no connection to CMIS Repository
			e.printStackTrace();
		}
	}
	
	@Test
	public void testTags() {
		try{
			String[] tags = "ptc,ssr".split(",");
			
			CMISCommandFactory factory = getCommandFactory();
			
			MethodArguments args = new MethodArguments();
			args.add(MethodArguments.PARAM_CMIS_PATH, "/Sites/extranet/documentLibrary/Portal/Editorial/root/en/");
			args.add(MethodArguments.PARAM_CMIS_LIMIT, "10");
			args.add(MethodArguments.PARAM_CMIS_TAGS,  StringUtils.join(tags, "|"));
			
			Method m = new Method();
			m.setName(Method.METHOD_BYTAG);
			m.setUsername(BaseCMISTest.USER);
			m.setPassword(BaseCMISTest.PASSWORD);
			m.setArguments(args);
			
			Command c = factory.buildCommand(m);
			CMISResponse response = c.execute();
			
			System.out.println( response.getBody() );

		}
		catch( Exception e){
			// Ignore, no connection to CMIS Repository
			e.printStackTrace();
		}
	}
	
}
