package org.jcrest.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.log4j.Logger;
import org.jcrest.bean.ContentEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class ContentListener implements EventListener, InitializingBean, DisposableBean  {
	
	private static final Logger LOGGER = Logger.getLogger(ContentListener.class);
	
	private static final Map<Integer, String> EVENT_TYPES;
	
	private JcrTemplate jcrTemplate;
	private Cache eventCache;
	
	private Session session;
	
	static {
		
		EVENT_TYPES = new HashMap<Integer, String>();
		
		EVENT_TYPES.put(Integer.valueOf(1), "NODE_ADDED");
		EVENT_TYPES.put(Integer.valueOf(2), "NODE_REMOVED");
		EVENT_TYPES.put(Integer.valueOf(4), "PROPERTY_ADDED");
		EVENT_TYPES.put(Integer.valueOf(8), "PROPERTY_CHANGED");
		EVENT_TYPES.put(Integer.valueOf(16), "PROPERTY_ADDED");
		
	}
	
	public void afterPropertiesSet() {
		
		try {
			session = jcrTemplate.getSessionFactory().getSession();
		}
		catch (RepositoryException ex) {
			LOGGER.error(ex);
		}
		
	}
	
	public void destroy() {
		session.logout();
	}

	public void onEvent(EventIterator eventIterator) {
		
		try  {
			
			// My application is just too fast.
			// Seriously, we are inspecting the node before CQ has even committed the changes.
			Thread.sleep(100);
			
		}
		catch (Exception ex) {
			LOGGER.error(ex);
		}
		
		int i = 1;
		
		long time = System.currentTimeMillis();
		
		while (eventIterator.hasNext()) {
			
			final Event event = eventIterator.nextEvent();
			
			final ContentEvent contentEvent = new ContentEvent();
			
			try {
				
				contentEvent.setPath(event.getPath());
				contentEvent.setType(EVENT_TYPES.get(Integer.valueOf(event.getType())));
				contentEvent.setUser(event.getUserID());
				
				final String nodePath;
				
				if (event.getType() == Event.NODE_ADDED || event.getType() == Event.PROPERTY_ADDED || event.getType() == Event.PROPERTY_CHANGED) {
					
					if (event.getType() == Event.NODE_ADDED)
						nodePath = event.getPath();
					else
						nodePath = event.getPath().substring(0, event.getPath().lastIndexOf("/"));
						
					final List<String> nodeTypes= (List<String>) getNodeTypes(nodePath);
					
					contentEvent.setNodeTypes(nodeTypes);
					
				}
				
				if (event.getType() == Event.PROPERTY_ADDED || event.getType() == Event.PROPERTY_CHANGED) {
					
					final List<String> propertyValues = (List<String>) getPropertyValues(event.getPath());
					
					contentEvent.setPropertyValues(propertyValues);
					
				}
				
				final StringBuilder sb = new StringBuilder();

				sb.append(time);
				sb.append("-");
				sb.append(i++);
				
				final Element element = new Element(sb.toString(), contentEvent);
				
				eventCache.put(element);
				
			}
			catch (RepositoryException ex) {
				LOGGER.error(ex);
			}
			
		}

	}
	
	private Object getNodeTypes(final String nodePath) {
		
		return jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {
				
				try {
					
					final List<String> nodeTypes = new ArrayList<String>();
				
					final Node node = (Node) session.getItem(nodePath);
				
					nodeTypes.add(node.getPrimaryNodeType().getName());
					
					final NodeType[] nodeTypeArr = node.getMixinNodeTypes();
					
					for (NodeType nodeType: nodeTypeArr)
						nodeTypes.add(nodeType.getName());
					
					return nodeTypes;
				
				}
				catch (PathNotFoundException ex) {
					
					return null;
					
				}
				
			}
			
		});
		
	}
	
	private Object getPropertyValues(final String propertyPath) {
		
		return jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {

				final Property prop = (Property) session.getItem(propertyPath);
			
				final List<String> values = new ArrayList<String>();
				
				if (prop.getType() == PropertyType.BINARY)
					values.add(PropertyType.TYPENAME_BOOLEAN);
				else if (prop.getDefinition().isMultiple()) {
					
					final Value[] propertyValues = prop.getValues();
					
					for (Value value: propertyValues) {
						
						if (value.getType() == PropertyType.BINARY)
							values.add(PropertyType.TYPENAME_BINARY);
						else
							values.add(value.getString());
						
					}
					
				}
				else
					values.add(prop.getString());
				
				return values;
				
			}
			
		});
		
	}
	
	public JcrTemplate getJcrTemplate() {
		return jcrTemplate;
	}

	public void setJcrTemplate(JcrTemplate jcrTemplate) {		
		this.jcrTemplate = jcrTemplate;	
	}

	public Cache getEventCache() {
		return eventCache;
	}

	public void setEventCache(Cache eventCache) {
		this.eventCache = eventCache;
	}

}
