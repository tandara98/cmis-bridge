package com.backbase.expert.cmis.model.command;

import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backbase.expert.cmis.CMISHelper;
import com.backbase.expert.cmis.impl.CMISHelperImpl;
import com.backbase.expert.cmis.model.CMISResponse;
import com.backbase.expert.cmis.model.impl.CMISResponseImpl;

public class GetFolderCommand extends CMISCommand {

	private static final Logger log = LoggerFactory.getLogger(GetFolderCommand.class);
	protected static final String TEXT_XML = "text/xml";
	
	protected static final String ELEMENT_ROOT = "root";
	protected static final String ATTR_PARENTNAME = "parentName";
	private static final int DEFAULT_PAGE_LIMIT = 15;
	
	@Override
	public CMISResponse execute() throws Exception{
		
		CMISHelper helper = this.getHelper();
		
		// Get limit
		String limitString 	= this.getMethod().getArguments().getFirst(MethodArguments.PARAM_CMIS_LIMIT);
		int limit = DEFAULT_PAGE_LIMIT;
		if (!StringUtils.isEmpty(limitString)) {
			limit = Integer.parseInt(limitString);
		}
		
		// Get offset
		String offsetString = this.getMethod().getArguments().getFirst(MethodArguments.PARAM_CMIS_OFFSET);
		int offset = 0;
		if (!StringUtils.isEmpty(offsetString)) {
			offset = Integer.parseInt(offsetString);
		}
		
		CMISResponse response = new CMISResponseImpl();
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getCompactFormat());

		Element rootElement = new Element( ELEMENT_ROOT );
		org.jdom.Document document = new org.jdom.Document(rootElement);

		Folder folder = (Folder)this.getObject();
		log.debug("folderName={}", folder.getName());
		if (folder != null) {

			rootElement.setAttribute( ATTR_PARENTNAME , folder.getName());

			List<CmisObject> objects = helper.listChildren(folder, limit, offset);
			for (CmisObject cmisObject : objects) {
				if (CMISHelperImpl.isDocument(cmisObject)) {
					Document childDocument = (Document) cmisObject;
					Element documentElement = helper.createDocumentElement( childDocument );
					rootElement.addContent(documentElement);
				} else {
					Folder childFolder = (Folder) cmisObject;
					Element folderElement = helper.createFolderElement( childFolder );
					rootElement.addContent(folderElement);
				}
			}
			response.setMimeType(TEXT_XML);
		}

		String xml = xmlOutputter.outputString(document);
		response.setBody(xml);

		return response;
	}

}
