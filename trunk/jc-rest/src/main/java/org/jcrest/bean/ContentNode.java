package org.jcrest.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

public class ContentNode {
	
	private String name;
	private String parent;
	
	private List<ContentProperty> properties;
	private List<ContentNode> nodes;

	public ContentProperty getProperty(final String property) {
		
		for (ContentProperty contentProperty: properties) {
			
			if (contentProperty.getName().equals(property))
				return contentProperty;
			
		}
		
		for (ContentNode contentNode: nodes) {
			
			for (ContentProperty contentProperty: contentNode.getProperties()) {
				
				if (contentProperty.getName().equals(property))
					return contentProperty;
				
			}
			
		}
		
		return null;
		
	}

	@XmlAttribute()
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute()
	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	@XmlElementWrapper(name="properties")
	@XmlElements(
	    @XmlElement(name="property", type=org.jcrest.bean.ContentProperty.class))
	public List<ContentProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<ContentProperty> properties) {
		this.properties = properties;
	}

	@XmlElementWrapper(name="nodes")
	@XmlElements(
	    @XmlElement(name="node", type=org.jcrest.bean.ContentNode.class))
	public List<ContentNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<ContentNode> nodes) {
		this.nodes = nodes;
	}

}
