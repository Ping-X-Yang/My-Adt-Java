package com.ping.adt.structure.browser.ui.parts;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

public class StructureTreeContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getElements(Object inputElement) {
		@SuppressWarnings("rawtypes")
		List list = (List)inputElement;
		return list.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		FieldNode node = (FieldNode)parentElement;
		return node.children.toArray();
	}

	@Override
	public Object getParent(Object element) {
		FieldNode node = (FieldNode)element;
		return node.parent;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		FieldNode node = (FieldNode)element;
		if (node.children.size() > 0) {
			return true;
		}
		return false;
	}

}
