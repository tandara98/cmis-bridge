package com.backbase.expert.cmis.impl.alfresco;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.ExtensionLevel;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backbase.expert.cmis.impl.CMISHelperImpl;

/**
 * Override the methods optimized for Alfresco CMS, like CMIS queries and XML extensions.
 * 
 * @author BartH
 *
 */
public class AlfrescoHelperImpl extends CMISHelperImpl {
	
	private static final Logger log = LoggerFactory.getLogger(AlfrescoHelperImpl.class);
	
	private static final String COMMON_ASSET_SELECT_CLAUSE = "SELECT d.cmis:objectId, d.cmis:objectTypeId, d.cmis:name, d.cmis:contentStreamLength, "
            + "d.cmis:contentStreamMimeType, d.cmis:lastModificationDate, t.cm:title, t.cm:description, wa.ws:publishedTime, "
            + "wa.ws:tags, a.cm:author ";
	
	private static final String COMMON_ASSET_FROM_CLAUSE = "FROM cmis:document AS d "
            + "JOIN ws:webasset AS wa ON d.cmis:objectId = wa.cmis:objectId "
            + "JOIN cm:author AS a ON d.cmis:objectId = a.cmis:objectId "
            + "JOIN cm:titled AS t ON d.cmis:objectId = t.cmis:objectId ";
	
	private static final String byTagQueryPattern = COMMON_ASSET_SELECT_CLAUSE + ", SCORE() " + COMMON_ASSET_FROM_CLAUSE
            + "WHERE IN_TREE(d, ''{0}'') AND ANY wa.ws:tags IN ({1})";
	
	private static final String byQueryPattern = COMMON_ASSET_SELECT_CLAUSE + ", SCORE() " + COMMON_ASSET_FROM_CLAUSE
            + "WHERE IN_TREE(d, ''{0}'')";
	
	/**
	 * Use a Alfresco query to get CMIS objects by path and tag.
	 */
	public <E> List<E> getTaggedByPath( String path, BaseTypeId filter, String tag, int limit, int offset ){
		
		CmisObject parent = this.getByPath(path);
		
		String folderId = parent.getId();
		String tagExpr = "'"+ tag  + "'";
		
		String cmisQuery = MessageFormat.format(byTagQueryPattern, folderId, tagExpr);		
		
		return pageQueryResults( cmisQuery, limit, offset );
	}
	
	/**
	 * Use a Alfresco query to get CMIS objects by path and tags.
	 */
	public <E> List<E> getTaggedByPath( String path, BaseTypeId filter, String[] tags, int limit, int offset ){
		
		CmisObject parent = this.getByPath(path);
		String folderId = parent.getId();
		List<String> expressions = new ArrayList<String>();
		for (String tag : tags) {
			String tagExpr = "'"+ tag  + "'";
			expressions.add(tagExpr);
		}
		String tagExpr = org.apache.commons.lang.StringUtils.join(expressions, " AND wa.ws:tags=");
		
		String cmisQuery = MessageFormat.format(byQueryPattern, folderId);	
		cmisQuery += " AND wa.ws:tags="+ tagExpr;
		
		return pageQueryResults( cmisQuery, limit, offset );
		
	}
	
	public <E> List<E> getAspectsByPath( String path, Map<String,String[]> aspects, int limit, int offset ){
		
		CmisObject parent = this.getByPath(path);
		String folderId = parent.getId();
		
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(COMMON_ASSET_SELECT_CLAUSE);
		queryBuilder.append(", SCORE() ");
		queryBuilder.append(COMMON_ASSET_FROM_CLAUSE);
		queryBuilder.append(" JOIN ext:demo AS e ON d.cmis:objectId = e.cmis:objectId");
		queryBuilder.append(" WHERE IN_TREE(d, ''{0}'')");
		
		List<String> expressions = new ArrayList<String>();
		for (String aspectName : aspects.keySet() ) {
			String[] aspectValues = aspects.get(aspectName);
			for(String aspectValue : aspectValues){
				expressions.add( String.format( "e.%s=''%s''", aspectName, aspectValue) );
			}
		}
		if( expressions.size() > 0){
			String tagExpr = org.apache.commons.lang.StringUtils.join(expressions, " AND ");
			queryBuilder.append(" AND ");
			queryBuilder.append(tagExpr);
		}
		
		String cmisQuery = MessageFormat.format(queryBuilder.toString(), folderId);	
		
		log.info(cmisQuery);
		
		return pageQueryResults( cmisQuery, limit, offset );
		
	}
	
	/**
	 * Extend the XML document defintion with title and description.
	 */
	public Element createDocumentElement( final Document document ){
		
		Element documentElement = super.createDocumentElement( document );
		
		List<CmisExtensionElement> list = document.getExtensions(ExtensionLevel.PROPERTIES);
		CmisExtensionElement extension = findExtension( list, "cm:title");
		if( extension != null ){
			documentElement.setAttribute("title", extension.getValue() );
		}
		CmisExtensionElement descriptionEx = findExtension( list, "cm:description");
		if( descriptionEx != null ){
			documentElement.setAttribute("description", descriptionEx.getValue() );
		}
		
		return documentElement;
	}
	
	/**
	 * Extend the XML folder defintion with title and description.
	 */
	public Element createFolderElement( final Folder folder ){
		Element folderElement = super.createFolderElement( folder );
		
		List<CmisExtensionElement> list = folder.getExtensions(ExtensionLevel.PROPERTIES);
		
		CmisExtensionElement titleEx = findExtension( list, "cm:title");
		if( titleEx != null ){
			folderElement.setAttribute("title", titleEx.getValue() );
		}
		CmisExtensionElement descriptionEx = findExtension( list, "cm:description");
		if( descriptionEx != null ){
			folderElement.setAttribute("description", descriptionEx.getValue() );
		}
		
		return folderElement;
	}
	
	/**
	 * Traverse over the CMIS extensions and try tro find a extension by name. The extension name
	 * is defined in the 'queryName' parameter. The children of that parameter contain the values.
	 * @param list The CMIS extension list
	 * @param extensionName The extension name.
	 * @return
	 */
	protected CmisExtensionElement findExtension( List<CmisExtensionElement> list, String extensionName ){
		
		CmisExtensionElement extensionElement = null;
		
		for (CmisExtensionElement element : list) {
			
			Map<String,String> attributes = element.getAttributes();
			
			if( attributes != null ){
				Set<String> keys = attributes.keySet();
				for (String key : keys) {
					String value = attributes.get(key);
					if( key.equals("queryName") && value.equals( extensionName )){
						List<CmisExtensionElement> listChildren = element.getChildren();
						if( listChildren != null && listChildren.size() > 0){
							extensionElement =  listChildren.get(0);
							break;
						}
					}
				}
			}
			
			if( extensionElement == null && element != null ){
				List<CmisExtensionElement> listChildren = element.getChildren();
				if( listChildren != null && listChildren.size() > 0){
					// Traverse children
					extensionElement = findExtension(listChildren, extensionName);
				}
			}
		}
		
		return extensionElement;
	}
	
	

}
