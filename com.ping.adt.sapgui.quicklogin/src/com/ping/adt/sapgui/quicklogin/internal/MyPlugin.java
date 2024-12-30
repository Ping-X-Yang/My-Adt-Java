package com.ping.adt.sapgui.quicklogin.internal;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;

/**
 * 插件内的静态数据
 */
public class MyPlugin {

	public static LoginConfigurationListModel model;
	public static String path;
	public static boolean isVisibleTcodeInputDialog = true;
	
	
	static {
		init();
	}

	static void init() {
		
		// 加载配置
		// 避免重复从文件系统中读取配置
		loadConfiguration();

	}

	private static void loadConfiguration() {
		
		if (model != null && path != null) {
			return;
		}
		
		String modelJson = "";

		ISecurePreferences preferences = SecurePreferencesFactory.getDefault();
		ISecurePreferences node = preferences.node(PluginConstants.NODE_ID);
		try {
			path = node.get(PluginConstants.KEY_PATH, "");
			modelJson = node.get(PluginConstants.KEY_CONFIGURATION, "");
			isVisibleTcodeInputDialog = Boolean.parseBoolean(node.get(PluginConstants.VISIBLE_TCODE_INPUT_DIALOG, "true"));
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 初始化模型
		model = new LoginConfigurationListModel(modelJson);
	}
}