package com.ping.adt.core.request.workbench.ui.jobs;

import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import com.ping.adt.core.request.workbench.ui.model.TRNode;
import com.ping.adt.core.request.workbench.ui.model.TasksOutput;
import com.ping.adt.core.tools.MyRestClient;

import jakarta.inject.Inject;

public class AsyncUpdateTaskNode {
	TreeViewer viewer;
	
	@Inject
	UISynchronize sync;
	
	Job job;
	
	Display display;
	
	TRNode parent;
	
	public AsyncUpdateTaskNode(Display display,TreeViewer viewer,TRNode parent) {
		this.display = display;
		this.viewer = viewer;
		this.parent = parent;
		
		createJob();
	}
	
	public void run() {
		job.schedule();
	}
	
	void createJob() {
		MyRestClient client = new MyRestClient("requests");
		client.setResource("request");
		client.addParam("request", parent.getNodeName());
		
		job = Job.create("TaskUpdate: "+parent.getNodeName(), (ICoreRunnable) monitor -> {
			//异步处理
			
			
			TasksOutput tasksOutput = client.get(TasksOutput.class);

			
			//UI同步
			this.display.asyncExec(new Runnable() {
				
				@Override
				public void run() {
					if (tasksOutput == null) {
						return;
					}
					parent.addNode(tasksOutput.data);
					viewer.expandToLevel(parent, 1);
					viewer.getTree().getColumn(0).pack();
					//宽度加宽
					viewer.getTree().getColumn(0).setWidth(viewer.getTree().getColumn(0).getWidth()+30);
				}
			});
			
		});
	}
}
