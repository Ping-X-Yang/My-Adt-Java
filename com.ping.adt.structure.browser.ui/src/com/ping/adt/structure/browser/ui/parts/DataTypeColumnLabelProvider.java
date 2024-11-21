package com.ping.adt.structure.browser.ui.parts;

import org.eclipse.jface.viewers.StyledString;

import com.ping.adt.structure.browser.ui.common.IComponentType;

public class DataTypeColumnLabelProvider extends ColumnLabelProvider {
	@Override
	public StyledString getStyledText(Object element) {
		String text;
		FieldNode node = (FieldNode) element;
		
		//不为基本类型则不显示数据类型
		if (node.componentType.equals(IComponentType.STRUCTURE) || node.componentType.equals(IComponentType.TABLE)) {
			return new StyledString("");
		}
		
		//设置显示样式
		if (node.length > 0 && node.decimals > 0 ) {
			text = String.format("%s(%d,%d)", node.dataType, node.length, node.decimals);
		}else if(node.length > 0) {
			text = String.format("%s(%d)", node.dataType, node.length);
		}else {
			text = String.format("%s", node.dataType);
		}
		return new StyledString(text);
	}
}
