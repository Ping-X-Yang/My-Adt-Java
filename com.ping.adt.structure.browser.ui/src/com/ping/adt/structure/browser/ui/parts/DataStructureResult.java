package com.ping.adt.structure.browser.ui.parts;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.annotation.JSONField;
import com.ping.adt.structure.browser.ui.common.DataStructureResponseStatus;

public class DataStructureResult {
	
	@JSONField(name = "status")
	public DataStructureResponseStatus status = new DataStructureResponseStatus();
	
	@JSONField(name = "data")
	public List<FieldNode> data = new ArrayList<FieldNode>();
}
