package org.jcrest.service.rest;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jcrest.bean.ContentAttribute;
import org.jcrest.bean.wrapper.ContentWrapper;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

@Path(value="/repository")
public class ContentRepositoryService {
	
	private JcrTemplate jcrTemplate;
	
	@GET
	@Path(value="/attributes")
	public ContentWrapper getRepositoryAttributes() {
		
		final ContentWrapper content = new ContentWrapper();
		
		final List<ContentAttribute> repositoryAttributes = (List<ContentAttribute>) getRepositoryAttributeContent();
		
		content.setRepositoryAttributes(repositoryAttributes);
		
		return content;
		
	}
	
	private Object getRepositoryAttributeContent() {
		
		return jcrTemplate.execute(new JcrCallback() {

			public Object doInJcr(Session session) throws RepositoryException {
				
				final List<ContentAttribute> attributes = new ArrayList<ContentAttribute>();
				
				final Repository repository = session.getRepository();
				
				final String[] names = repository.getDescriptorKeys();
				
				for (String name: names)
					attributes.add(new ContentAttribute(name.replaceAll("_", " "), repository.getDescriptor(name)));
				
				return attributes;
				
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
