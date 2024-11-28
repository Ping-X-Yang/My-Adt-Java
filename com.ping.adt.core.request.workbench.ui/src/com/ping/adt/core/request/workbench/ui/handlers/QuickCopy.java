 
package com.ping.adt.core.request.workbench.ui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.ui.IEditorPart;

import com.ping.adt.core.tools.MyAdtTools;
import com.sap.adt.tools.core.model.adtcore.AdtVersionEnum;
import com.sap.adt.tools.core.model.adtcore.IAdtObject;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;

public class QuickCopy {
		
	
	@Execute
	public void execute() {
		IEditorPart editor;
		
		
		editor = MyAdtTools.getActiveEditor();
		if (editor == null) {
			return;
		}
		
		IAdtObject adtObject = MyAdtTools.getAdtObject(editor);
		if (adtObject == null) {
			return;
		}
		
		String name = adtObject.getName();
		String typ = adtObject.getType();
		AdtVersionEnum version = adtObject.getVersion();
		
		
		
	}
	
	
	@CanExecute
	public boolean canExecute() {
		
		return true;
	}
		
}