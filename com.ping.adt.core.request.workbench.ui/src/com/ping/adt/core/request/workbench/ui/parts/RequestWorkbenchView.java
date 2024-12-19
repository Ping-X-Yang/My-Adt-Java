package com.ping.adt.core.request.workbench.ui.parts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import com.ping.adt.core.request.workbench.ui.events.Message;
import com.ping.adt.core.request.workbench.ui.jobs.AsyncUpdateTaskNode;
import com.ping.adt.core.request.workbench.ui.jobs.BackendJob;
import com.ping.adt.core.request.workbench.ui.jobs.InitRequestModelJob;
import com.ping.adt.core.request.workbench.ui.model.Output.Data;
import com.ping.adt.core.request.workbench.ui.model.Output.RequestMessage;
import com.ping.adt.core.request.workbench.ui.model.RequestNode;
import com.ping.adt.core.request.workbench.ui.model.RequestOutput;
import com.ping.adt.core.request.workbench.ui.model.SettingModel;
import com.ping.adt.core.request.workbench.ui.model.TRNode;
import com.ping.adt.core.request.workbench.ui.model.TaskNode;
import com.ping.adt.core.request.workbench.ui.model.TransportRequestModel;
import com.ping.adt.core.request.workbench.ui.widgets.SettingDialog;
import com.ping.adt.core.tools.MyAdtTools;
import com.ping.adt.core.tools.MyRestClient;
import com.sap.adt.communication.resources.IQueryParameter;
import com.sap.adt.communication.resources.QueryParameter;

import jakarta.inject.Inject;

public class RequestWorkbenchView {
	static RequestWorkbenchView requestWorkbenchView;

	Composite parent;

	SashForm parentSashForm;
	Composite mainContainer;
	Composite secondaryContainer;

	SourceViewer logViewer;
	Document document;

	CheckboxTreeViewer treeViewer;
	TransportRequestModel requestModel;

	IEventBroker broker;

	RequestWorkbenchView(Composite parent, IEventBroker broker) {
		this.parent = parent;
		this.broker = broker;
		requestModel = new TransportRequestModel();
	}

	public void createContents() {

		initContainers();

		createLogViewer(secondaryContainer);

		// 初始化请求树视图
		initRequestTreeView();
	}

	private void initRequestTreeView() {
		createTreeViewer(mainContainer);
		initData();
	}

	private void initData() {
//		RequestOutput output = null;
//
//		SettingModel settingModel = new SettingModel();
//		List<IQueryParameter> query = new ArrayList<IQueryParameter>();
//		query.add(new QueryParameter("user", settingModel.getUserName()));
//
//		// 请求释放状态
//		if (settingModel.isModifiable()) {
//			query.add(new QueryParameter("status", "D")); // 可修改
//		}
//		if (settingModel.isModifiable()) {
//			query.add(new QueryParameter("status", "R")); // 已释放
//		}
//
//		// 请求类型
//		if (settingModel.isWorkbench()) {
//			query.add(new QueryParameter("function", "K")); // 工作台请求
//		}
//		if (settingModel.isCustomizing()) {
//			query.add(new QueryParameter("function", "W")); // 定制请求
//		}
//		if (settingModel.isTransportofCopies()) {
//			query.add(new QueryParameter("function", "T")); // 副本请求
//		}
//
//		query.add(new QueryParameter("start_date", settingModel.getFromDateText()));
//		query.add(new QueryParameter("end_date", settingModel.getToDateText()));
//
//		MyRestClient client = new MyRestClient("requests");
//
//		try {
//			output = client.get(RequestOutput.class, query);
//			requestModel.removeAll();
//			requestModel.addNode(output);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		// 需要展开的节点，默认展开每层额第一个节点
//		// 一直展到请求层
//		try {
//			List<RequestNode> expandNode = new ArrayList<RequestNode>();
//			expandNode.add(requestModel.getChildren().get(0)); // 请求类型层
//			expandNode.add(requestModel.getChildren().get(0).getChildren().get(0));
//			expandNode.add(requestModel.getChildren().get(0).getChildren().get(0).getChildren().get(0));
//			treeViewer.setExpandedElements(expandNode.toArray());
//		} catch (Exception e) {
//		}
		
		//初始化模型
		var job = new InitRequestModelJob(treeViewer, requestModel);
		job.run();
	}

	private void createTreeViewer(Composite mainContainer2) {
		treeViewer = new CheckboxTreeViewer(mainContainer, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		createTreeColumns(treeViewer);

		treeViewer.setContentProvider(new RequestTreeContentProvider());

		treeViewer.setInput(requestModel);

		// 节点排序
		treeViewer.setComparator(new RequestTreeViewerNodeComparator());

		// 添加展开事件
		treeViewer.addTreeListener(new ITreeViewerListener() {

			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
			}

			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				if (event.getElement() instanceof TRNode) {
					TRNode node = (TRNode) event.getElement();
					node.getChildren().clear();
					lazyloadTasks(node);
//					if (node.getChildren().size() == 0) {
//						lazyloadTasks(node);
//					}
				}
			}
		});

	}

	private void createTreeColumns(CheckboxTreeViewer viewer) {
		TreeViewerColumn requestColumn = new TreeViewerColumn(viewer, SWT.NONE);
		requestColumn.getColumn().setText("请求号");
		requestColumn.getColumn().setWidth(300);
		requestColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new RequestColumnLabelProvider()));

		TreeViewerColumn ownerColumn = new TreeViewerColumn(viewer, SWT.NONE);
		ownerColumn.getColumn().setText("拥有者");
		ownerColumn.getColumn().setWidth(100);
		ownerColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new OwnerColumnLabelProvider()));

		// 对象类型
		TreeViewerColumn typeColumn = new TreeViewerColumn(viewer, SWT.NONE);
		typeColumn.getColumn().setText("类型");
		typeColumn.getColumn().setWidth(100);
		typeColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new TypeColumnLabelProvider()));

		TreeViewerColumn textColumn = new TreeViewerColumn(viewer, SWT.NONE);
		textColumn.getColumn().setText("描述");
		textColumn.getColumn().setWidth(600);
		textColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new TextColumnLabelProvider()));

		TreeViewerColumn lastChangeColumn = new TreeViewerColumn(viewer, SWT.NONE);
		lastChangeColumn.getColumn().setText("最后修改时间");
		lastChangeColumn.getColumn().setWidth(150);
		lastChangeColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new LastChangeColumnLabelProvider()));

	}

	private void initContainers() {
		parentSashForm = new SashForm(parent, SWT.VERTICAL);
		mainContainer = new Composite(parentSashForm, SWT.BORDER);
		secondaryContainer = new Composite(parentSashForm, SWT.BORDER);
		mainContainer.setLayout(new FillLayout());
		secondaryContainer.setLayout(new FillLayout());
		parentSashForm.setWeights(new int[] { 68, 32 });
	}

	private void createLogViewer(Composite parent) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		logViewer = new SourceViewer(parent, null, SWT.BORDER | SWT.V_SCROLL);
		logViewer.setEditable(false);
		document = new Document();
		logViewer.setDocument(document);
	}

	public void receiveLogMessages(Message message) {
		StyleRange range = null;
		TextPresentation textPresentation = null;
		String line = null;

		if (message.data == null || message.data.size() == 0) {
			return;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			document.replace(0, 0, "\n\n");
			String title = dateFormat.format(new Date()) + "\n" + message.topic + "\n";
			int offset = title.length();

			// 时间戳
			document.replace(0, 0, title);
			for (Data data : message.data) {
				line = "";
				line = String.format("    [%s]  %-12s  %s\n", data.request, data.username, data.text);

				// 设置请求号样式
				textPresentation = new TextPresentation();
				if (data.message.size() == 0) {
					range = new StyleRange(offset + 5, data.request.length(), new Color(0, 210, 0), null, SWT.BOLD);
				} else {
					range = new StyleRange(offset + 5, data.request.length(), new Color(230, 0, 0), null, SWT.BOLD);
				}

				// 添加错误消息
				for (RequestMessage msg : data.message) {
					line = line + String.format("        %s\n", msg.message);
				}

				document.replace(offset, 0, line);
				textPresentation.addStyleRange(range);
				logViewer.changeTextPresentation(textPresentation, true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void handleToolbarEvents(String parameter) {
		switch (parameter) {

		case "unSelectAll": // 取消全选
			handleUnSelectAll();
			break;
		case "release": // 释放
			handleRelease();
			break;

		case "one_copy": // 一键传输
			handleOneCopy();
			break;

		case "one_qas": // 一键传输QAS
			handleOneQas();
			break;

		case "one_prd": // 一键传输PRD
			handleOnePrd();
			break;

		case "refresh": // 刷新请求树
			handleEventRefresh();
			break;

		case "clearLogs":
			handleClearLogs();
			break;

		case "setting": // 设置
			handleEventSetting();
			break;
		default:
			break;
		}

	}

	private void handleClearLogs() {
		document.set("");
	}

	private void handleUnSelectAll() {
		for (Object element : treeViewer.getCheckedElements()) {
			treeViewer.setChecked(element, false);
		}
	}

	private void handleOnePrd() {
		callBackend("一键传输生产系统", "import_prd");
	}

	private void handleOneQas() {
		callBackend("一键传输测试系统", "import_qas");
	}

	private void handleOneCopy() {
		callBackend("一键副本传输", "one_key_copy");
	}

	private void handleRelease() {
		callBackend("释放请求", "release");
	}

	private void callBackend(String title, String action) {
		// 获取选中的请求和任务
		List<RequestNode> requestList = new ArrayList<RequestNode>();
		for (Object selectedObject : treeViewer.getCheckedElements()) {
			if (selectedObject instanceof TRNode || selectedObject instanceof TaskNode) {
				RequestNode node = (RequestNode) selectedObject;
				requestList.add(node);
			}
		}

		BackendJob job = new BackendJob(parent.getDisplay(), treeViewer, title, action, requestList, broker);
		job.run();
	}

	private void handleEventRefresh() {
		initData();
	}

	private void handleEventSetting() {
		Shell shell = MyAdtTools.getActiveShell();
		SettingDialog dialog = new SettingDialog(shell);
		int returnCode = dialog.open();
		if (returnCode == Window.OK) {
			handleEventRefresh();
		}
	}

	private void lazyloadTasks(TRNode node) {

		var job = new AsyncUpdateTaskNode(parent.getDisplay(), treeViewer, node);
		job.run();
	}

}
