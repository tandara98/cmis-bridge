package com.backbase.expert.cmis.model.command;

import org.apache.commons.lang.StringUtils;
import org.jdom.input.DOMBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.backbase.expert.cmis.CMISHelper;
import com.backbase.expert.cmis.model.CMISResponse;
import com.backbase.expert.cmis.model.impl.CMISResponseImpl;

/**
 * Arguments<br/>
 * <code>limit = (optional)numeric limit</code>
 * 
 * @author BartH
 *
 */
public class GetTraversalCommand extends CMISCommand {

	protected static final String TEXT_XML = "text/xml";
	private static final int DEFAULT_LIMIT = 3;
	
	public GetTraversalCommand(){
	}
	
	@Override
	public CMISResponse execute() throws Exception{
		
		Method method = this.getMethod();
		CMISHelper helper = this.getHelper();
		
		String limitString = method.getArguments().getFirst( MethodArguments.PARAM_CMIS_LIMIT );
		
		int limit = 0;

		if (StringUtils.isEmpty(limitString)) {
			limit = DEFAULT_LIMIT;
		}
		else{
			limit = Integer.parseInt(limitString);
		}
		
		CMISResponse response = new CMISResponseImpl();
		org.w3c.dom.Document xmlDocument = helper.traverse( method.getPath(), limit, null);

		DOMBuilder domBuilder = new DOMBuilder();
		org.jdom.Document jdomDoc = domBuilder.build(xmlDocument);

		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());

		String xml = xmlOutput.outputString(jdomDoc);
		
		response.setMimeType(TEXT_XML);
		response.setBody( xml );
		return response;
	}

}
