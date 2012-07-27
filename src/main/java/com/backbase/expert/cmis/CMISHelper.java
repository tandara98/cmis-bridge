package com.backbase.expert.cmis;

import java.io.InputStream;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.jdom.Element;

/**
 * This interface is the basic example of abstracting the CMIS Interfaces.
 * Only some basic operations are allowed.
 * 
 * @author BartH
 *
 */
public interface CMISHelper{
	
	Session getSession();
	void setSession( Session session );
	
	// Get Repository info
	String getServerInfo();
	
	// Generic methods
	<E> E getById( String id );
	<E> E getByPath( String path );
	
	// To support XML
	Element createFolderElement(final Folder folder);
	Element createDocumentElement(final Document document);
	
	// Folder Listing
	List<CmisObject> listChildren(Folder parent);
	List<CmisObject> listChildren(Folder parent, BaseTypeId filter);
	List<CmisObject> listChildren(Folder parent, int skipCount, int offset);
	List<CmisObject> listChildren(Folder parent, BaseTypeId filter, int skipCount, int offset);
	
	// Search
	<E> List<E> getTaggedByPath( String path, BaseTypeId filter, String tag, int limit, int offset );
	<E> List<E> getTaggedByPath( String path, BaseTypeId filter, String[] tags, int limit, int offset );
	List<String> getRenditionsByTag( String path, String kind, String tag, int limit, int offset);
	List<String> getRenditionsByTags( String path, String kind, String[] tags, int limit, int offset );	
	<E> List<E> searchByName( String name, BaseTypeId filter, int limit, int offset );
	
	// Traverse
	org.w3c.dom.Document traverse( String startPath, int maxLevel, BaseTypeId filter );
	
	// Document stream methods
	InputStream getDocumentStream( Document doc );
	String getDocumentStreamAsString( Document doc );
	String getRenditionAsString( String path, String kind );
	String getDocumentStreamAsXml( Document doc );
	
	// Folder methods
	Folder getRootFolder();	
	
	// Not in use
	String getRelationShip();	
	String getPolicy();	

}
