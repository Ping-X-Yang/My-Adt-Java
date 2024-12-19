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
import com.ping.adt.core.request.workbench.ui.model.RequestOutput;
import com.ping.adt.core.request.workbench.ui.model.SettingModel;
import com.ping.adt.core.request.workbench.ui.model.TransportRequestModel;
import com.ping.adt.core.tools.MyRestClient;
import com.sap.adt.communication.resources.IQueryParameter;
import com.sap.adt.communication.resources.QueryParameter;

import jakarta.inject.Inject;

public class InitRequestModelJob {
	TreeViewer viewer;
	
	@Inject
	UISynchronize sync;
	
	Job job;
	
	private TransportRequestModel requestModel;
	private Display display;
	RequestOutput output;

	public InitRequestModelJob(TreeViewer viewer, TransportRequestModel requestModel) {
		this.display = viewer.getTree().getDisplay();
		this.viewer = viewer;
		this.requestModel = requestModel;
		createJob();
	}
	
	public void run() {
		job.schedule();
	}
	
	private void createJob() {
		//创建客户端
		MyRestClient client = new MyRestClient("requests");
		
		//创建设置模型
		SettingModel settingModel = new SettingModel();
		
		String jobName = String.format("JOB: %s", "请求模型初始化");
		job = Job.create(jobName, (ICoreRunnable) monitor -> {
			//异步处理
			
			List<IQueryParameter> query = new ArrayList<IQueryParameter>();
			query.add(new QueryParameter("user", settingModel.getUserName()));

			// 请求释放状态
			if (settingModel.isModifiable()) {
				query.add(new QueryParameter("status", "D")); // 可修改
			}
			if (settingModel.isModifiable()) {
				query.add(new QueryParameter("status", "R")); // 已释放
			}

			// 请求类型
			if (settingModel.isWorkbench()) {
				query.add(new QueryParameter("function", "K")); // 工作台请求
			}
			if (settingModel.isCustomizing()) {
				query.add(new QueryParameter("function", "W")); // 定制请求
			}
			if (settingModel.isTransportofCopies()) {
				query.add(new QueryParameter("function", "T")); // 副本请求
			}

			query.add(new QueryParameter("start_date", settingModel.getFromDateText()));
			query.add(new QueryParameter("end_date", settingModel.getToDateText()));

			//查询请求
			try {
				InitRequestModelJob.this.output = client.get(RequestOutput.class, query);
			} catch (Exception e) {
				e.printStackTrace();
			}

			
			
			//UI同步
			display.asyncExec(new Runnable() {
				
				@Override
				public void run() {
					if (InitRequestModelJob.this.output == null) {
						return;
					}
					
					//添加到模型
					requestModel.removeAll();
					requestModel.addNode(InitRequestModelJob.this.output);
					
					// 需要展开的节点，默认展开每层额第一个节点
					// 一直展到请求层
					try {
						List<RequestNode> expandNode = new ArrayList<RequestNode>();
						expandNode.add(requestModel.getChildren().get(0)); // 请求类型层
						expandNode.add(requestModel.getChildren().get(0).getChildren().get(0));
						expandNode.add(requestModel.getChildren().get(0).getChildren().get(0).getChildren().get(0));
						viewer.setExpandedElements(expandNode.toArray());
					} catch (Exception e) {
					}
				    
					viewer.refresh();
					
					//清空
					InitRequestModelJob.this.output = null;
				}
			});
			
		});
		
	}

}
