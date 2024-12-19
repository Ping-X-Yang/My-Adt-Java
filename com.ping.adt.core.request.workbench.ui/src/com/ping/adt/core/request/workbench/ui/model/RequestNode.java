package com.ping.adt.core.request.workbench.ui.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.annotation.JSONField;
import com.ping.adt.core.request.workbench.ui.model.RequestOutput.Data;

public class RequestNode {
	
	private String nodeId = "";
	private String nodeName = "";
	private String onwer = "";
	private String text = "";
	private String lastChange = "";
	private String status = "";
	
	private String programId = "";
	private String objectType = "";
	private String workbenchObjectType = "";
	private String objectTypeText = "";
	private String objectName = "";
	
	private int position = 0;
	
	private List<RequestNode> children = new ArrayList<RequestNode>();
	
	private RequestNode parent;
	
	PropertyChangeSupport support = new PropertyChangeSupport(this);
	
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
	
	public void addChild(RequestNode node){
		this.children.add(node);
		support.firePropertyChange("addNode", false, node);
	}
	
	public void addPropertyChageListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}
	
	
	protected void findAndCreateNode(RequestNode parent, List<String> conditions, Data data,
			List<Class> classes) {
		String cond = "";
		Class clazz = null;
		RequestNode node = null;

		try {
			cond = conditions.remove(0);
			clazz = classes.remove(0);
		} catch (Exception e) {
			return;
		}

		for (RequestNode child : parent.getChildren()) {
			if (child.getNodeId().equals(cond)) {
				node = child;
				break;
			}
		}

		if (node == null) {
			try {
				node = (RequestNode) (clazz.getConstructor(null).newInstance(null));
			} catch (Exception e) {
				e.printStackTrace();
			}
			node.setNodeId(cond);
			parent.addChild(node);
		}

		if (clazz.getName().equals(FunctionNode.class.getName())) {
			
		}

		if (clazz.getName().equals(SystemNode.class.getName())) {

		}

		if (clazz.getName().equals(StatusNode.class.getName())) {

		}

		if (clazz.getName().equals(TRNode.class.getName())) {
			node.setOnwer(data.user);
			node.setText(data.text);
			node.setLastChange(data.lastChangeDate + " " +data.lastChangeTime);
			node.setStatus(data.status);
		}
		
		
		findAndCreateNode(node, conditions, data, classes);

	}

	public RequestNode getParent() {
		return parent;
	}

	public void setParent(RequestNode parent) {
		this.parent = parent;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String programId) {
		this.programId = programId;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getWorkbenchObjectType() {
		return workbenchObjectType;
	}

	public void setWorkbenchObjectType(String workbenchObjectType) {
		this.workbenchObjectType = workbenchObjectType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getObjectTypeText() {
		return objectTypeText;
	}

	public void setObjectTypeText(String objectTypeText) {
		this.objectTypeText = objectTypeText;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}
	
	public void removeAll() {
		getChildren().clear();
	}
	
}
