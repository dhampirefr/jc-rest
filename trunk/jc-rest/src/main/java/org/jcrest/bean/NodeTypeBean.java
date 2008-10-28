package org.jcrest.bean;

public class NodeTypeBean {
	
	private String name;
	private boolean mixin;
	private boolean orderableChildNodes;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isMixin() {
		return mixin;
	}
	
	public void setMixin(boolean mixin) {
		this.mixin = mixin;
	}

	public boolean isOrderableChildNodes() {
		return orderableChildNodes;
	}

	public void setOrderableChildNodes(boolean orderableChildNodes) {
		this.orderableChildNodes = orderableChildNodes;
	}

}
