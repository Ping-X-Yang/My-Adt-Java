package com.ping.adt.core.request.workbench.ui.parts;

import org.eclipse.jface.viewers.ITreeContentProvider;

import com.ping.adt.core.request.workbench.ui.model.RequestNode;

public class RequestTreeContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		RequestNode model = (RequestNode) inputElement;
		return model.getChildren().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		RequestNode model = (RequestNode) parentElement;
		return model.getChildren().toArray();
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
//		RequestNode model = (RequestNode) element;
//		if (model.getChildren().size() > 0) {
//			return true;
//		}
//		return false;
		return true;
	}

}
