package com.ping.adt.sapgui.quicklogin.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WinGuiPipeXmlResponse extends WinGuiPipeXml {
	public WinGuiPipeXmlResponse(String name) {
		this.name = name;
	}

	public void setProperty(String key, String value) {
		getProperties().put(key, value);
	}

	public Map<String, String> getProperties() {
		if (this.properties == null) {
			this.properties = new HashMap();
		}
		return this.properties;
	}

	public String serialize() {
		StringBuilder builder = new StringBuilder();

		builder.append("<?xml version=\"1.0\" encoding=\"UTF-16\" ?>\n");

		builder.append(composeOuterTag(getOuterTag(), this.name));

		Set<Map.Entry<String, String>> entrySet = getProperties().entrySet();

		appendOpenTag(builder, "SAPGUI:params");

		for (Map.Entry<String, String> entry : entrySet) {
			appendPropertyXml(builder, entry);
		}

		appendClosingTag(builder, "SAPGUI:params");

		appendClosingTag(builder, getOuterTag());

		return builder.toString();
	}

	protected String getOuterTag() {
		return "SAPGUI:EventResult";
	}
}
