package com.backbase.expert.cmis.model.command.alfresco;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.backbase.expert.cmis.impl.alfresco.AlfrescoHelperImpl;
import com.backbase.expert.cmis.model.CMISResponse;
import com.backbase.expert.cmis.model.command.CMISCommand;
import com.backbase.expert.cmis.model.command.Method;
import com.backbase.expert.cmis.model.command.MethodArguments;
import com.backbase.expert.cmis.model.impl.CMISResponseImpl;

/**
 * 
 * Example:
 * 
 * http://[URL TO COMMAND INVOKER]
 * 
 * 		&cmis_path=/Sites/extranet/documentLibrary/Portal/Editorial/root/en/
 * 		&cmis_method=aspects
 * 		&ext:browsers=IE6
 * 		&ext:browsers=IE7
 * 		&limit=10
 * 		&offset=0
 * 
 * 
 * @author BartH
 *
 */
public class GetByAspects extends CMISCommand{

	private static final Logger log = LoggerFactory.getLogger(GetByAspects.class);
	
	private static final String ELEMENT_ROOT = "root";
	private static final String TEXT_XML = "text/xml";
	private static final int DEFAULT_PAGE_LIMIT = 15;
	
	
	@Override
	public CMISResponse execute() throws Exception {
		Method method = this.getMethod();
		
		CMISHelper helper = this.getHelper();
		AlfrescoHelperImpl alfrescoHelper = null;
		if( helper instanceof AlfrescoHelperImpl ){
			alfrescoHelper = (AlfrescoHelperImpl)helper;
		}
		else{
			return null;
		}
		
		log.debug("GetByAspects");
		
		CMISResponse response = new CMISResponseImpl();

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
		
		// Copy the key + values of all argument that starts with 'ext:'
		Map<String,String[]> args = method.getArguments().toMap();
		Map<String,String[]> aspects = new HashMap<String, String[]>();
		for (String key : args.keySet()) {
			if( key.toLowerCase().startsWith("ext:") ){
				String[] values = method.getArguments().get(key);
				aspects.put( key, values);
			}
		}
		
		List<CmisObject> objects = alfrescoHelper.getAspectsByPath(method.getPath(), aspects, limit, offset);
		
		
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
