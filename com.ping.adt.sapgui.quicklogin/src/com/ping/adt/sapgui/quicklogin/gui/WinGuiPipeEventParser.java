package com.ping.adt.sapgui.quicklogin.gui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WinGuiPipeEventParser extends WinGuiPipeXml {
	private final String xml;
	private final SAXParser saxParser;

	public WinGuiPipeEventParser(String eventXml, SAXParser parser) {
		if (parser == null) {
			throw new IllegalArgumentException("Parser must not be null");
		}

		this.xml = eventXml;
		this.saxParser = parser;
	}

	public String getName() {
		if (this.name == null) {
			parse();
		}
		return this.name;
	}

	public String getProperty(String key) {
		if (this.properties == null) {
			parse();
		}
		return super.getProperty(key);
	}

	public void setProperty(String key, String value) {
		if (this.properties == null) {
			parse();
		}
		this.properties.put(key, value);
	}

	void parse() {
		this.properties = new HashMap();

		try {
			ByteArrayInputStream input = new ByteArrayInputStream(this.xml.getBytes("UTF-16"));

			this.saxParser.parse(input, new PipeXmlHandler());
		} catch (SAXException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	protected String getOuterTag() {
		return "SAPGUI:Event";
	}

	class PipeXmlHandler extends DefaultHandler {
		private String currentKey = null;
		private String currentValue = null;

		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {
			if (WinGuiPipeEventParser.this.getOuterTag().equals(qName)) {
				String value = attributes.getValue("name");
				WinGuiPipeEventParser.this.name = value;
			} else if ("SAPGUI:param".equals(qName)) {
				this.currentKey = attributes.getValue("name");
				this.currentValue = null;
			}
		}

		public void characters(char[] ch, int start, int length) throws SAXException {
			if (this.currentKey != null) {
				if (this.currentValue == null) {
					this.currentValue = new String(ch, start, length);
				} else {
					this.currentValue = String.valueOf(this.currentValue) + String.valueOf(this.currentValue);
				}
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (this.currentKey != null) {
				WinGuiPipeEventParser.this.properties.put(this.currentKey, this.currentValue);
				this.currentKey = null;
				this.currentValue = null;
			}
		}
	}

	public Map<String, String> getProperties() {
		if (this.properties == null) {
			parse();
		}
		return this.properties;
	}
}