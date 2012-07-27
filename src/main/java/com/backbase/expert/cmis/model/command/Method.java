package com.backbase.expert.cmis.model.command;

import com.backbase.expert.cmis.CMISCommandFactory;

/**
 * This is a wrapper class to create REST like interfaces. Eventually, the method will get transformed to a internal {@link Command}
 * by a {@link CMISCommandFactory}.
 * 
 * @author BartH
 *
 */
public class Method {
	
	public static final String PARAM_CMIS_METHOD = "cmis_method";	
	
	public static final String METHOD_TRAVERSE 			= "traverse";
	public static final String METHOD_BYTAG 			= "bytag";
	public static final String METHOD_RENDITIONS_BYTAG 	= "renbytag";
	public static final String METHOD_BINARY 			= "binary";
	
	private MethodArguments arguments;
	private String name;
	
	private String username;
	private String password;
	
	public Method(){}
	
	public Method(String name, MethodArguments arguments){
		this.name = name;
		this.arguments = arguments;
	}
	
	public MethodArguments getArguments() {
		return arguments;
	}
	public void setArguments(MethodArguments arguments) {
		this.arguments = arguments;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId(){
		if(this.arguments != null ){
			return this.arguments.getFirst( MethodArguments.PARAM_CMIS_ID );
		}
		return null;
	}
	
	public String getPath(){
		if(this.arguments != null ){
			return this.arguments.getFirst( MethodArguments.PARAM_CMIS_PATH );
		}
		return null;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}


}
