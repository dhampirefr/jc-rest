package org.jcrest.bean.wrapper;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.jcrest.bean.ContentEvent;
import org.jcrest.bean.ContentNode;

@XmlRootElement(name="content")
public class ContentWrapper {
	
	private List<ContentNode> nodes;
	private List<ContentEvent> events;

	@XmlElements(
	    @XmlElement(name="node", type=org.jcrest.bean.ContentNode.class))
	public List<ContentNode> getContentNodes() {
		return nodes;
	}

	public void setContentNodes(List<ContentNode> nodes) {
		this.nodes = nodes;
	}
	
	@XmlElements(
		@XmlElement(name="event", type=org.jcrest.bean.ContentEvent.class))
	public List<ContentEvent> getContentEvents() {
		return events;
	}

	public void setContentEvents(List<ContentEvent> events) {
		this.events = events;
	}

}
