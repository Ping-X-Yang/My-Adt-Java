package com.ping.adt.sapgui.quicklogin.PreferencePage;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.ping.adt.sapgui.quicklogin.internal.LoginConfiguration;

public class LoginConfigurationLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		LoginConfiguration configuration = (LoginConfiguration) element;
		switch (columnIndex) {
		case 0:
			return configuration.getSystemName();
		case 1:
			return configuration.getMenuItemName();
		case 2:
			return configuration.getClient();
		case 3:
			return configuration.getUsername();
		case 4:
			return configuration.getLanguage();
		case 5:
			return configuration.isMenuItemVisible() ? "X" : "";
//		case 6:	//暂时不考虑toolbar
//			return configuration.isToolbarItemVisible()?"X":"";
		default:
			break;
		}
		return null;
	}

}
