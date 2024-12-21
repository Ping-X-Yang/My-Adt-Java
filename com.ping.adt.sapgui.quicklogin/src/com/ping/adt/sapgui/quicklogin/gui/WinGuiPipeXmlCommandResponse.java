package com.ping.adt.sapgui.quicklogin.gui;

import javax.xml.parsers.SAXParser;

public class WinGuiPipeXmlCommandResponse extends WinGuiPipeEventParser {
	public WinGuiPipeXmlCommandResponse(String responseXml, SAXParser parser) {
		super(responseXml, parser);
	}

	protected String getOuterTag() {
		return "SAPGUI:CommandResult";
	}
}
