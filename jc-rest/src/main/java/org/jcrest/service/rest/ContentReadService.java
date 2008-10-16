package org.jcrest.service.rest;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import net.sf.ehcache.Cache;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.log4j.Logger;

import org.jcrest.bean.ContentAttribute;
import org.jcrest.bean.ContentEvent;
import org.jcrest.bean.ContentNode;
import org.jcrest.bean.ContentProperty;
import org.jcrest.bean.wrapper.ContentWrapper;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

@Path(value="/")
public class ContentReadService {
	
	private static final Logger LOGGER = Logger.getLogger(ContentReadService.class);
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	private Configuration freemarkerConfig;
	private Cache eventCache;
	private JcrTemplate jcrTemplate;
	
	@Context
    private UriInfo ui;
	
	@GET
	@Path(value="/path/nodes/{nodePath}", limited=false)
    public ContentWrapper getNodeContent(
    		@PathParam("nodePath") String nodePath,
    		@DefaultValue("1") @QueryParam("nodeDepth") int nodeDepth, 
    		@QueryParam("nodeType") String nodeType, 
    		@QueryParam("propertiesToShow") String propertiesToShow, 
    		@DefaultValue("false") @QueryParam("includeAllProperties") boolean includeAllProperties, 
    		@DefaultValue("false") @QueryParam("includePrimaryItem") boolean includePrimaryItem, 
    		@DefaultValue("false") @QueryParam("includePrimaryItemDescendants") boolean includePrimaryItemDescendants,
    		@DefaultValue("false") @QueryParam("includeAllChildren") boolean includeAllChildren,
    		@DefaultValue("false") @QueryParam("includeAllAttributes") boolean includeAllAttributes) {
		
		final ContentWrapper content = new ContentWrapper();
		
		final List<ContentNode> contentNodes = (List<ContentNode>) getContentNodes(nodePath, nodeDepth, 1, includeAllProperties, propertiesToShow, nodeType, includePrimaryItem, includePrimaryItemDescendants, includeAllChildren, includeAllAttributes);
		
		content.setContentNodes(contentNodes);
		
		return content;
		
	}
	
	@GET
	@Path(value="/search/nodes/{nodePath}", limited=false)
    public ContentWrapper getNodeContent(
    		@PathParam("nodePath") String nodePath,
    		@DefaultValue("nt:base") @QueryParam("nodeType") String nodeType, 
    		@QueryParam("propertiesToShow") String propertiesToShow, 
    		@DefaultValue("false") @QueryParam("includeAllProperties") boolean includeAllProperties, 
    		@DefaultValue("false") @QueryParam("includePrimaryItem") boolean includePrimaryItem, 
    		@DefaultValue("false") @QueryParam("includePrimaryItemDescendants") boolean includePrimaryItemDescendants,
    		@DefaultValue("false") @QueryParam("includeAllChildren") boolean includeAllChildren,
    		@DefaultValue("false") @QueryParam("includeAllAttributes") boolean includeAllAttributes) {
		
		final ContentWrapper content = new ContentWrapper();
		
		final List<ContentNode> contentNodes = (List<ContentNode>) getContentNodes(nodePath, includeAllProperties, propertiesToShow, nodeType, includePrimaryItem, includePrimaryItemDescendants, includeAllChildren, includeAllAttributes);
		
		content.setContentNodes(contentNodes);
		
		return content;
		
	}
	
	@GET
	@Path(value="/template/ftl/{tempate}/nodes/{nodePath}", limited=false)
    public String getTemplateContent(
    		@PathParam("nodePath") String nodePath,
    		@DefaultValue("1") @QueryParam("nodeDepth") int nodeDepth, 
    		@QueryParam("nodeType") String nodeType, 
    		@QueryParam("propertiesToShow") String propertiesToShow, 
    		@DefaultValue("false") @QueryParam("includeAllProperties") boolean includeAllProperties, 
    		@DefaultValue("false") @QueryParam("includePrimaryItem") boolean includePrimaryItem, 
    		@DefaultValue("false") @QueryParam("includePrimaryItemDescendants") boolean includePrimaryItemDescendants,
    		@DefaultValue("false") @QueryParam("includeAllChildren") boolean includeAllChildren,
    		@DefaultValue("false") @QueryParam("includeAllAttributes") boolean includeAllAttributes,
    		@DefaultValue("browser") @PathParam("template") String template) {
		
		final List<ContentNode> contentNodes = (List<ContentNode>) getContentNodes(nodePath, nodeDepth, 1, includeAllProperties, propertiesToShow, nodeType, includePrimaryItem, includePrimaryItemDescendants, includeAllChildren, includeAllAttributes);
		
		final Map<String, Object> model = new HashMap<String, Object>();
		
		model.put("path", nodePath);
		
		if (contentNodes.size() > 1)
			model.put("contentNodes", contentNodes);
		else
			model.put("contentNode", contentNodes.get(0));
		
		try {	
			return FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfig.getTemplate(template.concat(".ftl")), model);	
		}
		catch (TemplateException ex) {
			LOGGER.error(ex);
		}
		catch (IOException ex) {
			LOGGER.error(ex);
		}
		
		return "";
		
	}
	
	@GET
	@Path(value="/feed/{nodePath}", limited=false)
    public Feed getFeedContent(
    		@PathParam("nodePath") String nodePath,
    		@DefaultValue("1") @QueryParam("nodeDepth") int nodeDepth, 
    		@QueryParam("nodeType") String nodeType, 
    		@QueryParam("propertiesToShow") String propertiesToShow, 
    		@DefaultValue("false") @QueryParam("includeAllProperties") boolean includeAllProperties, 
    		@DefaultValue("false") @QueryParam("includePrimaryItem") boolean includePrimaryItem, 
    		@DefaultValue("false") @QueryParam("includePrimaryItemDescendants") boolean includePrimaryItemDescendants,
    		@DefaultValue("false") @QueryParam("includeAllChildren") boolean includeAllChildren,
    		@DefaultValue("false") @QueryParam("includeAllAttributes") boolean includeAllAttributes,
    		@QueryParam("entryTitle") String entryTitle,
    		@QueryParam("entryUpdated") String entryUpdated,
    		@QueryParam("entryContent") String entryContent,
    		@DefaultValue("text") @QueryParam("entryContentType") String entryContentType) {
		
		final Date now = Calendar.getInstance().getTime();
		
		final Feed feed = Abdera.getNewFactory().newFeed();
		
		// TODO recommended - author, link
		// TODO optional - category, contributor, generator, icon, logo, rights, subtitle
		
		feed.setId("http://www.jc-rest.org");
		feed.setTitle("/jc-rest/services/".concat(nodePath));
		feed.setUpdated("2008-09-12");
		
		final List<ContentNode> contentNodes = (List<ContentNode>) getContentNodes(nodePath, nodeDepth, 1, includeAllProperties, propertiesToShow, nodeType, includePrimaryItem, includePrimaryItemDescendants, includeAllChildren, includeAllAttributes);
		
		for (ContentNode contentNode: contentNodes) {
		
			final Entry e = feed.addEntry();
			
			// TODO recommended - author, link, summary
			// TODO optional - category, contributor, published, source, rights
			
			e.setId("/jc-rest/services".concat(contentNode.getParent().concat(contentNode.getName())));
			
			final ContentProperty titleProperty = contentNode.getProperty(entryTitle);
			
			if (titleProperty != null && titleProperty.getValues().size() > 0)
				e.setTitle(titleProperty.getValues().get(0));
			else
				e.setTitle("N/A");
			
			final ContentProperty updatedProperty = contentNode.getProperty(entryUpdated);
			
			if (updatedProperty != null && updatedProperty.getValues().size() > 0) {
				
				final String updated = updatedProperty.getValues().get(0);
				
				try {
					e.setUpdated(sdf.parse(updated));
				}
				catch (ParseException ex) {
					e.setUpdated(now);
				}
				
			}
			else
				e.setUpdated(now);
			
			final ContentProperty contentProperty = contentNode.getProperty(entryContent);
			
			if (contentProperty != null && contentProperty.getValues().size() > 0)
				e.setContent(contentProperty.getValues().get(0), Content.Type.typeFromString(entryContentType));
			else
				e.setContent("N/A");
					
		}
		
		return feed;
		
	}
	
	@GET
	@Path(value="/events/{nodePath}", limited=false)
    public ContentWrapper getEventContent(
    		@PathParam("nodePath") String nodePath, 
    		@QueryParam("nodeType") String nodeType, 
    		@DefaultValue("false") @QueryParam("descendants") boolean descendants, 
    		@QueryParam("user") String user, 
    		@QueryParam("eventType") String eventType) {
		
		final ContentWrapper content = new ContentWrapper();
		
		final List<ContentEvent> contentEvents = new ArrayList<ContentEvent>();
		
		final List<String> keys = eventCache.getKeys();
		
		for (String key: keys) {
			
			boolean valid = true;
			
			final ContentEvent event = (ContentEvent) eventCache.get(key).getValue();
			
			if (nodePath.length() != 0) {
		
				if (descendants && !event.getPath().substring(1).startsWith(nodePath))
					valid = false;
				else if (!descendants && !event.getPath().substring(1).equals(nodePath))
					valid = false;
				
			}
			
			if (user != null && !event.getUser().equals(user))
				valid = false;
			
			if (eventType != null && !event.getType().equals(eventType))
				valid = false;
			
			if (nodeType != null && (event.getNodeTypes() == null || !event.getNodeTypes().contains(nodeType)))
				valid = false;
			
			if (valid)
				contentEvents.add(event);
			
		}
		
		content.setContentEvents(contentEvents);
		
		return content;
		
	}
		
	private Object getContentNodes(final String nodePath, final int nodeDepth, final int depth, final boolean includeAllProperties, 
			final String propertiesToShow, final String nodeType, final boolean includePrimaryItem, final boolean includePrimaryItemDescendants,
			final boolean includeAllChildren, final boolean includeAllAttributes) {
		
		return jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {
				
				final StringBuilder sb = new StringBuilder();
				
				sb.append("/");
				sb.append(nodePath);
				
				final Node node = (Node) session.getItem(sb.toString());
				
				final List<ContentNode> contentNodes = (List<ContentNode>) getContentNodes(node, nodeDepth, 1, includeAllProperties, propertiesToShow, nodeType, includePrimaryItem, includePrimaryItemDescendants, includeAllChildren, includeAllAttributes);
				
				return contentNodes;
				
			}
			
		});
		
	}
	
	private Object getContentNodes(final Node node, final int nodeDepth, final int depth, final boolean includeAllProperties, 
			final String propertiesToShow, final String nodeType, final boolean includePrimaryItem, final boolean includePrimaryItemDescendants,
			final boolean includeAllChildren, final boolean includeAllAttributes) {
		
		return jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {
				
				final List<ContentNode> nodes = new ArrayList<ContentNode>();
				
				if (depth == nodeDepth) {
					
					if (nodeType == null || node.getPrimaryNodeType().getName().equals(nodeType)) {
					
						final ContentNode contentNode = (ContentNode) getContentNode(node, includeAllProperties, propertiesToShow, includePrimaryItem, includePrimaryItemDescendants, includeAllChildren, includeAllAttributes);
						
						nodes.add(contentNode);
					
					}
					
				}
				else if (node.hasNodes()) {
					
					final NodeIterator iter = node.getNodes();
					
					while (iter.hasNext())
						nodes.addAll((List<ContentNode>) getContentNodes(iter.nextNode(), nodeDepth, depth + 1, includeAllProperties, propertiesToShow, nodeType, includePrimaryItem, includePrimaryItemDescendants, includeAllChildren, includeAllAttributes));
					
				}
				
				return nodes;
				
			}
			
		});
		
	}

	private Object getContentNodes(final String nodePath, final boolean includeAllProperties, 
			final String propertiesToShow, final String nodeType, final boolean includePrimaryItem, final boolean includePrimaryItemDescendants,
			final boolean includeAllChildren, final boolean includeAllAttributes) {
		
		return jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {
				
				final List<ContentNode> contentNodes = new ArrayList<ContentNode>();
				
				final QueryManager manager = session.getWorkspace().getQueryManager();
				
				final StringBuilder sb = new StringBuilder();
				
				sb.append("select * from ");
				sb.append(nodeType);
				sb.append(" where jcr:path like '/");
				sb.append(nodePath);
				sb.append("/%'");
				
				final MultivaluedMap<String, String> parameters = ui.getQueryParameters();
				
				final String sql = sb.toString();
				
				final Query query = manager.createQuery(sql, Query.SQL);
			
				final QueryResult result = query.execute();
				
				final NodeIterator resultIter = result.getNodes();
				
				while (resultIter.hasNext()) {
					
					final Node node = resultIter.nextNode();
					
					final ContentNode contentNode = (ContentNode) getContentNode(node, includeAllProperties, propertiesToShow, includePrimaryItem, includePrimaryItemDescendants, includeAllChildren, includeAllAttributes);
					
					boolean valid = true;
					
					final Set<Map.Entry<String, List<String>>> entries = parameters.entrySet();
					
					for (Map.Entry<String, List<String>> entry: entries) {
						
						if (entry.getKey().startsWith("propEquals.")) {
							
							final String propertyName = entry.getKey().split("\\.")[1];
							final List<String> values = entry.getValue();
							
							final ContentProperty property = contentNode.getProperty(propertyName);
							
							if (!(property != null && property.getValues().contains(values.get(0))))
								valid = false;
							
						}
						else if (entry.getKey().startsWith("propNotEquals.")) {
							
							final String propertyName = entry.getKey().split("\\.")[1];
							final List<String> values = entry.getValue();
							
							final ContentProperty property = contentNode.getProperty(propertyName);
							
							if (property != null && property.getValues().contains(values.get(0)))
								valid = false;
							
						}
						
					}
					
					if (parameters.containsKey("nodeParentPathNotContains")) {
						
						final List<String> values = parameters.get("nodeParentPathNotContains");
						
						for (String value: values) {

							if (contentNode.getParent().contains(value))
								valid = false;
						
						}
						
					}
					
					if (valid)
						contentNodes.add(contentNode);
					
				}
				
				return contentNodes;
				
			}
			
		});
		
	}
	
	private Object getContentNode(final Node node, final boolean includeAllProperties, final String propertiesToShow, 
			final boolean includePrimaryItem, final boolean includePrimaryItemDescendants, final boolean includeAllChildren,
			final boolean includeAllAttributes) {
		
		return jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {
					
				final ContentNode contentNode = new ContentNode();
				
				if (!node.getPath().equals("/")) {
					
					contentNode.setName(node.getName());
					contentNode.setParent(node.getParent().getPath());
					
				}
				else
					contentNode.setName("Root");
				
				final List<ContentAttribute> contentAttributes = new ArrayList<ContentAttribute>();
				
				if (includeAllAttributes) {
				
					contentAttributes.add(new ContentAttribute("Checked Out", String.valueOf(node.isCheckedOut())));
					contentAttributes.add(new ContentAttribute("Locked", String.valueOf(node.isLocked())));
					contentAttributes.add(new ContentAttribute("Holds Lock", String.valueOf(node.holdsLock())));
					
					try {
						contentAttributes.add(new ContentAttribute("Primary Item", node.getPrimaryItem().getName()));
					}
					catch (ItemNotFoundException ex) {
						LOGGER.warn(ex);
					}
				
				}
				
				contentNode.setAttributes(contentAttributes);
				
				final List<ContentProperty> contentProperties = new ArrayList<ContentProperty>();
				
				if ((includeAllProperties || propertiesToShow != null) && node.hasProperties()) {
					
					final List<String> propertyNamesToShow = new ArrayList<String>();
					
					if (propertiesToShow != null) {
						
						final String[] propertyNameArr = propertiesToShow.split(",");
						
						propertyNamesToShow.addAll(Arrays.asList(propertyNameArr));
						
					}
					
					final PropertyIterator props = node.getProperties();
					
					while (props.hasNext()) {
						
						final Property prop = props.nextProperty();
						
						if (includeAllProperties || propertyNamesToShow.contains(prop.getName())) {
						
							final ContentProperty contentProperty = new ContentProperty();
							
							contentProperty.setName(prop.getName());
							contentProperty.setType(PropertyType.nameFromValue(prop.getType()));
							
							final List<String> values = new ArrayList<String>();
							
							if (prop.getType() != PropertyType.BINARY && !prop.getDefinition().isMultiple())
								values.add(prop.getString());
							else if (prop.getDefinition().isMultiple()) {
								
								final Value[] propertyValues = prop.getValues();
								
								for (Value value: propertyValues) {
									
									if (value.getType() != PropertyType.BINARY)
										values.add(value.getString());
									
								}
								
							}
							
							contentProperty.setValues(values);
							
							contentProperties.add(contentProperty);
							
						}
						
					}
					
				}
				
				contentNode.setProperties(contentProperties);
				
				final List<ContentNode> contentNodes = new ArrayList<ContentNode>();
				
				if (includeAllChildren) {
					
					final NodeIterator nodeIter = node.getNodes();
					
					while (nodeIter.hasNext()) {
						
						final Node node = nodeIter.nextNode();
						
						final ContentNode childContentNode = (ContentNode) getContentNode(node, includeAllProperties, propertiesToShow, false, includePrimaryItemDescendants, false, includeAllAttributes);
						
						contentNodes.add(childContentNode);
						
					}
					
				}
				else if (includePrimaryItem) {
					
					try {
						
						final Item primaryItem = node.getPrimaryItem();
						
						if (primaryItem.isNode()) {
							
							final Node primaryItemNode = (Node) primaryItem;
							
							final ContentNode primaryItemContentNode = (ContentNode) getContentNode(primaryItemNode, includeAllProperties, propertiesToShow, false, includePrimaryItemDescendants, false, includeAllAttributes);
							
							contentNodes.add(primaryItemContentNode);
							
						}
						
					}
					catch (ItemNotFoundException ex) {
						LOGGER.warn(ex);
					}
					
				}
				else if (includePrimaryItemDescendants) {
					
					final NodeIterator descendants = node.getNodes();
						
					while (descendants.hasNext()) {
							
						final ContentNode descendantContentNode = (ContentNode) getContentNode(descendants.nextNode(), includeAllProperties, propertiesToShow, false, includePrimaryItemDescendants, false, includeAllAttributes);
							
						contentNodes.add(descendantContentNode);
							
					}
					
				}
				
				contentNode.setNodes(contentNodes);
				
				return contentNode;
				
			}
			
		});
		
	}
		
	public Configuration getFreemarkerConfig() {
		return freemarkerConfig;
	}

	public void setFreemarkerConfig(Configuration freemarkerConfig) {
		this.freemarkerConfig = freemarkerConfig;
	}

	public Cache getEventCache() {
		return eventCache;
	}

	public void setEventCache(Cache eventCache) {
		this.eventCache = eventCache;
	}

	public JcrTemplate getJcrTemplate() {
		return jcrTemplate;
	}

	public void setJcrTemplate(JcrTemplate jcrTemplate) {
		this.jcrTemplate = jcrTemplate;
	}

}
