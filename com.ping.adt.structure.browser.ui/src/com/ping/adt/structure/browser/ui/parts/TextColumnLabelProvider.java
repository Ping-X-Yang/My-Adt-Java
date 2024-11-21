package com.ping.adt.structure.browser.ui.parts;

import org.eclipse.jface.viewers.StyledString;

public class TextColumnLabelProvider extends ColumnLabelProvider {
	@Override
	public StyledString getStyledText(Object element) {
		FieldNode node = (FieldNode) element;
		return new StyledString(node.text);
	}
}
