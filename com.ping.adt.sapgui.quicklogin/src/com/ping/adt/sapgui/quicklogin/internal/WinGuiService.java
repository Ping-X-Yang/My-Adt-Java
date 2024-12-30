package com.ping.adt.sapgui.quicklogin.internal;

import java.util.Map;

import com.ping.adt.sapgui.quicklogin.gui.SapGuiStartupData;
import com.ping.adt.sapgui.quicklogin.gui.WinGuiServerProxy;
import com.sap.adt.destinations.model.IDestinationDataWritable;
import com.sap.adt.destinations.model.ISystemConfiguration;
import com.sap.adt.destinations.model.config.AdtSystemConfigurationServiceFactory;
import com.sap.adt.destinations.model.internal.DestinationDataWritable;
import com.sap.adt.tools.core.IAdtObjectReference;

public class WinGuiService {

	private static LoginConfiguration logonConfiguration = null;
	private static ISystemConfiguration systemConfiguration = null;
	
	
	public static boolean addConfiguration(String parameter) {
		addLogonConfiguration(parameter);
		addSystemConfiguration();
		
		if (logonConfiguration == null || systemConfiguration == null) {
			return false;
		}
		return true;
	}
	
	private static void addLogonConfiguration(String parameter) {
		// 查找参数对应的配置
		logonConfiguration = MyPlugin.model.getElements().stream().filter(e -> e.getMenuItemName().equals(parameter)).findAny().get();
	}
	
	private static void addSystemConfiguration() {
		Map<String, ISystemConfiguration> systemConfigurationMap = null;

		// 获取系统名称
		// 配置来源是SAPGUI上配置的登录系统
		try {
			systemConfigurationMap = AdtSystemConfigurationServiceFactory.createSystemConfigurationService()
					.getSystemConfigurations();
			systemConfiguration = systemConfigurationMap.get(logonConfiguration.getSystemName());
		} catch (Exception var3) {
		}
	}
	
	
	public static void openGui(IAdtObjectReference objectReference) {
		// 组合配置数据
		IDestinationDataWritable destinationDataWritable = new DestinationDataWritable(
				String.format("%d", System.currentTimeMillis()));
		destinationDataWritable.setSystemConfiguration(getSystemConfiguration());
		destinationDataWritable.setClient(getLogonConfiguration().getClient());
		destinationDataWritable.setUser(getLogonConfiguration().getUsername());
		destinationDataWritable.setPassword(getLogonConfiguration().getPassword());
		destinationDataWritable.setLanguage(getLogonConfiguration().getLanguage());

		// 创建启动参数
		SapGuiStartupData startupInfo = new SapGuiStartupData(destinationDataWritable, objectReference, false,
				"WB:DISPLAY", null, null, true);

		// 启动Winows上的SAP GUI,并导航至目标
		WinGuiServerProxy.getProxy().openConnection(startupInfo, 0);

	}

	public static void openGui() {
		// 跳转到GUI的导航页
//		openGui("SESSION_MANAGER");
		openGui("SMEN");
	}

	public static void openGui(String tcode) {
		// 组合配置数据
		IDestinationDataWritable destinationDataWritable = new DestinationDataWritable(
				String.format("%d", System.currentTimeMillis()));
		destinationDataWritable.setSystemConfiguration(getSystemConfiguration());
		destinationDataWritable.setClient(getLogonConfiguration().getClient());
		destinationDataWritable.setUser(getLogonConfiguration().getUsername());
		destinationDataWritable.setPassword(getLogonConfiguration().getPassword());
		destinationDataWritable.setLanguage(getLogonConfiguration().getLanguage());
		

		// 创建启动参数
		SapGuiStartupData startupInfo = new SapGuiStartupData(destinationDataWritable, tcode, false, null, null, true);

		// 启动Winows上的SAP GUI,并导航至目标
		WinGuiServerProxy.getProxy().openConnection(startupInfo, 0);
	}

	public static LoginConfiguration getLogonConfiguration() {
		return logonConfiguration;
	}

	public static void setLogonConfiguration(LoginConfiguration logonConfiguration) {
		WinGuiService.logonConfiguration = logonConfiguration;
	}

	public static ISystemConfiguration getSystemConfiguration() {
		return systemConfiguration;
	}

	public static void setSystemConfiguration(ISystemConfiguration systemConfiguration) {
		WinGuiService.systemConfiguration = systemConfiguration;
	}
	
}