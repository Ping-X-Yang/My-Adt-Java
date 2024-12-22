
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
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.renderers.swt.HandledContributionItem;

import com.ping.adt.core.tools.MyAdtTools;
import com.ping.adt.sapgui.quicklogin.internal.LoginConfiguration;
import com.ping.adt.sapgui.quicklogin.internal.MyPlugin;
import com.ping.adt.sapgui.quicklogin.internal.PluginConstants;

public class NavToTargetWithGUI {
	@AboutToShow
	public void aboutToShow(List<MMenuElement> items /*, ECommandService commandService*/ , EModelService modelService) {
		ECommandService commandService = MyAdtTools.getActiveWindow().getService(ECommandService.class);
		
		// 获取命令参数对象
		IParameter parameter;
		Command command = commandService.getCommand("com.ping.adt.sapgui.quicklogin.MFD.command.navToTarget");
		try {
			parameter = command
					.getParameter("com.ping.adt.sapgui.quicklogin.MFD.command.navToTarget.parameter.MenuItemName");
		} catch (NotDefinedException e) {
			e.printStackTrace();
			return;
		}

		// 包装命令
		MCommand mCommand = MCommandsFactory.INSTANCE.createCommand();
		mCommand.setElementId("com.ping.adt.sapgui.quicklogin.MFD.command.navToTarget");

		// 动态添加菜单项
		int count = 0;
		for (LoginConfiguration configuration : MyPlugin.model.getElements()) {
			if (configuration.isMenuItemVisible()) {
				MHandledMenuItem menuItem = modelService.createModelElement(MHandledMenuItem.class);
				
				//加入贡献中
				HandledContributionItem handledContributionItem = new HandledContributionItem();
				handledContributionItem.setModel(menuItem);
				
		
				
//				MHandledMenuItem menuItem = MMenuFactory.INSTANCE.createHandledMenuItem();
				items.add(menuItem);
				menuItem.setElementId("com.ping.adt.sapgui.quicklogin.MFD.menu.navgation.dynamicMenuContribution."+ count++ );
				menuItem.setLabel(configuration.getMenuItemName());
				menuItem.setIconURI(PluginConstants.SAP_GUI_ICON);
				menuItem.setToBeRendered(true);
				menuItem.setVisible(true);
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
		int a = 10;
	}

}