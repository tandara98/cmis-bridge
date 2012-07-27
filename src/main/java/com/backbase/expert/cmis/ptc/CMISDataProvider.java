package com.backbase.expert.cmis.ptc;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backbase.expert.cmis.CMISCommandFactory;
import com.backbase.expert.cmis.model.CMISResponse;
import com.backbase.expert.cmis.model.command.Command;
import com.backbase.expert.cmis.model.command.Method;
import com.backbase.expert.cmis.model.command.MethodArguments;
import com.backbase.expert.commons.ptc.dataprovider.BaseDataProvider;
import com.backbase.portal.ptc.config.element.DataProviderConfig;
import com.backbase.portal.ptc.context.MutableProxyContext;
import com.backbase.portal.ptc.exception.ProxyResourceRetrieveException;
import com.backbase.portal.ptc.request.ProxyRequest;
import com.backbase.portal.ptc.response.MutableProxyResponse;

/**
 * <p>
 * The CMIS dataprovider wraps the CMIS bridge. The dataprovider constructs a {@link Method} on every request and will return the response.
 * This response can be a xml or html file or a binary file. 
 * <h2>Method</h2>
 * <p>A {@link Method} will be created by the dataprovider on every request. A 'method' object is a wrapper class to create the actual CMIS Brigde command.</p>
 * <p>  
 * The method objects needs the name, user credentials for the 
 * CMIS connection and opionally some parameters.
 * Eventually, the method will be transformed into a 'command' by the {@link CMISCommandFactory} and executed.
 * </p>
 * 
 * <p>
 * Use this dataprovider as follows:
 * </p>
 * 
 * <pre>
 * <data-provider id="cmisDataProvider" class="com.backbase.expert.cmis.ptc.CMISDataProvider">
 *   	<param name="username" value="[CMIS_USERNAME]" />
 *   	<param name="password" value="[CMIS_PASSWORD]" />
 *   	
 *   	<!-- Optional parameters -->
 *   	<param name="jndiEnabled" value="true" /> <!-- If set to True, the usr/pwd settings will be overritten by JNDI settings -->
 *   	<param name="customErrorMessage" value="[Oeps an error occured]" />
 * </data-provider>
 * </pre>
 * 
 * <h3>JNDI</h3>
 * <p>You can use JNDI settings to set the username and password of the dataprovider</p>
 * <p> Used <code>cmis/username</code> for the username and <code>cmis/password</code> for the password. </p>
 * <p>Example in jetty</p>
 * <pre>
 * <New id="jndiCMISUsername" class="org.eclipse.jetty.plus.jndi.EnvEntry">
 *       <Arg>cmis/username</Arg>
 *       <Arg type="java.lang.String">cmis/username</Arg>
 *       <Arg type="string">[USERNAME]</Arg>
 * </New>
 * </pre>
 * 
 * @author BartH
 *
 */
public class CMISDataProvider extends BaseDataProvider  {

	private static final Logger log = LoggerFactory.getLogger(CMISDataProvider.class);

	protected static final String UTF_8 = "UTF-8";
	protected static final String TEXT_PLAIN = "text/plain";
	protected static final String TEXT_HTML = "text/html";
	protected static final String TEXT_XML = "text/xml";

	protected static final String PARAM_CMIS_METHOD = "cmis_method";
	protected static final String PARAM_CMIS_PATH = "cmis_path";

	private static final String CMIS_COMMAND_FACTORY_BEAN_NAME = "cmisCommandFactory";
	
	private CMISCommandFactory cmisCommandFactory = null;
	
	private String username,password,customErrorMessage;
	private Boolean isJndiEnabled;

	// Set cache expiration to 1 hour...
	private static final int DEFAULT_BINARY_CACHE_EXPIRATION = 3600;

	/**
	 * Execute the request to the CMIS Repository and return
	 */
	public MutableProxyResponse executeRequest(DataProviderConfig dataProviderConfig,
			MutableProxyContext mutableProxyContext, ProxyRequest proxyRequest) throws UnsupportedEncodingException {
		
		initSettings( dataProviderConfig );
		
		MutableProxyResponse mutableProxyResponse = new MutableProxyResponse();
		mutableProxyResponse.setStatusCode(200);

		logRequestInformation(proxyRequest);

		try {

			// Build the command to execute based on the CMIS parameters
			Method method = buildMethod(proxyRequest);

			if (cmisCommandFactory == null) {
				cmisCommandFactory = this.loadBean(CMIS_COMMAND_FACTORY_BEAN_NAME, CMISCommandFactory.class, mutableProxyContext);
			}

			Command command = cmisCommandFactory.buildCommand(method);

			// Get the response
			CMISResponse response = command.execute();

			log.info("IsBinaryMode={}", response.isBinaryMode());

			// Set body and content type of the CMIS response
			if (response.isBinaryMode()) {
				setBinaryBody(mutableProxyResponse, response);
			} else {
				setStringBody(mutableProxyResponse, response);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			setErrorBody(mutableProxyResponse, e);
		}

		return mutableProxyResponse;
	}

	private void setBinaryBody(MutableProxyResponse mutableProxyResponse, CMISResponse response) {
		Calendar expireDate = GregorianCalendar.getInstance();
		expireDate.add(Calendar.SECOND, DEFAULT_BINARY_CACHE_EXPIRATION);

		mutableProxyResponse.addHeaderValue("Cache-Control", "max-age=" + DEFAULT_BINARY_CACHE_EXPIRATION);
		mutableProxyResponse.addHeaderValue("Expires", "" + expireDate.getTime().getTime());
		mutableProxyResponse.addHeaderValue("Content-Length", "" + response.getStreamLength());
		mutableProxyResponse.addHeaderValue("XBinary", "true");

		mutableProxyResponse.addContentTypeHeader(response.getMimeType(), UTF_8);

		mutableProxyResponse.setBodyBytes(response.getBinaryBody());
	}

	private void setStringBody(MutableProxyResponse mutableProxyResponse, CMISResponse response) {
		mutableProxyResponse.addContentTypeHeader(response.getMimeType(), UTF_8);
		mutableProxyResponse.setBody(response.getBody());
	}

	private void setErrorBody(MutableProxyResponse mutableProxyResponse, Throwable e) {
		// Set body and content type of the error
		if( customErrorMessage != null ){
			mutableProxyResponse.setBody( customErrorMessage );
		}
		else{
			mutableProxyResponse.setBody("<pre>Error:" + e.getMessage() + "</pre>");
		}
		
		mutableProxyResponse.addContentTypeHeader(TEXT_HTML, UTF_8);
	}

	/**
	 * Log the request parameters
	 * 
	 * @param proxyRequest
	 */
	private void logRequestInformation(final ProxyRequest proxyRequest) {
		if(log.isDebugEnabled()){
			@SuppressWarnings("unchecked")
			Map<String, String[]> parameters = proxyRequest.getRequest().getParameterMap();
			log.debug("URI requested {}", proxyRequest.getRequest().getRequestURI());
			for (String key : parameters.keySet()) {
				log.debug("\tParam " + key + "=" + parameters.get(key)[0]);
			}
		}
	}

	/**
	 * Initialize the CMIS Bridge Parameters: method, method argument and the
	 * cmis path
	 * 
	 * @param proxyRequest
	 * @throws ProxyResourceRetrieveException
	 */
	@SuppressWarnings("unchecked")
	private Method buildMethod(ProxyRequest proxyRequest) throws ProxyResourceRetrieveException {

		Method method = null;

		HttpServletRequest request = proxyRequest.getRequest();
		Map<String, String[]> parameters = request.getParameterMap();
		try {
			// Create a new method
			method = new Method();
			
			// Set credentials for the method
			method.setUsername( username );
			method.setPassword( password );
			
			// Get the parameters for the method
			String[] cmisMethodParameter = parameters.get(PARAM_CMIS_METHOD);
			log.debug("cmisMethodParameter=" + cmisMethodParameter);
			
			// Method name can be empty so check it
			if (!ArrayUtils.isEmpty(cmisMethodParameter)) {
				String cmisMethodName = URLDecoder.decode(cmisMethodParameter[0], UTF_8);
				method.setName(cmisMethodName);
			}

			// Set the argument of the method
			method.setArguments(new MethodArguments(parameters));
			
		} 
		catch (Exception e) {
			throw new ProxyResourceRetrieveException("Cannot find parameter", e);
		}
		return method;
	}
	
	/**
	 * Initialize the dataprovider settings
	 * 
	 * @param config The dataprovider configuration.
	 */
	private void initSettings(DataProviderConfig config) {
		
		// If JNDI is enabled, try to get the usr/pwd settings from the application server
		String jndiEnabled = config.getParamValue("jndiEnabled");
		if(StringUtils.isNotBlank(jndiEnabled)){
			isJndiEnabled = Boolean.parseBoolean(jndiEnabled);
		}
				
		if( username == null ){
			if( isJndiEnabled ){
				username = (String) localJNDILookup( "cmis/username" );
			}
			else{
				username = config.getParamValue("username");
			}
		}
		if( password == null ){
			if( isJndiEnabled ){
				password = (String) localJNDILookup( "cmis/password" );
			}
			else{
				password = config.getParamValue("password");
			}
		}
		
		if( customErrorMessage == null ){
			customErrorMessage = config.getParamValue("customErrorMessage");
		}
		
	}
}
