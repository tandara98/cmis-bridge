package com.backbase.expert.cmis.model;

/**
 * To wrap the CMIS objects.
 * 
 * @author BartH
 *
 */
public interface CMISResponse {
	
	String getBody();
	void setBody(String body);
	
	byte[] getBinaryBody();
	void setBinaryBody(byte[] body);
	
	boolean isBinaryMode();
	void setBinaryMode(boolean isBinaryMode);
	
	void setStreamLength( long length );
	long getStreamLength();
	
	String getMimeType();
	void setMimeType(String mimeType);

}
