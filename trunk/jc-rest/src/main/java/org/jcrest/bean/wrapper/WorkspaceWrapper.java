package org.jcrest.bean.wrapper;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.jcrest.bean.NamespaceBean;
import org.jcrest.bean.NodeTypeBean;

@XmlRootElement(name="workspace")
public class WorkspaceWrapper {
	
	private List<NamespaceBean> namespaces;
	private List<NodeTypeBean> nodeTypes;
	
	@XmlElementWrapper(name="namespaces")
	@XmlElements(
	    @XmlElement(name="namespace", type=org.jcrest.bean.NamespaceBean.class))
	public List<NamespaceBean> getNamespaces() {
		return namespaces;
	}
	
	public void setNamespaces(List<NamespaceBean> namespaces) {
		this.namespaces = namespaces;
	}
	
	@XmlElementWrapper(name="nodeTypes")
	@XmlElements(
	    @XmlElement(name="nodeType", type=org.jcrest.bean.NodeTypeBean.class))
	public List<NodeTypeBean> getNodeTypes() {
		return nodeTypes;
	}
	
	public void setNodeTypes(List<NodeTypeBean> nodeTypes) {
		this.nodeTypes = nodeTypes;
	}

}
