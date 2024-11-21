package com.ping.adt.structure.browser.ui.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.alibaba.fastjson2.JSON;
import com.ping.adt.core.tools.MyAdtTools;
import com.ping.adt.core.tools.MyRestClient;
import com.ping.adt.structure.browser.ui.common.IComponentType;
import com.ping.adt.structure.browser.ui.common.ILocalImages;
import com.sap.adt.communication.message.IResponse;

public class StructureBrower {
	private Text search;
//	private TreeViewer treeViewer;
	private CheckboxTreeViewer treeViewer;
	Map<String, ImageDescriptor> images = new HashMap<String, ImageDescriptor>();
	
	public StructureBrower(Composite grandParent) {
		Composite parent = new Composite(grandParent, SWT.None);
		GridLayout parentLayout = new GridLayout(1, true);
		parentLayout.verticalSpacing = 0;
		parentLayout.horizontalSpacing = 0;
		parentLayout.marginHeight = 0;
		parentLayout.marginWidth = 0;

		parent.setLayout(parentLayout);
//		parent.setBackground(new Color(0,0,0));
		
		
		//加载图片
		loadImages();
		
		
		//创建搜索框
		createSearchControl(parent);
		
		
		//创建树控件
		createTreeControl(parent);
		
	}

	
	/**
	 * 创建树控件
	 * @param parent
	 */
	private void createTreeControl(Composite parent) {
//		treeViewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		treeViewer = new CheckboxTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Tree tree = treeViewer.getTree();
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 2,1);		//水平填充，垂直填充，占用水平剩余空间，占据垂直剩余空间，行，列
		tree.setLayoutData(gridData);
		tree.setHeaderVisible(true); 		//抬头可见
		tree.setLinesVisible(true);
		
		//创建列
		createTreeColumns(treeViewer);
		
		//设置提供器
		treeViewer.setContentProvider(new StructureTreeContentProvider());
		
	}

	private void createTreeColumns(TreeViewer viewer) {				
		//字段名
		TreeViewerColumn fieldNameColumn = new TreeViewerColumn(viewer, SWT.NONE);
		fieldNameColumn.getColumn().setText("  字段名");
		fieldNameColumn.getColumn().setWidth(300);
		fieldNameColumn.getColumn().setAlignment(SWT.CENTER);
		fieldNameColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new FieldNameColumnLabelProvider(images)));
		
		
		//组件类型
		TreeViewerColumn rollNameColumn = new TreeViewerColumn(viewer, SWT.NONE);
		rollNameColumn.getColumn().setText("  组件类型");
		rollNameColumn.getColumn().setWidth(300);
//		rollNameColumn.getColumn().setAlignment(SWT.CENTER);
		rollNameColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new RollNameColumnLabelProvider(images)));
		
		//数据类型
		TreeViewerColumn dataTypeColumn = new TreeViewerColumn(viewer, SWT.NONE);
		dataTypeColumn.getColumn().setText("  数据类型");
		dataTypeColumn.getColumn().setWidth(300);
//		dataTypeColumn.getColumn().setAlignment(SWT.CENTER);
		dataTypeColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new DataTypeColumnLabelProvider()));
		
		//字段描述
		TreeViewerColumn textColumn = new TreeViewerColumn(viewer, SWT.NONE);
		textColumn.getColumn().setText("  描述");
		textColumn.getColumn().setWidth(300);
//		textColumn.getColumn().setAlignment(SWT.CENTER);
		textColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(new TextColumnLabelProvider()));
	}


	/**
	 * 加载图片
	 */
	private void loadImages() {
		images.put(ILocalImages.KEY_FIELD_ICON, 	MyAdtTools.CreateImageDescriptor(getClass(), ILocalImages.KEY_FIELD_ICON));
		images.put(ILocalImages.DATA_ELEMENT_ICON, 	MyAdtTools.CreateImageDescriptor(getClass(), ILocalImages.DATA_ELEMENT_ICON));
		images.put(ILocalImages.STRUCTURE_ICON, 	MyAdtTools.CreateImageDescriptor(getClass(), ILocalImages.STRUCTURE_ICON));
		images.put(ILocalImages.TABLE_TYPE_ICON, 	MyAdtTools.CreateImageDescriptor(getClass(), ILocalImages.TABLE_TYPE_ICON));
	}


	/**
	 * 创建搜索框
	 * @param parent
	 */
	private void createSearchControl(Composite parent) {
		search = new Text(parent, SWT.SEARCH |SWT.BORDER | SWT.ICON_SEARCH | SWT.ICON_CANCEL);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1,1);		//水平填充，垂直填充，占用水平剩余空间，占据垂直剩余空间，行，列
		search.setLayoutData(gridData);
		
		search.setToolTipText("请输入结构...");
		
		search.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				//回车触发搜索
				if (e.character == '\r') {	//e.keyCode == 13 || e.keyCode == 16777296
					onSearch();
				}
			}
		});
	}
	
	
	/**
	 * 处理Search事件
	 * @param e
	 */
	private void onSearch() {
		List<FieldNode> fields = getFields(search.getText(), null);
		treeViewer.setInput(fields);
		treeViewer.expandAll();
		for (TreeColumn column : treeViewer.getTree().getColumns()) {
			//根据内容自适应列宽
			column.pack();	
			//加宽列的宽度
			int w = column.getWidth();
			column.setWidth(w + 100);
		}
		treeViewer.refresh();
	}


	/**
	 * 递归获取嵌套结构
	 * @param structureName		结构名称
	 * @param parent			父节点
	 * @return
	 */
	private List<FieldNode> getFields(String structureName, FieldNode parent) {
		IResponse response;
		
		MyRestClient client = new MyRestClient("data_structure");
		client.addParam("name", structureName);
		
		try {
			response = client.get();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ArrayList<FieldNode>();
		}
		
		if (response.getStatus() != 200) {
			return new ArrayList<FieldNode>();
		}
		
		//反序列化
		DataStructureResult res = JSON.parseObject(response.getBody().toString(), DataStructureResult.class);
		
		if (res.status.code.equals("0")) {
			System.out.println(res.status.message);
			return new ArrayList<FieldNode>();
		}
		
		List<FieldNode> fields = res.data;
		for (FieldNode fieldNode : fields) {
			if (fieldNode.componentType.equals(IComponentType.STRUCTURE) || fieldNode.componentType.equals(IComponentType.TABLE) ) {
				fieldNode.children = getFields(fieldNode.rollName, fieldNode);
			}
		}
		return fields;
	}	
}
