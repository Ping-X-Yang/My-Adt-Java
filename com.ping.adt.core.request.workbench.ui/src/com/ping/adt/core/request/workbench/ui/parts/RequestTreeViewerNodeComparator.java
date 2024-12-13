package com.ping.adt.core.request.workbench.ui.parts;

import org.eclipse.jface.viewers.ViewerComparator;

import com.ping.adt.core.request.workbench.ui.model.RequestNode;

public class RequestTreeViewerNodeComparator extends ViewerComparator {
	@Override
	public int category(Object element) {
		int position = super.category(element);
		try {
			RequestNode node = (RequestNode) element;
			position = node.getPosition();
		} catch (Exception e) {
		}
		return position;
	}
}
