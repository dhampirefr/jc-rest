package org.jcrest.service.rest;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

@Path(value="/write")
public class ContentWriteService {
	
	private JcrTemplate jcrTemplate;
	
	@PUT
	@Path(value="/node/{nodePath}", limited=false)
    public Response createNode(@PathParam("nodePath") String nodePath, @QueryParam("nodeType") String nodeType) {
		
		final Node node = (Node) createContentNode(nodePath, nodeType);
		
		return Response.ok().build();
		
	}
	
	@DELETE
	@Path(value="/node/{nodePath}", limited=false)
    public Response createNode(@PathParam("nodePath") String nodePath) {
		
		deleteContentNode(nodePath);
		
		return Response.ok().build();
		
	}
	
	@PUT
	@Path(value="/property/{propertyPath}", limited=false)
    public Response setProperty(@PathParam("propertyPath") String propertyPath, @QueryParam("propertyValue") String propertyValue,
    		@QueryParam("propertyType") String propertyType) {
		
		final Property property = (Property) setContentNodeProperty(propertyPath, propertyValue, propertyType);
		
		return Response.ok().build();
		
	}
	
	@DELETE
	@Path(value="/property/{propertyPath}", limited=false)
    public Response setProperty(@PathParam("propertyPath") String propertyPath) {
		
		deleteContentNodeProperty(propertyPath);
		
		return Response.ok().build();
		
	}
	
	private Object createContentNode(final String nodePath, final String nodeType) {
		
		return jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {
				/*
				final String user = "";
				final char[] pass = {};
				
				final Credentials credentials = new SimpleCredentials(user, pass);
				
				session.impersonate(credentials);
				*/
				final Node node;
				
				if (nodeType == null)
					node = session.getRootNode().addNode(nodePath);
				else
					node = session.getRootNode().addNode(nodePath, nodeType);

				return node;
				
			}
			
		});
		
	}
	
	private Object deleteContentNode(final String nodePath) {
		
		return jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {
				
				session.getRootNode().getNode(nodePath).remove();
				
				return null;
				
			}
			
		});
		
	}
	
	private Object setContentNodeProperty(final String propertyPath, final String propertyValue, final String propertyType) {
		
		return jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {
				
				final String nodePath = propertyPath.substring(0, propertyPath.lastIndexOf("/"));
				final String propertyName = propertyPath.substring(propertyPath.lastIndexOf("/"));
				
				final Property property;
				
				if (propertyType != null)
					property = session.getRootNode().getNode(nodePath).setProperty(propertyName, propertyValue, PropertyType.valueFromName(propertyType));
				else
					property = session.getRootNode().getNode(nodePath).setProperty(propertyName, propertyValue);
				
				return property;
				
			}
			
		});
		
	}
	
	private Object deleteContentNodeProperty(final String propertyPath) {
		
		return jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {
				
				final String nodePath = propertyPath.substring(0, propertyPath.lastIndexOf("/"));
				final String propertyName = propertyPath.substring(propertyPath.lastIndexOf("/"));
				
				session.getRootNode().getNode(nodePath).getProperty(propertyName).remove();
				
				return null;
				
			}
			
		});
		
	}

	public JcrTemplate getJcrTemplate() {
		return jcrTemplate;
	}

	public void setJcrTemplate(JcrTemplate jcrTemplate) {
		this.jcrTemplate = jcrTemplate;
	}

}
