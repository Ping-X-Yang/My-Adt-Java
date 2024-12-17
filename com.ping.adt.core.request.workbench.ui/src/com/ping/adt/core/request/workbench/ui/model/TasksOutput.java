package com.ping.adt.core.request.workbench.ui.model;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson2.annotation.JSONField;

public class TasksOutput {
	@JSONField(name = "status")
	public Status status = new Status();
	
	@JSONField(name = "data")
	public Data data = new Data();
	
	
	public class Status {
		@JSONField(name = "code")
		public int code = 0;
		
		@JSONField(name = "message")
		public String message = "";
	}
	
	
	public class Data {
		@JSONField(name = "request")
		public String request = "";
		
		@JSONField(name = "tasks")
		public List<Task> tasks = new ArrayList<Task>() ;
		
		@JSONField(name = "requestObjects")
		public List<TRObject> requestObjects = new ArrayList<TRObject>() ;
	}
	
	
	public class Task {
		@JSONField(name = "task")
		public String task = "";
		
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
		
		@JSONField(name = "taskObjects")
		public List<TRObject> taskObjects = new ArrayList<TRObject>() ;
	}
	
	public class TRObject {
		@JSONField(name = "position")
		public int position = 0;
		
		@JSONField(name = "programId")
		public String programId = "";
		
		@JSONField(name = "objectType")
		public String objectType = "";
		
		@JSONField(name = "objectName")
		public String objectName = "";
		
		@JSONField(name = "workbenchObjectType")
		public String workbenchObjectType = "";
		
		@JSONField(name = "objectTypeText")
		public String objectTypeText = "";
		
		@JSONField(name = "objectText")
		public String objectText = "";
		
	}
	
}
