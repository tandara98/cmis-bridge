package com.backbase.expert.cmis.model.command;

import java.net.URLDecoder;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backbase.expert.cmis.CMISHelper;
import com.backbase.expert.cmis.model.CMISResponse;
import com.backbase.expert.cmis.model.impl.CMISResponseImpl;

/**
 * Arguments<br/>
 * <code>tags = comma seperated list of tags</code>
 * <code>limit = (optional) number</code>
 * <code>kind = (optional) rendition name</code>
 * 
 * @author BartH
 *
 */
public class GetRenditionsByTagCommand extends CMISCommand {

	private static final Logger log = LoggerFactory.getLogger(GetRenditionsByTagCommand.class);
	
	
	protected static final String TEXT_HTML = "text/html";
	private static final String DELIMITER = "|";
	private static final int DEFAULT_PAGE_LIMIT = 15;
	
	@Override
	public CMISResponse execute() throws Exception{
		
		Method method = this.getMethod();
		CMISHelper helper = this.getHelper();
		
		CMISResponse response = new CMISResponseImpl();

		List<String> renditions;
		
		// Get kind
		String kind	 		= method.getArguments().getFirst(MethodArguments.PARAM_CMIS_KIND);
		if(StringUtils.isEmpty(kind)){
			kind = "renditionPoc"; 
		}
		
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
		
		// Get tags
		String tagsArgument = method.getArguments().getFirst(MethodArguments.PARAM_CMIS_TAGS);
		String tagSequence = URLDecoder.decode(tagsArgument, "UTF-8");
		if( tagSequence.contains(DELIMITER)){
			String[] tags = tagSequence.split("\\" + DELIMITER);
			log.info("kind="+ kind + ",limit="+ limit + ",tags="+ tags);
			renditions = helper.getRenditionsByTags( method.getPath(), kind, tags, limit, offset);
		}
		else{
			String tag = tagSequence;
			log.info("kind="+ kind + ",limit="+ limit + ",tag="+ tag);
			renditions = helper.getRenditionsByTag( method.getPath(), kind, tag, limit, offset);
		}
		
		
		StringBuilder sb = new StringBuilder();
		sb.append( getWrapperStart() );
		for (String rendition : renditions) {
			sb.append( rendition );
		}
		sb.append( getWrapperEnd() );
		
		response.setMimeType( TEXT_HTML );
		response.setBody(sb.toString());
		return response;
	}
	
	
	public String getWrapperStart(){
		return "<div><ul>";
	}
	
	public String getWrapperEnd(){
		return "</ul></div>";
	}

}
