package com.ping.adt.sapgui.quicklogin.internal;

import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;

public class Sapshcut {

	/**
	 * 快速启动SAP GUI
	 * 
	 * @param path          程序路径
	 * @param configuration 配置信息
	 */
	public static void run(String path, LoginConfiguration configuration) {
		if (path.isEmpty() || configuration == null) {
			return;
		}

		String app = path + "\\sapshcut";
		String commandString = String.format("%s -sysname=%s -client=%s -user=%s -pw=%s -language=%s ", app,
				configuration.getSystemName(), configuration.getClient(), configuration.getUsername(),
				configuration.getPassword(), configuration.getLanguage());

		// 打开SAPGUI
		CommandLine command = CommandLine.parse(commandString);
		Executor executor = DefaultExecutor.builder().get();
		try {
			executor.execute(command);
		} catch (ExecuteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
