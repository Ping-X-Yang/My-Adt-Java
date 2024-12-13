package com.ping.adt.core.request.workbench.ui.model;

public class SystemNode extends RequestNode {

	@Override
	public void setNodeId(String nodeId) {
		super.setNodeId(nodeId);
		setNodeName(nodeId);
		if (nodeId.equals("")) {
			setNodeName("本地请求");
		}
	}
	

}
