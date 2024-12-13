package com.ping.adt.core.request.workbench.ui.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ping.adt.core.request.workbench.ui.model.RequestOutput.Data;

public class TransportRequestModel extends RequestNode {


	public TransportRequestModel(RequestOutput ouput) {

		var dataList = ouput.data;

		

		for (Data data : dataList) {
			List<Class> classes = new ArrayList<Class>();
			classes.add(FunctionNode.class);
			classes.add(SystemNode.class);
			classes.add(StatusNode.class);
			classes.add(TRNode.class);
			
			
			List<String> conditions = new ArrayList<String>();
			conditions.add(data.type);
			conditions.add(data.target);
			conditions.add(data.status);
			conditions.add(data.request);
			
			findAndCreateNode(getChildren(), conditions, data, classes);
		}

	}

	private void findAndCreateNode(List<RequestNode> children, List<String> conditions, Data data,
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

		for (RequestNode child : children) {
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
			children.add(node);
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
		}
		
		
		findAndCreateNode(node.getChildren(), conditions, data, classes);

	}

}
