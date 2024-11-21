package com.ping.adt.structure.browser.ui.parts;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;

public class ColumnLabelProvider extends LabelProvider implements IStyledLabelProvider {
	
	@Override
	public StyledString getStyledText(Object element) {
		return null;
	}
	
	
	@Override
	public Image getImage(Object element) {
		return super.getImage(element);
	}
	
	
	@Override
	public void dispose() {
		super.dispose();
	}

	
}
