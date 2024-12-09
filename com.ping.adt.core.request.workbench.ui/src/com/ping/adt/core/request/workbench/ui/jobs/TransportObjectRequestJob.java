package com.ping.adt.core.request.workbench.ui.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.notifications.NotificationPopup;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.ping.adt.core.request.workbench.ui.common.MyPluginContants;
import com.ping.adt.core.request.workbench.ui.events.MessageTool;
import com.ping.adt.core.request.workbench.ui.model.Input;
import com.ping.adt.core.request.workbench.ui.model.ObjectsOutput;
import com.ping.adt.core.request.workbench.ui.model.Output;
import com.ping.adt.core.tools.MyRestClient;

import jakarta.inject.Inject;
import jakarta.inject.Named;

public class TransportObjectRequestJob {
	
	String type;
	String name;
	String action;
	
	
	Shell shell;
	
	@Inject
	UISynchronize sync;
	
	IEventBroker broker;
	
	Display display;
	
	Job job;
	private String requestType;
	
	public TransportObjectRequestJob(Shell shell, String type, String name, String action, String requestType, IEventBroker eventBroker) {
		this.type = type;
		this.name = name;
		this.action = action;
		this.shell = shell;
		this.requestType = requestType;
		display = shell.getDisplay();
		this.broker = eventBroker;
		
		createJob();
	}
	
	public void run() {
		job.schedule();
	}

	private void createJob() {
		MyRestClient objectClient = new MyRestClient("objects");
		MyRestClient requestsClient = new MyRestClient("requests");
		String jobName = String.format("JOB: %s > %s", this.action, this.name);
		job = Job.create(jobName, (ICoreRunnable) monitor -> {
			//异步处理
			
			
			//获取对象的修改请求号
			objectClient.addParam("type", this.type);
			objectClient.addParam("name", this.name);
			objectClient.setResource("information");
			ObjectsOutput objectsOutput =  objectClient.get(ObjectsOutput.class);
			if (objectsOutput == null) {
				return;
			}
			
			//获取请求号
			String request = "";
			switch (this.requestType) {
			case MyPluginContants.REQUEST:	//请求
				request = objectsOutput.data.currentRequest;
				break;
			case MyPluginContants.TASK:	//任务
				request = objectsOutput.data.currentTask;
				break;
			default:
				break;
			}
			
			
			//接口入参
			Input input = new Input();
			input.requests.add(request);
			
			//设置参数
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("action", this.action);
			
			//发送请求
			Output output = requestsClient.post(input, Output.class, map);
			
			
			//UI同步
			this.display.asyncExec(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String message = null;
					
					if (output == null) {
						return;
					}
					
					
					if (output.status.code == 0) {
						//报错
						message = "服务处理异常";
					}
					
					if (output.data.size() > 0 && output.data.get(0).message.size() > 0) {
						//报错
						message = output.data.get(0).message.get(0).message;
					}
					
					if(message == null) {
						message = String.format("%s传输成功!!!", input.requests.get(0));
					}
					
					String title = "";
					switch (action) {
					case MyPluginContants.ONE_KEY_COPY:	//请求
						title = "快捷副本";
						break;
					case MyPluginContants.ONE_KEY_QAS:	//任务
						title = "快捷QAS";
						break;
					case MyPluginContants.ONE_KEY_PRD:	//任务
						title = "快捷PRD";
						break;
					default:
						break;
					}
					
					//发送消息
					MessageTool.PostMessage(broker, "com.ping.adt.core.request.workbench.ui.WorkbenchView", title, output.data);
					
//					NotificationPopup
//						.forDisplay(display)
////						.forShell(shell)
//						.text(message)
//						.title("请求传输通知", true)
//						.delay(10000)			//10秒
//						.fadeIn(true)
//						.open();
				}
			});
			
		});
		
	}
}
