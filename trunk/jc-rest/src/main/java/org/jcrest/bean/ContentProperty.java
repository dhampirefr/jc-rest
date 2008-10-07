package org.jcrest.bean;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class ContentProperty {
	
	private String name;
	private String type;
	private List<String> values;
	
	public boolean equals(Object o) {
		
		if (o == null)
			return false;
		
		if (o == this)
			return true;
		
		if (!(o instanceof ContentProperty))
			return false;
		
		final ContentProperty prop = (ContentProperty) o;
		
		return prop.getName().equals(name);
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElements(
		@XmlElement(name="value", type=String.class))
	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

}
