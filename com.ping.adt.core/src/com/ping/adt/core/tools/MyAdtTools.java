package com.ping.adt.core.tools;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.alibaba.fastjson2.JSON;
import com.sap.adt.destinations.logon.AdtLogonServiceFactory;
import com.sap.adt.destinations.model.IAuthenticationToken;
import com.sap.adt.destinations.model.IDestinationData;
import com.sap.adt.destinations.model.internal.AuthenticationToken;
import com.sap.adt.destinations.ui.logon.AdtLogonServiceUIFactory;
import com.sap.adt.project.IAdtCoreProject;
import com.sap.adt.project.ui.dialogs.AdtProjectSelectionDialog;
import com.sap.adt.project.ui.util.ProjectUtil;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourceMultiPageEditor;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;
import com.sap.adt.tools.core.model.adtcore.IAdtObject;

import org.eclipse.core.runtime.Path;


public class MyAdtTools {
	
	public static IWorkbenchWindow getActiveWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}
	
	public static Shell getActiveShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}
	
	public static IWorkbenchPage getActivePage() {
		return PlatformUI
				.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage();
	}
	

	/**
	 * 获取活动项目对象
	 * @return IProject
	 */
	public static IProject getActiveProject() { 
		System.out.println("获取活动的项目");
		//获取活动页
		IWorkbenchPage page = PlatformUI
								.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage();
		
		//获取window
		IWorkbenchWindow window = page.getWorkbenchWindow();
		
		//获取selection
		ISelection selection = window
									.getSelectionService()
									.getSelection();
		
		//获取活动项目
		IProject project = ProjectUtil
								.getActiveAdtCoreProject(selection, null, null, IAdtCoreProject.ABAP_PROJECT_NATURE);
		
		return project;
	}
	
	/**
	 * 获取活动项目
	 * @param selection
	 * @return
	 */
	public static IProject getActiveProject(ISelection selection) { 
		System.out.println("获取活动的项目");

		//获取活动项目
		IProject project = ProjectUtil
								.getActiveAdtCoreProject(selection, null, null, IAdtCoreProject.ABAP_PROJECT_NATURE);
		
		return project;
	}
	
	
	/**
	 * 获取活动的ADT项目对象
	 * @return IAdtCoreProject
	 */
	public static IAdtCoreProject getActiveAdtProject() {
		System.out.println("获取活动的Adt项目");
		return getActiveProject().getAdapter(IAdtCoreProject.class);
	}
	
	
	/**
	 * 根据项目名称获取项目对象
	 * @param projectName	项目名称
	 * @return	项目对象
	 */
	public static IProject getProjectByName(String projectName) {
		System.out.println("通过名称获取活动的项目");
		
		IProject project = ResourcesPlugin
								.getWorkspace()
								.getRoot()
								.getProject(projectName);
		return project;
	}
	
	
	/**
	 * 根据对话框选择项目对象
	 * @return 项目对象
	 */
	public static IProject getProjectByChooseDialog() {
		System.out.println("通过对话框获取活动的项目");
		
		IProject project = AdtProjectSelectionDialog.open(
											PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
											, null);
		return project;
	}
	
	
	/**
	 * 确保登录
	 * @param project 项目
	 */
	public static void ensureLoggedOn(IProject project) {
		try {
			IAdtCoreProject adtProject = project.getAdapter(IAdtCoreProject.class);
			AdtLogonServiceUIFactory
				.createLogonServiceUI()
				.ensureLoggedOn(
						adtProject.getDestinationData(), 
						PlatformUI.getWorkbench().getProgressService()
				);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * 确保登录
	 * @param project 项目
	 */
	public static boolean isLoggedOn(IProject project) {
		boolean logged = false;
		try {
			IAdtCoreProject adtProject = project.getAdapter(IAdtCoreProject.class);
			logged = AdtLogonServiceFactory.createLogonService().isLoggedOn(adtProject.getDestinationId());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return logged;
	}
	
	
	/**
	 * 根据密码登录sap
	 * @param project	项目
	 * @param password	密码
	 */
	public static void logonByPassword(IProject project, String password) {
		IAdtCoreProject adtProject = project.getAdapter(IAdtCoreProject.class);
		IDestinationData destinationData = adtProject.getDestinationData();
		try {
			if (!password.isEmpty()) {
				IAuthenticationToken authenticationToken = new AuthenticationToken();
				authenticationToken.setPassword(password);
				AdtLogonServiceFactory
					.createLogonService().
					ensureLoggedOn(destinationData, authenticationToken, new NullProgressMonitor());
			}
		} catch (Exception e) {
			ensureLoggedOn(project);
		}
	}
	
	
	/**
	 * 返回活动的编辑器
	 * @return 活动编辑器
	 */
	public static IEditorPart getActiveEditor() {
		try {
			IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			return editor;
		} catch (Exception e) {
			return null;
		}
		
	}
	
	/**
	 * 创建资源管理器
	 * @return 资源管理器
	 */
	public static ResourceManager CreateResourceManager() {
		return new LocalResourceManager(JFaceResources.getResources());
	}
	
	
	/**
	 * @param editor 编辑器
	 * @return Adt对象
	 */
	public static IAdtObject getAdtObject(IEditorPart editor) {
		if (editor instanceof IAbapSourceMultiPageEditor ) {
			IAbapSourcePage abapSourcePage = (IAbapSourcePage) editor.getAdapter(IAbapSourcePage.class);
			if(abapSourcePage != null) {
				return abapSourcePage.getMultiPageEditor().getModel();
			}
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param classFromBundle 类
	 * @param path 路径
	 * @return
	 */
	public static ImageDescriptor CreateImageDescriptor(Class< ? > classFromBundle,String path) {
		Bundle bundle = FrameworkUtil.getBundle(classFromBundle);
		URL url = FileLocator.find(bundle, new Path(path), null);
		return ImageDescriptor.createFromURL(url);
	}
}
