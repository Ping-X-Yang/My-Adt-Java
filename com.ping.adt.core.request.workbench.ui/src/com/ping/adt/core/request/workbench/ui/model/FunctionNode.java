package com.ping.adt.core.request.workbench.ui.model;

public class FunctionNode extends RequestNode  {
	
	@Override
	public void setNodeId(String nodeId) {
		super.setNodeId(nodeId);
		
		switch (nodeId) {
		case "K":	//工作台请求
			setNodeName("工作台");
			setPosition(1);
			break;
		
		case "W":	//定制请求
			setNodeName("定制");
			setPosition(2);
			break;
			
		case "T":	//工作台请求
			setNodeName("副本");
			setPosition(3);
			break;

		default:
			setNodeName("");
		}
	}
	
}
