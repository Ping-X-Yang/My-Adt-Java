package com.ping.adt.core.request.workbench.ui.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.annotation.JSONField;

public class RequestOutput {
	@JSONField(name = "status")
	public Status status = new Status();
	
	@JSONField(name = "data")
	public List<Data> data = new ArrayList<Data>();
	
	
	public class Status {
		@JSONField(name = "code")
		public int code = 0;
		
		@JSONField(name = "message")
		public String message = "";
	}
	
	
	
	public class Data {
		@JSONField(name = "request")
		public String request = "";
		
		@JSONField(name = "type")
		public String type = "";
		
		@JSONField(name = "status")
		public String status = "";
		
		@JSONField(name = "target")
		public String target = "";
		
		@JSONField(name = "category")
		public String category = "";
		
		@JSONField(name = "user")
		public String user = "";
		
		@JSONField(name = "lastChangeDate")
		public String lastChangeDate = "";
		
		@JSONField(name = "lastChangeTime")
		public String lastChangeTime = "";
		
		@JSONField(name = "text")
		public String text = "";
	}
}
