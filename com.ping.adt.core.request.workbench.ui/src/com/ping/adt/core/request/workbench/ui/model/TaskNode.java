package com.ping.adt.core.request.workbench.ui.model;

public class TaskNode extends RequestNode {

	@Override
	public void setNodeId(String nodeId) {
		// TODO Auto-generated method stub
		super.setNodeId(nodeId);
		setNodeName(nodeId);
	}
	
}
