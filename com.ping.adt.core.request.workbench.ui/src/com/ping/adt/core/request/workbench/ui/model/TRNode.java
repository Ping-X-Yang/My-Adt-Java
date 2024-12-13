package com.ping.adt.core.request.workbench.ui.model;

public class TRNode extends RequestNode {
	
	static int trPositon = 0;

	@Override
	public void setNodeId(String nodeId) {
		super.setNodeId(nodeId);
		setNodeName(nodeId);
		setPosition(getNextPosition());
	}
	
	static int getNextPosition() {
		return trPositon++;
	}
	
	@Override
	public String toString() {
		return getNodeName() + " " + getText();
	}
	
}
