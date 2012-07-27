package com.backbase.expert.cmis.model.command;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MethodArguments {
	
	public static final String UTF_8 = "UTF-8";
	public static final String PARAM_CMIS_PATH = "cmis_path";
	public static final String PARAM_CMIS_ID = "cmis_id";
	public static final String PARAM_CMIS_LIMIT = "limit";
	public static final String PARAM_CMIS_OFFSET = "offset";
	public static final String PARAM_CMIS_KIND = "kind";
	public static final String PARAM_CMIS_TAGS = "tags";
	
	private Map<String,String[]> arguments;
	
	public MethodArguments(){
		this.arguments = new HashMap<String, String[]>();
	}
	
	/**
	 * Copy map to internal argument map
	 * @param argumentMap
	 */
	public MethodArguments( Map<String,String[]> argumentMap ){
		this();
		
		// Clone the hashmap
		Set<String> keys = argumentMap.keySet();
		for (String key : keys) {

			String[] values = argumentMap.get(key );
			String[] decodedValues = new String[values.length];
			for (int i=0; i < values.length; i++) {
				try {
					String decodeValue = URLDecoder.decode( values[i], UTF_8);
					decodedValues[i] = decodeValue;
				} 
				catch (UnsupportedEncodingException e) {
					// Fall back to original value
					decodedValues[i] = values[i];
				}
			}
			
			this.arguments.put( key , decodedValues );
		}
	}

	public String[] get(String name){
		return arguments.get(name);
	}
	
	public Map<String,String[]> toMap(){
		return arguments;
	}
	
	public String[] add(String name, String value ){
		return arguments.put(name, new String[]{value} );
	}
	
	public String[] add(String name, String[] values ){
		return arguments.put(name, values );
	}
	
	public String getFirst(String name){
		String[] values =  arguments.get(name);
		if( values != null && values.length > 0 ){
			String value = values[0];
			return value;
		}
		return null;		
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (String key : arguments.keySet()) {
			sb.append( key +"="+ getFirst(key) + "," );
		}
		sb.append("]");
		return sb.toString();
	}

}
