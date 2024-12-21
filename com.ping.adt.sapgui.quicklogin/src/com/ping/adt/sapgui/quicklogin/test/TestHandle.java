package com.ping.adt.sapgui.quicklogin.test;

import java.net.URI;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;

import com.ping.adt.core.tools.MyAdtTools;
import com.ping.adt.sapgui.quicklogin.gui.SapGuiStartupData;
import com.ping.adt.sapgui.quicklogin.gui.WinGuiServerProxy;
import com.ping.adt.sapgui.quicklogin.internal.MyPlugin;
import com.sap.adt.destinations.model.IDestinationDataWritable;
import com.sap.adt.destinations.model.ISystemConfiguration;
import com.sap.adt.destinations.model.config.AdtSystemConfigurationServiceFactory;
import com.sap.adt.destinations.model.internal.DestinationDataWritable;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourceMultiPageEditor;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourcePage;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAdtCompoundTextSelection;
import com.sap.adt.tools.core.model.adtcore.IAdtCoreFactory;
import com.sap.adt.tools.core.model.adtcore.IAdtObjectReference;
import com.sap.adt.tools.core.ui.internal.navigation.ITextNavigationSource;
import com.sap.adt.tools.core.urimapping.AdtUriMappingServiceFactory;
import com.sap.adt.tools.core.urimapping.IAdtUriMappingService;
import com.sap.adt.tools.core.urimapping.typeservice.AdtUriMappingTypeServiceFactory;

import jakarta.inject.Named;

public class TestHandle {
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SELECTION) ISelection selection) throws ExecutionException {
		if (!(selection instanceof IAdtCompoundTextSelection)) {
			return;
		}
		
		IAdtCompoundTextSelection textSelection = (IAdtCompoundTextSelection) selection;
		if (textSelection.getPart() instanceof IEditorPart) {
			IEditorPart part = (IEditorPart) textSelection.getPart();
			handleSelection(part);
		}	
	}

	private void handleSelection(IEditorPart part) {
		IAbapSourcePage sourcePage = null;
		
		ITextSelection selection = (ITextSelection) part.getEditorSite().getSelectionProvider().getSelection();


		
		if (part instanceof IAbapSourceMultiPageEditor) {
			IEditorPart ae = ((IAbapSourceMultiPageEditor)part).getActiveEditor();// 64
            if (ae instanceof IAbapSourcePage) {// 65
               sourcePage = (IAbapSourcePage)ae;// 66
            }
		} else if (part instanceof IAbapSourcePage) {// 68
            sourcePage = (IAbapSourcePage)part;// 69
        }
		
		
		if (sourcePage != null) {// 73
            ITextNavigationSource n = sourcePage.getTextNavigationSource();// 74
            if (n != null) {// 75
               URI uri = n.getResourceUri(selection, false);// 76
               if (uri != null) {// 77
                  IAdtObjectReference objectReference = IAdtCoreFactory.eINSTANCE.createAdtObjectReference();// 78
                  objectReference.setUri(uri.toString());// 79
                  objectReference.setName(AdtUriMappingTypeServiceFactory.createUriMappingTypeService().getObjectName(sourcePage.getFile()));// 81
                  openGui(objectReference);
                  return;
               }
            }
         }
		
		
		IEditorInput editorInput = part.getEditorInput();// 89
        if (editorInput instanceof IFileEditorInput) {// 90
           IFile file = ((IFileEditorInput)editorInput).getFile();// 91
           IAdtUriMappingService mapper = AdtUriMappingServiceFactory.createUriMappingService();// 92
           URI uri = mapper.getAdtUri(file);// 93
           IAdtObjectReference objectReference = IAdtCoreFactory.eINSTANCE.createAdtObjectReference();// 94
           objectReference.setUri(uri.toString());// 95
           objectReference.setName(AdtUriMappingTypeServiceFactory.createUriMappingTypeService().getObjectName(file));// 96
           openGui(objectReference);
           return;
        }
		
		
	}

	private void openGui(IAdtObjectReference objectReference) {
		
		
		
		//获取GUI的登录配置
		var logonConfigration = MyPlugin.model.getElements().get(0);
		
		
		//获取系统配置
		Map<String, ISystemConfiguration> systemConfigurationMap = null;
		try {
			systemConfigurationMap = AdtSystemConfigurationServiceFactory.createSystemConfigurationService()
					.getSystemConfigurations();
		} catch (Exception var3) {
			return;
		}
		var systemConfiguration = systemConfigurationMap.get(logonConfigration.getSystemName());
		
		
		//组合配置数据
		IDestinationDataWritable destinationDataWritable = new DestinationDataWritable(String.format("%d", System.currentTimeMillis()));
		destinationDataWritable.setSystemConfiguration(systemConfiguration);
		destinationDataWritable.setClient(logonConfigration.getClient());
		destinationDataWritable.setUser(logonConfigration.getUsername());
		destinationDataWritable.setPassword(logonConfigration.getPassword());
		destinationDataWritable.setLanguage(logonConfigration.getLanguage());
		
		
		//创建启动参数
//		 SapGuiStartupData startupInfo = new SapGuiStartupData(destinationDataWritable, objectReference, false, "WB:DISPLAY", null, null, true);
		
		 
		 //启动Winows上的SAP GUI
//		 WinGuiServerProxy.getProxy().openConnection(startupInfo, 0);
		 
	}
		
}