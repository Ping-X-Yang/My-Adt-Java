package com.ping.adt.sapgui.quicklogin.gui;

import java.util.Map;

public class WinGuiPipeXml {
	protected static final String NAME_ATTRIBUTE = "name";
	protected static final String SAPGUI_PARAM_TAG = "SAPGUI:param";
	protected static final String SAPGUI_PARAMS_TAG = "SAPGUI:params";
	protected static final String SAPGUI_EVENT_TAG = "SAPGUI:Event";
	protected static final String SAPGUI_EVENT_TAG_END = "</SAPGUI:Event>";
	protected static final String SAPGUI_EVENT_RESULT_TAG = "SAPGUI:EventResult";
	protected static final String SAPGUI_COMMAND_TAG = "SAPGUI:Command";
	protected static final String SAPGUI_COMMAND_RESULT_TAG = "SAPGUI:CommandResult";
	protected static final String SAPGUI_PARAM = "<SAPGUI:param name=\"%s\">%s</SAPGUI:param>\n";
	protected static final String SAPGUI_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-16\" ?>\n";
	protected static final String SAPGUI_START = "<%s name=\"%s\" xmlns:SAPGUI=\"http://www.w3.org/2001/XMLSchema\">\n";
	public static final String COMMAND_PROPERTY_COMMAND_PIPE = "CommandPipe";
	public static final String COMMAND_CREATE_SESSION = "CreateSession";
	public static final String COMMAND_SET_FOCUS = "SetFocus";
	public static final String COMMAND_SET_MAINFRAME_ACTIVE = "MainFrameIsActive";
	public static final String COMMAND_SHUTDOWN = "Shutdown";
	public static final String COMMAND_START_TRANSACTION = "StartTransaction";
	public static final String COMMAND_DESTROY_SESSION = "DestroySession";
	public static final String COMMAND_START_OKCODE = "StartOKCode";
	public static final String COMMAND_STOP_TRANSACTION = "StopTransaction";
	public static final String COMMAND_PROPERTY_TRANSACTION = "transaction";
	public static final String COMMAND_PROPERTY_SHORTCUT_PARAMS = "shortcutParams";
	public static final String COMMAND_SUBSCRIBE_HOTKEYS = "SubscribeHotkeys";
	public static final String COMMAND_PROPERTY_VALUE_MERGE_TOOLBARS = "0";
	public static final String COMMAND_PROPERTY_MERGE_TOOLBARS = "mergeToolbars";
	public static final String COMMAND_PROPERTY_SSO2 = "cookie";
	public static final String COMMAND_PROPERTY_USER = "user";
	public static final String COMMAND_PROPERTY_PASSWORD = "password";
	public static final String EVENT_SESSION_CREATED = "SessionCreated";
	public static final String EVENT_SESSION_DESTROYED = "SessionDestroyed";
	public static final String EVENT_SHUTDOWN = "Shutdown";
	public static final String EVENT_REQUEST_ENDED = "RequestEnded";
	public static final String EVENT_LSAPI = "InvokeLSAPI";
	public static final String EVENT_HOTKEY_PRESSED = "SubscribedHotkeyPressed";
	public static final String PROPERTY_ID = "id";
	public static final String PROPERTY_HR = "hr";
	public static final String PROPERTY_ERROR_DESCRIPTION = "errorDescription";
	public static final String PROPERTY_OKCODE = "okcode";
	public static final String PROPERTY_FOCUS_ACTION = "focusAction";
	public static final String PROPERTY_RESTORE_FOCUS = "restoreFocus";
	public static final String PROPERTY_METHOD = "method";
	public static final String PROPERTY_P0 = "p0";
	public static final String PROPERTY_HWND_MAIN_FRAME = "hwndMainFrame";
	public static final String METHOD_RAISE_ADT_EVENT = "raiseAdtEvent";
	public static final String PROPERTY_VALUE_HR_OK = "S_OK";
	public static final String PROPERTY_KEYCODE = "keyCode";
	public static final String PROPERTY_SHIFT_PRESSED = "shiftPressed";
	public static final String PROPERTY_CTRL_PRESSED = "ctrlPressed";
	public static final String PROPERTY_ALT_PRESSED = "altPressed";
	public static final String XML_FALSE = "0";
	public static final String XML_TRUE = "1";
	protected String name;
	protected Map<String, String> properties;

	public static String boolToXmlString(boolean value) {
		return value ? "1" : "0";
	}

	public String getProperty(String key) {
		if (this.properties != null) {
			return (String) this.properties.get(key);
		}
		return null;
	}

	public String getName() {
		return this.name;
	}

	protected Object composeOuterTag(String tag, String name) {
		return String.format("<%s name=\"%s\" xmlns:SAPGUI=\"http://www.w3.org/2001/XMLSchema\">\n",
				new Object[] { tag, name });
	}

	protected void appendPropertyXml(StringBuilder builder, Map.Entry<String, String> entry) {
		builder.append(String.format("<SAPGUI:param name=\"%s\">%s</SAPGUI:param>\n",
				new Object[] { entry.getKey(), escapeForXml((String) entry.getValue()) }));
	}

	private static String escapeForXml(String value) {
		if (value != null) {
			value = value.replaceAll("&", "&amp;");
			value = value.replaceAll("<", "&lt;");
			value = value.replaceAll(">", "&gt;");
			value = value.replaceAll("\"", "&quot;");
			value = value.replaceAll("'", "&apos;");
		}
		return value;
	}

	protected void appendOpenTag(StringBuilder builder, String tag) {
		builder.append("<").append(tag).append(">\n");
	}

	protected void appendClosingTag(StringBuilder builder, String tag) {
		builder.append("</").append(tag).append(">\n");
	}
}
