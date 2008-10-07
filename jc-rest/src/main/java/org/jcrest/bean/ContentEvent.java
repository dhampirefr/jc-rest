package org.jcrest.bean;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

public class ContentEvent implements Serializable {
	
	private String path;
	private String type;
	private String user;
	
	private List<String> nodeTypes;
	private List<String> propertyValues;
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}

	@XmlElementWrapper(name="node")
	@XmlElements(
			@XmlElement(name="type", type=String.class))
	public List<String> getNodeTypes() {
		return nodeTypes;
	}

	public void setNodeTypes(List<String> nodeTypes) {
		this.nodeTypes = nodeTypes;
	}

	@XmlElementWrapper(name="property")
	@XmlElements(
	    @XmlElement(name="value", type=String.class))
	public List<String> getPropertyValues() {
		return propertyValues;
	}

	public void setPropertyValues(List<String> propertyValues) {
		this.propertyValues = propertyValues;
	}

}
