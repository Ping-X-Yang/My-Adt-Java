package com.ping.adt.sapgui.quicklogin.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.sap.adt.destinations.model.ISystemConfiguration;
import com.sap.adt.destinations.model.config.AdtSystemConfigurationServiceFactory;

/**
 * SAP系统名称下拉框控件
 */
public class SapSystemNameCombo {

	ComboViewer combo;
	List<String> systemNameList = new ArrayList<String>();

	public SapSystemNameCombo(Composite parent, int style) {

		initSystemNameList();

		combo = new ComboViewer(parent, style);
		combo.setContentProvider(ArrayContentProvider.getInstance());
		combo.setInput(systemNameList.toArray());
		if (systemNameList.size() > 0) {
			setSelected(systemNameList.get(0));
		}
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		combo.addSelectionChangedListener(listener);
	}

	public void setLayoutData(Object layoutData) {
		getCombo().setLayoutData(layoutData);
	}

	public Combo getCombo() {
		return combo.getCombo();
	}

	/**
	 * 返回选中的系统名称
	 * 
	 * @return
	 */
	public String getSelected() {
		String text;
		text = combo.getStructuredSelection().getFirstElement().toString();
		if (text == null) {
			text = "";
		}
		return text;
	}

	/**
	 * 设置选中系统
	 * 
	 * @param value 系统名称
	 */
	public void setSelected(String value) {
		combo.setSelection(new StructuredSelection(value));
	}

	/**
	 * 初始化下拉框列表
	 */
	@SuppressWarnings("restriction")
	private void initSystemNameList() {
		Map<String, ISystemConfiguration> systemConfigurationMap = null;

		// 获取系统名称
		// 配置来源是SAPGUI上配置的登录系统
		try {
			systemConfigurationMap = AdtSystemConfigurationServiceFactory.createSystemConfigurationService()
					.getSystemConfigurations();
		} catch (Exception var3) {
			return;
		}

		for (Entry<String, ISystemConfiguration> entry : systemConfigurationMap.entrySet()) {
			systemNameList.add(entry.getKey());
		}

	}

}
