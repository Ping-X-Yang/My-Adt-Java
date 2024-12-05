
package com.ping.adt.sapgui.quicklogin.handlers;

import org.eclipse.e4.core.di.annotations.Execute;

import com.ping.adt.sapgui.quicklogin.internal.LoginConfiguration;
import com.ping.adt.sapgui.quicklogin.internal.MyPlugin;
import com.ping.adt.sapgui.quicklogin.internal.Sapshcut;

import jakarta.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;

public class DynamicMenuItemHandler {
	@Execute
	public void execute(@Named("com.ping.adt.sapgui.quicklogin.commandparameter.systemName") String parameter) {
		// 查找参数对应的配置
		LoginConfiguration configuration = MyPlugin.model.getElements().stream()
				.filter(e -> e.getMenuItemName().equals(parameter)).findAny().get();

		// 打开SAP GUI
		Sapshcut.run(MyPlugin.path, configuration);
	}

	@CanExecute
	public boolean canExecute() {

		return true;
	}

}