package com.ping.adt.sapgui.quicklogin.internal;

import com.alibaba.fastjson2.JSON;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class LoginConfigurationListModel {

	List<LoginConfiguration> list = new ArrayList<LoginConfiguration>();
	PropertyChangeSupport support = new PropertyChangeSupport(this);

	public LoginConfigurationListModel() {

	}

	/**
	 * 用json初始化
	 * 
	 * @param modelJson JSON
	 */
	public LoginConfigurationListModel(String modelJson) {
		List<LoginConfiguration> listModel = JSON.parseArray(modelJson, LoginConfiguration.class);
		if (listModel != null) {
			list.addAll(listModel);
		}
	}

	public void add(LoginConfiguration element) {
		if (list.add(element)) {
			support.firePropertyChange("ADD", null, element);
		}
	}

	public void add(int index, LoginConfiguration element) {
		try {
			list.add(index, element);
			support.firePropertyChange("ADD", null, element);
		} catch (Exception e) {
		}
	}

	public void remove(LoginConfiguration element) {
		if (list.remove(element)) {
			support.firePropertyChange("REMOVE", element, null);
		}
	}

	public int getIndex(LoginConfiguration element) {
		return list.indexOf(element);
	}

	public void addPropertyChageListener(PropertyChangeListener listener) {
		support.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListeer(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}

	public List<LoginConfiguration> getElements() {
		return list;
	}

}
