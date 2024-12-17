package com.ping.adt.core.request.workbench.ui.model;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ping.adt.core.request.workbench.ui.model.RequestOutput.Data;

public class TransportRequestModel extends RequestNode {

	public void addNode(RequestOutput ouput) {
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

			findAndCreateNode(this, conditions, data, classes);
		}
	}

}
