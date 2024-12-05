package com.ping.adt.sapgui.quicklogin.PreferencePage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.ping.adt.sapgui.quicklogin.internal.LoginConfigurationListModel;

public class LoginConfigurationContentProvider
		implements IContentProvider, IStructuredContentProvider, PropertyChangeListener {
	Viewer viewer = null;

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof LoginConfigurationListModel) {
			return ((LoginConfigurationListModel) inputElement).getElements().toArray();
		}
		return new Object[0];
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		if (oldInput instanceof LoginConfigurationListModel) {
			((LoginConfigurationListModel) oldInput).removePropertyChangeListeer(this);
		}

		if (newInput instanceof LoginConfigurationListModel) {
			((LoginConfigurationListModel) newInput).addPropertyChageListener(this);
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("ADD".equals(evt.getPropertyName())) {
			viewer.refresh();
		}

		if ("REMOVE".equals(evt.getPropertyName())) {
			viewer.refresh();
		}
	}

}
