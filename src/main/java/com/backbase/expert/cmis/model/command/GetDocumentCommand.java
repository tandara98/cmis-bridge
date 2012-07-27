package com.backbase.expert.cmis.model.command;

import org.apache.chemistry.opencmis.client.api.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backbase.expert.cmis.CMISHelper;
import com.backbase.expert.cmis.model.CMISResponse;
import com.backbase.expert.cmis.model.impl.CMISResponseImpl;

public class GetDocumentCommand extends CMISCommand {

	private static final Logger log = LoggerFactory.getLogger(GetDocumentCommand.class);
	
	@Override
	public CMISResponse execute() throws Exception{
		
		CMISHelper helper = this.getHelper();
		
		Document document = (Document) this.getObject();
		CMISResponse response = new CMISResponseImpl();

		log.debug("documentName={},documentMime={}", document.getName(),
				document.getContentStreamMimeType());
		
		if (document != null) {
			
			String documentStream = helper.getDocumentStreamAsString(document);
			response.setBody(documentStream);
			response.setMimeType(document.getContentStreamMimeType());
		}

		return response;
	}

}
