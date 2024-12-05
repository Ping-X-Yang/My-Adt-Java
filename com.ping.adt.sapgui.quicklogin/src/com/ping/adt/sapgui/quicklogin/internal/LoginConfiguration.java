package com.ping.adt.sapgui.quicklogin.internal;

/**
 * 配置信息
 */
public class LoginConfiguration {

	// SAP系统名称
	private String systemName = "";

	// 菜单名称
	private String menuItemName = "";

	// 登录客户端
	private String client = "";

	// 登录语言
	private String language = "ZH";

	// 用户名称
	private String username = "";

	// 密码
	private String password = "";

	// 作为菜单项
	private boolean menuItemVisible = false;

	// 作为工具栏按钮
	private boolean toolbarItemVisible = false;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getMenuItemName();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.getMenuItemName().hashCode();
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getMenuItemName() {
		return menuItemName;
	}

	public void setMenuItemName(String menuItemName) {
		this.menuItemName = menuItemName;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isMenuItemVisible() {
		return menuItemVisible;
	}

	public void setMenuItemVisible(boolean menuItemVisible) {
		this.menuItemVisible = menuItemVisible;
	}

	public boolean isToolbarItemVisible() {
		return toolbarItemVisible;
	}

	public void setToolbarItemVisible(boolean toolbarItemVisible) {
		this.toolbarItemVisible = toolbarItemVisible;
	}

	/**
	 * 克隆
	 */
	public LoginConfiguration clone() {
		LoginConfiguration configuration = new LoginConfiguration();
		configuration.setMenuItemName(menuItemName);
		configuration.setSystemName(systemName);
		configuration.setClient(client);
		configuration.setUsername(username);
		configuration.setPassword(password);
		configuration.setLanguage(language);
		configuration.setMenuItemVisible(menuItemVisible);
		configuration.setToolbarItemVisible(toolbarItemVisible);
		return configuration;
	}

}
