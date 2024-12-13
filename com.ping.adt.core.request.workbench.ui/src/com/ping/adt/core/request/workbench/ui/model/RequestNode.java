package com.ping.adt.core.request.workbench.ui.model;

import java.util.ArrayList;
import java.util.List;

public class RequestNode {
	
	private String nodeId = "";
	private String nodeName = "";
	private String onwer = "";
	private String text = "";
	private String lastChange = "";
	
	private int position = 0;
	
	private List<RequestNode> children = new ArrayList<RequestNode>();

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getOnwer() {
		return onwer;
	}

	public void setOnwer(String onwer) {
		this.onwer = onwer;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLastChange() {
		return lastChange;
	}

	public void setLastChange(String lastChange) {
		this.lastChange = lastChange;
	}

	public List<RequestNode> getChildren() {
		return children;
	}

	public void setChildren(List<RequestNode> children) {
		this.children = children;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
	@Override
	public String toString() {
		return this.getNodeName();
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
}
