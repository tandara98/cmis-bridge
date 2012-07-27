package com.backbase.expert.cmis.client;

import org.junit.Before;

import com.backbase.expert.cmis.CMISCommandFactory;
import com.backbase.expert.cmis.CMISHelper;
import com.backbase.expert.cmis.CMISService;
import com.backbase.expert.cmis.impl.CMISServiceImpl;
import com.backbase.expert.cmis.impl.alfresco.AlfrescoCMISCommandFactoryImpl;
import com.backbase.expert.cmis.impl.alfresco.AlfrescoHelperImpl;

public abstract class BaseCMISTest {
	
	private static final String CMIS_ENDPOINT_TEST_SERVER = "http://practices-01.dev.backbase.com:8080/cms-repository/service/cmis";
	
	protected static final String USER = "admin";
	protected static final String PASSWORD = "admin";

	private CMISService service;
	private CMISHelper helper;

	@Before
	public void setup(){
		service = new CMISServiceImpl( CMIS_ENDPOINT_TEST_SERVER );
		helper = new AlfrescoHelperImpl();
	}
	
	public CMISService getService(){
		return service;
	}
	
	public CMISHelper getHelper(){
		return helper;
	}
	
	public CMISCommandFactory getCommandFactory(){
//		return new CMISCommandFactoryImpl( getService(), getHelper() );
		return new AlfrescoCMISCommandFactoryImpl(getService(), getHelper() );
	}

}
