package com.ping.adt.sapgui.quicklogin.widgets;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ping.adt.sapgui.quicklogin.internal.LoginConfiguration;

/**
 * 系统配置信息编辑框
 */
public class LoginConfigurationModifyDialog extends Dialog {
	LoginConfiguration configuration;

	Text menuItemNameText;
	SapSystemNameCombo systemNameCombo;
	Text clientText;
	Text languageText;
	Text userNameText;
	Text passwordText;
	Button menuItemVisibleCheckBox;
	Button toolbarItemVisibleCheckBox;

	public LoginConfigurationModifyDialog(Shell parentShell) {
		super(parentShell);
	}

	public LoginConfigurationModifyDialog(Shell parentShell, LoginConfiguration configuration) {
		super(parentShell);
		this.configuration = configuration;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
//		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginRight = 5;
		layout.marginLeft = 10;
		container.setLayout(layout);

		Label menuItemNameLabel = new Label(container, SWT.NONE);
		menuItemNameLabel.setText("菜单项名称*: ");
		menuItemNameText = new Text(container, SWT.NONE);
		menuItemNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label systemNameLabel = new Label(container, SWT.NONE);
		GridData systemNameLabelGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		systemNameLabelGridData.horizontalIndent = 1;
		systemNameLabel.setLayoutData(systemNameLabelGridData);
		systemNameLabel.setText("系统名称*: ");
		systemNameCombo = new SapSystemNameCombo(container, SWT.READ_ONLY);
		systemNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Label clientLabel = new Label(container, SWT.NONE);
		clientLabel.setText("客户端*:");
		clientText = new Text(container, SWT.NONE);
		clientText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Label languageLabel = new Label(container, SWT.NONE);
		languageLabel.setText("语言*:");
		languageText = new Text(container, SWT.NONE);
		languageText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Label userNameLabel = new Label(container, SWT.NONE);
		userNameLabel.setText("用户名*:");
		userNameText = new Text(container, SWT.NONE);
		userNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		Label passwordLabel = new Label(container, SWT.NONE);
		passwordLabel.setText("密码*:");
		passwordText = new Text(container, SWT.PASSWORD);
		passwordText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		menuItemVisibleCheckBox = new Button(container, SWT.CHECK);
		menuItemVisibleCheckBox.setText("作为菜单项");

//		toolbarItemVisibleCheckBox = new Button(container, SWT.CHECK);
//		toolbarItemVisibleCheckBox.setText("作为工具栏项");

		if (!configuration.getMenuItemName().equals("")) {
			menuItemNameText.setEditable(false);
		}

		// 初始话登录框中的值
		menuItemNameText.setText(configuration.getMenuItemName());
		systemNameCombo.setSelected(configuration.getSystemName());
		clientText.setText(configuration.getClient());
		languageText.setText(configuration.getLanguage());
		userNameText.setText(configuration.getUsername());
		passwordText.setText(configuration.getPassword());
		menuItemVisibleCheckBox.setSelection(configuration.isMenuItemVisible());
//		toolbarItemVisibleCheckBox.setSelection(configuration.isToolbarItemVisible());

		return container;
	}

	// override method to use "Login" as label for the OK button
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确认", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	@Override
	protected void okPressed() {
		// 确认后写入配置对象

		configuration.setMenuItemName(menuItemNameText.getText());
		configuration.setSystemName(systemNameCombo.getSelected());
		configuration.setClient(clientText.getText());
		configuration.setLanguage(languageText.getText());
		configuration.setUsername(userNameText.getText());
		configuration.setPassword(passwordText.getText());
		configuration.setMenuItemVisible(menuItemVisibleCheckBox.getSelection());
//		configuration.setToolbarItemVisible(toolbarItemVisibleCheckBox.getSelection());
		super.okPressed();
	}

	/**
	 * 返回配置
	 * 
	 * @return 配置
	 */
	public LoginConfiguration getConfiguration() {
		return this.configuration;
	}
}
