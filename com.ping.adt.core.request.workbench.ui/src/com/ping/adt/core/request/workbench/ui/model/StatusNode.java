package com.ping.adt.core.request.workbench.ui.model;

public class StatusNode extends RequestNode {

	@Override
	public void setNodeId(String nodeId) {
		super.setNodeId(nodeId);
		switch (nodeId) {
		case "D":	//可修改的
			setNodeName("可修改");
			setPosition(1);
			break;
		
		case "R":	//已释放
			setNodeName("已释放");
			setPosition(2);
			break;

		default:
			break;
		}
	}
}
