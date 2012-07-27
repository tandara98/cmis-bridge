package com.backbase.expert.cmis.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.commons.io.IOUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backbase.expert.cmis.CMISHelper;

/**
 * This class is the basic example of abstracting the CMIS Interfaces.
 * Only some basic operations are allowed.
 *
 * @author BartH
 *
 */
public class CMISHelperImpl implements CMISHelper {

	private static final Logger log = LoggerFactory.getLogger(CMISHelperImpl.class);

	/** The default encoding type of the content */
	private static final String ENCODING = "UTF-8";

	/** The current session */
	private Session session;
	
	private int defaultPageLimit = 15;

	/** Default empty contructor to support IoC */
	public CMISHelperImpl(){
		
	}
	
	public Session getSession(){
		return this.session;
	}
	
	public void setSession( Session session ){
		this.session = session;
	}

	public int getDefaultPageLimit() {
		return defaultPageLimit;
	}

	public void setDefaultPageLimit(int defaultPageLimit) {
		this.defaultPageLimit = defaultPageLimit;
	}

	/**
	 * Get the current repository name.
	 */
	public String getServerInfo() {
		RepositoryInfo repInfo = session.getRepositoryInfo();
		return repInfo.getName();
	}
	
	/**
	 * Transform the CMIS document to a InputStream.
	 */
	public InputStream getDocumentStream( Document doc ) {
		InputStream is = null;
		ContentStream stream = doc.getContentStream();
    	if( stream != null ){
        	is = stream.getStream();
    	}
    	return is;
	}

	/**
	 * Transform the CMIS document stream to a UTF-8 string.
	 */
	public String getDocumentStreamAsString( Document doc ) {
		String content = null;
		ContentStream stream = doc.getContentStream();
    	if( stream != null ){
        	InputStream is = stream.getStream();
        	try {
				content = IOUtils.toString(is, ENCODING);
			}
        	catch (IOException e) {
				// Ignore
			}
    	}
    	return content;
	}

	/**
	 * Find the rendition by object path and rendition kind.
	 * @param path The path to the CMIS object
	 * @param kind The CMIS rendition kind.
	 * @return The rendered rendition.
	 */
	public String getRenditionAsString( String path, String kind ) {

		OperationContext context = session.createOperationContext();
        context.setRenditionFilterString( "*" );

        Document doc = (Document)session.getObjectByPath(path, context);

		String content = null;
		List<Rendition> renditions = doc.getRenditions();
    	if( renditions != null ){

    		Rendition matchedRendition = null;
    		for (Rendition rendition : renditions) {

				if(rendition.getKind().equals( kind )){
					matchedRendition = rendition;
					break;
				}
			}
    		if( matchedRendition != null){
    			ContentStream stream = matchedRendition.getContentStream();
    			InputStream is = stream.getStream();
            	try {
    				content = IOUtils.toString(is, ENCODING);
    			}
            	catch (IOException e) {
    				// Ignore
    			}
    		}

    	}
    	else{
    		log.error("No renditions found of kind '"+ kind +"'");
    	}

    	return content;
	}
	
	/**
	 * Find the rendition by object id and rendition kind.
	 * @param objectId The unique identifier of the CMIS object
	 * @param kind The CMIS rendition kind.
	 * @return The rendered rendition.
	 */
	private String getRenditionById(String objectId, String kind){
		OperationContext context = session.createOperationContext();
        context.setRenditionFilterString( "*" );

        Document doc = (Document)session.getObject(objectId, context);

		String content = null;
		List<Rendition> renditions = doc.getRenditions();
    	if( renditions != null ){

    		Rendition matchedRendition = null;
    		for (Rendition rendition : renditions) {

				if(rendition.getKind().equals( kind )){
					matchedRendition = rendition;
					break;
				}
			}
    		if( matchedRendition != null){
    			ContentStream stream = matchedRendition.getContentStream();
    			InputStream is = stream.getStream();
            	try {
    				content = IOUtils.toString(is, ENCODING);
    			}
            	catch (IOException e) {
    				// Ignore
    			}
    		}

    	}
    	else{
    		log.error("No renditions found of kind '"+ kind +"'");
    	}

    	return content;
	}
	
	/**
	 * Wrap a CMIS document in a XML document.<br>
	 * <code>
	 * 	 &lt;root&gt; <br>
	 * 		&lt;text&gt;<br>
	 * 		The raw CMIS document response.<br>
	 * 		&lt;/text&gt; <br>
	 *   &lt;/root>
	 * </code>
	 */
	public String getDocumentStreamAsXml(Document doc) {
		String rootElementName = "root";
		Element root = new Element(rootElementName);

		Element text = new Element("text");
		text.addContent( getDocumentStreamAsString(doc) );
		root.addContent(text);

		XMLOutputter outputter = new XMLOutputter();
		String xml = outputter.outputString(root);

		return xml;
	}

	/**
	 * Get the CMIS root folder inside the repository.
	 */
	public Folder getRootFolder() {
		Folder root = session.getRootFolder();
		return root;
	}

	/**
	 * Search a CMIS object by name.
	 */
	public <E> List<E> searchByName( String name, BaseTypeId filter, int limit, int offset ){
		
		String tableName = "cmis:document";
		String simpleTagQueryPattern = "SELECT * FROM {0} WHERE cmis:name='{1}'";
		if( filter != null ){
			tableName = filter.value();
		}
		String query = MessageFormat.format(simpleTagQueryPattern, tableName, name );		
		
		List<E> objects = pageQueryResults(query, limit, offset );
		return objects;
	}
	
	@SuppressWarnings("unchecked")
	public <E> List<E> pageQueryResults( String cmisQuery, int limit, int offset ){
		
		int skipCount = limit * offset;
		
		List<E> objects = new ArrayList<E>();
		
		OperationContext operationContext = session.createOperationContext();
		operationContext.setMaxItemsPerPage( limit );
		
		ItemIterable<QueryResult> results = session.query( cmisQuery , false, operationContext);
		ItemIterable<QueryResult> page = results.skipTo( skipCount ).getPage();
		
		log.info("limit={},skipCount={}", limit, skipCount);

		if( page != null ){
			Iterator<QueryResult> pageItems = page.iterator();

			while(pageItems.hasNext()) {
				QueryResult result = pageItems.next();
				String objectId = result.getPropertyValueById(PropertyIds.OBJECT_ID);
				String objectPath = result.getPropertyValueById(PropertyIds.PATH);
				
				// First try it with the id, otherwise get the object by path
				if( objectId != null ){
					CmisObject cmisObject = (CmisObject)session.getObject(objectId);
					objects.add( (E)cmisObject );
				}
				else if( objectPath != null ){
					CmisObject cmisObject = (CmisObject)session.getObjectByPath(objectPath);
					objects.add( (E)cmisObject );
				}
				
		    }
		}
		
		return objects;
		
	}
	

	/**
	 * Not yet implemented
	 */
	public String getRelationShip() {
		throw new UnsupportedOperationException("RelationShips not yet supported by this class");
	}
	
	/**
	 * Not yet implemented
	 */
	public String getPolicy() {
		throw new UnsupportedOperationException("Policies not yet supported by this class");
	}


	/**
	 * 	Get a CMIS object by identifier.
	 */
	@SuppressWarnings("unchecked")
	public <E> E getById( String id ){
		E genericObject = (E)session.getObject( id );
		return genericObject;
	}

	/**
	 * Get a CMIS object by path.
	 */
	@SuppressWarnings("unchecked")
	public <E> E getByPath( String path ){
		
//    	Class<E> clazz;
		
		E genericObject = (E)session.getObjectByPath( path );	;
		return genericObject;
	}
	
	/**
	 * Get all children of a CMIS folder.
	 */
	public List<CmisObject> listChildren(Folder parent){
		List<CmisObject> list = this.listChildren(parent, getDefaultPageLimit(), 0);
		return list;
	}
	
	public List<CmisObject> listChildren(Folder parent, BaseTypeId filter){
		List<CmisObject> list = this.listChildren(parent, filter, getDefaultPageLimit(), 0);
		return list;
	}
	
	/**
	 * List al the chidren of a Folder and filter on the CMIS object type.
	 * @param parent
	 * @param type
	 * @return List of the type E.
	 */
	public List<CmisObject> listChildren(Folder parent, BaseTypeId filter, int limit, int offset){
		List<CmisObject> filteredChildren = null;
		List<CmisObject> children = listChildren(parent,limit,offset);
		
		// Do filtering
		if( children != null ){
			filteredChildren = new ArrayList<CmisObject>();
		    for (CmisObject obj : children) {
		    	if( obj != null && obj.getBaseTypeId().value().equals( filter.value() ) ){
		    		filteredChildren.add( obj );
		    	}
		    }
		}
		else{
			filteredChildren = children;
		}

		return filteredChildren;
	}

	/**
	 * Get a list a folder children with a offset.
	 */
	public List<CmisObject> listChildren(Folder parent, int limit, int offset){

		List<CmisObject> list = null;
		int skipCount = limit * offset;

		OperationContext operationContext = session.createOperationContext();
		operationContext.setMaxItemsPerPage(limit);

		ItemIterable<CmisObject> children = parent.getChildren(operationContext);
		ItemIterable<CmisObject> page = children.skipTo(skipCount).getPage();

		if( page != null ){
			Iterator<CmisObject> pageItems = page.iterator();

			list = new ArrayList<CmisObject>();
			while(pageItems.hasNext()) {
				CmisObject item = pageItems.next();
		    	list.add( item );
		    }
		}
		return list;
	}
	
	/**
	 * Get @link BaseTypeId by tag within the CMIS path and limit by <code>limit</code>
	 */
	public <E> List<E> getTaggedByPath( String path, BaseTypeId filter, String tag, int limit, int offset ){

		CmisObject parent = this.getByPath(path);

		String folderId = parent.getId();
		
		String cmisQuery = "";
		String simpleTagQueryPattern = "";
		if( filter == null ){
			simpleTagQueryPattern = "SELECT * FROM cmis:document WHERE IN_TREE(''{0}'')";
			cmisQuery = MessageFormat.format(simpleTagQueryPattern, folderId );	
		}
		else{
			simpleTagQueryPattern = "SELECT * FROM {0} WHERE IN_TREE(''{1}'')";
			cmisQuery = MessageFormat.format(simpleTagQueryPattern, filter.value(), folderId);	
		}

		log.info("FolderId="+ folderId);
		log.info(cmisQuery);
		
		List<E> list = pageQueryResults( cmisQuery, limit, offset);
		return list;
	}
	
	/**
	 * Generic method to get renditions by tags
	 */
	public <E> List<E> getTaggedByPath(String path, BaseTypeId filter,
			String[] tags, int limit, int offset) {
		String tagsDelimited = org.apache.commons.lang.StringUtils.join(tags, ",");
		return this.getTaggedByPath(path, filter, tagsDelimited, limit, offset);
	}
	
	/**
	 * Get renditions single tag. This only applies on CMIS documents. 
	 */
	public List<String> getRenditionsByTag( String path, String kind, String tag, int limit, int offset ){
		List<Document> documentList = this.getTaggedByPath( path, BaseTypeId.CMIS_DOCUMENT, tag, limit, offset);
		List<String> contentList = getRenditions( documentList, kind );
		return contentList;
	}
	
	/**
	 * Get renditions by one or more tags. This only applies on CMIS documents. 
	 */
	public List<String> getRenditionsByTags(String path, String kind,
			String[] tags, int limit, int offset) {
		List<Document> documentList = this.getTaggedByPath( path, BaseTypeId.CMIS_DOCUMENT, tags, limit, offset);
		List<String> contentList = getRenditions( documentList, kind );
		return contentList;
	}
	
	/**
	 * Get list of the rendered renditions
	 * @param documentList
	 * @param kind
	 * @return
	 */
	private List<String> getRenditions( List<Document> documentList, String kind ){
		List<String> contentList = new ArrayList<String>();  
		for (Document document : documentList) {
			String content = getRenditionById(document.getId(), kind);
			if(content != null){
				contentList.add( content );
			}
		}
		return contentList;
	}

	/**
	 * Traverse a CMIS path with max and object filter.
	 */
	public org.w3c.dom.Document traverse( final String startPath, int maxLevel, final BaseTypeId filter ){

		Element root = new Element("root");
		root.setAttribute("path", startPath);
		
		org.jdom.Document doc = new org.jdom.Document(root);
		
        // Build the XML tree
		traverseInternal(startPath, -1, root, filter, maxLevel );
		
		org.jdom.output.DOMOutputter domOutputter = new org.jdom.output.DOMOutputter();
		org.w3c.dom.Document domDocument = null;
		try {
			domDocument = domOutputter.output( doc );
		} 
		catch (JDOMException e) {
			log.error(e.getMessage(), e);
		}
		return domDocument;
	}

	/**
	 * TODO: Consider using folder.getFolderTree(3) to do the traversal
	 * 
	 * @param parentPath
	 * @param level
	 * @param parentElement
	 * @param filter
	 * @param maxLevel
	 */
	private void traverseInternal( final String parentPath, int level, Element parentElement, final BaseTypeId filter, final int maxLevel ){

		// Break traverse if the max level is reached
		if( level == maxLevel ){
			return;
		}

		// Entering()
		level++;

		// HTTP CALL
		Folder parent = this.getByPath( parentPath );
		
		// HTTP CALL
		List<CmisObject> children = listChildren(parent);
		
		if( children.size() > 0 ){
			ListIterator<CmisObject> iterator = children.listIterator();

			// Loop over the children
			while(iterator.hasNext()){
				CmisObject child = iterator.next();

				// CMIS:FOLDER
				if( isFolder(child) ){
					Folder folder = (Folder)child;
					String folderPath = folder.getPaths().get(0);

					// Create XML folder element
					Element folderElement = createFolderElement( folder );

					// Apply filtering
					if( filter == null || filter.value().equals(BaseTypeId.CMIS_FOLDER.value()) ){
						parentElement.addContent(folderElement);
					}

					// Do recursion
					traverseInternal( folderPath, level, folderElement, filter, maxLevel );
				}

				// CMIS:DOCUMENT
				else{

					// Apply filtering
					if( filter == null || filter.value().equals(BaseTypeId.CMIS_DOCUMENT.value()) ){
						Document document = (Document)child;
						
						// Create XML document element
						Element documentElement = createDocumentElement( document );

						parentElement.addContent(documentElement);
					}

				}
			}
		}

		// Leaving()
		level--;
	}

	/**
	 * Create a XML folder element
	 * @param folder The CMIS folder object
	 * @return A XML org.jdom.Element
	 */
	public Element createFolderElement(final Folder folder){
		String folderPath = folder.getPaths().get(0);
		String folderName = folder.getName();
		String folderId = folder.getId();
		int childCount = folder.getDescendants(1).size();

		Element folderElement = new Element("folder");
		folderElement.setAttribute("id", folderId);
		folderElement.setAttribute("path", folderPath);
		folderElement.setAttribute("name", folderName);
		folderElement.setAttribute("childCount", String.valueOf(childCount));
		
		return folderElement;
	}

	/**
	 * Create a XML document element
	 * @param document The CMIS document element
	 * @return A XML org.jdom.Element
	 */
	public Element createDocumentElement(final Document document){
		String documentPath = document.getPaths().get(0);
		String documentName = document.getName();
		String documentId = document.getId();

		Element documentElement = new Element("document");
		documentElement.setAttribute("id", documentId);
		documentElement.setAttribute("path", documentPath);
		documentElement.setAttribute("name", documentName);
		
		return documentElement;
	}
	
	
	
	/**
	 * Check if the cmis object is of base type FOLDER
	 * @param cmisObject
	 * @return True if the object is a FOLDER, otherwise false
	 */
	public static boolean isFolder( CmisObject cmisObject  ){
		boolean isFolder = false;
		if( cmisObject.getBaseTypeId().value().equals(BaseTypeId.CMIS_FOLDER.value()) ){
			isFolder = true;
		}
		return isFolder;
	}
	
	/**
	 * Check if the cmis object is of base type DOCUMENT
	 * @param cmisObject
	 * @return True if the object is a DOCUMENT, otherwise false
	 */
	public static boolean isDocument( CmisObject cmisObject  ){
		boolean isDocument = false;
		if( cmisObject.getBaseTypeId().value().equals(BaseTypeId.CMIS_DOCUMENT.value()) ){
			isDocument = true;
		}
		return isDocument;
	}
	
}
