package com.backbase.expert.cmis.model.command;

import java.io.InputStream;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;

import com.backbase.expert.cmis.CMISHelper;
import com.backbase.expert.cmis.model.CMISResponse;
import com.backbase.expert.cmis.model.impl.CMISResponseImpl;

/**
 * 
 * @author BartH
 *
 */
public class GetBinaryCommand extends CMISCommand {
	
	@Override
	public CMISResponse execute() throws Exception{

		CMISResponse response = new CMISResponseImpl();
		
		CMISHelper helper = this.getHelper();
		
		String path = this.getMethod().getPath();
		CmisObject object = null;
		if( path != null ){
			object = helper.getByPath( path );
		}
		else{
			String id = this.getMethod().getId();
			object = helper.getById(id); 
		}

		if (object != null) {
			Document document = (Document)object;
			InputStream is = null;
			ContentStream stream = document.getContentStream();
	    	if( stream != null ){
	        	is = stream.getStream();
	    	}
	    	
			response.setBinaryMode(true);
			response.setBinaryBody( IOUtils.toByteArray( stream.getStream() ) );
			response.setMimeType(document.getContentStreamMimeType());
			response.setStreamLength( stream.getLength() );
			
			IOUtils.closeQuietly( is );
			
		}

		return response;
	}

}
