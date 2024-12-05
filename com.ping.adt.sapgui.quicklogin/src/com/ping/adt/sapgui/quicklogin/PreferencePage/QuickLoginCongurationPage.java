package com.ping.adt.sapgui.quicklogin.PreferencePage;

import java.io.IOException;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.alibaba.fastjson2.JSON;
import com.ping.adt.sapgui.quicklogin.internal.LoginConfiguration;
import com.ping.adt.sapgui.quicklogin.internal.LoginConfigurationListModel;
import com.ping.adt.sapgui.quicklogin.internal.MyPlugin;
import com.ping.adt.sapgui.quicklogin.internal.PluginConstants;
import com.ping.adt.sapgui.quicklogin.widgets.LoginConfigurationModifyDialog;

public class QuickLoginCongurationPage extends PreferencePage implements IWorkbenchPreferencePage {

	LoginConfigurationListModel model;
	TableViewer viewer;
	Shell shell;
	DirectoryFieldEditor directoryFieldEditor;
	String path;

	public QuickLoginCongurationPage() {
		// TODO Auto-generated constructor stub
	}

	public QuickLoginCongurationPage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public QuickLoginCongurationPage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(IWorkbench workbench) {
		this.path = MyPlugin.path;
		this.model = MyPlugin.model;

		setDescription("快捷登录SAPGUI配置页");
	}

	@Override
	protected Control createContents(Composite parent) {
		this.shell = parent.getShell();

		Composite conentParent = new Composite(parent, SWT.NONE | SWT.BORDER);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.fill = true;
		conentParent.setLayout(rowLayout);

		Composite top = new Composite(conentParent, SWT.BORDER);
//		RowData rowData = new RowData();
//		rowData.width = 1000;
//		top.setLayoutData(rowData);
		RowLayout topRowLayout = new RowLayout(SWT.HORIZONTAL);
		top.setLayout(topRowLayout);

		Composite center = new Composite(conentParent, SWT.BORDER);
		GridLayout girdLayout = new GridLayout(2, false);
		center.setLayout(girdLayout);

		Composite centerLeft = new Composite(center, SWT.NONE | SWT.BORDER);
		GridData leftGridData = new GridData();
		leftGridData.grabExcessHorizontalSpace = true;
		leftGridData.grabExcessVerticalSpace = true;
		leftGridData.horizontalAlignment = SWT.FILL;
		leftGridData.verticalAlignment = SWT.FILL;
		centerLeft.setLayoutData(leftGridData);
		FillLayout leftFillLayout = new FillLayout(SWT.VERTICAL);
		centerLeft.setLayout(leftFillLayout);

		Composite centerRight = new Composite(center, SWT.NONE | SWT.BORDER);
		GridData rightGridData = new GridData();
		rightGridData.grabExcessHorizontalSpace = true;
//		rightGridData.grabExcessVerticalSpace = true;
		rightGridData.horizontalAlignment = SWT.FILL;
//		rightGridData.verticalAlignment = SWT.FILL;
		centerRight.setLayoutData(rightGridData);
		FillLayout rightFillLayout = new FillLayout(SWT.VERTICAL);
		rightFillLayout.spacing = 20; // 控件间距
		centerRight.setLayout(rightFillLayout);

		// 创建路径控件并设置初始值
		directoryFieldEditor = new DirectoryFieldEditor(PluginConstants.KEY_PATH, "sapshcut路径:", top);
		directoryFieldEditor.setStringValue(path);

		// 创建表格控件
		createTableViewer(centerLeft);

		// 创建按钮
		createChangeButtons(centerRight);

		return conentParent;
	}

	private void createChangeButtons(Composite centerRight) {

		Button addButton = new Button(centerRight, SWT.NONE);
		addButton.setText("添  加");

		Button editButton = new Button(centerRight, SWT.NONE);
		editButton.setText("编  辑");

		Button removeButton = new Button(centerRight, SWT.NONE);
		removeButton.setText("删  除");

		Button upButton = new Button(centerRight, SWT.NONE);
		upButton.setText("上  移");

		Button downButton = new Button(centerRight, SWT.NONE);
		downButton.setText("下  移");

		Button topButton = new Button(centerRight, SWT.NONE);
		topButton.setText("置  顶");

		Button bottomButton = new Button(centerRight, SWT.NONE);
		bottomButton.setText("置  底");

		// 添加事件
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				openAddDialog();
			}
		});

		// 编辑事件
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = viewer.getStructuredSelection();
				if (selection == null || selection.isEmpty()) {
					return;
				}
				openEditDialog(selection.getFirstElement());
			}
		});

		// 删除事件
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = viewer.getStructuredSelection();
				if (selection == null || selection.isEmpty()) {
					return;
				}
				removeConfiguration(selection.getFirstElement());
			}
		});

		// 上移事件
		upButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = viewer.getStructuredSelection();
				if (selection == null || selection.isEmpty()) {
					return;
				}
				moveConfiguration(PluginConstants.UP, selection.getFirstElement());
			}

		});

		// 下移事件
		downButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = viewer.getStructuredSelection();
				if (selection == null || selection.isEmpty()) {
					return;
				}
				moveConfiguration(PluginConstants.DOWN, selection.getFirstElement());
			}
		});

		// 置顶事件
		topButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = viewer.getStructuredSelection();
				if (selection == null || selection.isEmpty()) {
					return;
				}
				moveConfiguration(PluginConstants.TOP, selection.getFirstElement());
			}
		});

		// 置底事件
		bottomButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = viewer.getStructuredSelection();
				if (selection == null || selection.isEmpty()) {
					return;
				}
				moveConfiguration(PluginConstants.BOTTOM, selection.getFirstElement());
			}
		});

	}

	/**
	 * 打开配置编辑对话框
	 */
	protected void openAddDialog() {
		LoginConfiguration configuration = new LoginConfiguration();
		LoginConfigurationModifyDialog dialog = new LoginConfigurationModifyDialog(this.shell, configuration);
		if (dialog.open() == Window.OK) {
			model.add(dialog.getConfiguration());
		}
	}

	/**
	 * 创建表格控件
	 * 
	 * @param conentParent 父容器
	 */
	private void createTableViewer(Composite conentParent) {
		this.viewer = new TableViewer(conentParent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);

		TableColumn column;
		Table table = this.viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		column = new TableColumn(table, SWT.NONE);
		column.setText("系统名称");
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText("菜单项名称");
		column.setWidth(100);

		column = new TableColumn(table, SWT.NONE);
		column.setText("客户端");
		column.setWidth(50);

		column = new TableColumn(table, SWT.NONE);
		column.setText("用户名");
		column.setWidth(80);

		column = new TableColumn(table, SWT.NONE);
		column.setText("语言");
		column.setWidth(40);

		column = new TableColumn(table, SWT.NONE);
		column.setText("菜单项");
		column.setWidth(50);

//		column = new TableColumn(table, SWT.NONE);
//		column.setText("工具栏项");
//		column.setWidth(60);

		this.viewer.setLabelProvider(new LoginConfigurationLabelProvider());
		this.viewer.setContentProvider(new LoginConfigurationContentProvider());
		this.viewer.setInput(model);

	}

	/**
	 * 打开编辑对话框
	 * 
	 * @param object 选中的配置
	 */
	private void openEditDialog(Object object) {
		LoginConfiguration oldConfiguration = (LoginConfiguration) object;
		LoginConfiguration newConfiguration = oldConfiguration.clone();
		LoginConfigurationModifyDialog dialog = new LoginConfigurationModifyDialog(this.shell, newConfiguration);
		if (dialog.open() == Window.OK) {
			int index = model.getIndex(oldConfiguration);
			model.remove(oldConfiguration);
			model.add(index, newConfiguration);
		}
	}

	/**
	 * 删除配置
	 * 
	 * @param object 需要删除的配置
	 */
	private void removeConfiguration(Object object) {
		LoginConfiguration configuration = (LoginConfiguration) object;
		model.remove(configuration);
	}

	/**
	 * 上下移动配置的位置
	 * 
	 * @param sign   移动标识
	 * @param object 需要移动的配置
	 */
	private void moveConfiguration(String sign, Object object) {
		LoginConfiguration configuration = (LoginConfiguration) object;
		int index = model.getIndex(configuration);
		switch (sign) {
		case PluginConstants.TOP:
			if (index == 0) {
				return;
			}
			model.remove(configuration);
			model.add(0, configuration);
			break;

		case PluginConstants.BOTTOM:
			if (index == model.getElements().size() - 1) {
				return;
			}
			model.remove(configuration);
			model.add(configuration);
			break;

		case PluginConstants.UP:
			if (index == 0) {
				return;
			}
			model.remove(configuration);
			model.add(--index, configuration);
			break;

		case PluginConstants.DOWN:
			if (index == model.getElements().size() - 1) {
				return;
			}
			model.remove(configuration);
			model.add(index++, configuration);
			break;

		default:
			break;
		}

	}

	@Override
	public boolean performOk() {
		// 同意后，持久化配置信息

		// 转为JSON字符串
		String modelJson = JSON.toJSONString(model.getElements());

		// 加密持久化配置
		// 其中包含账号密码信息，需要加密
		ISecurePreferences preferences = SecurePreferencesFactory.getDefault();
		ISecurePreferences node = preferences.node(PluginConstants.NODE_ID);
		try {
			node.put(this.directoryFieldEditor.getPreferenceName(), this.directoryFieldEditor.getStringValue(), true);
			node.put(PluginConstants.KEY_CONFIGURATION, modelJson, true);
			node.flush();
		} catch (StorageException | IOException e) {
			e.printStackTrace();
		}

		return true;
	}

}
