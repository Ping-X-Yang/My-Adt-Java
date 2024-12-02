package com.ping.adt.core.request.workbench.ui.model;

import com.alibaba.fastjson2.annotation.JSONField;

public class ObjectsOutput {
	
	@JSONField(name = "status")
	public Status status = new Status();
	
	@JSONField(name = "data")
	public Data data = new Data();
	
	
	public class Status{
		@JSONField(name = "code")
		public int code = 0;
		
		@JSONField(name = "message")
		public String message = "";
	}
	
	public class Data{
		@JSONField(name = "currentRequest")
		public String currentRequest = "";
		
		@JSONField(name = "currentTask")
		public String currentTask = "";
	}
}
