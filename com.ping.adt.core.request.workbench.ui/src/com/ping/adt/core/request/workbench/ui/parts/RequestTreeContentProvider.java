package com.ping.adt.core.request.workbench.ui.parts;

import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;

import com.ping.adt.core.request.workbench.ui.model.RequestNode;
import com.ping.adt.core.request.workbench.ui.model.TRNode;
import com.ping.adt.core.request.workbench.ui.model.TRObjectNode;
import com.ping.adt.core.request.workbench.ui.model.TaskNode;

public class RequestTreeContentProvider implements ITreeContentProvider,PropertyChangeListener {

	private CheckboxTreeViewer viewer;

	@Override
	public Object[] getElements(Object inputElement) {
		RequestNode model = (RequestNode) inputElement;
		return model.getChildren().toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		RequestNode model = (RequestNode) parentElement;
//		if ( !(parentElement instanceof TRNode) &&  !(parentElement instanceof TaskNode)) {
//			viewer.setGrayed(parentElement, true);
//		}
		return model.getChildren().toArray();
	}

	@Override
	public Object getParent(Object element) {
		RequestNode node = (RequestNode) element;
		return node.getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof TRObjectNode) {
			return false;
		}
		return true;
	}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (CheckboxTreeViewer)viewer;
		if (newInput instanceof RequestNode) {
			((RequestNode) newInput).addPropertyChageListener(this);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("addNode".equals(evt.getPropertyName())) {
			viewer.refresh();
		}
	}

}


//public class RequestTreeContentProvider extends ObservableListTreeContentProvider<RequestNode> {
//
//	public RequestTreeContentProvider(
//			IObservableFactory<? super RequestNode, ? extends IObservableList<RequestNode>> listFactory,
//			TreeStructureAdvisor<? super RequestNode> structureAdvisor) {
//		super(listFactory, structureAdvisor);
//	}
//	
//	@Override
//	public Object[] getElements(Object inputElement) {
//		RequestNode model = (RequestNode) inputElement;
//		return model.getChildren().toArray();
//	}
//	
//	@Override
//	public Object[] getChildren(Object parentElement) {
//		RequestNode model = (RequestNode) parentElement;
//		return model.getChildren().toArray();
//	}
//	
//	@Override
//	public Object getParent(Object element) {
//		return null;
//	}
//	
//	@Override
//	public boolean hasChildren(Object element) {
//		return true;
//	}
//
//
//}