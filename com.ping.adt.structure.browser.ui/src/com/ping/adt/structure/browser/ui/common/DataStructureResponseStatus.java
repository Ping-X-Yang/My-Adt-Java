package com.ping.adt.structure.browser.ui.common;

import com.alibaba.fastjson2.annotation.JSONField;

public class DataStructureResponseStatus {
	@JSONField(name = "code")
	public String code = "0";
	
	@JSONField(name = "message")
	public String message = "";
}
