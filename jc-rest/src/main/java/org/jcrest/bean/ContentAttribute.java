package org.jcrest.bean;

public class ContentAttribute {
	
	private String name;
	private String value;
	
	public ContentAttribute() {}
	
	public ContentAttribute(final String name, final String value) {
		
		this.name = name;
		this.value = value;
		
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

}
