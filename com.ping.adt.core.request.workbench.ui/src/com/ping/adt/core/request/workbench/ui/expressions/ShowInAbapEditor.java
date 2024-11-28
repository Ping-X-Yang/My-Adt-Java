 
package com.ping.adt.core.request.workbench.ui.expressions;

import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import com.sap.adt.tools.abapsource.ui.sources.editors.IAbapSourceMultiPageEditor;
import com.sap.adt.tools.abapsource.ui.sources.editors.IAdtCompoundTextSelection;

import jakarta.inject.Inject;
import jakarta.inject.Named;


public class ShowInAbapEditor {
	
	@Inject
	@Named("activeEditor")
	IEditorPart editorPart;
	
	
	@Inject
	@Named(IServiceConstants.ACTIVE_SELECTION)
	ISelection selection;
	
	
	@SuppressWarnings("restriction")
	@Evaluate
	public boolean evaluate() {
		boolean visible = false;
		
		//上下文菜单项在ABAP编辑器中才显示
		if (editorPart != null && editorPart instanceof IAbapSourceMultiPageEditor) {
			if (selection != null && selection instanceof IAdtCompoundTextSelection) {
				visible = true;
			}
		}
		
		return visible;
	}
}
