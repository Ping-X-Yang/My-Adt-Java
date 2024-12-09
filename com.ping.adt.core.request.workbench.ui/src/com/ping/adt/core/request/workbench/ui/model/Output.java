package com.ping.adt.core.request.workbench.ui.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.annotation.JSONField;

public class Output {
	
	@JSONField(name = "status")
	public Status status = new Status();
	
	@JSONField(name = "data")
	public List<Data> data = new ArrayList<Output.Data>();
	
	
	public class Status {
		@JSONField(name = "code")
		public int code = 0;
		
		@JSONField(name = "message")
		public String message = "";
	}
	
	
	
	public class Data {
		@JSONField(name = "request")
		public String request = "";
		
		@JSONField(name = "username")
		public String username = "";
		
		@JSONField(name = "text")
		public String text = "";
		
		@JSONField(name = "message")
		public List<RequestMessage> message = new ArrayList<RequestMessage>();
	}
	
	public class RequestMessage {
		@JSONField(name = "type")
		public String type = "";
		
		@JSONField(name = "message")
		public String message = "";
	}
}
