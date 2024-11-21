package com.ping.adt.structure.browser.ui.parts;

import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import com.ping.adt.core.tools.MyAdtTools;
import com.ping.adt.structure.browser.ui.common.IComponentType;
import com.ping.adt.structure.browser.ui.common.ILocalImages;

public class RollNameColumnLabelProvider extends ColumnLabelProvider {
	private Map<String, ImageDescriptor> images;
	private ResourceManager resourceManager;
	
	public RollNameColumnLabelProvider(Map<String, ImageDescriptor> images) {
		this.images = images;
		this.resourceManager = MyAdtTools.CreateResourceManager();
	}
	
	@Override
	public StyledString getStyledText(Object element) {
		FieldNode node = (FieldNode)element;
		StyledString styledString = new StyledString();
		styledString.append(node.rollName);
		return styledString;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public Image getImage(Object element) {
		ImageDescriptor image;
		
		FieldNode node = (FieldNode)element;
		switch (node.componentType) {
		case IComponentType.ELEMENT:	//数据元素
			image = images.get(ILocalImages.DATA_ELEMENT_ICON);
			break;
		case IComponentType.STRUCTURE:	//结构
			image = images.get(ILocalImages.STRUCTURE_ICON);
			break;
		case IComponentType.TABLE:	//表类型
			image = images.get(ILocalImages.TABLE_TYPE_ICON);
			break;	
		default:
			image = null;
		}
		
		if (image != null) {
			return resourceManager.createImage(image);
		}
		return super.getImage(element);
	}
	
	
	@Override
	public void dispose() {
		resourceManager.dispose();
		super.dispose();
	}
}
