package com.ping.adt.core.request.workbench.ui.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import com.ping.adt.core.request.workbench.ui.common.MyPluginContants;
import com.ping.adt.core.request.workbench.ui.events.MessageTool;
import com.ping.adt.core.request.workbench.ui.model.Input;
import com.ping.adt.core.request.workbench.ui.model.ObjectsOutput;
import com.ping.adt.core.request.workbench.ui.model.Output;
import com.ping.adt.core.request.workbench.ui.model.RequestNode;
import com.ping.adt.core.tools.MyRestClient;

import jakarta.inject.Inject;

public class BackendJob {
	TreeViewer viewer;
	
	@Inject
	UISynchronize sync;
	
	Job job;
	
	private Display display;
	private String title;

	public BackendJob(Display display,TreeViewer viewer,String title,String action, List<RequestNode> requestList, IEventBroker broker) {
		this.display = display;
		this.viewer = viewer;
		createJob(title, action, requestList, broker);
	}
	
	public void run() {
		job.schedule();
	}
	
	private void createJob(String title,String action,List<RequestNode> list, IEventBroker broker) {
		MyRestClient requestsClient = new MyRestClient("requests");
		String jobName = String.format("JOB: %s", title);
		job = Job.create(jobName, (ICoreRunnable) monitor -> {
			//异步处理
			
			
			//接口入参
			Input input = new Input();
			for (RequestNode requestNode : list) {
				input.requests.add(requestNode.getNodeName());
			}
			
			
			//设置参数
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("action", action);
			
			//发送请求
			Output output = requestsClient.post(input, Output.class, map);
			
			
			//UI同步
			this.display.asyncExec(new Runnable() {
				
				@Override
				public void run() {
					List<RequestNode> waitRefresh = new ArrayList<RequestNode>();
					
					if (output == null) {
						return;
					}
					
					//更新模型状态
					for(Output.Data d:output.data){
						if (d.message.size() > 0) {
							continue;
						}
						
						Optional<RequestNode> nodeOptional = list.stream().filter(elem -> elem.getNodeId().equals(d.request)).findFirst();
						if (nodeOptional.get() != null) {
							RequestNode node = nodeOptional.get();
							//更新释放状态
							node.setStatus("R");
							waitRefresh.add(node);
						}
					}
					
					//更新树控件
					if (waitRefresh.size() > 0) {
						viewer.update(waitRefresh.toArray(), new String[] {"nodeName"});
					}
					
					//发送消息
					MessageTool.PostMessage(broker, "com.ping.adt.core.request.workbench.ui.WorkbenchView", title, output.data);
				}
			});
			
		});
		
	}

}
