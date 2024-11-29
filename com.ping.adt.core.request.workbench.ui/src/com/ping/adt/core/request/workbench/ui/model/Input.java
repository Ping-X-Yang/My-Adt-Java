package com.ping.adt.core.request.workbench.ui.model;

import java.util.ArrayList;

import com.alibaba.fastjson2.annotation.JSONField;

public class Input {
	
	@JSONField(name = "object")
	public AdtObject object = new AdtObject();
	
	@JSONField(name = "requests")
	public ArrayList<String> requests = new ArrayList<String>(); 
	
	public class AdtObject{
		
		@JSONField(name = "type")
		public String type = "";
		
		@JSONField(name = "name")
		public String name = "";
	}
}
