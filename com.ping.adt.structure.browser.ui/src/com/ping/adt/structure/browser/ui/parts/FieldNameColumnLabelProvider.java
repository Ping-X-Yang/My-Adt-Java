package com.ping.adt.structure.browser.ui.parts;

import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import com.ping.adt.core.tools.MyAdtTools;
import com.ping.adt.structure.browser.ui.common.ILocalImages;

public class FieldNameColumnLabelProvider extends ColumnLabelProvider {
	
	private Map<String, ImageDescriptor> images;
	private ResourceManager resourceManager;
	
	public FieldNameColumnLabelProvider(Map<String, ImageDescriptor> images) {
		this.images = images;
		this.resourceManager = MyAdtTools.CreateResourceManager();
	}
	
	@Override
	public StyledString getStyledText(Object element) {
		FieldNode node = (FieldNode)element;
		StyledString styledString = new StyledString();
		if (node.keyFlag == true) {
			styledString.append(node.fieldName, StyledString.DECORATIONS_STYLER);	//主键添加风格
		}else {
			styledString.append(node.fieldName);
		}
		return styledString;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		FieldNode node = (FieldNode)element;
		if (node.keyFlag == true) {
			return resourceManager.createImage(images.get(ILocalImages.KEY_FIELD_ICON));
		}
		return super.getImage(element);
	}
	
	
	@Override
	public void dispose() {
		resourceManager.dispose();
		super.dispose();
	}

}
