
package com.ping.adt.sapgui.quicklogin.handlers;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.di.AboutToHide;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;

import com.ping.adt.sapgui.quicklogin.internal.LoginConfiguration;
import com.ping.adt.sapgui.quicklogin.internal.MyPlugin;
import com.ping.adt.sapgui.quicklogin.internal.PluginConstants;

public class DynamicMenu {
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items, ECommandService commandService) {
		// 获取命令参数对象
		IParameter parameter;
		Command command = commandService.getCommand("com.ping.adt.sapgui.quicklogin.MFD.command");
		try {
			parameter = command.getParameter("com.ping.adt.sapgui.quicklogin.commandparameter.systemName");
		} catch (NotDefinedException e) {
			e.printStackTrace();
			return;
		}

		// 包装命令
		MCommand mCommand = MCommandsFactory.INSTANCE.createCommand();
		mCommand.setElementId("com.ping.adt.sapgui.quicklogin.MFD.command");

		// 动态添加菜单项
		for (LoginConfiguration configuration : MyPlugin.model.getElements()) {
			if (configuration.isMenuItemVisible()) {
				MHandledMenuItem menuItem = MMenuFactory.INSTANCE.createHandledMenuItem();
				items.add(menuItem);
				menuItem.setLabel(configuration.getMenuItemName());
				menuItem.setIconURI(PluginConstants.SAP_GUI_ICON);
				menuItem.setCommand(mCommand);

				// 设置参数值
				Parameterization[] parameterizations = new Parameterization[1];
				parameterizations[0] = new Parameterization(parameter, configuration.getMenuItemName());
				ParameterizedCommand parameterizedCommand = new ParameterizedCommand(command, parameterizations);
				menuItem.setWbCommand(parameterizedCommand);
			}
		}

	}

	@AboutToHide
	public void aboutToHide(List<MMenuElement> items) {

	}

}