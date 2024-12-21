package com.ping.adt.sapgui.quicklogin.gui;

public class WinGuiPipeXmlCommand extends WinGuiPipeXmlResponse {
	public WinGuiPipeXmlCommand(String name) {
		super(name);
	}

	protected String getOuterTag() {
		return "SAPGUI:Command";
	}
}
