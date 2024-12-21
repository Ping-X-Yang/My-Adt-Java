package com.ping.adt.sapgui.quicklogin.gui;

import com.sap.adt.sapgui.ui.IGuiVersionInfo;
import com.sap.adt.util.MessageUtil;
import java.util.Locale;

public class WinGuiVersionInfo implements IGuiVersionInfo {
	protected static final int MINIMUM_PROTOCOLVERSION = 13;
	protected static final int MINIMUM_RELEASE = 740;
	private int protocolVersion = -1;
	private int productVersion = -1;

	public void setProtocolVersion(int protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public void setProductVersion(int versionNumber) {
		this.productVersion = versionNumber;
	}

	public String getVersionDescription() {
		if (this.productVersion == -1) {
			return "Unknown (GUI not yet used)";
		}
		return getVersionDescription(Integer.valueOf(this.productVersion), this.protocolVersion);
	}

	public boolean isSupported() {
		return (this.productVersion >= 740 && this.protocolVersion >= 13);
	}

	public String getRequiredVersionDescription() {
		return getVersionDescription(Integer.valueOf(740), 13);
	}

	private static String getVersionDescription(Integer productVersion, int protocolVersion) {
		return MessageUtil.formatMessage(Locale.ENGLISH, "SAP GUI for Windows: {0} / LSAPI protocol version: {1}",
				new Object[] { productVersion, Integer.valueOf(protocolVersion) });
	}
}
