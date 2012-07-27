package com.backbase.expert.cmis.model.impl;

import com.backbase.expert.cmis.model.CMISResponse;

public class CMISResponseImpl implements CMISResponse {

	protected static final String EMPTY = "";
	protected static final String TEXT_PLAIN = "text/plain";
	
	private String body;
	private byte[] binaryBody;
	private boolean isBinaryMode;
	private long streamLength;
	private String mimeType;
	
	public CMISResponseImpl(){
		this.body = EMPTY;
		this.mimeType = TEXT_PLAIN;
		this.isBinaryMode = false;
	}		
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	public byte[] getBinaryBody(){
		return binaryBody;
	}
	
	public void setBinaryBody(byte[] body){
		this.binaryBody = body;
	}
	
	public boolean isBinaryMode() {
		return isBinaryMode;
	}
	public void setBinaryMode(boolean isBinaryMode) {
		this.isBinaryMode = isBinaryMode;
	}
	
	public void setStreamLength( long length ){
		this.streamLength = length;
	}
	
	public long getStreamLength(){
		return streamLength;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

}
