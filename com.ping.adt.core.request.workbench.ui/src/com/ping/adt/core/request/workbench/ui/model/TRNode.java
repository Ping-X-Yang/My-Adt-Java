package com.ping.adt.core.request.workbench.ui.model;

import java.util.Iterator;
import java.util.List;

import com.ping.adt.core.request.workbench.ui.model.TasksOutput.TRObject;
import com.ping.adt.core.request.workbench.ui.model.TasksOutput.Task;

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
	
	public void addNode(TasksOutput.Data data) {
		int nextPosition = 0;
		
		//添加任务号
		for (Task task : data.tasks) {
			TaskNode node = new TaskNode();
			node.setNodeId(task.task);
			node.setOnwer(task.user);
//			node.setText(task.text);
			node.setLastChange(task.lastChangeDate + " " + task.lastChangeTime );
			node.setStatus(task.status);
			node.setPosition(nextPosition++);
			this.addChild(node);
			
			//添加对象
			addObjects(task.taskObjects, node, 0);
		}
		
		//添加对象
		addObjects(data.requestObjects, this, nextPosition);
		
	}
	
	void addObjects(List<TasksOutput.TRObject> objects, RequestNode node, int nextPosition){
		//添加对象 
				for (TRObject obj : objects) {
					TRObjectNode objectNode = new TRObjectNode();
					objectNode.setNodeId(obj.objectType+ obj.programId +obj.objectName);
					objectNode.setNodeName(obj.objectName);
					objectNode.setProgramId(obj.programId);
					objectNode.setObjectType(obj.objectType);
					objectNode.setWorkbenchObjectType(obj.workbenchObjectType);
					objectNode.setObjectTypeText(obj.objectTypeText);
					objectNode.setObjectName(obj.objectName);
					objectNode.setText(obj.objectName);
					objectNode.setPosition(nextPosition++);
					node.addChild(objectNode);
				}
	}
	
}
