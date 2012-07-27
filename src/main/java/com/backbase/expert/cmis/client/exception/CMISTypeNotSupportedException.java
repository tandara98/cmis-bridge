package com.backbase.expert.cmis.client.exception;

public class CMISTypeNotSupportedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1834549251400423399L;
	
	public CMISTypeNotSupportedException(String error)
    {
    	super( error );
    }
	
	

}
