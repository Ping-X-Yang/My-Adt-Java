
package com.ping.adt.sapgui.quicklogin.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.statushandlers.StatusManager;

import com.ping.adt.core.tools.MyAdtTools;
import com.ping.adt.sapgui.quicklogin.gui.SapGuiStartupData;
import com.ping.adt.sapgui.quicklogin.gui.WinGuiServerProxy;
import com.ping.adt.sapgui.quicklogin.internal.LoginConfiguration;
import com.ping.adt.sapgui.quicklogin.internal.MyPlugin;
import com.sap.adt.destinations.model.IDestinationDataWritable;
import com.sap.adt.destinations.model.ISystemConfiguration;
import com.sap.adt.destinations.model.config.AdtSystemConfigurationServiceFactory;
import com.sap.adt.destinations.model.internal.DestinationDataWritable;
import com.sap.adt.projectexplorer.ui.internal.node.AbapRepositoryPackageNode;
import com.sap.adt.projectexplorer.ui.internal.virtualfolders.VirtualFolderNode;
import com.sap.adt.projectexplorer.ui.node.IAbapRepositoryObjectNode;
import com.sap.adt.tm.IRequest;
import com.sap.adt.tm.ITask;
import com.sap.adt.tools.abapsource.sources.AdtSourceServicesFactory;
import com.sap.adt.tools.abapsource.sources.IAdtSourceServicesFactory;
import com.sap.adt.tools.abapsource.sources.codeelementinformation.ICodeElementInformationBackendService;
import com.sap.adt.tools.abapsource.ui.internal.navigation.SourceCodeNavigationHandler;
import com.sap.adt.tools.abapsource.ui.internal.sqlscriptparser.AmdpNavigationService;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourceMultiPageEditor;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAdtCompoundTextSelection;
import com.sap.adt.tools.core.AbapCore;
import com.sap.adt.tools.core.IAdtObjectReference;
import com.sap.adt.tools.core.internal.navigation.AbapNavigationServices;
import com.sap.adt.tools.core.internal.navigation.AbapNavigationServicesFactory;
import com.sap.adt.tools.core.model.adtcore.IAdtCoreFactory;
import com.sap.adt.tools.core.model.util.AdtObjectReferenceAdapterFactory;
import com.sap.adt.tools.core.navigation.IAbapNavigationServices;
import com.sap.adt.tools.core.navigation.IAbapNavigationServices.FilterValue;
import com.sap.adt.tools.core.ui.internal.navigation.ITextNavigationSource;
import com.sap.adt.tools.core.urimapping.AdtUriMappingServiceFactory;
import com.sap.adt.tools.core.urimapping.IAdtUriMappingService;
import com.sap.adt.tools.core.urimapping.typeservice.AdtUriMappingTypeServiceFactory;

import jakarta.inject.Named;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;

public class NavToTargetHandle {

	LoginConfiguration logonConfiguration = null;
	ISystemConfiguration systemConfiguration = null;
	boolean connectGUI;

	@Execute
	public void execute(
			@Named("com.ping.adt.sapgui.quicklogin.MFD.command.navToTarget.parameter.MenuItemName") String parameter) {

		// 初始化变量
		connectGUI = false;

		// 获取对应系统配置的登录账号密码
		logonConfiguration = getLogonConfiguration(parameter);
		if (logonConfiguration == null) {
			return;
		}

		// 获取对应系统的登录配置
		systemConfiguration = getSystemConfiguration(logonConfiguration.getSystemName());
		if (systemConfiguration == null) {
			return;
		}

		// 处理不同的Selection
		ISelection selection = MyAdtTools.getActivePage().getSelection();
		if (selection != null) {
			handleSelection(selection);
		}

	}

	private void handleSelection(ISelection selection) {
		if (selection instanceof ITreeSelection) {
			handleTreeSelection((ITreeSelection) selection);
		} else if (selection instanceof IAdtCompoundTextSelection) {
			IAdtCompoundTextSelection textSelection = (IAdtCompoundTextSelection) selection;
			if (textSelection.getLength() > 0) {
				// 当ABAP编辑器中的文本被选中时，根据选中文本导航对象
				handleTextSelectionForSelected(textSelection);
			} else {
				// 导航到当前ABAP编辑器中对象
				handleTextSelection(textSelection);
			}
		} else {
			handleOthers();
		}

	}

	private void handleOthers() {
		openGui();
	}

	private void handleTextSelection(ITextSelection textSelection) {
		// 只处理ABAP上下文的Selection
		if (!(textSelection instanceof IAdtCompoundTextSelection)) {
			openGui();
			return;
		}

		IAdtCompoundTextSelection AdtCompoundTextSelection = (IAdtCompoundTextSelection) textSelection;
		if (!(AdtCompoundTextSelection.getPart() instanceof IEditorPart)) {
			openGui();
			return;
		}

		// 获取编辑器和编辑器中的Selection
		IEditorPart part = (IEditorPart) AdtCompoundTextSelection.getPart();
		ITextSelection newSelection = (ITextSelection) part.getEditorSite().getSelectionProvider().getSelection();

		// 获取ABAP代码页
		IAbapSourcePage sourcePage = null;
		if (part instanceof IAbapSourceMultiPageEditor) {
			IEditorPart ae = ((IAbapSourceMultiPageEditor) part).getActiveEditor();// 64
			if (ae instanceof IAbapSourcePage) {// 65
				sourcePage = (IAbapSourcePage) ae;// 66
			}
		} else if (part instanceof IAbapSourcePage) {// 68
			sourcePage = (IAbapSourcePage) part;// 69
		}

		// 根据源码导航对象
		if (sourcePage != null) {// 73
			ITextNavigationSource n = sourcePage.getTextNavigationSource();// 74
			if (n != null) {// 75
				URI uri = n.getResourceUri(AdtCompoundTextSelection, false);// 76
				if (uri != null) {// 77
					IAdtObjectReference objectReference = AdtObjectReferenceAdapterFactory
							.createFromEmfReference(IAdtCoreFactory.eINSTANCE.createAdtObjectReference());
					objectReference.setUri(uri);// 79
					objectReference.setName(AdtUriMappingTypeServiceFactory.createUriMappingTypeService()
							.getObjectName(sourcePage.getFile()));// 81
					openGui(objectReference);
					return;
				}
			}
		}

		// 根据编辑器导航对象
		IEditorInput editorInput = part.getEditorInput();// 89
		if (editorInput instanceof IFileEditorInput) {// 90
			IFile file = ((IFileEditorInput) editorInput).getFile();// 91
			IAdtUriMappingService mapper = AdtUriMappingServiceFactory.createUriMappingService();// 92
			URI uri = mapper.getAdtUri(file);// 93
			IAdtObjectReference objectReference = createAdtObjectReference(uri);
			objectReference.setName(AdtUriMappingTypeServiceFactory.createUriMappingTypeService().getObjectName(file));// 96
			openGui(objectReference);
			return;
		}

		openGui();
	}

	private IAdtObjectReference createAdtObjectReference(URI uri) {
		IAdtObjectReference objectReference = AdtObjectReferenceAdapterFactory
				.createFromEmfReference(IAdtCoreFactory.eINSTANCE.createAdtObjectReference());// 94
		objectReference.setUri(uri);// 95
		return objectReference;
	}

	private void handleTextSelectionForSelected(IAdtCompoundTextSelection adtCompoundTextSelection) {
		IWorkbenchPart part = adtCompoundTextSelection.getPart();
		final ITextNavigationSource navigationSource = (ITextNavigationSource) part
				.getAdapter(ITextNavigationSource.class);
//		if (navigationSource != null || !navigationSource.supportsNavigation()) {
//			openGui();
//			return;
//		}

		// 定义导航类型
		final FilterValue[] filters = new FilterValue[1];
		filters[0] = IAbapNavigationServices.FilterValue.DEFINITION;

		// 根据第一个选中项，获取源码URI
		final URI sourceUri = navigationSource.getResourceUri(adtCompoundTextSelection.getRegions()[0]);

		final String destination = navigationSource.getDestination();
		final String sourceText = navigationSource.getSourceText();
		final IProject project = navigationSource.getProject();

		// 创建Job获取对象
		Job getTargetJob = new Job("ABAP源码中查找存储库对象") {
			protected IStatus run(IProgressMonitor monitor) {
				try {
					IAdtObjectReference target = getNavigationTarget(destination, sourceText, sourceUri, filters,
							monitor, navigationSource);
					if (target == null) {
						return Status.OK_STATUS;
					}

					// 返回的URI中包含ddic, 说明这是一个DDIC对象
					/// wb/object_type/viewdvf/表示DDIC视图对象类型
					// 然后再用元素信息服务获取其对应的字段(如果有字段)
					if (target.getUri().getPath().contains("/ddic/")
							|| target.getUri().getPath().contains("/wb/object_type/viewdvf/")) {
						IAdtSourceServicesFactory adtSourceServicesFactory = AdtSourceServicesFactory.createInstance();
						ICodeElementInformationBackendService codeElementInformationService = adtSourceServicesFactory
								.createCodeElementInformationService(destination);
						Object codeElementInformation = (IAdtObjectReference) codeElementInformationService
								.getCodeElementInformation(sourceUri, sourceText, monitor);
						if (codeElementInformation instanceof IAdtObjectReference) {
							target = (IAdtObjectReference) codeElementInformation;
						}

						// 转义分号
						target.setUri(new URI(target.getUri().toString().replace(";", "%3B")));

						// 没有片段则添加一个片段，使每个URI都能进入fragment mapping增强
						addFragmentForSpecialObject(target);
					}

					if (target != null) {
						openGui(target);
					} else {
						openGui();
					}

					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}

//				             Job job = new SourceCodeNavigationHandler.ShowNavigationResultJob(SourceCodeNavigationHandler.this, target, project, part);
//				             job.schedule();
				} catch (Exception e) {
					IStatus status = new Status(4, "com.sap.adt.tools.abapsource.ui", e.getMessage(), e);
					StatusManager.getManager().handle(status, 3);
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		};

		getTargetJob.schedule();

	}

	private void handleTreeSelection(ITreeSelection selection) {

		// 存储库对象
		Object element = selection.getFirstElement();
		if (element instanceof IAbapRepositoryObjectNode) { // ABAP存储库对象
			IAbapRepositoryObjectNode node = (IAbapRepositoryObjectNode) selection.getFirstElement();
			IAdtObjectReference target = node.getNavigationTarget();
			openGui(target);
		} else if (element instanceof VirtualFolderNode) { // ABAP开发包树节点
			VirtualFolderNode node = (VirtualFolderNode) selection.getFirstElement();
			Object adapter = node.getAdapter(IAdtObjectReference.class);
			if (adapter instanceof IAdtObjectReference) {
				IAdtObjectReference target = (IAdtObjectReference) adapter;
				openGui(target);
			} else {
				openGui();
			}
		} else if (element instanceof IRequest) { // 请求号
			IRequest request = (IRequest) element;
			URI requestURI = null;
			try {
				requestURI = new URI(request.getUri());
			} catch (URISyntaxException e) {
				openGui();
				return;
			}
			IAdtObjectReference objectReference = createAdtObjectReference(requestURI);
			openGui(objectReference);
		} else if (element instanceof ITask) { // 任务号
			ITask task = (ITask) element;
			URI taskURI = null;
			try {
				taskURI = new URI(task.getUri());
			} catch (URISyntaxException e) {
				openGui();
				return;
			}
			IAdtObjectReference objectReference = createAdtObjectReference(taskURI);
			openGui(objectReference);
		} else {
			openGui();
		}

	}

	private ISystemConfiguration getSystemConfiguration(String systemName) {
		Map<String, ISystemConfiguration> systemConfigurationMap = null;
		ISystemConfiguration systemConfiguration = null;

		// 获取系统名称
		// 配置来源是SAPGUI上配置的登录系统
		try {
			systemConfigurationMap = AdtSystemConfigurationServiceFactory.createSystemConfigurationService()
					.getSystemConfigurations();
			systemConfiguration = systemConfigurationMap.get(systemName);
		} catch (Exception var3) {
		}

		return systemConfiguration;
	}

	private LoginConfiguration getLogonConfiguration(String parameter) {
		// 查找参数对应的配置
		return MyPlugin.model.getElements().stream().filter(e -> e.getMenuItemName().equals(parameter)).findAny().get();
	}

	@CanExecute
	public boolean canExecute() {

		return true;
	}

	private void openGui(IAdtObjectReference objectReference) {
		if (connectGUI == true) {
			return;
		}

		// 组合配置数据
		IDestinationDataWritable destinationDataWritable = new DestinationDataWritable(
				String.format("%d", System.currentTimeMillis()));
		destinationDataWritable.setSystemConfiguration(systemConfiguration);
		destinationDataWritable.setClient(logonConfiguration.getClient());
		destinationDataWritable.setUser(logonConfiguration.getUsername());
		destinationDataWritable.setPassword(logonConfiguration.getPassword());
		destinationDataWritable.setLanguage(logonConfiguration.getLanguage());

		// 创建启动参数
		SapGuiStartupData startupInfo = new SapGuiStartupData(destinationDataWritable, objectReference, false,
				"WB:DISPLAY", null, null, true);

		// 启动Winows上的SAP GUI,并导航至目标
		WinGuiServerProxy.getProxy().openConnection(startupInfo, 0);

		connectGUI = true;
	}

	private void openGui() {
		// 跳转到GUI的导航页
		openGui("SESSION_MANAGER");
	}

	private void openGui(String tcode) {
		// 组合配置数据
		IDestinationDataWritable destinationDataWritable = new DestinationDataWritable(
				String.format("%d", System.currentTimeMillis()));
		destinationDataWritable.setSystemConfiguration(systemConfiguration);
		destinationDataWritable.setClient(logonConfiguration.getClient());
		destinationDataWritable.setUser(logonConfiguration.getUsername());
		destinationDataWritable.setPassword(logonConfiguration.getPassword());
		destinationDataWritable.setLanguage(logonConfiguration.getLanguage());

		// 创建启动参数
		SapGuiStartupData startupInfo = new SapGuiStartupData(destinationDataWritable, tcode, false, null, null, true);

		// 启动Winows上的SAP GUI,并导航至目标
		WinGuiServerProxy.getProxy().openConnection(startupInfo, 0);
	}

	private IAdtObjectReference getNavigationTarget(String destination, String sourceCode, URI uri,
			FilterValue[] filters, IProgressMonitor monitor, ITextNavigationSource navigationSource) throws Exception {
//		     if (uri.toString().startsWith(AbapNavigationServices.URI_NAVIGATION.toString())) {
//		 
//		       
//		       AbapNavigationServices coreNavigationServices = (AbapNavigationServices)AbapNavigationServicesFactory.getInstance()
//		         .createNavigationService(destination);
//		       return coreNavigationServices.getNavigationTargetAtRequestUri(uri, monitor);
//		     } 

		IAbapNavigationServices abapNavigationService = navigationSource.getNavigationServices(destination);
		if (abapNavigationService == null) {
			abapNavigationService = AbapCore.getInstance().getAbapNavigationServiceFactory()
					.createNavigationService(destination);
		}
		return abapNavigationService.getNavigationTarget(uri, sourceCode, monitor, filters);
	}

	private void addFragmentForSpecialObject(IAdtObjectReference target) throws URISyntaxException {
		String fragment = target.getUri().getFragment();

		// 存在fragment直接退出
		if (fragment != null && !fragment.equals("")) {
			return;
		}

		String type = target.getType();
		if (type == null) {
			return;
		}

		// 表、结构、视图、CDS视图都需要添加一个增强标记(#postion), 才能使其进入增强
		if (type.contains("TABL") || type.contains("VIEW") || type.contains("DDLS")) {
			target.setUri(new URI(target.getUri() + "#position"));
		}

	}

}