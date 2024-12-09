package com.ping.adt.core.request.workbench.ui.parts;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.InitialContext;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.alibaba.fastjson2.JSON;
import com.ping.adt.core.request.workbench.ui.events.IRequestEventConstants;
import com.ping.adt.core.request.workbench.ui.events.Message;
import com.ping.adt.core.request.workbench.ui.model.Output.Data;
import com.ping.adt.core.request.workbench.ui.model.Output.RequestMessage;
import com.ping.adt.core.tools.MyAdtTools;

public class WorkbenchView {
	RequestWorkbenchView requestWorkbenchView;
	
	
	@PostConstruct
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		requestWorkbenchView = new RequestWorkbenchView(parent);
		requestWorkbenchView.createContents();

	}

	@Focus
	public void setFocus() {

	}

	@Inject
	@Optional
	public void receiveLogMessages(
			@UIEventTopic(IRequestEventConstants.SEND_MESSAGES) String messageString
			) {
		
		Message message = JSON.parseObject(messageString, Message.class);
		
		requestWorkbenchView.receiveLogMessages(message);
		
		
		System.out.println("点击：");
	}
	
}
