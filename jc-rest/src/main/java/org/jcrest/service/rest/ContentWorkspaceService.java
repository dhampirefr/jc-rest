package org.jcrest.service.rest;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jcrest.bean.NamespaceBean;
import org.jcrest.bean.NodeTypeBean;
import org.jcrest.bean.wrapper.WorkspaceWrapper;

import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

@Path(value="/workspace")
public class ContentWorkspaceService {
	
	private JcrTemplate jcrTemplate;
	
	@GET
	@Path(value="/namespaces")
	public WorkspaceWrapper getWorkspaceNamespaces() {
		
		final WorkspaceWrapper ws = new WorkspaceWrapper();
		
		final List<NamespaceBean> namespaces = (List<NamespaceBean>) getWorkspaceNamespaceValues();
		
		ws.setNamespaces(namespaces);
		
		return ws;
		
	}
	
	@GET
	@Path(value="/nodeTypes")
	public WorkspaceWrapper getWorkspaceNodeTypes() {
		
		final WorkspaceWrapper ws = new WorkspaceWrapper();
		
		final List<NodeTypeBean> nodeTypes = (List<NodeTypeBean>) getWorkspaceNodeTypeValues();
		
		ws.setNodeTypes(nodeTypes);
		
		return ws;
		
	}
	
	private Object getWorkspaceNamespaceValues() {
		
		return jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {
				
				final List<NamespaceBean> namespaces = new ArrayList<NamespaceBean>();
				
				final Workspace workspace = session.getWorkspace();
				
				final String[] uris = workspace.getNamespaceRegistry().getURIs();
				
				for (String uri: uris) {
				
					final NamespaceBean namespace = new NamespaceBean();
					
					namespace.setPrefix(workspace.getNamespaceRegistry().getPrefix(uri));
					namespace.setUri(uri);
					
					namespaces.add(namespace);
				
				}
				
				return namespaces;
				
			}
			
		});
		
	}
	
	private Object getWorkspaceNodeTypeValues() {
		
		return jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {
				
				final List<NodeTypeBean> nodeTypes = new ArrayList<NodeTypeBean>();
				
				final Workspace workspace = session.getWorkspace();
				
				final NodeTypeIterator iter = workspace.getNodeTypeManager().getAllNodeTypes();
				
				while (iter.hasNext()) {
					
					final NodeType nt = iter.nextNodeType();
				
					final NodeTypeBean nodeType = new NodeTypeBean();
					
					nodeType.setName(nt.getName());
					nodeType.setMixin(nt.isMixin());
					nodeType.setOrderableChildNodes(nt.hasOrderableChildNodes());
					
					nodeTypes.add(nodeType);
				
				}
				
				return nodeTypes;
				
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
