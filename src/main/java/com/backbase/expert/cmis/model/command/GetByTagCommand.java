package com.backbase.expert.cmis.model.command;

import java.net.URLDecoder;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.backbase.expert.cmis.CMISHelper;
import com.backbase.expert.cmis.impl.CMISHelperImpl;
import com.backbase.expert.cmis.model.CMISResponse;
import com.backbase.expert.cmis.model.impl.CMISResponseImpl;

/**
 * Arguments<br/>
 * <code>tags = comma seperated list of tags</code>
 * 
 * @author BartH
 *
 */
public class GetByTagCommand extends CMISCommand {

	private static final String ELEMENT_ROOT = "root";
	private static final String TEXT_XML = "text/xml";
	private static final String DELIMITER = "|";
	private static final int DEFAULT_PAGE_LIMIT = 15;
	
	@Override
	public CMISResponse execute() throws Exception{
		
		Method method = this.getMethod();
		
		CMISHelper helper = this.getHelper();
		
		CMISResponse response = new CMISResponseImpl();

		List<CmisObject> objects = null;
		
		// Get limit
		String limitString 	= method.getArguments().getFirst(MethodArguments.PARAM_CMIS_LIMIT);
		int limit = DEFAULT_PAGE_LIMIT;
		if (!StringUtils.isEmpty(limitString)) {
			limit = Integer.parseInt(limitString);
		}
		
		// Get offset
		String offsetString = method.getArguments().getFirst(MethodArguments.PARAM_CMIS_OFFSET);
		int offset = 0;
		if (!StringUtils.isEmpty(offsetString)) {
			offset = Integer.parseInt(offsetString);
		}
		
		String tagSequence = URLDecoder.decode(method.getArguments().getFirst(MethodArguments.PARAM_CMIS_TAGS), "UTF-8");
		
		if( tagSequence.contains(DELIMITER)){			
			String[] tags = tagSequence.split("\\" + DELIMITER);			
			objects = helper.getTaggedByPath(method.getPath(), BaseTypeId.CMIS_DOCUMENT, tags, limit, offset);
		}
		else{
			String tag = tagSequence;
			objects = helper.getTaggedByPath(method.getPath(), BaseTypeId.CMIS_DOCUMENT, tag, limit, offset);
		}
		
		// Create XML
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getCompactFormat());
		Element rootElement = new Element(ELEMENT_ROOT);
		org.jdom.Document document = new org.jdom.Document(rootElement);
		for (CmisObject cmisObject : objects) {
			if (CMISHelperImpl.isDocument(cmisObject)) {
				Document childDocument = (Document) cmisObject;
				Element documentElement = helper.createDocumentElement( childDocument );
				rootElement.addContent(documentElement);
			} 
			else {
				Folder childFolder = (Folder) cmisObject;
				Element folderElement = helper.createFolderElement( childFolder );
				rootElement.addContent(folderElement);
			}
		}
		
		response.setMimeType( TEXT_XML );
		String xml = xmlOutputter.outputString(document);
		response.setBody(xml);
		return response;
	}

}
