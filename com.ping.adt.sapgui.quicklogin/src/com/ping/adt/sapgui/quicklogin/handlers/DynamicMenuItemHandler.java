
package com.ping.adt.sapgui.quicklogin.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;

import com.ping.adt.core.tools.MyAdtTools;
import com.ping.adt.sapgui.quicklogin.internal.LoginConfiguration;
import com.ping.adt.sapgui.quicklogin.internal.MyPlugin;
import com.ping.adt.sapgui.quicklogin.internal.Sapshcut;
import com.ping.adt.sapgui.quicklogin.internal.WinGuiService;

import jakarta.inject.Named;

import java.awt.im.*;

import org.eclipse.e4.core.di.annotations.CanExecute;

import java.util.Locale;

public class DynamicMenuItemHandler {
	@Execute
	public void execute(@Named("com.ping.adt.sapgui.quicklogin.commandparameter.systemName") String parameter) {
//		// 查找参数对应的配置
//		LoginConfiguration configuration = MyPlugin.model.getElements().stream()
//				.filter(e -> e.getMenuItemName().equals(parameter)).findAny().get();
//
//		// 打开SAP GUI
//		Sapshcut.run(MyPlugin.path, configuration);

		// 添加系统，账号
		if (!WinGuiService.addConfiguration(parameter)) {
			return;
		}

		
		//打开GUI
		if (MyPlugin.isVisibleTcodeInputDialog) {
			openInputDialog();
		}else {
			WinGuiService.openGui();
		}
		

	}

	private void openInputDialog() {
		String tcode = null;
		boolean swtich = true;
		
		//切换为英文输入法
		InputContext inputContext = InputContext.getInstance();
		Locale currentLocale = inputContext.getLocale();
		inputContext.selectInputMethod(Locale.ENGLISH);
		
		InputDialog dialog = new InputDialog(MyAdtTools.getActiveShell(), "请输入事务码(可选)", null, null, null);
		if (dialog.open()  == Window.OK) {
			tcode = dialog.getValue();
		}
		
		//切换为上一个状态
		inputContext.selectInputMethod(currentLocale);
		
		if (tcode == null || tcode.equals("")) {
			WinGuiService.openGui();
		}else {
			WinGuiService.openGui(tcode);
		}
		
	}

	@CanExecute
	public boolean canExecute() {

		return true;
	}
	
	

}