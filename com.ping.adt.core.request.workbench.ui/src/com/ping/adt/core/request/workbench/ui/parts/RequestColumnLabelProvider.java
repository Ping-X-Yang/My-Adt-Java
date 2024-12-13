package com.ping.adt.core.request.workbench.ui.parts;

import org.eclipse.jface.viewers.StyledString;

import com.ping.adt.core.request.workbench.ui.model.RequestNode;



public class RequestColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public StyledString getStyledText(Object element) {
		RequestNode node = (RequestNode)element;
		StyledString styledString = new StyledString();
		styledString.append(node.getNodeName());
		return styledString;
	}
}
