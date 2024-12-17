package com.ping.adt.core.request.workbench.ui.parts;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import com.ping.adt.core.request.workbench.ui.Activator;
import com.ping.adt.core.request.workbench.ui.model.FunctionNode;
import com.ping.adt.core.request.workbench.ui.model.RequestNode;
import com.ping.adt.core.request.workbench.ui.model.StatusNode;
import com.ping.adt.core.request.workbench.ui.model.SystemNode;
import com.ping.adt.core.request.workbench.ui.model.TRNode;
import com.ping.adt.core.request.workbench.ui.model.TRObjectNode;
import com.ping.adt.core.request.workbench.ui.model.TaskNode;
import com.sap.adt.tools.core.ui.AbapCoreUi;
import com.sap.adt.tools.core.ui.IAdtObjectTypeInfoUi;



public class RequestColumnLabelProvider extends ColumnLabelProvider {

	@Override
	public StyledString getStyledText(Object element) {
		RequestNode node = (RequestNode)element;
		StyledString styledString = new StyledString();
		styledString.append(node.getNodeName());
		return styledString;
	}
	
	@Override
	public Image getImage(Object element) {
		
		if (element instanceof FunctionNode || element instanceof SystemNode || element instanceof StatusNode) {
			return PlatformUI.getWorkbench().getSharedImages().getImage("IMG_OBJ_FOLDER");
		}
		
		
		if (element instanceof TRNode) {
			TRNode node = (TRNode) element;
			Image baseRequestImage = Activator.getDefault().getImageRegistry().get("$nl$/icons/obj/transport_request.png");
			switch (node.getStatus()) {
			case "R":
				 return Activator.getDefault().decorateImage(baseRequestImage, "$nl$/icons/requestoverlay_released.png");
			case "O":
				 return Activator.getDefault().decorateImage(baseRequestImage, "$nl$/icons/ovr/waiting_ovr.png");
			case "L":
				return Activator.getDefault().decorateImage(baseRequestImage, "$nl$/icons/taskoverlay_lock.png");
			default:
				return baseRequestImage;
			}
		}
		
		if (element instanceof TaskNode) {
			TaskNode node = (TaskNode) element;
			Image baseTaskImage = Activator.getDefault().getImageRegistry().get("$nl$/icons/obj/transport_task.png");
			switch (node.getStatus()) {
			case "R":
			case "N":
				return Activator.getDefault().decorateImage(baseTaskImage, "$nl$/icons/taskoverlay_released.png");
			default:
				return baseTaskImage;
			}
		}
		
		
		if (element instanceof TRObjectNode) {
			var obj = (TRObjectNode) element;
			IAdtObjectTypeInfoUi objectTypeByGlobalWorkbenchType = AbapCoreUi.getObjectTypeRegistry().getObjectTypeByGlobalWorkbenchType(obj.getWorkbenchObjectType());
			if (objectTypeByGlobalWorkbenchType != null) {
				Image imageByType = objectTypeByGlobalWorkbenchType.getImage();
				if (imageByType == null) {
					imageByType = AbapCoreUi.getSharedImages().getImage("IMG_OBJECT_VISUAL_INTEGRATED");
				}
				return imageByType;
			}
			return AbapCoreUi.getSharedImages().getImage("IMG_OBJECT_VISUAL_INTEGRATED");
		}
		
		return super.getImage(element);
	}
}
